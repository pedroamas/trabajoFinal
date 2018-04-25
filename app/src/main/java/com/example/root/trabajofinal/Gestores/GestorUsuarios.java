package com.example.root.trabajofinal.Gestores;

import android.content.Context;

import com.example.root.trabajofinal.Listeners.LoginListener;
import com.example.root.trabajofinal.Listeners.RegistrarListener;
import com.example.root.trabajofinal.Objetos.Usuario;

public class GestorUsuarios{

    private Usuario usuario;
    private static GestorUsuarios gestorUsuarios;
    private Context context;

    private GestorUsuarios(Context context) {
        this.context = context;
    }

    public static GestorUsuarios getInstance(Context context){
        if(gestorUsuarios==null){
            gestorUsuarios=new GestorUsuarios(context);
        }
        return gestorUsuarios;
    }

    public void login(Usuario usuario, LoginListener loginListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.login(usuario,loginListener);
    }

    public void registrar(Usuario usuario, RegistrarListener registrarListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
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
