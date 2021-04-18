package com.pipudev.k_onda.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.providers.AuthProvider;
import com.pipudev.k_onda.providers.UsersProvider;


/**
 * Modal Bottom Sheet
 * Clase que muestra el fragmento para editar la informacion del usuario cuando se hace clic en el lapiz(icon) en Profile activity
 * extiende BottomSheetDialogFragment
 */


public class BottomSheetChangeInfo extends BottomSheetDialogFragment {

    private EditText etInfo;
    private Button btnCancel;
    private Button btnSave;
    private AuthProvider authProvider;
    private UsersProvider usersProvider;
    private String currentInfo;

    public void setCurrentInfo(String currentInfo) { //proveniente de profileActivity
        this.currentInfo = currentInfo;
    }

    public BottomSheetChangeInfo() {
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
        View view = inflater.inflate(R.layout.bottom_sheet_change_info, container, false);
        etInfo = view.findViewById(R.id.bottom_sheet_change_info_et);
        btnCancel = view.findViewById(R.id.bottom_sheet_change_info_btn_cancel);
        btnSave = view.findViewById(R.id.bottom_sheet_change_info_btn_save);
        etInfo.setText(currentInfo);
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        //obtener la info del usuario a modificar
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

        //cancelar la modificacion del info
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });



        return view;

    }

    /**
     * Actualizar en firebase data base la info de usuario
     */
    private void updateInfo() {
        if (!etInfo.getText().toString().isEmpty()) {
            //guardar en la base de datos de firebase
            usersProvider.updateInfo(authProvider.getCurrentUserID(),etInfo.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "informacion se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}//clase

