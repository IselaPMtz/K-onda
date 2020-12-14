package com.pipudev.k_onda.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.pipudev.k_onda.activities.OnCompleteInfoActivity;

import java.io.File;
import java.util.ArrayList;

public class PixUtils extends AppCompatActivity {

    private Options optionImage;//libreria de proyecto github
    private ArrayList<String> aReturnValue = new ArrayList<>();
    private File imageFile;

    /**
     * Configuracion de las opciones para agregar imagen al perfil
     */
    public Options setOptionsImage() {

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
        return optionImage;
    }

    public Options getOptionImage() {
        return optionImage;
    }


}