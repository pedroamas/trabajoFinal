package com.example.root.trabajofinal.Gestores;

import android.content.Context;
import android.util.Log;

import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.EditarPuntoListener;
import com.example.root.trabajofinal.Listeners.EliminarPuntoListener;
import com.example.root.trabajofinal.Listeners.SetPuntoListener;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by root on 26/08/17.
 */

public class GestorPuntos {

    private ArrayList<Punto> puntos;
    private static GestorPuntos gestorPuntos;
    private Context context;
    public World mundo;


    public GestorPuntos(Context context) {
        puntos=new ArrayList<Punto>();
        this.context=context;
        puntos= GestorBD.getInstance(context).getPuntos();
    }


    public static GestorPuntos getInstance(Context context){
        if(gestorPuntos == null){
            gestorPuntos = new GestorPuntos(context);
        }
        return gestorPuntos;
    }

    public World generarMundo(Context context) {

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

    public void actualizarPuntos(final ActualizarPuntoListener actualizarPuntoListener){

        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.actualizarPuntos(new ActualizarPuntoListener() {
            @Override
            public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                GestorBD gestorBD=GestorBD.getInstance(context);
                if(!puntos.isEmpty()) {
                    gestorBD.actualizarPuntos(puntos,actualizarPuntoListener);
                }else {
                    actualizarPuntoListener.onResponseActualizarPunto(puntos);
                }
            }
        });
    }


    public void editarPunto(Punto punto, EditarPuntoListener editarPuntoListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.editarPunto(punto,editarPuntoListener);

    }

    public void eliminarPunto(int idPunto, EliminarPuntoListener eliminarPuntoListener) {
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.eliminarPunto(idPunto,eliminarPuntoListener);
    }

    public void agregarPunto(Punto punto, SetPuntoListener setPuntoListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.agregarPunto(punto,setPuntoListener);

    }

    public ArrayList<Punto> getPuntos(){
        GestorBD gestorBD=GestorBD.getInstance(context);
        puntos=gestorBD.getPuntos();
        return puntos;
    }

    public Punto getPunto(int id){
        GestorBD gestorBD=GestorBD.getInstance(context);
        puntos=gestorBD.getPuntos();
        Punto punto;
        Iterator<Punto> iterator=puntos.iterator();
        while (iterator.hasNext()){
            punto=iterator.next();
            if(id==punto.getId()){
                return punto;
            }
        }
        return null;
    }

    public Punto getPunto(String titulo){
        GestorBD gestorBD=GestorBD.getInstance(context);
        gestorBD.leerPuntos();
        Punto p;
        Iterator<Punto> iterator=puntos.iterator();
        while (iterator.hasNext()){
            p=iterator.next();
            if(titulo.equals(p.getTitulo())){
                return p;
            }
        }
        return null;
    }

    public boolean puntosMalDescargados(){
        GestorBD gestorBD=GestorBD.getInstance(context);
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
        ArrayList<Punto> puntosMalDescargados=gestorBD.puntosMalDescargados();
        if(puntosMalDescargados.size()==0){
            return true;
        }
        Iterator<Punto> ite=puntosMalDescargados.iterator();
        while (ite.hasNext()){
            Punto punto=ite.next();
            File file=new File(punto.getPathFotoWeb());
            gestorMultimedia.descargarImagen(punto.getPathFotoWeb(), file.getName(), punto.getId(), new ActualizarPuntoListener() {
                @Override
                public void onResponseActualizarPunto(ArrayList<Punto> puntos) {

                }
            });
            Log.e("<puntosMalDescargados>","Punto "+punto.getId()+" url: "+punto.getPathFotoWeb());
        }
        return false;
    }


}
