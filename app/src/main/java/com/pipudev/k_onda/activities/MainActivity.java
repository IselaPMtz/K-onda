package com.pipudev.k_onda.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.pipudev.k_onda.R;

public class MainActivity extends AppCompatActivity {

    private Button btnsendCodeVerification;
    private EditText txtPhone;
    private CountryCodePicker ccPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //referencia del btn y editText en el xml al btn java class
        btnsendCodeVerification = findViewById(R.id.main_btn_sendCode);
        txtPhone = findViewById(R.id.main_et_phoneNumber);
        ccPicker = findViewById(R.id.main_ccp_codePicker);

        // cuando haga clic en el btn
        btnsendCodeVerification.setOnClickListener(v -> getCountryCodePhoneNumber());

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