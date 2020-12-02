package com.pipudev.k_onda.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pipudev.k_onda.R;

public class CodeVerificationActivity extends AppCompatActivity {

    private String phoneNumber;
    private Button btnCodeVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        /* referencia del btn en el xml al btn java class*/
        btnCodeVerification = findViewById(R.id.btn_codeVerification_codeVerification);
        /* obtenemos los parametros (nombre debe ser igual ) del  intent proveniente de MainActivity.java*/
        phoneNumber = getIntent().getStringExtra("phoneNumber");

    }


}