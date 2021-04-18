package com.pipudev.k_onda.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.models.Chat;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.ChatsProvider;
import com.pipudev.k_onda.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String contactUserID;
    private UsersProvider usersProvider;
    private AuthProvider authProvider;
    private ImageView imgViewBack;
    private TextView tvContactUsername;
    private CircleImageView civContactImageUser;
    private ChatsProvider chatsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //consulta a FB para obtener el userID del contacto seleccionado para el chat
        contactUserID = getIntent().getStringExtra("userID"); //userID del contacto
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        chatsProvider = new ChatsProvider();
        showChatToolBar(R.layout.chat_toolbar);
        getContactUserInfo();
        checkIfChatExist(); //verificar si ya existe el char en la bd firebase
    }

    private void checkIfChatExist() {
        chatsProvider.getChatByCurrentUserandContact(authProvider.getCurrentUserID(), contactUserID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() == 0) {//Si no encontro nada
                    createChat(); //creamos el chat
                } else {
                    Toast.makeText(ChatActivity.this, "el chat ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void createChat() {
        //IMPORTANTE: el usuario del chat es la combinacion del ID del usuario en sesion + el ID del contacto
        //parametros para la creacion del chat
        Chat chat = new Chat();
        chat.setChatID(authProvider.getCurrentUserID() + contactUserID);
        chat.setTimeStamp(new Date().getTime()); //devuelve un numero de tipo long
        ArrayList<String> chatsID = new ArrayList<>();
        chatsID.add(authProvider.getCurrentUserID());
        chatsID.add(contactUserID);
        chat.setChatIDs(chatsID);
        //creacion del chat con los parametros en caso de ser exitoso
        chatsProvider.createChat(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, "Chat creado exitosamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //obtener los datos del contacto
    private void getContactUserInfo() {
        //obtener la informacion del contacto en tiempo real
        usersProvider.getUserOnFirebase(contactUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) { //si existe el usuario-contacto en la bd
                    User user = documentSnapshot.toObject(User.class); //obtenemos la coleccion del contacto y lo convertimos a objeto User
                    if (user != null) {
                        tvContactUsername.setText(user.getUserName());
                        if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                            Picasso.get().load(user.getUserImage()).into(civContactImageUser);
                        } else {
                            civContactImageUser.setImageResource(R.drawable.ic_person_circle); //obtenemos la imagen predeterminada
                        }
                    }
                }

            }
        });
    }

    //permite mostrar el toolbar pesonalizado para los chats (usuario - online)
    private void showChatToolBar(int resource) {
        Toolbar toolbar = findViewById(R.id.action_bar_toolbar);
        setSupportActionBar(toolbar);
        //para poner los parametros
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource, null);
        actionBar.setCustomView(view);

        //hacer que la flecha nos regrese a la pantalla de contactos
        imgViewBack = view.findViewById(R.id.chat_toolbar_img_arrowback);
        tvContactUsername = view.findViewById(R.id.chat_toolbar_tv_username);
        civContactImageUser = view.findViewById(R.id.chat_toolbar_civ_img_user);

        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //permite regresar a la actividad anterior
            }
        });
    }
}