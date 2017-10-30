package com.example.root.trabajofinal;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;

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

    public String enviar_imagen(String imagenURL, String urlString){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024*1024;
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagenURL, options);
/**/
            ExifInterface ei = new ExifInterface(imagenURL);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:

                    bitmap=RotateBitmap(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap=RotateBitmap(bitmap, 180);
                    break;
// etc.
            }
/**/
            OutputStream os = new FileOutputStream(imagenURL);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
/**/
            FileInputStream fileInputStream = new FileInputStream(new File(imagenURL));
/**/
            java.net.URL url = new java.net.URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            String post="POST";
            conn.setRequestMethod(post);
            String keep="Keep-Alive";
            String connection="Connection";
            conn.setRequestProperty(connection, keep);
            String multipart="multipart/form-data;boundary="+boundary;
            String imagen = "Content-Disposition: form-data; name=\"imagen\";filename=\"" + imagenURL + "\"" + lineEnd+"Content-type: image/"+MimeTypeMap.getFileExtensionFromUrl(imagenURL.toLowerCase())+";"+lineEnd;
/**/
            long contentLength = fileInputStream.available();
            int longitud = Integer.parseInt(String.valueOf(contentLength));
/**/
            conn.setChunkedStreamingMode(longitud);
            conn.setRequestProperty("Content-Type", multipart);
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes(imagen);
            dos.writeBytes(lineEnd);
/**/
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            fileInputStream.close();
/**/
            int totalRead = 0;
/**/
            int buffersend=bytesRead/100;
/**/
            dos.flush();
            while (bytesRead > 0)
            {
                if(bytesRead-buffersend<0)
                    buffersend=bytesRead;
                dos.write(buffer, totalRead, buffersend);
                dos.flush();
                bytesRead-=buffersend;
                totalRead += buffersend;
                int progress = (int) (totalRead * (100/(double) contentLength));
                MainActivity.config.progreso.setProgress(progress);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();
            dos.close();
        }
        catch(SocketException ex)
        {}
        catch (MalformedURLException ex)
        {}
        catch (IOException ioe)
        {}

        String str="NOT OK";
        try {
            inStream = new DataInputStream ( conn.getInputStream() );
            String test="";
            while (( test = inStream.readLine()) != null)
            {
                str=test;
            }
            inStream.close();
        }
        catch (IOException ioex)
        {}
        return str;
    }
}
