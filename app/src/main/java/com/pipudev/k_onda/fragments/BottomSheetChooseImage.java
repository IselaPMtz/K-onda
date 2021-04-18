package com.pipudev.k_onda.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.activities.ProfileActivity;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.ImageProvider;
import com.pipudev.k_onda.providers.UsersProvider;


/**
 * Modal Bottom Sheet
 * Clase que muestra el fragmento para eliger una imagen cuando se hace clic en editar imagen en Profile activity
 * extiende BottomSheetDialogFragment
 */


public class BottomSheetChooseImage extends BottomSheetDialogFragment {

    private LinearLayout linearLayoutBottomSheetDeleteImage; // contiene el icono y el texto de eliminar foto
    private LinearLayout linearLayoutBottomSheetImageGalery;  // contiene el icono y el texto de agregar foto
    private ImageProvider imageProvider;
    private AuthProvider authProvider;
    private UsersProvider usersProvider;
    private String imageProfileUrl;

    public void setImageProfileUrl(String imageProfileUrl) { //proveniente de profileActivity
        this.imageProfileUrl = imageProfileUrl;
    }


    public BottomSheetChooseImage() {
    }

    /**
     * Crear este metodo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Crear este metodo
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //creamos una vista y le pasamos el layout a mostrar, el contenedor
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_choose_image, container, false);
        linearLayoutBottomSheetDeleteImage = view.findViewById(R.id.bottom_sheet_choose_image_ly_deleteImage);
        linearLayoutBottomSheetImageGalery = view.findViewById(R.id.bottom_sheet_choose_image_ly_imageGalery);
        imageProvider = new ImageProvider();
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();


        linearLayoutBottomSheetDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageFromFirebase();
            }
        });


        linearLayoutBottomSheetImageGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImageToFirebase();
            }
        });

        return view;

    }

    private void updateImageToFirebase() {
        ((ProfileActivity) getActivity()).setOptionsImage(); //metodo en profileActivity
        //((ProfileActivity) getActivity()).setUserImageProfileData();
    }

    private void deleteImageFromFirebase() {

        if (imageProfileUrl != null) { //el valor de imageProfileUrl proviene de profileActivity
            imageProvider.deleteImageFromStorage(imageProfileUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) { //Si se elimino la imagen en Storage pero no en la BD aun asi que procedemos a poner el valor a null
                        usersProvider.setNullUserImage(authProvider.getCurrentUserID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if (task2.isSuccessful()) {
                                    Toast.makeText(getContext(), "La imagen se elimino correctamente", Toast.LENGTH_SHORT).show();
                                    imageProfileUrl = null;
                                } else {
                                    Toast.makeText(getContext(), "No se pudo elimnar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            }//onComplete
                        });
                    } else {
                        Toast.makeText(getContext(), "No se pudo elimnar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }//if
            });//listener
        }//if
        else {
            Toast.makeText(getContext(), "Imagen Vacia", Toast.LENGTH_SHORT).show();
        }
    }//metodo


}//clase

