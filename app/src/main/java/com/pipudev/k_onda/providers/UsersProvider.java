package com.pipudev.k_onda.providers;

import androidx.arch.core.executor.TaskExecutor;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pipudev.k_onda.models.User;

public class UsersProvider {

    private CollectionReference fbCollection; //almacena una coleccion de usuarios

    public UsersProvider() {
        fbCollection = FirebaseFirestore.getInstance().collection("Users");

    }

    /**
     * almacena los datos del usario en firebase
     */
    public Task<Void> createUser(User user) {
        //inserta una nueva coleccion en firebase con su document y fields
        return fbCollection.document(user.getUserID()).set(user); //document en firebase sera igual al User ID (autentificacion en firebase) cuando se registra un usuario
    }


}
