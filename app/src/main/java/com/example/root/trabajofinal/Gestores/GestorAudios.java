package com.example.root.trabajofinal.Gestores;

import android.content.Context;

import com.example.root.trabajofinal.Listeners.AudioListener;
import com.example.root.trabajofinal.Listeners.AudiosListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Listeners.VideosListener;

import java.util.ArrayList;

/**
 * Created by pedro on 26/02/18.
 */

public class GestorAudios {
    private Context context;
    private static GestorAudios gestorAudios;

    public GestorAudios(Context context) {
        this.context = context;
    }

    public static GestorAudios getGestorAudios(Context context){
        if(gestorAudios==null){
            gestorAudios=new GestorAudios(context);
        }
        return gestorAudios;
    }


    public void getAudio(int idAudio,AudioListener audioListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.getAudio(idAudio,audioListener);
    }
    public void getAudios(int idPunto,AudiosListener audiosListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.getAudios(idPunto,audiosListener);
    }
}
