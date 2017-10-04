package com.example.root.trabajofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by root on 12/09/17.
 */

public class GestorBD {
    private int versionDB=1;
    Context context;
    private static GestorBD gestorBD;
    private ArrayList<Punto> puntosBD;

    private GestorBD(Context context) {
        this.context = context;
        puntosBD=new ArrayList<Punto>();
    }

    public static GestorBD getGestorBD(Context context){
        if(gestorBD==null){
            gestorBD=new GestorBD(context);
        }
        return gestorBD;
    }

    public void actualizarPuntos(ArrayList<Punto> puntos){
        leerPuntos();
        Iterator<Punto> ite=puntos.iterator();
        while (ite.hasNext()){
            Punto puntoIn=ite.next();
            Punto puntoOut=localizacion(puntoIn.getId());

            //El punto no fue encontrado, entonces agregarlo
            if(puntoOut==null){
                insertarNuevoPunto(puntoIn);

            }else if(!puntoIn.equals(puntoOut)){             //Comparar si tienen las mismas caracteristicas
                //Si no son iguales, actualizar el punto
                actualizarPunto(puntoIn);
            }

        }


    }

    public void insertarNuevoPunto(Punto punto){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("id", punto.getId());
        registro.put("titulo", punto.getTitulo());
        registro.put("latitud", punto.getLatitud());
        registro.put("longitud", punto.getLongitud());
        registro.put("foto", punto.getFoto());

        bd.insert("puntos", null, registro);
        bd.close();
    }
    public void eliminarPunto(Punto punto){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("DELETE FROM puntos WHERE id="+punto.getId());
        bd.close();
    }

    public void actualizarPunto(Punto punto){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Log.e("<BD>","UPDATE puntos " +
                        "SET "+
                        " titulo='"+punto.getTitulo()+"'"+
                        " ,longitud='"+punto.getLongitud()+"'"+
                        " ,latitud='"+punto.getLatitud()+"'"+
                        " ,foto='"+punto.getFoto()+"'"+
                        " WHERE id="+punto.getId());
        bd.execSQL("UPDATE puntos " +
                "SET "+
                    " titulo='"+punto.getTitulo()+"'"+
                    " ,longitud='"+punto.getLongitud()+"'"+
                    " ,latitud='"+punto.getLatitud()+"'"+
                    " ,foto='"+punto.getFoto()+"'"+
                " WHERE id="+punto.getId()
        );
        bd.close();
    }


    public Punto localizacion(int id){
        Iterator<Punto> ite = puntosBD.iterator();
        while (ite.hasNext()){
            Punto puntoAux=ite.next();
            if(puntoAux.getId()==id){
                return puntoAux;
            }
        }
        return null;
    }

    public void leerPuntos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select id,latitud, longitud, titulo,foto from puntos", null);

        puntosBD=new ArrayList<Punto>();
        if (fila.moveToFirst()) {
            do{
                Punto puntoBD=new Punto(
                        Integer.parseInt(fila.getString(0)),        //id
                        fila.getString(3),                          //titulo
                        Double.parseDouble(fila.getString(1)),      //latitud
                        Double.parseDouble(fila.getString(2)),      //longitud
                        fila.getString(4)                           //foto
                );
                puntosBD.add(puntoBD);
            }while (fila.moveToNext());
        }
        bd.close();
    }


    public void logMostrarPuntos(){
        leerPuntos();
        Iterator<Punto> ite = puntosBD.iterator();
        while (ite.hasNext()){
            Punto punto = ite.next();
            Log.e("<MostrarPunto>",punto.getId()+" - "+
                punto.getTitulo()+" - "+
                punto.getLatitud()+" - "+
                punto.getLongitud()+" - "+
                punto.getFoto());
        }
    }

    public ArrayList<Punto> getPuntos(){
        leerPuntos();
        return puntosBD;
    }

    public void borrarPuntos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();

        bd.delete("puntos","",null);
        bd.close();
    }

}
