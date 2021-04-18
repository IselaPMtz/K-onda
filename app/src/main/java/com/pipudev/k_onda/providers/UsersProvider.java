package com.pipudev.k_onda.providers;

import androidx.arch.core.executor.TaskExecutor;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pipudev.k_onda.models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase para creacion,eliminacion,actualizacion y manejo de datos de usuarios en Firebase
 */

public class UsersProvider {

    private CollectionReference fbCollection; //almacena una coleccion de usuarios

    public UsersProvider() {
        fbCollection = FirebaseFirestore.getInstance().collection("Users");

    }

    /**
     * obtiene todos la coleccion del usuario
     */
    public DocumentReference getUserOnFirebase(String userID) {
        return fbCollection.document(userID);
    }

    /**
     * metodo usada en ContactsFragment , nos retorna un objeto de tipo query
     */
    public Query getAllUsersByName() {
    return fbCollection.orderBy("userName");
    }


    /**
     * almacena los datos del usario en firebase
     */
    public Task<Void> createUser(User user) {
        //inserta una nueva coleccion en firebase con su document y fields
        return fbCollection.document(user.getUserID()).set(user); //document en firebase sera igual al User ID (autentificacion en firebase) cuando se registra un usuario
    }

    /**
     * actualiza los datos del usuario en firebase en la actividad OnCompleteInfoActivity
     * la primera vez que se registra el usuario
     */
    public Task<Void> updateUser(User user) {
        //pasamos las propiedades de user como un map a firebase
        Map<String, Object> map = new HashMap<>();
        map.put("userName", user.getUserName());
        map.put("userImage", user.getUserImage());
        return fbCollection.document(user.getUserID()).update(map);
    }

    /**
     * establece la imagen del usuario en la database firebase
     */
    public void updateUserImage(User user) {
        //pasamos las propiedades de user como un map a firebase
        Map<String, Object> map = new HashMap<>();
        map.put("userImage", user.getUserImage());
        fbCollection.document(user.getUserID()).update(map);//actualizamos la imagen en firebase Database
    }

    /**
     * establece la imagen del usuario en Null en la database firebase
     */
    public Task<Void> setNullUserImage(String userID) {
        //pasamos las propiedades de user como un map a firebase
        Map<String, Object> map = new HashMap<>();
        map.put("userImage", null);
        return fbCollection.document(userID).update(map);//actualizamos la imagen en firebase Database
    }

    /**
     * establece el userName del usuario en la database firebase
     */
    public Task<Void> updateUserName(String userID, String newUserName) {
        //pasamos las propiedades de user como un map a firebase
        Map<String, Object> map = new HashMap<>();
        map.put("userName", newUserName);
        return fbCollection.document(userID).update(map);//actualizamos la imagen en firebase Database
    }

    /**
     * establece el Info del usuario en la database firebase
     */
    public Task<Void> updateInfo(String userID, String newInfo) {
        //pasamos las propiedades de user como un map a firebase
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo", newInfo);
        return fbCollection.document(userID).update(map);//actualizamos la imagen en firebase Database
    }

}
