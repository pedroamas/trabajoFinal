package com.example.root.trabajofinal;

import android.content.Context;
import android.util.Log;

import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by root on 26/08/17.
 */

public class GestorDePuntos {

    private ArrayList<Punto> puntos;
    private static GestorDePuntos gestorDePuntos;
    private Context context;
    public static String TAG="<Web service>";
    public World mundo;
    private ArrayList<IRespuesta> vistas;


    public GestorDePuntos(Context context) {
        puntos=new ArrayList<Punto>();
        this.context=context;
        this.vistas=new ArrayList<IRespuesta>();
    }

    public void registerView(IRespuesta vista){
        vistas.add(vista);
    }

    public static GestorDePuntos getGestorDePuntos(Context context){
        if(gestorDePuntos == null){
            gestorDePuntos= new GestorDePuntos(context);
        }
        return gestorDePuntos;
    }

    public void setLongLat(double lat,double lon){
        mundo.setGeoPosition(lat,lon);
    }

    public World generarMundo(Context context) {
        if (mundo != null) {
            return mundo;
        }
        puntos=getPuntos();

        mundo = new World(context);

        // The user can set the default bitmap. This is useful if you are
        // loading images form Internet and the connection get lost
        mundo.setDefaultImage(R.drawable.beyondar_default_unknow_icon);

        // Create an object with an image in the app resources.

        Iterator<Punto> ite=puntos.iterator();
        Log.e("<Lectura de puntos>","Cant puntos: "+puntos.size());
        GeoObject go ;
        while (ite.hasNext()){
            Punto punto=ite.next();
            go= new GeoObject(punto.getId());
            go.setGeoPosition(punto.getLatitud(),punto.getLongitud());
            go.setName(punto.getTitulo());
            go.setImageUri(punto.getFoto());
            mundo.addBeyondarObject(go);
            Log.e("<Lectura de puntos>",punto.getTitulo());
        }
        Log.e("<Lectura de puntos>","FIN");

        return mundo;
    }

    public void actualizarPuntos(){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.actualizarPuntos();
    }

    public void respActualizarPuntos(ArrayList<Punto> puntos){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
        gestorBD.actualizarPuntos(puntos);
    }

    public void logMostrarPuntos(){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
        gestorBD.logMostrarPuntos();
    }

    public ArrayList<Punto> getPuntos(){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
        return gestorBD.getPuntos();
    }

}
