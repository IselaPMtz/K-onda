package com.pipudev.k_onda.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.activities.ProfileActivity;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.ImageProvider;
import com.pipudev.k_onda.providers.UsersProvider;


/**
 * Modal Bottom Sheet
 * Clase que muestra el fragmento para editar el nombre de usuario cuando se hace clic en el lapiz(icon) en Profile activity
 * extiende BottomSheetDialogFragment
 */


public class BottomSheetChangeUsername extends BottomSheetDialogFragment {

    private EditText etUsername;
    private Button btnCancel;
    private Button btnSave;
    private AuthProvider authProvider;
    private UsersProvider usersProvider;
    private String currentUsername;

    public void setCurrentUsername(String currentUsername) { //proveniente de profileActivity
        this.currentUsername = currentUsername;
    }

    public BottomSheetChangeUsername() {
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
        //creamos una vista y le pasamos los controles a mostrar, el contenedor
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_change_username, container, false);
        etUsername = view.findViewById(R.id.bottom_sheet_change_username_et_username);
        btnCancel = view.findViewById(R.id.bottom_sheet_change_username_btn_cancel);
        btnSave = view.findViewById(R.id.bottom_sheet_change_username_btn_save);
        etUsername.setText(currentUsername);
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        //obtener la informacion del usuario a modificar
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsername();
            }
        });

        //cancelar la modificacion del username
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });



        return view;

    }

    /**
     * Actualizar en firebase data base el nombre de usuario
     */
    private void updateUsername() {
        if (!etUsername.getText().toString().isEmpty()) {
            //guardar en la base de datos de firebase
            usersProvider.updateUserName(authProvider.getCurrentUserID(),etUsername.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "nombre de usuario se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}//clase

