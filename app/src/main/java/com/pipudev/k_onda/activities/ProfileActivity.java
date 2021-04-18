package com.pipudev.k_onda.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.UploadTask;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.fragments.BottomSheetChangeInfo;
import com.pipudev.k_onda.fragments.BottomSheetChangeUsername;
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
    private ImageView ivEditUsername, ivEditInfo;
    private ListenerRegistration listenerRegistration; //escuchar los eventos en tiempo real


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
        ivEditUsername = findViewById(R.id.profile_iv_icon_edit_username);
        ivEditInfo = findViewById(R.id.profile_iv_icon_edit_info);

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        imageProvider = new ImageProvider(); //clase para conexion a storage

        getUserInfoFromFirebase(); //obtenemos la info de perfil del usuario

        /**Boton para editar foto de perfil*/
        btnChooseImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetChooseImage();
            }
        });

        /**Boton para editar foto de perfil*/
        ivEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) { //si ya se cargaron los datos
                    showBottomSheetChangeUsername();
                }
            }
        });

        /**Boton para editar foto de perfil*/
        ivEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) { //si ya se cargaron los datos
                    showBottomSheetChangeInfo();
                }
            }
        });


    }

    /**
     * Al abrir la actividad Profile obtenemos en timepo real la informacion
     */
    private void getUserInfoFromFirebase() {
        //obtenemos el user ID y get para obtener su informacion
        //hay que creer una variable para saber cuando termina el listener de tiempo real y ser removido para que no prosiga en las demas actividades

       listenerRegistration = usersProvider.getUserOnFirebase(authProvider.getCurrentUserID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) { //Si el documento del usuario existe , el usuario si esta registrado y procedemos a obtener la informacion de el
                        user = documentSnapshot.toObject(User.class); //Obtenemos todos los valores(campos) del document de firebase y lo convertimos a tipo User (mismos campos)
                        if (user != null) {
                            tvUserName.setText(user.getUserName());
                            tvPhoneNumber.setText(user.getPhoneNumber());
                            if (user.getUserInfo()!=null && !user.getUserInfo().isEmpty()){ //obtenemos la info del usuario
                                tvInfo.setText(user.getUserInfo());
                            }
                            if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                                Picasso.get().load(user.getUserImage()).into(civImageProfile); //obtenemos la imagen desde firebase y la mostramos en el circleImageView
                            } else if (user.getUserImage() == null) {
                                setDefaultImage();
                                user.setUserImage(null);//si en firebase el campo userImage esta vacio (null) por que elimino la foto de perfil
                            }

                        }
                    }
                }
            }
        });

    }

    /**Destruir la actividad*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration!= null){ //si se creo el listener de firebase tiempo real
            listenerRegistration.remove(); //removerlo salir de la actividad
        }
    }

    /**
     * Configuracion de las opciones para agregar imagen al perfil usa los siguientes 2 metodos
     */
    public void setOptionsImage() {
        Pix.start(ProfileActivity.this, pixUtils.setOptionsImage());
    }

    /**
     * Imagen por defecto
     */
    private void setDefaultImage() {
        civImageProfile.setImageResource(R.drawable.ic_person_white);
    }

    /**
     * Se ejecuta automaticamente cuando el usuario elige una imagen
     * metodo propio de la 3rd party library
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            aReturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            imageFile = new File(aReturnValue.get(0));//obtener la imagen seleccionada
            //Toast.makeText(ProfileActivity.this, "Si entra", Toast.LENGTH_LONG).show();
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
                            usersProvider.updateUserImage(user);//actualizar foto de perfil de usuario en la base de datos de firebase
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "error al guardar la imagen seleccionada", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Muestra el fragmento creado en la actividad al hacer clic en editar imagen
     */
    private void showBottomSheetChooseImage() {
        BottomSheetChooseImage bottomSheetChooseImage = new BottomSheetChooseImage();
        //obtenemos la imagen de la url para proceder a modificarla,elimnar
        if (user != null) { //si ya tiene imagen establecida la enviamos
            bottomSheetChooseImage.setImageProfileUrl(user.getUserImage());//tipo String
        }
        bottomSheetChooseImage.show(getSupportFragmentManager(), bottomSheetChooseImage.getTag());

    }

    /**
     * Muestra el fragmento creado en la actividad al hacer clic en editar Username
     */
    private void showBottomSheetChangeUsername() {
        BottomSheetChangeUsername bottomSheetChangeUsername = new BottomSheetChangeUsername();
        //obtenemos el nombre de usuario para proceder a modificarla
        //!tvUserName.getText().toString().isEmpty()
        if (user.getUserName() != null && !user.getUserName().isEmpty()) { //si ya tiene un username
            bottomSheetChangeUsername.setCurrentUsername(user.getUserName());//tipo String
        }
        bottomSheetChangeUsername.show(getSupportFragmentManager(), bottomSheetChangeUsername.getTag());

    }

    /**
     * Muestra el fragmento creado en la actividad al hacer clic en editar Info
     */
    private void showBottomSheetChangeInfo() {
        BottomSheetChangeInfo bottomSheetChangeInfo = new BottomSheetChangeInfo();
        //obtenemos el nombre de usuario para proceder a modificarla
        //!tvUserName.getText().toString().isEmpty()
        if (user.getUserInfo() != null && !user.getUserInfo().isEmpty()) { //si ya tiene un username
            bottomSheetChangeInfo.setCurrentInfo(user.getUserInfo());//tipo String
        }
        bottomSheetChangeInfo.show(getSupportFragmentManager(), bottomSheetChangeInfo.getTag());

    }



}