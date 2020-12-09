package com.pipudev.k_onda.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.ImageProvider;
import com.pipudev.k_onda.providers.UsersProvider;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnCompleteInfoActivity extends AppCompatActivity {

    private TextInputEditText ietUsername;
    private Button btnRegisterUserInfo;
    private UsersProvider usersProvider;
    private AuthProvider authProvider;
    private CircleImageView civImage; //libreria de proyecto github
    private Options optionImage;//libreria de proyecto github
    private ArrayList<String> aReturnValue = new ArrayList<>();
    private File imageFile;
    private ImageProvider imageProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_complete_info);

        ietUsername = findViewById(R.id.on_complete_info_et_username);
        btnRegisterUserInfo = findViewById(R.id.on_complete_info_btn_register);
        civImage = findViewById(R.id.on_complete_info_civ_picture);
        imageProvider = new ImageProvider();

        civImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOptionsImage();
            }
        });


        usersProvider = new UsersProvider();
        btnRegisterUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ietUsername.getText().toString().isEmpty() && imageFile!=null){
                    setUserProfileData();
                }else{
                    Toast.makeText(OnCompleteInfoActivity.this, "Seleccione una imagen e ingrese un nombre de usuario ", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    /**
     * Configuracion de las opciones para agregar imagen al perfil
     */
    private void setOptionsImage() {

        optionImage = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(aReturnValue)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setExcludeVideos(false)                                       //Option to exclude videos
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");                                    //Custom Path For media Storage
        Pix.start(OnCompleteInfoActivity.this, optionImage);
    }

    /**
     * Metodo propio de la liberia de github
     * Se ejecuta cuando el usuario elige una imagen
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            aReturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            imageFile = new File(aReturnValue.get(0));//obtener la imagen seleccionada
            if (imageFile != null) {
                civImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));//establecer la imagen en el control ImageView
            }
        }
    }

    /**
     * Permisos para activar la camara frontal
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(OnCompleteInfoActivity.this, optionImage);
                } else {
                    Toast.makeText(OnCompleteInfoActivity.this, "Conceda los permisos de acceso a la cámara ", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Guarda la imagen a firebase
     */
    private void setUserProfileData() {
        //envia el contexto y la imagen seleccionada
        imageProvider.saveImageToStorage(OnCompleteInfoActivity.this, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //si se guardo
                if (task.isSuccessful()) {
                    imageProvider.getImageUrlFromStorage().addOnSuccessListener(new OnSuccessListener<Uri>() { //obtenemos la direccion de la imagen
                        @Override
                        public void onSuccess(Uri uri) { //si se pudo obtener la url de la imagen
                            updateUserInfo(uri.toString());//actualizamos la informacion
                        }
                    });
                } else {
                    Toast.makeText(OnCompleteInfoActivity.this, "error al guardar la imagen seleccionada", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * almacena el resto de la informacion del usuario en firebase(actualizar datos) ya que
     * el id y el phonenumber ya se regsitraron al iniciar por primera vez la app
     */
    private void updateUserInfo(String imageUrl) {

        if (!ietUsername.getText().toString().isEmpty()) { //si no esta vacio el textinput
            authProvider = new AuthProvider();
            User user = new User();
            user.setUserID(authProvider.getCurrentUserID());
            user.setUserName(ietUsername.getText().toString());
            user.setUserImage(imageUrl);
            usersProvider.updateUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) { //Si se actualizo correctamente en firebase
                    Toast.makeText(OnCompleteInfoActivity.this, "Exito actualizando", Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}