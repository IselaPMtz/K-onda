package com.pipudev.k_onda.providers;

import android.app.Activity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pipudev.k_onda.activities.CodeVerificationActivity;

import java.util.concurrent.TimeUnit;

public class AuthProvider extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private String verificationId;

    public AuthProvider() {
        //instanciar al cargar la clase
        fbAuth = FirebaseAuth.getInstance(); //FirebaseAuth.getInstance();
    }

    /**
     * pasa el número de teléfono del usuario al método PhoneAuthProvider.verifyPhoneNumber para solicitar que Firebase lo verifique
     * recibe 2 parametros una variable string y una de tipo phoneAuth
     * IMPORTANTE : activar  en el proyecto -google cloud platform - Android Device Verification  , safetynet y agregar certificado sha256 en firebase console
     */
    public void sendCodeVerificationToPhone(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fbAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /**
     * Después de que el usuario ingrese el código de verificación que Firebase envió al teléfono del usuario
     * crea un objeto PhoneAuthCredential con el código y el ID de verificación que se pasaron a la devolución de llamada
     */
    public Task<AuthResult> signInWithPhoneNumber(String verificationId, String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            return fbAuth.signInWithCredential(credential);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * obtener los datos del usuario como UserID generado en la authentificacion
     */
    public String getCurrentUserID() {
        if (fbAuth.getCurrentUser() != null) {
            return fbAuth.getCurrentUser().getUid();
        }else{
            return null;
        }
    }
}
