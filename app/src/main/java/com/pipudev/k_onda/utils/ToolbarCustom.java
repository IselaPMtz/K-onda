package com.pipudev.k_onda.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pipudev.k_onda.R;

public class ToolbarCustom {

    /**
     * Muestra el toolbar en la actividad
     * recibe 3 parametros
     * la actividad que hace la llamada
     * el titulo(texto) que mostrara el toolbar
     * El boton de regresar (volver a otra actividad)
     */
    public static void showToolbar(AppCompatActivity activity, String title, boolean backbtn) {

        Toolbar toolbar = activity.findViewById(R.id.action_bar_toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(backbtn);

    }


}
