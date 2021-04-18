package com.pipudev.k_onda.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pipudev.k_onda.models.Chat;

import java.util.ArrayList;

public class ChatsProvider {

    CollectionReference fbCollection;

    public ChatsProvider() {
        fbCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    //crea un chat si este no exite ya en la sesion del usuario
    public Task<Void> createChat(Chat chat) {
        return fbCollection.document().set(chat); //document va vacio para ke se genere automaticamente en firebase
    }

    //verificar si el chat entre el usuario en sesion y el contacto ya existe
    public Query getChatByCurrentUserandContact(String currentUserID, String contactID) {
        ArrayList<String> aIds = new ArrayList<>();
        //ambas formas de combinacion ya que pudiera aparecer asi en firebase
        aIds.add(currentUserID + contactID);
        aIds.add(contactID + currentUserID);
        return fbCollection.whereIn("chatID", aIds); //busca en la coleccion(document) de ambas maneras, eso lo hace firebase

    }

    /**
     * metodo usada en ChatsFragment , nos retorna un objeto de tipo query
     */
    public Query getCurrentUserChats(String currentUserID) {
        return fbCollection.whereArrayContains("chatIDs",currentUserID);
    }


}
