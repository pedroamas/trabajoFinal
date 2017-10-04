package com.example.root.trabajofinal;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by root on 25/07/17.
 */

public class GestorUsuarios{

    private Usuario usuario;
    private static GestorUsuarios gestorUsuarios;
    private static ArrayList<IRespuesta> vistas;
    private Context context;

    private GestorUsuarios(Context context) {
        this.context = context;
        vistas=new ArrayList<IRespuesta>();
    }

    public static GestorUsuarios getGestorUsuarios(Context context){
        if(gestorUsuarios==null){
            gestorUsuarios=new GestorUsuarios(context);
        }
        return gestorUsuarios;
    }

    public void registerView(IRespuesta respuesta){
        vistas.add(respuesta);
    }

    public void login(Usuario usuario){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.login(usuario);
    }

    public void notifyViews(Usuario usuario){
        this.usuario=usuario;
        Log.e("ASD","RESPUESTA LOGIN    ");
        Iterator<IRespuesta> ite=vistas.iterator();
        while (ite.hasNext()){
            ite.next().onResponse(1,TipoMensaje.USUARIO,this.usuario);
        }
    }


    public Usuario getUsuario() {
        return usuario;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void cerrarSesion(){
        usuario=null;
    }
}
