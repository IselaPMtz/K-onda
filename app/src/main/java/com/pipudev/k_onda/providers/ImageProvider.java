package com.pipudev.k_onda.providers;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

/**
 * Clase que permite obtener , crear, actualizar y eliminar una imagen en firebase Storage y database
 */
public class ImageProvider {

    StorageReference storage, storageP;
    FirebaseStorage fbStorage;
    UploadTask task;

    public ImageProvider() {
        fbStorage = FirebaseStorage.getInstance(); //obtenemos la instancia a firebaseStorage
        storage = fbStorage.getReference(); //la referencia la storage
    }


    /**
     * regresa un tipo de storageFirebase
     */
    public UploadTask saveImageToStorage(Context context, String userID, File file) {

        //pasamos el contexto y obtenemos la ruta del file y establecemos el tamaño de la imagen
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        //crear una referencia para una ubicación de un nivel más bajo en el árbol (storage fireabse) -ahora apunta a la imagen
        //cuando se registra el usuario por primera vez se crea una carpeta ese usuario
        storageP = storage.child("images/" + userID + "/" + "profile/" + "imageProfile.jpg");
        return task = storageP.putBytes(imageByte);
    }

    /**
     * retorna la URL de la imagen obtenida en storage firebase
     */
    public Task<Uri> getImageUrlFromStorage(String userID) {
        return storage.child("images/" + userID + "/" + "profile/" + "imageProfile.jpg").getDownloadUrl();
    }


    public Task<Void> deleteImageFromStorage(String url) {
        return fbStorage.getReferenceFromUrl(url).delete(); //permite borrar la imagen en Firebase Storage
    }


}
