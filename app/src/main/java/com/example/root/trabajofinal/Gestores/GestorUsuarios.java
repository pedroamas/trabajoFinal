package com.example.root.trabajofinal.Gestores;

import android.content.Context;
import android.util.Log;

import com.example.root.trabajofinal.IRespuesta;
import com.example.root.trabajofinal.Listeners.LoginListener;
import com.example.root.trabajofinal.Listeners.RegistrarListener;
import com.example.root.trabajofinal.TipoMensaje;
import com.example.root.trabajofinal.Objetos.Usuario;

import java.util.ArrayList;
import java.util.Iterator;

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

    public void login(Usuario usuario, LoginListener loginListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.login(usuario,loginListener);
    }

    public void notifyViews(Usuario usuario){
        this.usuario=usuario;
        Log.e("ASD","RESPUESTA LOGIN    ");
        Iterator<IRespuesta> ite=vistas.iterator();
        while (ite.hasNext()){
            ite.next().onResponse(1, TipoMensaje.USUARIO,this.usuario);
        }
    }

    public void registrar(Usuario usuario, RegistrarListener registrarListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.registrar(usuario,registrarListener);
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
