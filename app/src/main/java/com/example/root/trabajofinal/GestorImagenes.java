package com.example.root.trabajofinal;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by root on 30/06/17.
 */

public class GestorImagenes {

    private static Bitmap bitmapRetorno;
    private static String nombreImagen;
    private String[] nombreDescargas;
    private int count,countDescargados;
    private static GestorImagenes gestorImagenes;
    private Context context;

    private GestorImagenes(Context context) {
        nombreDescargas=new String[400];
        count=-1;
        countDescargados=-1;
        this.context=context;
    }

    public static GestorImagenes obtenerGestorImagenes(Context context){
        if (GestorImagenes.gestorImagenes==null){
            GestorImagenes.gestorImagenes=new GestorImagenes(context);
        }
        return GestorImagenes.gestorImagenes;
    }

    public String guardarImagen(Context context, Bitmap bitmapImage, String nombreImagen){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,nombreImagen);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {

            e.printStackTrace();
            Log.d("<Error>", "Mensaje: "+ e.getMessage());
        }
        return directory.getAbsolutePath();
    }

    public Bitmap cargarImagen(String nombreImagen)
    {
        String path=nombreImagen;

        Bitmap bitmap=null;
        try {
            File f=new File("", nombreImagen);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void descargarImagen(final String url, String nombreImagen){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);

        nombreDescargas[++count]=nombreImagen;

        ImageRequest request = new ImageRequest(
                url ,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        guardarImagen(context,bitmap,nombreDescargas[++countDescargados]);
                        }
                }, 0, 0, null,null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //imagenPost.setImageResource(R.drawable.illia);
                        Log.d("<Error>", "Error en respuesta Bitmap: "+ error.getMessage());
                    }
                });
        requestQueue.add(request);

    }
}
