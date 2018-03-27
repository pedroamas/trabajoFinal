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

public class IndiceRtree {

    public void crearIndice(){
        System.loadLibrary("sqliteX");
        SQLiteDatabase db=null;
        SQLiteStatement st;
        String res;
        db=SQLiteDatabase.openOrCreateDatabase(":memory:",null);
        st=db.compileStatement("SELECT sqlite_version()");
        res=st.simpleQueryForString();

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

        db.execSQL("CREATE VIRTUAL TABLE r_tree_index USING rtree(id,latitud,longitud);");

        db.execSQL("INSERT INTO r_tree_index VALUES(1,-70.7749, 35.3776);");
        db.execSQL("INSERT INTO r_tree_index VALUES(2,-81.0, 36.2);");
        //Cursor cursor = db.rawQuery("SELECT id FROM demo_index WHERE minX>=-81.08 AND maxX<=-80.58 AND minY>=35.00  AND maxY<=35.44;", null);
        Cursor cursor = db.rawQuery("SELECT id  FROM r_tree_index;", null);

        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
                Log.e("Rtree"," : "+id);
            } while (cursor.moveToNext());
        }

        db.close();
    }
}
