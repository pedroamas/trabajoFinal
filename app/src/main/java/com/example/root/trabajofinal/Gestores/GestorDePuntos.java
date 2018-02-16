package com.example.root.trabajofinal.Gestores;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.IRespuesta;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.EditarPuntoListener;
import com.example.root.trabajofinal.Listeners.EliminarPuntoListener;
import com.example.root.trabajofinal.Listeners.SetPuntoListener;
import com.example.root.trabajofinal.Punto;
import com.example.root.trabajofinal.R;

import java.io.File;
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
        puntos= GestorBD.getGestorBD(context).getPuntos();
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

    public void actualizarPuntos(final ActualizarPuntoListener actualizarPuntoListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.actualizarPuntos(new ActualizarPuntoListener() {
            @Override
            public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                GestorBD gestorBD=GestorBD.getGestorBD(context);
                if(!puntos.isEmpty()) {
                    Toast.makeText(context,"Se está actualizando",Toast.LENGTH_LONG).show();
                    gestorBD.actualizarPuntos(puntos);
                }
                actualizarPuntoListener.onResponseActualizarPunto(puntos);
            }
        });
    }



    public void logMostrarPuntos(){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
        gestorBD.logMostrarPuntos();
    }

    public ArrayList<Punto> getPuntos(){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
        puntos=gestorBD.getPuntos();
        return puntos;
    }

    public Punto getPunto(int id){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
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
        GestorBD gestorBD=GestorBD.getGestorBD(context);
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

    public void puntosMalDescargados(){
        GestorBD gestorBD=GestorBD.getGestorBD(context);
        GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(context);
        ArrayList<Punto> puntosMalDescargados=gestorBD.puntosMalDescargados();
        Iterator<Punto> ite=puntosMalDescargados.iterator();
        while (ite.hasNext()){
            Punto punto=ite.next();
            File file=new File(punto.getFotoWeb());
            gestorImagenes.descargarImagen(punto.getFotoWeb(), file.getName(), punto.getId());
            Log.e("<puntosMalDescargados>","Punto "+punto.getId()+" url: "+punto.getFotoWeb());
        }
    }

    public void eliminarPunto(int idPunto, EliminarPuntoListener eliminarPuntoListener) {
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.eliminarPunto(idPunto,eliminarPuntoListener);
    }

    public void setPunto(Punto punto, SetPuntoListener setPuntoListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.setPunto(punto,setPuntoListener);

    }

    public void editarPunto(Punto punto, EditarPuntoListener editarPuntoListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.editarPunto(punto,editarPuntoListener);

    }


}
