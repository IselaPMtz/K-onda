package com.pipudev.k_onda.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.models.User;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.UsersProvider;


public class CodeVerificationActivity extends AppCompatActivity {

    private String phoneNumber;
    private Button btnCodeVerification;
    private EditText etCode;
    private TextView tvSms;
    private ProgressBar pgbar;

    private AuthProvider authProvider;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    private String mVerificationId;
    private UsersProvider userProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        // referencia del btn en el xml al btn java class
        btnCodeVerification = findViewById(R.id.code_verification_btn_codeVerification);
        etCode = findViewById(R.id.code_verification_et_inputCode);
        tvSms = findViewById(R.id.code_verification_tv_envioSms);
        pgbar = findViewById(R.id.code_verification_pgb_logIn);
        // obtenemos los parametros (nombre debe ser igual ) del  intent proveniente de MainActivity.java
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        //verificar si el phonenumber del usuario ya esta registrado firebase


        //obtener los callback para el envio del codigo
        callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                //ocultar los controles
                hideControls();
                String code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    etCode.setText(code);
                    signInWithPhoneNumber(code);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                hideControls();
                Toast.makeText(CodeVerificationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                //regresar a pantalla para reenvio de codigo
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = s;
                hideControls();

            }
        };

        // cuando haga clic en el btn
        btnCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();
                if (!code.isEmpty() && code.length() >= 6) {
                    pgbar.setVisibility(View.VISIBLE);
                    tvSms.setText("Validando código");
                    tvSms.setVisibility(View.VISIBLE);
                    signInWithPhoneNumber(code);
                } else {
                    Toast.makeText(CodeVerificationActivity.this, "código erroneo", Toast.LENGTH_LONG).show();
                }
            }
        });

        //enviamos el codigo
        authProvider = new AuthProvider();
        authProvider.sendCodeVerificationToPhone(phoneNumber, callback);
        userProvider = new UsersProvider();

    }


    private void hideControls() {
        //ocultar los controles
        pgbar.setVisibility(View.GONE);
        tvSms.setVisibility(View.GONE);
    }


    /**
     * Después de que el usuario ingrese el código de verificación que Firebase envió al teléfono del usuario
     * crea un objeto PhoneAuthCredential con el código y el ID de verificación que se pasaron a la devolución de llamada
     */
    private void signInWithPhoneNumber(String code) {
        authProvider.signInWithPhoneNumber(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User();
                    user.setUserID(authProvider.getCurrentUserID()); //id en firebase
                    user.setPhoneNumber(phoneNumber);

                    //validar el telefono del usuario en firebase si ya esta registrado previamente / .get() obtiene informacion de firebase
                    //los campos userName e userImage estan nullos por el momento en Firebase
                    userProvider.getUserOnFirebase(authProvider.getCurrentUserID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) { //hace referencia al document en firebase database
                            if (!documentSnapshot.exists()) { //si no existe el telefono y el id de usuario en firebase procedemos a crearlo
                                userProvider.createUser(user).addOnSuccessListener(new OnSuccessListener<Void>() { //Si se ha creado el usuario correctamente
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        goToOnCompleteInfoActivity(); //procedems a capturar los datos restantes como la imagen y el userName
                                    }
                                });
                            } else { //Si existe el telefono y el id ya registrados en firebase
                                //verificamos que el documento (firebase) contenga los campos userName e userImage en null o en blanco de ser asi
                                // se procedera a registrar ambos campos

                                if (documentSnapshot.contains("userName") && documentSnapshot.contains("userImage")) { //si los contiene pero estan null

                                    if ((documentSnapshot.getString("userName") == null && documentSnapshot.getString("userImage") == null) ||
                                            (documentSnapshot.getString("userName").isEmpty() && documentSnapshot.getString("userImage").isEmpty())) {

                                        goToOnCompleteInfoActivity(); //procedems a capturar los datos faltantes
                                    } else { //si ya contienen datos userName y userImage
                                        goToHomeActivity();//nos manda a la pantalla principal
                                    }
                                }else{ //Si no estan userName e userImage en el documment (firebase) aun
                                    goToOnCompleteInfoActivity(); //procedems a capturar los datos faltantes
                                }
                            }
                        }
                    });
                } else { //Si fallo en iniciar sesion con telefono
                    Toast.makeText(CodeVerificationActivity.this, "error", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void goToOnCompleteInfoActivity() {
        Intent intent = new Intent(CodeVerificationActivity.this, OnCompleteInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Envia a la Activity(Pantalla) Principal de la app y elimina el historial de activities anteriores(task)
     * para que al dar boton regresar no regrese a la verificacion del codigo ni a perdir el numero telefonico
     */
    private void goToHomeActivity() {
        Intent intent = new Intent(CodeVerificationActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}