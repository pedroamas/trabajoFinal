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

        db.execSQL("create table puntos(" +
                "id integer primary key, " +
                "descripcion text, "+
                "latitud varchar(15), " +
                "longitud varchar(15), " +
                "foto text," +
                "foto_Web text,"+
                "estado_foto integer,"+
                "titulo text," +
                "fecha_ult_mod text)");
    }
    @Override

    public void onUpgrade(SQLiteDatabase db, int version1, int version2) {

        db.execSQL("drop table if exists puntos");
        db.execSQL("create table puntos(" +
                "id integer primary key, " +
                "descripcion text, "+
                "latitud varchar(15), " +
                "longitud varchar(15), " +
                "foto text," +
                "foto_Web text,"+
                "estado_foto integer,"+
                "titulo text," +
                "fecha_ult_mod text)");

    }


}