package com.example.root.trabajofinal;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Listeners.ImagenesListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

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
    public static String TAG="<GestorImagenes>";
    private ArrayList<ImagenesListener> imagenesListener;

    private GestorImagenes(Context context) {
        nombreDescargas=new String[400];
        count=-1;
        countDescargados=-1;
        this.context=context;
        imagenesListener=new ArrayList<ImagenesListener>();
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

    public void descargarImagen(final String url, final String nombreImagen){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);

        nombreDescargas[++count]=nombreImagen;
        Log.e("DescargaIMG","Descargame esta capo: "+nombreImagen);

        ImageRequest request = new ImageRequest(
                url ,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.e("",url);
                        guardarImagen(context,bitmap,nombreImagen);
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

    public void descargarImagen(final String url, final String nombreImagen, final int idPunto){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);

        nombreDescargas[++count]=nombreImagen;
        Log.e("DescargaIMG","Descargame esta capo: "+nombreImagen);

        ImageRequest request = new ImageRequest(
                url ,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.e("",url);
                        guardarImagen(context,bitmap,nombreImagen);
                        GestorBD gestorBD=GestorBD.getGestorBD(context);
                        gestorBD.setEstadoPunto(idPunto,1);
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

    public void enviarImagen(Multimedia multimedia){
        Bitmap image=cargarImagen(multimedia.getPath());
        String imagenBase64=getStringImage(image);
        Log.e(TAG,imagenBase64);
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        gestorWebService.enviarImagen(imagenBase64,multimedia);
    }

    public String getImagenBase64(String path){
        Bitmap image=cargarImagen(path);
        return getStringImage(image);
    }



    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
    public void borrarImagen(String url){
        File fichero = new File(url);
        if (fichero.delete()){
            Log.e(TAG,"Borrado: "+url);
        }else{
            Log.e(TAG,"Error en Borrado: "+url);
        }
    }

    public void addImagenesListener(ImagenesListener imagenesListener){
        this.imagenesListener.add(imagenesListener);
    }
    public void removeImagenesListener(ImagenesListener imagenesListener){
        this.imagenesListener.remove(imagenesListener);
    }
    public void getImagenes(int idPunto,ImagenesListener imagenesListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        Log.e(TAG,"Entro en gestor de imagnes");
        gestorWebService.getImagenes(idPunto,imagenesListener);

    }
    public Bitmap rotarImagen(Bitmap img){
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);  // La rotación debe ser decimal (float o double)

        //Ahora creamos el bitmap y le aplicamos la matriz generada anteriormente:

        Bitmap rotatedBitmap = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public void getImagen(int idImagen, ImagenListener imagenListener){
        GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
        Log.e(TAG,"Entro en gestor de imagnes");
        gestorWebService.getImagen(idImagen,imagenListener);

    }
}
