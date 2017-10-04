package com.example.root.trabajofinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    private Context context;
    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {

        super(context, nombre, factory, version);
        this.context=context;

    }

    @Override

    public void onCreate(SQLiteDatabase db) {

        //aquí creamos la tabla de usuario (dni, nombre, ciudad, numero)

        db.execSQL("create table puntos(" +
                "id integer primary key, " +
                "latitud varchar(15), " +
                "longitud varchar(15), " +
                "foto text," +
                "titulo text)");
    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int version1, int version2) {

        db.execSQL("drop table if exists usuario");
        db.execSQL("drop table if exists puntos");
        db.execSQL("create table puntos(" +
                "id integer primary key, " +
                "latitud varchar(15), " +
                "longitud varchar(15), " +
                "foto text," +
                "titulo text)");
        /*db.execSQL("INSERT INTO puntos(id,latitud,longitud,titulo,foto) VALUES" +

                "(1,'-33.288236','-66.328130','Calle Illia','creature_1.png'" +"),"+
                "(2,'-33.292860','-66.338053','Catedral','creature_2.png'" +"),"+
                "(3,'-33.300872','-66.333537','Museo','creature_3.png'" +"),"+
                "(4,'-33.300872','-66.333537','Reloj','creature_4.png'" +"),"+
                "(5,'-33.291138','-66.338981','Terminal','illia.jpg'" +"),"+
                "(6,'-33.288103','-66.326955','Correo','creature_5.png'" +"),"+
                "(7,'-31.555800','-68.522437','Municipalidad','creature_6.png'" +"),"+
                "(8,'-31.555842','-68.521931','Casa de gobierno','creature_7.png'" +")"
        );
        */

    }

}