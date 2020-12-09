package com.pipudev.k_onda.providers;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pipudev.k_onda.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference storage,storageP;
    UploadTask task;

    public ImageProvider() {
        storage = FirebaseStorage.getInstance().getReference();
    }


    /**
     * regresa un tipo de storageFirebase
     */
    public UploadTask saveImageToStorage(Context context, File file) {

        //pasamos el contexto y obtenemos la ruta del file y establecemos el tamaño de la imagen
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        //crear una referencia para una ubicación de un nivel más bajo en el árbol (storage fireabse) -ahora apunta a la imagen
        storageP = storage.child(new Date() + ".jpg");
        return task = storageP.putBytes(imageByte);
    }

    /**retorna la URL de la imagen obtenida en storage firebase*/
    public Task<Uri> getImageUrlFromStorage(){
        return storageP.getDownloadUrl();
    }

}
