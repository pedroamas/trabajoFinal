package com.example.root.trabajofinal.Gestores;

import android.content.Context;

import com.example.root.trabajofinal.Listeners.EliminarCometarioListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.SetComentarioListener;
import com.example.root.trabajofinal.Objetos.Comentario;

/**
 * Created by pedro on 19/02/18.
 */

public class GestorComentarios {

    private Context context;
    private static GestorComentarios gestorComentarios;

    public GestorComentarios(Context context) {
        this.context=context;
    }

    public static GestorComentarios obtenerGestorComentarios(Context context){
        if (gestorComentarios==null){
            gestorComentarios=new GestorComentarios(context);
        }
        return gestorComentarios;
    }

    public void setComentarioPunto(Comentario comentario, SetComentarioListener setComentarioListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.setComentarioPunto(comentario,setComentarioListener);
    }

    public void setComentarioMultimedia(Comentario comentario, SetComentarioListener setComentarioListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.setComentarioMultimedia(comentario,setComentarioListener);
    }
    public void getComentariosMultimedia(int idMultimedia, GetComentariosListener getComentariosListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.getComentariosMultimedia(idMultimedia,getComentariosListener);
    }

    public void eliminarComentario(int idComentario, EliminarCometarioListener eliminarCometarioListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.eliminarComentario(idComentario,eliminarCometarioListener);
    }


}
