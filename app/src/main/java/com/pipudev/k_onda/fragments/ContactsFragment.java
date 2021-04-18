package com.pipudev.k_onda.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.pipudev.k_onda.Adapters.ContactsAdapter;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.UsersProvider;

public class ContactsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerViewContacts;
    private ContactsAdapter contactsAdapter;
    private UsersProvider usersProvider;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerViewContacts = view.findViewById(R.id.fragment_contacts_recyclerViewContacts);
        usersProvider = new UsersProvider();
        //para que nuestros elementos (fragmentos) se posicionen uno debajo del otro
        LinearLayoutManager linearLayoutmanager = new LinearLayoutManager(getContext());
        recyclerViewContacts.setLayoutManager(linearLayoutmanager);
        return view;
    }

    //para poder usar Firebase Ui (permite listar los datos(colecciones) de cloud firebase) es necesario sobreescribir estos metodos propios de el

    @Override
    public void onStart() {
        super.onStart();
        //hacer la consulta a la bd para obtener las colecciones de usuarios
        Query qr = usersProvider.getAllUsersByName();
        // se usa RecyclerView para poder listar las colecciones y se necesita especificar el modelo a usar en este caso seria User
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<User>().setQuery(qr,User.class).build();
        contactsAdapter = new ContactsAdapter(options,getContext());//creamos el adaptador para poder mostrar los componentes del xml
        recyclerViewContacts.setAdapter(contactsAdapter);
        //escuche en tiempo real los cambios en la BD
        contactsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //deje de escuchar los cambios en la bd
        contactsAdapter.stopListening();
    }
}