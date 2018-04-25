package com.example.root.trabajofinal.Objetos;

/**
 * Created by pedro on 26/03/18.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;

import org.sqlite.database.sqlite.SQLiteDatabase;
import org.sqlite.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Iterator;

public class IndiceRtree {

    private SQLiteDatabase db;
    private SQLiteStatement st;
    private ArrayList<Punto> puntos;
    private ArrayList<Punto> puntosMasCercanos;

    public IndiceRtree(ArrayList<Punto> puntos){
        System.loadLibrary("sqliteX");
        db=null;
        db=SQLiteDatabase.openOrCreateDatabase(":memory:",null);

        try {
            db.execSQL("CREATE VIRTUAL TABLE r_tree_index USING rtree(id,latitud,latitudSup,longitud,longitudSup);");
            if(this.puntos==null){
                this.puntos=puntos;
                Iterator<Punto> ite=this.puntos.iterator();


                //Inserta los puntos
                while (ite.hasNext()){
                    Punto punto=ite.next();
                    insertar(punto.getId(),punto.getLatitud(),punto.getLongitud());

                }
            }
        }catch (Exception e){}
    }

    public void insertar(int id,double latitud, double longitud){
        if (db!=null){
            ////Log.e("inserta",id + "," + latitud + ", "  + longitud );
            db.execSQL("INSERT INTO r_tree_index VALUES(" + id + "," + latitud + ", " + latitud + "," + longitud+ ", " + longitud + ");");
            //db.execSQL("INSERT INTO r_tree_index VALUES(" + id + "," + latitud + ", " + longitud + ");");

        }
    }

    public ArrayList<Punto> getPuntosMasCercanos( double latitud,double longitud,double distanciaKm){
        puntosMasCercanos=new ArrayList<Punto>();
        ArrayList<Punto> puntosSuperiores;

        puntosMasCercanos=consultarAreaInferior(latitud,calculoIncrementoLatitud(distanciaKm),longitud,calculoIncrementoLongitud(latitud,distanciaKm));
        puntosSuperiores=consultarAreaSuperior(latitud,calculoIncrementoLatitud(distanciaKm),longitud,calculoIncrementoLongitud(latitud,distanciaKm));
        Iterator<Punto> ite=puntosSuperiores.iterator();
        while (ite.hasNext()){
            Punto puntoTemp=ite.next();
            if(distance(latitud,longitud,puntoTemp.getLatitud(),puntoTemp.getLongitud())<=distanciaKm){
                puntosMasCercanos.add(puntoTemp);
            }
        }
        return puntosMasCercanos;



    }
    public Punto buscarPunto(int idPunto){
        Punto puntoTemp;
        Iterator<Punto> ite=puntos.iterator();
        while (ite.hasNext()){
            puntoTemp=ite.next();
            if(puntoTemp.getId()==idPunto){
                return puntoTemp;
            }
        }
        return null;
    }
    public ArrayList<Punto> consultarAreaSuperior(double latitud, double incrementoLatitud, double longitud, double incrementoLongitud){

        ArrayList<Punto> puntosSuperiores=new ArrayList<Punto>();
        String sql="SELECT id,latitud,longitud  FROM r_tree_index " +
                "WHERE ("+(latitud-incrementoLatitud)+")<=latitud and latitud<=("+(latitud+incrementoLatitud)+") AND " +
                "("+(longitud-incrementoLongitud)+")<=longitud and longitud<=("+(longitud+incrementoLongitud)+") AND " +
                " NOT (("+(latitud-incrementoLatitud/Math.sqrt(2))+")<=latitud and latitud<=("+(latitud+incrementoLatitud/Math.sqrt(2))+") AND " +
                "("+(longitud-incrementoLongitud/Math.sqrt(2))+")<=longitud and longitud<=("+(longitud+incrementoLongitud/Math.sqrt(2))+"))";

        Cursor cursor = db.rawQuery(sql, null);

        //Log.e("SQL area sup",sql);

        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                puntosSuperiores.add(buscarPunto(id));
                //Log.e("Consulta Rtree Area sup"," : "+id+" - "+cursor.getDouble(1)+" - "+cursor.getDouble(2)+" - distancia: "+distance(latitud,longitud,cursor.getDouble(1),cursor.getDouble(2)));
            } while (cursor.moveToNext());
        }
        return puntosSuperiores;
    }

    public ArrayList<Punto> consultarAreaInferior(double latitud,double incrementoLatitud,double longitud,double incrementoLongitud){

        ArrayList<Punto> puntosInferiores=new ArrayList<Punto>();
        String sql="SELECT id,latitud,longitud  FROM r_tree_index " +
                "WHERE ("+(latitud-incrementoLatitud/Math.sqrt(2))+")<=latitud and latitud<=("+(latitud+incrementoLatitud/Math.sqrt(2))+") AND " +
                "("+(longitud-incrementoLongitud/Math.sqrt(2))+")<=longitud and longitud<=("+(longitud+incrementoLongitud/Math.sqrt(2))+") ";

        //Log.e("SQL area inf",sql);
        Cursor cursor = db.rawQuery(sql , null);
        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                puntosInferiores.add(buscarPunto(id));
                //Log.e("Consulta Rtree Area inf"," : "+id+" - "+cursor.getDouble(1)+" - "+cursor.getDouble(2)+" - distancia: "+distance(latitud,longitud,cursor.getDouble(1),cursor.getDouble(2)));
            } while (cursor.moveToNext());
        }
        return puntosInferiores;
    }

    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM
    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }
    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public double calculoIncrementoLatitud(double distancia){
        return distancia/111.325;
    }

    public double calculoIncrementoLongitud(double latitud, double distancia){
        return (360*distancia)/(Math.cos(Math.toRadians(latitud))*40076);
    }


}
