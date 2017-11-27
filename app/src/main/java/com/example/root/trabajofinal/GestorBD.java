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
    public static String TAG="<GestorBD>";

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
        String listaPuntosEliminar="";
        leerPuntos();
        Iterator<Punto> ite=puntos.iterator();
        while (ite.hasNext()){
            Punto puntoIn=ite.next();
            listaPuntosEliminar+=puntoIn.getId()+",";

            Punto puntoOut=localizacion(puntoIn.getId());

            //El punto no fue encontrado, entonces agregarlo
            if(puntoOut==null){
                insertarNuevoPunto(puntoIn);

            }else if(!puntoIn.equals(puntoOut)){             //Comparar si tienen las mismas caracteristicas
                //Si no son iguales, actualizar el punto
                actualizarPunto(puntoIn);
            }

        }

        //Eliminación de puntos sobrantes
        if(!listaPuntosEliminar.isEmpty()){
            //Elimino la última coma que sobra
            listaPuntosEliminar = listaPuntosEliminar.substring(0, listaPuntosEliminar.length() - 1);
            eliminarPuntos(listaPuntosEliminar);
        }


        leerPuntos();


    }

    public void insertarNuevoPunto(Punto punto){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);

        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("id", punto.getId());
        registro.put("titulo", punto.getTitulo());
        registro.put("descripcion", punto.getDescripcion());
        registro.put("latitud", punto.getLatitud());
        registro.put("longitud", punto.getLongitud());
        registro.put("foto", punto.getFoto());
        registro.put("foto_web", punto.getFotoWeb());
        registro.put("estado_foto", 0);

        bd.insert("puntos", null, registro);
        bd.close();
    }
    public void eliminarPuntos(String listaPuntosEliminar){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("DELETE FROM puntos WHERE id not in ("+listaPuntosEliminar+")");

        Log.e(TAG,"DELETE FROM puntos WHERE id not in ("+listaPuntosEliminar+")");
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
                    " ,descripcion='"+punto.getDescripcion()+"'"+
                    " ,longitud='"+punto.getLongitud()+"'"+
                    " ,latitud='"+punto.getLatitud()+"'"+
                    " ,foto='"+punto.getFoto()+"'"+
                    " ,foto_web='"+punto.getFotoWeb()+"'"+
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
                "select id,latitud, longitud, titulo,foto,descripcion,foto_web from puntos " +
                        "WHERE estado_foto=1", null);

        puntosBD=new ArrayList<Punto>();
        if (fila.moveToFirst()) {
            do{
                Punto puntoBD=new Punto(
                        Integer.parseInt(fila.getString(0)),        //id
                        fila.getString(3),                          //titulo
                        fila.getString(5),                          //descripcion
                        Double.parseDouble(fila.getString(1)),      //latitud
                        Double.parseDouble(fila.getString(2)),      //longitud
                        fila.getString(4),                          //foto
                        fila.getString(6),                          //foto_web
                        1                                           //estado_foto
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
                    punto.getDescripcion()+" - "+
                    punto.getLatitud()+" - "+
                    punto.getLongitud()+" - "+
                    punto.getFoto()+" - "+
                    punto.getFotoWeb()
            );
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

    public void setEstadoPunto(int idPunto, int estado){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("UPDATE puntos " +
                "SET "+
                " estado_foto="+estado+
                " WHERE id="+idPunto
        );
        bd.close();
    }

    public ArrayList<Punto> puntosMalDescargados(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context,
                "administracion", null, versionDB);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select id,latitud, longitud, titulo,foto,descripcion,foto_web from puntos " +
                        "WHERE estado_foto=0", null);

        ArrayList<Punto> puntosMalCargados=new ArrayList<Punto>();
        if (fila.moveToFirst()) {
            do{
                Punto puntoBD=new Punto(
                        Integer.parseInt(fila.getString(0)),        //id
                        fila.getString(3),                          //titulo
                        fila.getString(5),                          //descripcion
                        Double.parseDouble(fila.getString(1)),      //latitud
                        Double.parseDouble(fila.getString(2)),      //longitud
                        fila.getString(4),                          //foto
                        fila.getString(6),                          //fotoWeb
                        0
                );
                puntosMalCargados.add(puntoBD);
            }while (fila.moveToNext());
        }
        bd.close();
        return puntosMalCargados;
    }

}
