package com.example.root.trabajofinal.Gestores;

import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.root.trabajofinal.Listeners.ActualizarEstadoImgListener;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.AgregarImagenSecListener;
import com.example.root.trabajofinal.Listeners.AudioListener;
import com.example.root.trabajofinal.Listeners.EditarMultimediaListener;
import com.example.root.trabajofinal.Listeners.EliminarImagenSecListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Listeners.VideosListener;
import com.example.root.trabajofinal.Objetos.Multimedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.DEVICE_POLICY_SERVICE;

/**
 * Created by root on 30/06/17.
 */

public class GestorMultimedia {
    private static GestorMultimedia gestorMultimedia;
    private Context context;
    public static String TAG="<GestorMultimedia>";
    public static int contadorDescargas=0;


    private GestorMultimedia(Context context) {
        this.context=context;
    }

    public static GestorMultimedia getInstance(Context context){
        if (GestorMultimedia.gestorMultimedia ==null){
            GestorMultimedia.gestorMultimedia =new GestorMultimedia(context);
        }
        return GestorMultimedia.gestorMultimedia;
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

    public Bitmap cargarImagen(String nombreImagen) {
        Log.e("Hay algun path",nombreImagen);
        String path=nombreImagen;

        Bitmap bitmap=null;
        try {
            File f=new File("", nombreImagen);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            bitmap=rotarImagen(bitmap,
                    getRotacionNecesaria(nombreImagen));
        }
        catch (FileNotFoundException e)
        {
            Log.e("Hay algun path","Errores por todos lados");
            e.printStackTrace();
        }
        return bitmap;
    }

/*
    public void descargarImagen(final String url, final String nombreImagen, final int idPunto){
        URL imageUrl = null;
        Log.e("img","url "+url);
        try {
            imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());

            guardarImagen(context,bitmap,nombreImagen);
            GestorBD gestorBD=GestorBD.getInstance(context);
            gestorBD.setEstadoPunto(idPunto,1);

        } catch (Exception e) {
            Toast.makeText(context, "Error cargando la imagen: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }*/

    /*public void descargarImagen(final String url, final String nombreImagen, final int idPunto){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);
        Log.e("DescargaIMG","Descargame esta capo: "+nombreImagen);
        ImageRequest request = new ImageRequest(
                url ,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.e("",url);
                        guardarImagen(context,bitmap,nombreImagen);
                        GestorBD gestorBD=GestorBD.getInstance(context);
                        gestorBD.setEstadoPunto(idPunto,1);
                    }
                }, 0, 0, null,null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        //imagenPost.setImageResource(R.drawable.illia);
                        Log.d("<Error>", "Error en respuesta Bitmap: "+ error.getMessage());
                        error.printStackTrace();
                    }
                });
        requestQueue.add(request);

    }*/

    public void borrarImagen(String url){
        File fichero = new File(url);
        if (fichero.delete()){
            Log.e(TAG,"Borrado: "+url);
        }else{
            Log.e(TAG,"Error en Borrado: "+url);
        }
    }

    public void getImagenes(int idPunto,ImagenesListener imagenesListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.getImagenes(idPunto,imagenesListener);
    }

    public void getImagenesUsuarios(int idPunto,ImagenesListener imagenesListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.getImagenesUsuarios(idPunto,imagenesListener);
    }

    public void getImagenesUsuariosPendientes(int idPunto,ImagenesListener imagenesListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.getImagenesUsuariosPendientes(idPunto,imagenesListener);
    }

    public Bitmap rotarImagen(Bitmap img,int grados){
        Matrix matrix = new Matrix();
        matrix.postRotate(grados);  // La rotación debe ser decimal (float o double)

        //Ahora creamos el bitmap y le aplicamos la matriz generada anteriormente:

        Bitmap rotatedBitmap = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public void getImagen(int idImagen, ImagenListener imagenListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        Log.e(TAG,"Entro en gestor de imagnes");
        gestorWebService.getImagen(idImagen,imagenListener);

    }

    public void getImagenPortada(int idPunto, ImagenListener imagenListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        Log.e(TAG,"Entro en gestor de imagnes");
        gestorWebService.getImagenPortada(idPunto,imagenListener);

    }

    public int getRotacionNecesaria( String imagePath){
        int rotate = 0;
        try {
            //context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public void setImagenSec(Multimedia multimedia, AgregarImagenSecListener agregarImagenSecListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.agregarImagenSec(multimedia,agregarImagenSecListener);
    }

    public void setImagenSecUsuario(Multimedia multimedia, AgregarImagenSecListener agregarImagenSecListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.agregarImagenSecUsuario(multimedia,agregarImagenSecListener);
    }

    public void editarImagenSec(Multimedia multimedia,EditarMultimediaListener editarMultimediaListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.editarImagenSec(multimedia,editarMultimediaListener);

    }

    public void eliminarImagenSec(int idImagen,EliminarImagenSecListener eliminarImagenSecListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.eliminarImagenSec(idImagen,eliminarImagenSecListener);

    }

    public void getImagenConEstado(int idImagen,int estado, ImagenListener imagenListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        Log.e(TAG,"Entro en gestor de imagnes");
        gestorWebService.getImagenConEstado(idImagen,estado,imagenListener);

    }

    public void setEstadoImagen(int idImagen, int estado , final ActualizarEstadoImgListener actualizarEstadoImgListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.setEstadoImagen(idImagen,estado,actualizarEstadoImgListener);
    }

    public File ajustarImagen(File f){
        Bitmap bitmapOriginal=decodeFile(f.getAbsolutePath());
        Bitmap imgAjustada=rotarImagen(
                bitmapOriginal,
                getRotacionNecesaria(f.getAbsolutePath()));
        try {
            FileOutputStream fOut = new FileOutputStream(f);
            imgAjustada.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public File ajustarImagen2(File f){
        Bitmap imgAjustada=rotarImagen(decodeFile(f.getAbsolutePath()),getRotacionNecesaria(f.getAbsolutePath()));

        try {
            FileOutputStream fOut = new FileOutputStream(f);
            imgAjustada.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public void getVideo(int idVideo,VideoListener videoListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.getVideo(idVideo,videoListener);
    }

    public void getVideos(int idPunto,VideosListener videosListener){
        GestorWebService gestorWebService=GestorWebService.getInstance(context);
        gestorWebService.getVideos(idPunto,videosListener);
    }



    public void descargarImagen(final String url, final String nombreImagen, final int idPunto, ActualizarPuntoListener actualizarPuntoListener) {
        new DownloadImage(nombreImagen,idPunto,actualizarPuntoListener).execute(url);
    }



    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private String nombreImagen;
        private int idPunto;
        private ActualizarPuntoListener actualizarPuntoListener;

        private Bitmap downloadImageBitmap(String sUrl) {
            Log.e("TAG","downloadImageBitmap");
            Bitmap bitmap = null;
            try {
                Log.e("path",""+sUrl);
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }

            return bitmap;
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

        public DownloadImage(String nombreImagen,int idPunto,ActualizarPuntoListener actualizarPuntoListener) {
            this.actualizarPuntoListener=actualizarPuntoListener;
            this.nombreImagen=nombreImagen;
            this.idPunto=idPunto;
            GestorMultimedia.contadorDescargas++;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.e("TAG","doInBackground");
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            if (result!=null){
                guardarImagen(context,result,nombreImagen);
                GestorBD gestorBD=GestorBD.getInstance(context);
                gestorBD.setEstadoPunto(idPunto,1);
            }

            GestorMultimedia.contadorDescargas--;
            if(GestorMultimedia.contadorDescargas==0){
                actualizarPuntoListener.onResponseActualizarPunto(null);
            }
        }
    }

    private Bitmap downloadImageBitmap(String sUrl) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
                InputStream inputStream = new URL(sUrl).openStream();
                try {
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                    break;
                } catch (OutOfMemoryError outOfMemoryError) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap decodeFile(String imgPath)
    {

        Bitmap b = null;
        int max_size = 1500;
        File f = new File(imgPath);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;
            if (o.outHeight > max_size || o.outWidth > max_size)
            {
                scale = (int) Math.max(
                        Math.ceil(o.outWidth / max_size),
                        Math.ceil(o.outHeight / max_size));
               }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        }
        catch (Exception e)
        {
        }
        return b;
    }

    public static Bitmap reduceBitmap(Context contexto, String uri) {
        int maxAncho=900;
        int maxAlto=1280;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;

        }
    }
}
