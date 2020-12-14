package com.pipudev.k_onda.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.pipudev.k_onda.utils.PixUtils;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnCompleteInfoActivity extends AppCompatActivity {

    private TextInputEditText ietUsername;
    private Button btnRegisterUserInfo;
    private UsersProvider usersProvider;
    private AuthProvider authProvider;
    private CircleImageView civImage; //libreria de proyecto github
    private PixUtils pixUtils = new PixUtils();
    private ArrayList<String> aReturnValue = new ArrayList<>();
    private File imageFile;
    private ImageProvider imageProvider;
    private ProgressDialog pgDialog;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_complete_info);
        //obtencion de referencias de controles en xml
        ietUsername = findViewById(R.id.on_complete_info_et_username);
        btnRegisterUserInfo = findViewById(R.id.on_complete_info_btn_register);
        civImage = findViewById(R.id.on_complete_info_civ_picture);
        imageProvider = new ImageProvider(); //clase para conexion a storage
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        user = new User();

        civImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOptionsImage();
            }
        });


        //btn Registar informacion
        btnRegisterUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ietUsername.getText().toString().isEmpty() && imageFile != null) {
                    setUserProfileData();
                } else {
                    Toast.makeText(OnCompleteInfoActivity.this, "Seleccione una imagen e ingrese un nombre de usuario ", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    /**
     * Configuracion de las opciones para agregar imagen al perfil
     */
    private void setOptionsImage() {
        Pix.start(OnCompleteInfoActivity.this, pixUtils.setOptionsImage());
    }

    /**
     * Metodo propio de la liberia de github 3r party para obtener los permisos
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
     * Metodo propio de la liberia de github 3r party
     * Permisos para activar la camara frontal
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(OnCompleteInfoActivity.this, pixUtils.getOptionImage());
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


        //mostramos el dialog que nos indique que se esta guardando la imagen
        pgDialog = new ProgressDialog(OnCompleteInfoActivity.this); //dialog que mostrara cuando se este guardando una imagen
        pgDialog.setTitle("Espere un momento porfavor");
        pgDialog.setMessage("Guardando información");
        pgDialog.show();

        //obtenemos el ID del usuario
        String userID = authProvider.getCurrentUserID();
        //user.setUserID(authProvider.getCurrentUserID());

        //envia el contexto y la imagen seleccionada
        imageProvider.saveImageToStorage(OnCompleteInfoActivity.this, userID, imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //si se guardo
                if (task.isSuccessful()) {
                    imageProvider.getImageUrlFromStorage(userID).addOnSuccessListener(new OnSuccessListener<Uri>() { //obtenemos la direccion de la imagen
                        @Override
                        public void onSuccess(Uri uri) { //si se pudo obtener la url de la imagen
                            updateUserInfo(uri.toString());//actualizamos la informacion en la base de datos
                        }
                    });
                } else {
                    pgDialog.dismiss();
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
            user.setUserName(ietUsername.getText().toString());
            user.setUserImage(imageUrl);
            usersProvider.updateUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) { //Si se actualizo correctamente en firebase
                    pgDialog.dismiss();
                    //Toast.makeText(OnCompleteInfoActivity.this, "Exito actualizando", Toast.LENGTH_LONG).show();
                    goToHomeActivity();

                }
            });

        }

    }

    /**
     * Envia a la Activity(Pantalla) Principal de la app y elimina el historial de activities anteriores(task)
     * para que al dar boton regresar no regrese a la verificacion del codigo ni a perdir el numero telefonico
     */
    private void goToHomeActivity() {
        Intent intent = new Intent(OnCompleteInfoActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}