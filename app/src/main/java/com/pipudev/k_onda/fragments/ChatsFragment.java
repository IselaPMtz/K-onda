package com.pipudev.k_onda.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.Query;
import com.pipudev.k_onda.Adapters.ChatsAdapter;
import com.pipudev.k_onda.Adapters.ContactsAdapter;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.models.Chat;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.ChatsProvider;
import com.pipudev.k_onda.providers.UsersProvider;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private View view;
    private RecyclerView recyclerViewChats;
    private ChatsAdapter chatsAdapter;
    private ChatsProvider chatsProvider;
    private AuthProvider authProvider;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerViewChats = view.findViewById(R.id.fragment_chats_recyclerViewChats);
        chatsProvider = new ChatsProvider();
        authProvider = new AuthProvider();
        //para que nuestros elementos (fragmentos) se posicionen uno debajo del otro
        LinearLayoutManager linearLayoutmanager = new LinearLayoutManager(getContext());
        recyclerViewChats.setLayoutManager(linearLayoutmanager);
        return view;

    }

    //para poder usar Firebase Ui (permite listar los datos(colecciones) de cloud firebase) es necesario sobreescribir estos metodos propios de el

    @Override
    public void onStart() {
        super.onStart();
        //hacer la consulta a la bd para obtener las colecciones de chats
        Query qr = chatsProvider.getCurrentUserChats(authProvider.getCurrentUserID());
        // se usa RecyclerView para poder listar las colecciones y se necesita especificar el modelo a usar en este caso seria Chat
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(qr, Chat.class).build();
        chatsAdapter = new ChatsAdapter(options,getContext());//creamos el adaptador para poder mostrar los componentes del xml
        recyclerViewChats.setAdapter(chatsAdapter);
        //escuche en tiempo real los cambios en la BD
        chatsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //deje de escuchar los cambios en la bd
        chatsAdapter.stopListening();
    }

    //cuando la actividad se cierra
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatsAdapter.getListener()!=null){
            chatsAdapter.getListener().remove();
        }
    }
}