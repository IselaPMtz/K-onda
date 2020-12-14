package com.pipudev.k_onda.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.fragments.BottomSheetChooseImage;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.ImageProvider;
import com.pipudev.k_onda.providers.UsersProvider;
import com.pipudev.k_onda.utils.PixUtils;
import com.pipudev.k_onda.utils.ToolbarCustom;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FloatingActionButton btnChooseImagen;
    private UsersProvider usersProvider;
    private AuthProvider authProvider;
    //Variables para establecer los valores del usuario en la actividad
    private TextView tvUserName, tvInfo, tvPhoneNumber;
    private CircleImageView civImageProfile;
    private User user;
    private PixUtils pixUtils = new PixUtils();
    private ArrayList<String> aReturnValue = new ArrayList<>();
    private File imageFile;
    private ImageProvider imageProvider;
    private String getLastImageProfileFromStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //para que funcione al oprimir el btnRegresar en el toolbar hayq ue agregar en el manifest la actividad padre e hija
        ToolbarCustom.showToolbar(this, "Perfil", true); //al ser un metodo statico no requiere instanciar la clase

        tvUserName = findViewById(R.id.profile_tv_username);
        tvInfo = findViewById(R.id.profile_tv_info);
        tvPhoneNumber = findViewById(R.id.profile_tv_phonenumber);
        civImageProfile = findViewById(R.id.profile_ci_imageprofile);
        btnChooseImagen = findViewById(R.id.profile_chooseImage);

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        imageProvider = new ImageProvider(); //clase para conexion a storage


        /**Boton para editar foto de perfil*/
        btnChooseImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetChooseImage();
            }
        });

        getUserInfoFromFirebase();

    }

    /**
     * Al abrir la actividad se carga la informacion
     */
    private void getUserInfoFromFirebase() {
        //obtenemos el user ID y get para obtener su informacion

        usersProvider.getUserOnFirebase(authProvider.getCurrentUserID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) { //Si el documento del usuario existe , el usuario si esta registrado y procedemos a obtener la informacion de el
                    user = documentSnapshot.toObject(User.class); //Obtenemos todos los valores(campos) del document de firebase y lo convertimos a tipo User (mismos campos)
                    if (user != null) {
                        tvUserName.setText(user.getUserName());
                        tvPhoneNumber.setText(user.getPhoneNumber());
                        if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                            Picasso.get().load(user.getUserImage()).into(civImageProfile); //obtenemos la imagen desde firebase y la mostramos en el circleImageView
                        } else if (user.getUserImage()== null){
                            user.setUserImage(null);//si en firebase el campo userImage esta vacio (null) por que elimino la foto de perfil
                        }

                    }
                    //

                }

            }
        });
    }

    /**
     * Imagen por defecto
     */
    public void setDefaultImage() {
        civImageProfile.setImageResource(R.drawable.ic_person_white);
    }

    /**
     * Configuracion de las opciones para agregar imagen al perfil usa los siguientes 2 metodos
     */
    public void setOptionsImage() {
        Pix.start(ProfileActivity.this, pixUtils.setOptionsImage());
        //setUserImageProfile();
    }

    /**
     * Metodo propio de la liberia de github 3r party para obtener los permisos
     * Se ejecuta automaticamente cuando el usuario elige una imagen
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            aReturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            imageFile = new File(aReturnValue.get(0));//obtener la imagen seleccionada
            Toast.makeText(ProfileActivity.this, "Si entra", Toast.LENGTH_LONG).show();
            if (imageFile != null) {
                civImageProfile.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));//establecer la imagen en el control ImageView
                setUserImageProfile();
            }
        }
    }

    /**
     * Metodo propio de la liberia de github 3r party
     * Permisos para activar la camara frontal
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ProfileActivity.this, pixUtils.getOptionImage());
                } else {
                    Toast.makeText(ProfileActivity.this, "Conceda los permisos de acceso a la cámara ", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Guarda la imagen a Storage y a firebase
     */
    public void setUserImageProfile() {

        //envia el contexto y la imagen seleccionada
        //si no hay una imagen mostrada en el circleViewImage , el userImage esta null
        //obtenemos el ID del usuario
        String userID = authProvider.getCurrentUserID();
        imageProvider.saveImageToStorage(ProfileActivity.this, userID, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //si se guardo
                if (task.isSuccessful()) {
                    imageProvider.getImageUrlFromStorage(userID).addOnSuccessListener(new OnSuccessListener<Uri>() { //obtenemos la direccion de la imagen
                        @Override
                        public void onSuccess(Uri uri) { //si se pudo obtener la url de la imagen
                            user.setUserImage(uri.toString());
                            usersProvider.updateUser(user);//actualizar foto de perfil de usuario en la base de datos de firebase
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "error al guardar la imagen seleccionada", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Muestra el fragmento creado en la actividad
     */
    private void showBottomSheetChooseImage() {
        BottomSheetChooseImage bottomSheetChooseImage = new BottomSheetChooseImage();
        //obtenemos la imagen de la url para proceder a modificarla,elimnar
        if (user != null) { //si ya tiene imagen establecida la enviamos
            bottomSheetChooseImage.setImageProfileUrl(user.getUserImage());//tipo String
        }
        bottomSheetChooseImage.show(getSupportFragmentManager(), bottomSheetChooseImage.getTag());

    }

}