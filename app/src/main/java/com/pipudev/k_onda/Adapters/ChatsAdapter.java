package com.pipudev.k_onda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.activities.ChatActivity;
import com.pipudev.k_onda.models.Chat;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

//Se usa RecyclerView para poder listar las colecciones
//especificar el tipo de modelo ke se usuara en este caso user para trabajar con la coleccion users en firestorage
//necesitamos segundo parametro ke proviene de la clase interna
public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    User userContact;
    ListenerRegistration listener;

    //constructor de la clase
    public ChatsAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        userContact = new User();
    }

    //implementar metodos de FirestoreRecyclerAdapter
    //Metodo que mostrara las colecciones de chats desde la BD firebase
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        String chatContactUserID = "";

        //obtenemos el idDelContacto para mostrar en el chat
        for (String i : chat.getChatIDs()) {
            if (!authProvider.getCurrentUserID().equals(i)) { //si es diferente de mi currentUserID
                chatContactUserID = i;
                break;
            }
        }

        //para poder usar los controles(xml) que son fragmentos de chat en la actividad usamos el holder
        getChatContactUserInfo(holder, chatContactUserID);

    }

    private void getChatContactUserInfo(@NonNull ViewHolder holder, String chatContactUserID) {
        //obtener la info en tiempo real de los mensajes
        //cada que usamos snapshot debemos usar un evento listener para saber en que momento se debe destruir este SnapShotlistener
        //para evitar que se trabe y consuma recursos la app
        listener= usersProvider.getUserOnFirebase(chatContactUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    userContact = documentSnapshot.toObject(User.class); //si existe el documento(objeto) en fb lo mapeamos a tipo User
                    //poblamos los controles con los datos del usuario-contacto en el chat
                    holder.tvUsernameContact.setText(userContact.getUserName());
                    if (userContact.getUserImage() != null && !userContact.getUserImage().isEmpty()) {
                        Picasso.get().load(userContact.getUserImage()).into(holder.civUserContact);
                    }else{
                        holder.civUserContact.setImageResource(R.drawable.ic_person_circle); //obtenemos la imagen predeterminada
                    }

                }

            }
        });
    }

    //en un adapter no se pueda usar directamente el metodo ondestroy del listener
    //asi que creamos un listener que sera usado en chatsFragment.java
    public ListenerRegistration getListener() {
        return listener;
    }

    //necesitamos crear un viewHolder(fragmentos) dentro de la actividad chats
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsernameContact, tvUserContactLastMsg, tvTimestamp;
        private CircleImageView civUserContact;
        private ImageView imgCheck;
        View cview;

        //constructor de la subclase
        //cada persona-contacto es un view
        public ViewHolder(View view) { //proviene de esta misma clase en metodo onCreateViewHolder
            super(view); //lo mandamos la clase inicial ke es ContacsAdapter aqui arriba
            //creamos las referencias a los componentes del xml
            cview = view;
            tvUsernameContact = view.findViewById(R.id.cardview_chats_tv_username);
            tvUserContactLastMsg = view.findViewById(R.id.cardview_chats_tv_userLastMsg);
            tvTimestamp = view.findViewById(R.id.cardview_chats_tv_timestamp);
            civUserContact = view.findViewById(R.id.cardview_chats_ci_image);
            imgCheck = view.findViewById(R.id.cardview_chats_img_check);
        }

    }//clase

    //metodo que crea los fragmentos de Contactos en la actividad
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //almacenara un array del xml CardView_chat
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }


}
