package com.pipudev.k_onda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.activities.ChatActivity;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

//Se usa RecyclerView para poder listar las colecciones
//especificar el tipo de modelo ke se usuara en este caso user para trabajar con la coleccion users en firestorage
//necesitamos segundo parametro ke proviene de la clase interna
public class ContactsAdapter extends FirestoreRecyclerAdapter<User, ContactsAdapter.ViewHolder> {

    Context context;
    AuthProvider authProvider;

    //constructor de la clase
    public ContactsAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
    }

//implementar metodos de FirestoreRecyclerAdapter

    //Metodo que mostrara las colecciones de usuario desde la BD firebase
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {

        //para listar todos los contactos menos el usuario propio en sesion
        if (user.getUserID().equals(authProvider.getCurrentUserID())) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.height = 0;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            params.bottomMargin = 0;
            holder.itemView.setVisibility(View.VISIBLE);
        }

        //llamar al holder sus propiedades
        holder.tvUsernameContact.setText(user.getUserName());
        holder.tvUserStatusInfoContact.setText(user.getUserInfo());
        if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
            Picasso.get().load(user.getUserImage()).into(holder.civUserContact); //obtenemos la imagen desde firebase y la mostramos en el circleImageView
        } else {
            holder.civUserContact.setImageResource(R.drawable.ic_person_circle); //obtenemos la imagen predeterminada
        }
        //cuando el usuario hace clic en la persona-contacto en el apartado de contactos para iniciar el chat
        holder.cview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(user.getUserID());
            }
        });
    }

    private void goToChatActivity(String contactID) {
        Intent intent = new Intent(this.context, ChatActivity.class);
        intent.putExtra("userID",contactID);
        context.startActivity(intent);
    }

    //necesitamos crear un viewHolder(fragmentos) dentro de la actividad chats
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsernameContact, tvUserStatusInfoContact;
        private CircleImageView civUserContact;
        View cview;

        //constructor de la subclase
        //cada persona-contacto es un view
        public ViewHolder(View view) { //proviene de esta misma clase en metodo onCreateViewHolder
            super(view); //lo mandamos la clase inicial ke es ContacsAdapter aqui arriba
            //creamos las referencias a los componentes del xml
            cview = view;
            tvUsernameContact = view.findViewById(R.id.cardview_contacts_tv_username);
            tvUserStatusInfoContact = view.findViewById(R.id.cardview_contacts_tv_userstatus);
            civUserContact = view.findViewById(R.id.cardview_contacts_ci_image);

        }
    }

    //metodo que crea los fragmentos de Contactos en la actividad
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //almacenara un array del xml CardView_contacts
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts, parent, false);
        return new ViewHolder(view);
    }


}
