package com.example.root.trabajofinal.Gestores;

import android.content.Context;

import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Listeners.VideosListener;

import java.util.ArrayList;


public class GestorVideos {
    private Context context;
    private static GestorVideos gestorVideos;
    private ArrayList<VideoListener> listeners;

    public GestorVideos(Context context) {
        listeners=new ArrayList<VideoListener>();
        this.context = context;
    }

    public static GestorVideos getGestorVideos(Context context){
        if(gestorVideos==null){
            gestorVideos=new GestorVideos(context);
        }
        return gestorVideos;
    }


    public void getVideo(int idVideo,VideoListener videoListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.getVideo(idVideo,videoListener);
    }
    public void getVideos(int idPunto,VideosListener videosListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.getVideos(idPunto,videosListener);
    }
}
