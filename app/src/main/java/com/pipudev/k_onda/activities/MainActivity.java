package com.pipudev.k_onda.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {

    private Button btnsendCodeVerification;
    private EditText txtPhone;
    private CountryCodePicker ccPicker;
    private TextView tvSms;
    private AuthProvider authProvider;

    /**
     * Llamado la primera vez que se crea la app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authProvider = new AuthProvider();
        //referencia del btn y editText en el xml al btn java class
        btnsendCodeVerification = findViewById(R.id.main_btn_sendCode);
        txtPhone = findViewById(R.id.main_et_phoneNumber);
        ccPicker = findViewById(R.id.main_ccp_codePicker);

        // cuando haga clic en el btn
        btnsendCodeVerification.setOnClickListener(v -> getCountryCodePhoneNumber());

    }

    /**
     * Metodo propio de android que se ejecuta despues de onCreate method la primera vez
     * Despues cada que se salga de la app sin matar la app , al regresar a ella se llama a onStart
     * Aqui validaremos si el usuario esta registrodo- si es asi manda directamente a la actividad (Home Activity) principal
     * si no procede a registrar el numero telefonico (Main activity)
     * onStart is not waiting for all the code inside onCreate to finish before executing it's own code
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (authProvider.getSessionUser() != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void getCountryCodePhoneNumber() {


        if (!txtPhone.getText().toString().isEmpty()) {
            goToCodeVerificationActivity(ccPicker.getSelectedCountryCodeWithPlus() + txtPhone.getText().toString());

        } else {
            Toast.makeText(MainActivity.this, "introduce tu número telefónico", Toast.LENGTH_LONG).show();
        }

    }


    private void goToCodeVerificationActivity(String phoneNumber) {
        // creamos el intent para acceder a la otra actividad
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }


}