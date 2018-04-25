package com.example.root.trabajofinal.Gestores;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.root.trabajofinal.Listeners.ActualizarEstadoImgListener;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.AgregarImagenSecListener;
import com.example.root.trabajofinal.Listeners.AudioListener;
import com.example.root.trabajofinal.Listeners.AudiosListener;
import com.example.root.trabajofinal.Listeners.EditarMultimediaListener;
import com.example.root.trabajofinal.Listeners.EditarPuntoListener;
import com.example.root.trabajofinal.Listeners.EliminarCometarioListener;
import com.example.root.trabajofinal.Listeners.EliminarImagenSecListener;
import com.example.root.trabajofinal.Listeners.EliminarPuntoListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Listeners.LoginListener;
import com.example.root.trabajofinal.Listeners.RegistrarListener;
import com.example.root.trabajofinal.Listeners.SetComentarioListener;
import com.example.root.trabajofinal.Listeners.SetPuntoListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Listeners.VideosListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.TiposEnumerados.TipoMultimedia;
import com.example.root.trabajofinal.Objetos.Usuario;
import com.example.root.trabajofinal.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class GestorWebService {

    public Usuario usuario;
    private Context context;
    private static GestorWebService gestorWebService;
    public static String TAG="<GestorWebService>";
    public SimpleDateFormat dt1;
    public SimpleDateFormat dt2;

    private GestorWebService(Context context){

        this.context=context;
        dt1=new SimpleDateFormat("yyyy-MM-dd");
        dt2=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    }

    public static GestorWebService getInstance(Context context){
        if(gestorWebService==null){
            gestorWebService=new GestorWebService(context);
        }
        return gestorWebService;
    }

    public void login(Usuario usuario, final LoginListener loginListener){
        GestorUsuarios.getInstance(context);

        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_usuario.php?" +
                                        "username="+usuario.getUsername()+
                                        "&contrasena="+usuario.getContrasena(),
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Usuario usuarioComleto=null;
                                        try {
                                            if(response.length()>0){
                                                JSONArray usuarioJSON=response;
                                                boolean admin;
                                                if(usuarioJSON.getInt(6)==0){
                                                    admin=false;
                                                }else {
                                                    admin=true;
                                                }
                                                usuarioComleto=new Usuario(
                                                        usuarioJSON.getInt(0),
                                                        usuarioJSON.getString(1),
                                                        usuarioJSON.getString(2),
                                                        usuarioJSON.getString(3),
                                                        usuarioJSON.getString(4),
                                                        usuarioJSON.getString(5),
                                                        admin
                                                );

                                            }
                                        }catch (Exception e){
                                            usuarioComleto=null;
                                            Log.d(TAG, "Error Volley: " );
                                        }

                                        loginListener.onResponseLoginListener(usuarioComleto);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        loginListener.onResponseLoginListener(null);
                                    }
                                }

                        )
                );

    }

    public void eliminarPunto(int idPunto , final EliminarPuntoListener eliminarPuntoListener){
        GestorUsuarios.getInstance(context);

        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new StringRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/eliminar_punto.php?id_punto=" + idPunto ,
                                new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Log.e(TAG,response);
                                            eliminarPuntoListener.onResponseEliminarPunto(response);

                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                            eliminarPuntoListener.onResponseEliminarPunto("No se pudo eliminar correctamente");
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        eliminarPuntoListener.onResponseEliminarPunto("No se pudo eliminar correctamente");
                                    }
                                }

                        )
                );

    }

    public void actualizarPuntos(final ActualizarPuntoListener actualizarPuntoListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_puntos.php" ,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        try {

                                            int i;
                                            ArrayList<Punto> puntos=new ArrayList<Punto>();

                                            for(i=0;i<response.length();i++){

                                                JSONArray puntoJSON=response.getJSONArray(i);

                                                Log.e(TAG,"ES aca: "+puntoJSON.getInt(0));
                                                Punto puntoWB=new Punto(
                                                        puntoJSON.getInt(0),        //id
                                                        puntoJSON.getString(1),     //titulo
                                                        puntoJSON.getString(2),     //descripcion
                                                        Double.parseDouble(puntoJSON.getString(3)),       //latitud
                                                        Double.parseDouble(puntoJSON.getString(4)),       //longitud
                                                        "",
                                                        puntoJSON.getString(5),             //foto_web
                                                        0                                 //estado_foto
                                                );
                                                puntoWB.setFechaUltMod(puntoJSON.getString(6));

                                                puntos.add(puntoWB);

                                            }
                                            actualizarPuntoListener.onResponseActualizarPunto(puntos);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            actualizarPuntoListener.onResponseActualizarPunto(new ArrayList<Punto>());
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley(ActualizarPuntos): " + error.getMessage());
                                        actualizarPuntoListener.onResponseActualizarPunto(new ArrayList<Punto>());

                                    }
                                }

                        )
                );
    }

    public void getImagen(int idImagen, final ImagenListener imagenListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_imagen_sec.php?" +
                                        "id_imagen="+idImagen,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        Log.e(TAG,response.toString());
                                        Multimedia imagen=null;
                                        try {


                                            JSONArray imagenJSON=response;

                                            imagen=new Multimedia(
                                                    imagenJSON.getInt(0),
                                                    imagenJSON.getString(1),
                                                    imagenJSON.getString(2),
                                                    imagenJSON.getString(3),
                                                    dt1.parse(imagenJSON.getString(4)),
                                                    dt1.parse(imagenJSON.getString(5)),
                                                    imagenJSON.getInt(6),
                                                    TipoMultimedia.imagen
                                            );
                                            if (imagenJSON.getInt(7)!=0){
                                                imagen.setIdUsuario(imagenJSON.getInt(7));
                                                imagen.setUsername(imagenJSON.getString(8));
                                            }


                                            imagenListener.onResponseImagen(imagen);

                                        }catch (Exception e){
                                            Log.e(TAG, "Error Volley: " );
                                            imagenListener.onResponseImagen(null);
                                        }



                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        imagenListener.onResponseImagen(null);
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }

                        )
                );
    }

    public void getImagenConEstado(int idImagen,int estado, final ImagenListener imagenListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_imagen_sec_usuarios.php?" +
                                        "id_imagen="+idImagen+
                                        "&estado="+estado
                                ,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            Log.e(TAG,response.toString());

                                            Multimedia imagen=null;
                                            JSONArray imagenJSON=response;
                                            imagen=new Multimedia(
                                                    imagenJSON.getInt(0),
                                                    imagenJSON.getString(1),
                                                    imagenJSON.getString(2),
                                                    imagenJSON.getString(3),
                                                    null,
                                                    null,
                                                    //dt1.parse(imagenJSON.getString(4)),
                                                    //dt1.parse(imagenJSON.getString(5)),
                                                    imagenJSON.getInt(6),
                                                    TipoMultimedia.imagen
                                            );
                                            Log.e(TAG,"donde es el error?? 2");
                                            if (imagenJSON.getInt(7)!=0){
                                                imagen.setIdUsuario(imagenJSON.getInt(7));
                                                imagen.setUsername(imagenJSON.getString(8));
                                            }

                                            Log.e(TAG,"donde es el error?? 3");

                                            Log.e("TAG obj img",imagen.getTitulo()+"");
                                            Log.e("TAG obj img",imagen.getUsername()+"");
                                            Log.e("TAG obj img",imagen.getPath()+"");



                                            Log.e(TAG,"donde es el error?? 4");
                                            imagenListener.onResponseImagen(imagen);
                                            //imagenListener.onResponseImagen(imagen);

                                        }catch (Exception e){
                                            Log.e(TAG, "Error Volley(tipo): "+e.getMessage() );
                                        }




                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        imagenListener.onResponseImagen(null);
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }

                        )
                );
    }

    public void getImagenPortada(int idPunto, final ImagenListener imagenListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_imagen_portada.php?" +
                                        "id_punto="+idPunto,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        Log.e(TAG,response.toString());
                                        Multimedia imagen=null;
                                        try {

                                            JSONArray imagenJSON=response;
                                            imagen=new Multimedia(
                                                    imagenJSON.getInt(0),
                                                    imagenJSON.getString(1),
                                                    imagenJSON.getString(2),
                                                    imagenJSON.getString(3),
                                                    dt1.parse(imagenJSON.getString(4)),
                                                    dt1.parse(imagenJSON.getString(5)),
                                                    imagenJSON.getInt(6),
                                                    TipoMultimedia.imagen
                                            );

                                            imagenListener.onResponseImagen(imagen);

                                        }catch (Exception e){
                                            Log.e(TAG, "1 - Error Volley: " );
                                            imagenListener.onResponseImagen(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        imagenListener.onResponseImagen(null);
                                        Log.d(TAG, "1 - Error Volley: " + error.getMessage());
                                    }
                                }

                        )
                );
    }

    public void agregarPunto(final Punto punto, final SetPuntoListener setPuntoListener){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                //obtiene la extension
                String extension = MimeTypeMap.getFileExtensionFromUrl(punto.getFoto().substring(punto.getFoto().lastIndexOf("/")+1)  );
                String content_type  = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                File f=new File(punto.getFoto());
                GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                f= gestorMultimedia.ajustarImagen(f);
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                Log.e("que es estp",content_type);
                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("titulo", punto.getTitulo())
                        .addFormDataPart("latitud", ""+punto.getLatitud())
                        .addFormDataPart("longitud", ""+punto.getLongitud())
                        .addFormDataPart("descripcion", punto.getDescripcion())
                        .addFormDataPart("nombre_imagenImg",punto.getImagen().getNombreArchivo())
                        .addFormDataPart("tituloImg", punto.getImagen().getTitulo())
                        .addFormDataPart("descripcionImg", punto.getImagen().getDescripcion())
                        .addFormDataPart("fecha_subidaImg", ""+dt1.format(new Date()))
                        //.addFormDataPart("fecha_captura",null)
                        .addFormDataPart("type",content_type)
                        //.addFormDataPart("uploaded_file",video.getPath().substring(video.getPath().lastIndexOf("/")+1), file_body)
                        .addFormDataPart("uploaded_file",punto.getFoto().substring(punto.getFoto().lastIndexOf("/")+1), file_body)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://pedroamas.xyz/set_punto.php")
                        .post(request_body)
                        .build();

                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    Log.e("Body response",response.body().string());
                    if(!response.isSuccessful()){

                        setPuntoListener.onResponseSetPunto("Error");
                        throw new IOException("Error : "+response);
                    }else{
                        Log.e("Resp de ws",response.message());
                        setPuntoListener.onResponseSetPunto("Ok");
                    }

                    Log.e("","Correcto");

                    return;

                } catch (IOException e) {
                    Log.e("","incorrecto");
                    e.printStackTrace();
                    setPuntoListener.onResponseSetPunto("Error");
                }


            }
        });

        t.start();
    }

    public void editarPunto(final Punto punto, final EditarPuntoListener editarPuntoListener){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //obtiene la extension
                OkHttpClient client = new OkHttpClient();
                RequestBody request_body;
                if(punto.getImagen().getPath()!=null) {
                    String extension= MimeTypeMap.getFileExtensionFromUrl(punto.getImagen().getPath().substring(punto.getImagen().getPath().lastIndexOf("/") + 1));
                    String content_type= MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                    File f = new File(punto.getImagen().getPath());
                    GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                    f = gestorMultimedia.ajustarImagen(f);

                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                    request_body= new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id_punto",""+punto.getId())
                            .addFormDataPart("titulo",punto.getTitulo())
                            .addFormDataPart("latitud",punto.getLatitud()+"")
                            .addFormDataPart("longitud",punto.getLongitud()+"")
                            .addFormDataPart("descripcion",punto.getDescripcion())
                            .addFormDataPart("tituloImg", punto.getImagen().getTitulo())
                            .addFormDataPart("descripcionImg", punto.getImagen().getDescripcion())
                            .addFormDataPart("fecha_capturaImg", ""+dt1.format(punto.getImagen().getFechaCaptura()))
                            .addFormDataPart("fecha_subidaImg", "")
                            .addFormDataPart("envia_imagen","SI")
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("uploaded_file",punto.getImagen().getPath().substring(punto.getImagen().getPath().lastIndexOf("/")+1), file_body)
                            .build();
                }else{
                    request_body= new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id_punto",""+punto.getId())
                            .addFormDataPart("titulo",punto.getTitulo())
                            .addFormDataPart("latitud",punto.getLatitud()+"")
                            .addFormDataPart("longitud",punto.getLongitud()+"")
                            .addFormDataPart("descripcion",punto.getDescripcion())
                            .addFormDataPart("tituloImg", punto.getImagen().getTitulo())
                            .addFormDataPart("descripcionImg", punto.getImagen().getDescripcion())
                            .addFormDataPart("fecha_capturaImg", ""+dt1.format(punto.getImagen().getFechaCaptura()))
                            .addFormDataPart("fecha_subidaImg", "")
                            .build();
                }
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://pedroamas.xyz/editar_punto.php")
                        .post(request_body)
                        .build();

                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    Log.e("HTML",response.body().string());
                    if(!response.isSuccessful()){

                        editarPuntoListener.onResponseEditarPunto("Error");
                        throw new IOException("Error : "+response);
                    }else{
                        Log.e("Resp de ws",response.message());
                        editarPuntoListener.onResponseEditarPunto("Ok");
                    }

                    Log.e("","Correcto");

                    return;

                } catch (IOException e) {
                    Log.e("","incorrecto");
                    e.printStackTrace();
                    editarPuntoListener.onResponseEditarPunto("Error");
                }


            }
        });

        t.start();


    }

    public void getVideo(int idVideo, final VideoListener videoListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_video.php?" +
                                        "id_video="+idVideo,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        Multimedia video=null;
                                        try {
                                            if(response.length()>0){
                                                JSONArray videoJSON=response;
                                                video=new Multimedia(
                                                        videoJSON.getInt(0),
                                                        videoJSON.getString(1),
                                                        videoJSON.getString(2),
                                                        videoJSON.getString(3),
                                                        dt1.parse(videoJSON.getString(4)),
                                                        dt1.parse(videoJSON.getString(5)),
                                                        videoJSON.getInt(6),
                                                        TipoMultimedia.video
                                                );

                                            }
                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                        }
                                        videoListener.onResponseVideo(video);


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        videoListener.onResponseVideo(null);
                                    }
                                }

                        )
                );
    }

    public void getAudio(int idAudio, final AudioListener audioListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_audio.php?" +
                                        "id_audio="+idAudio,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        Multimedia audio=null;
                                        try {
                                            if(response.length()>0){
                                                JSONArray audioJSON=response;
                                                audio=new Multimedia(
                                                        audioJSON.getInt(0),
                                                        audioJSON.getString(1),
                                                        audioJSON.getString(2),
                                                        audioJSON.getString(3),
                                                        dt1.parse(audioJSON.getString(4)),
                                                        dt1.parse(audioJSON.getString(5)),
                                                        audioJSON.getInt(6),
                                                        TipoMultimedia.audio
                                                );

                                            }
                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                        }
                                        audioListener.onResponseAudioListener(audio);


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        audioListener.onResponseAudioListener(null);
                                    }
                                }

                        )
                );
    }

    public void getImagenes(int idPunto, final ImagenesListener imagenesListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_imagenes_sec.php?" +
                                        "id_punto="+idPunto,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.e("Respuesta getImagenes",response.toString());
                                        ArrayList<Multimedia> imagenes=new ArrayList<Multimedia>();

                                        Multimedia imagen=null;
                                        try {
                                            int i;
                                            for(i=0;i<response.length();i++){

                                                JSONArray imagenJSON=response.getJSONArray(i);
                                                imagen=new Multimedia(
                                                        imagenJSON.getInt(0),
                                                        imagenJSON.getString(1),
                                                        imagenJSON.getString(2),
                                                        imagenJSON.getString(3),
                                                        dt1.parse(imagenJSON.getString(4)),
                                                        dt1.parse(imagenJSON.getString(5)),
                                                        imagenJSON.getInt(6),
                                                        TipoMultimedia.imagen
                                                );
                                                imagenes.add(imagen);
                                                Log.e(TAG,"path WS: "+imagen.getPath());

                                            }


                                        }catch (Exception e){
                                            Log.e(TAG, "2 - Error Volley: " );
                                        }

                                        imagenesListener.onResponseImagenes(imagenes);


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "2 - Error Volley: " + error.getMessage());
                                        imagenesListener.onResponseImagenes(new ArrayList<Multimedia>());
                                    }
                                }

                        )
                );
    }

    public void getImagenesUsuarios(int idPunto, final ImagenesListener imagenesListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_imagenes_sec_usuarios.php?" +
                                        "id_punto="+idPunto,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.e(TAG,response.toString());
                                        ArrayList<Multimedia> imagenes=new ArrayList<Multimedia>();

                                        Multimedia imagen=null;
                                        try {
                                            int i;
                                            for(i=0;i<response.length();i++){

                                                JSONArray imagenJSON=response.getJSONArray(i);
                                                imagen=new Multimedia(
                                                        imagenJSON.getInt(0),
                                                        imagenJSON.getString(1),
                                                        imagenJSON.getString(2),
                                                        imagenJSON.getString(3),
                                                        dt1.parse(imagenJSON.getString(4)),
                                                        dt1.parse(imagenJSON.getString(5)),
                                                        imagenJSON.getInt(6),
                                                        TipoMultimedia.imagen
                                                );
                                                imagen.setIdUsuario(imagenJSON.getInt(7));
                                                imagen.setUsername(imagenJSON.getString(8));
                                                imagenes.add(imagen);
                                                Log.e(TAG,"path WS: "+imagen.getPath());

                                            }


                                        }catch (Exception e){
                                            Log.e(TAG, "2 - Error Volley: " );
                                        }

                                        imagenesListener.onResponseImagenes(imagenes);


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "2 - Error Volley: " + error.getMessage());
                                        imagenesListener.onResponseImagenes(new ArrayList<Multimedia>());
                                    }
                                }

                        )
                );
    }

    public void getImagenesUsuariosPendientes(final ImagenesListener imagenesListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_imagenes_sec_usuarios_no_apr.php",
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.e(TAG,response.toString());
                                        ArrayList<Multimedia> imagenes=new ArrayList<Multimedia>();

                                        Multimedia imagen=null;
                                        try {
                                            int i;
                                            for(i=0;i<response.length();i++){

                                                JSONArray imagenJSON=response.getJSONArray(i);
                                                imagen=new Multimedia(
                                                        imagenJSON.getInt(0),
                                                        imagenJSON.getString(1),
                                                        imagenJSON.getString(2),
                                                        imagenJSON.getString(3),
                                                        dt1.parse(imagenJSON.getString(4)),
                                                        dt1.parse(imagenJSON.getString(5)),
                                                        imagenJSON.getInt(6),
                                                        TipoMultimedia.imagen
                                                );
                                                imagen.setIdUsuario(imagenJSON.getInt(7));
                                                imagen.setUsername(imagenJSON.getString(8));
                                                imagenes.add(imagen);
                                                Log.e(TAG,"path WS: "+imagen.getPath());

                                            }


                                        }catch (Exception e){
                                            Log.e(TAG, "2 - Error Volley: " );
                                        }

                                        imagenesListener.onResponseImagenes(imagenes);


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "2 - Error Volley: " + error.getMessage());
                                        imagenesListener.onResponseImagenes(new ArrayList<Multimedia>());
                                    }
                                }

                        )
                );
    }

    public void getVideos(int idPunto, final VideosListener videosListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_videos.php?"+
                                        "id_punto="+idPunto,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        ArrayList<Multimedia> videos=new ArrayList<Multimedia>();
                                        Log.e("asdasdasdasasdasdasdas",response.toString());
                                        Multimedia video=null;
                                        try {
                                            int i;
                                            SimpleDateFormat dt1=new SimpleDateFormat("yyyy-MM-dd");
                                            for(i=0;i<response.length();i++){

                                                JSONArray videoJSON=response.getJSONArray(i);
                                                Log.e(TAG,"path de video: "+videoJSON.getString(4));
                                                Log.e(TAG,"path de video: "+videoJSON.getString(5));
                                                video=new Multimedia(
                                                        videoJSON.getInt(0),
                                                        videoJSON.getString(1),
                                                        videoJSON.getString(2),
                                                        videoJSON.getString(3),
                                                        dt1.parse(videoJSON.getString(4)),
                                                        dt1.parse(videoJSON.getString(5)),
                                                        videoJSON.getInt(6),
                                                        TipoMultimedia.video
                                                );
                                                videos.add(video);
                                                Log.e(TAG,"path de video: "+videoJSON.getString(4));
                                                Log.e(TAG,"path de video: "+videoJSON.getString(5));

                                            }


                                        }catch (Exception e){
                                            Log.e(TAG, "Error Volley: " );
                                        }
                                        videosListener.onResponseVideos(videos);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        videosListener.onResponseVideos(new ArrayList<Multimedia>());
                                    }
                                }

                        )
                );
    }

    public void getAudios(int idPunto, final AudiosListener audiosListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_audios.php?"+
                                        "id_punto="+idPunto,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        ArrayList<Multimedia> audios=new ArrayList<Multimedia>();
                                        Multimedia audio=null;
                                        try {
                                            int i;
                                            SimpleDateFormat dt1=new SimpleDateFormat("yyyy-MM-dd");
                                            for(i=0;i<response.length();i++){

                                                JSONArray audioJSON=response.getJSONArray(i);
                                                Log.e(TAG,"path de audio: "+audioJSON.getString(4));
                                                Log.e(TAG,"path de audio: "+audioJSON.getString(5));
                                                audio=new Multimedia(
                                                        audioJSON.getInt(0),
                                                        audioJSON.getString(1),
                                                        audioJSON.getString(2),
                                                        audioJSON.getString(3),
                                                        dt1.parse(audioJSON.getString(4)),
                                                        dt1.parse(audioJSON.getString(5)),
                                                        audioJSON.getInt(6),
                                                        TipoMultimedia.audio
                                                );
                                                audios.add(audio);
                                                Log.e(TAG,"path de video: "+audioJSON.getString(4));
                                                Log.e(TAG,"path de video: "+audioJSON.getString(5));

                                            }


                                        }catch (Exception e){
                                            Log.e(TAG, "Error Volley: " );
                                        }
                                        audiosListener.onResponseAudiosListener(audios);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        audiosListener.onResponseAudiosListener(new ArrayList<Multimedia>());
                                    }
                                }

                        )
                );
    }


    public void agregarImagenSec(final Multimedia imagen , final AgregarImagenSecListener agregarImagenSecListener){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //obtiene la extension
                String extension = MimeTypeMap.getFileExtensionFromUrl(imagen.getPath().substring(imagen.getPath().lastIndexOf("/")+1)  );
                String content_type  = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                File f=new File(imagen.getPath());
                GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                f= gestorMultimedia.ajustarImagen(f);
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                Log.e("que es estp",content_type);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id_punto",""+imagen.getIdPunto())
                        .addFormDataPart("titulo",imagen.getTitulo())
                        .addFormDataPart("descripcion",imagen.getDescripcion())
                        //.addFormDataPart("fecha_captura",null)
                        .addFormDataPart("type",content_type)
                        //.addFormDataPart("uploaded_file",video.getPath().substring(video.getPath().lastIndexOf("/")+1), file_body)
                        .addFormDataPart("uploaded_file",imagen.getPath().substring(imagen.getPath().lastIndexOf("/")+1), file_body)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://pedroamas.xyz/set_imagen_sec.php")
                        .post(request_body)
                        .build();

                try {
                    Log.e("","entro en try");
                    okhttp3.Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){

                        agregarImagenSecListener.onResponseAgregarImagenSecListener("Error");
                        throw new IOException("Error : "+response);
                    }else{
                        Log.e("Resp de ws",response.message());
                        agregarImagenSecListener.onResponseAgregarImagenSecListener("Ok");
                    }

                    Log.e("","Correcto");

                    return;

                } catch (IOException e) {
                    Log.e("","incorrecto");
                    e.printStackTrace();
                    agregarImagenSecListener.onResponseAgregarImagenSecListener("Error");
                }


            }
        });

        t.start();
    }

    public void agregarImagenSecUsuario(final Multimedia imagen, final AgregarImagenSecListener agregarImagenSecListener){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //obtiene la extension
                String extension = MimeTypeMap.getFileExtensionFromUrl(imagen.getPath().substring(imagen.getPath().lastIndexOf("/")+1)  );
                String content_type  = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                File f=new File(imagen.getPath());
                GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                f= gestorMultimedia.ajustarImagen(f);
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                Log.e("que es estp","id usuario"+imagen.getIdUsuario());

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id_punto",""+imagen.getIdPunto())
                        .addFormDataPart("titulo",imagen.getTitulo())
                        .addFormDataPart("descripcion",imagen.getDescripcion())
                        .addFormDataPart("id_usuario",imagen.getIdUsuario()+"")
                        //.addFormDataPart("fecha_captura",null)
                        .addFormDataPart("type",content_type)
                        //.addFormDataPart("uploaded_file",video.getPath().substring(video.getPath().lastIndexOf("/")+1), file_body)
                        .addFormDataPart("uploaded_file",imagen.getPath().substring(imagen.getPath().lastIndexOf("/")+1), file_body)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://pedroamas.xyz/set_imagen_sec_usuario.php")
                        .post(request_body)
                        .build();

                try {
                    Log.e("","entro en try");
                    okhttp3.Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){

                        agregarImagenSecListener.onResponseAgregarImagenSecListener("Error");
                        throw new IOException("Error : "+response);
                    }else{
                        Log.e("Resp de ws",response.message());
                        agregarImagenSecListener.onResponseAgregarImagenSecListener("Ok");
                    }

                    Log.e("","Correcto");

                    return;

                } catch (IOException e) {
                    Log.e("","incorrecto");
                    e.printStackTrace();
                    agregarImagenSecListener.onResponseAgregarImagenSecListener("Error");
                }


            }
        });

        t.start();
    }


    public void editarImagenSec(final Multimedia multimedia, final EditarMultimediaListener editarMultimediaListener){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //obtiene la extension
                OkHttpClient client = new OkHttpClient();
                RequestBody request_body;
                if(multimedia.getPath()!=null) {
                    String extension= MimeTypeMap.getFileExtensionFromUrl(multimedia.getPath().substring(multimedia.getPath().lastIndexOf("/") + 1));
                    String content_type= MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                    File f = new File(multimedia.getPath());
                    GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                    f = gestorMultimedia.ajustarImagen(f);

                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);
                    request_body= new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id_imagen",""+multimedia.getId())
                            .addFormDataPart("titulo",multimedia.getTitulo())
                            .addFormDataPart("descripcion",multimedia.getDescripcion())
                            .addFormDataPart("envia_imagen","SI")
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("uploaded_file",multimedia.getPath().substring(multimedia.getPath().lastIndexOf("/")+1), file_body)
                            .build();
                }else{
                    request_body= new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id_imagen",""+multimedia.getId())
                            .addFormDataPart("titulo",multimedia.getTitulo())
                            .addFormDataPart("descripcion",multimedia.getDescripcion())
                            .build();
                }
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://pedroamas.xyz/editar_imagen_sec.php")
                        .post(request_body)
                        .build();

                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    Log.e("HTML",response.body().string());
                    if(!response.isSuccessful()){

                        editarMultimediaListener.onResponseEditarMultimedia("Error");
                        throw new IOException("Error : "+response);
                    }else{
                        Log.e("Resp de ws",response.message());
                        editarMultimediaListener.onResponseEditarMultimedia("Ok");
                    }

                    Log.e("","Correcto");

                    return;

                } catch (IOException e) {
                    Log.e("","incorrecto");
                    e.printStackTrace();
                    editarMultimediaListener.onResponseEditarMultimedia("Error");
                }


            }
        });

        t.start();


    }

    public void eliminarImagenSec(int idImagen, final EliminarImagenSecListener eliminarImagenSecListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new StringRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/eliminar_imagen_sec.php?id_imagen=" + idImagen ,
                                new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Log.e(TAG,response);
                                            eliminarImagenSecListener.onResponseEliminarImagenSecListener(response);

                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                            eliminarImagenSecListener.onResponseEliminarImagenSecListener("No se pudo eliminar correctamente");
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        eliminarImagenSecListener.onResponseEliminarImagenSecListener("No se pudo eliminar correctamente");
                                    }
                                }

                        )
                );
    }

    public void registrar(final Usuario usuario, final RegistrarListener registrarListener){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);
        String url = "http://www.pedroamas.xyz/registrar.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        registrarListener.onResponseRegistrarListener(response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.getMessage());
                        registrarListener.onResponseRegistrarListener("-1");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("nombre", usuario.getNombre());
                params.put("apellido", usuario.getApellido());
                params.put("username", usuario.getUsername());
                params.put("email", usuario.getEmail());
                params.put("contrasena", usuario.getContrasena());

                return params;
            }

        };
        requestQueue.add(postRequest);

    }

    public void setComentarioPunto(final Comentario comentario, final SetComentarioListener setComentarioListener){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);
        String url = "http://www.pedroamas.xyz/set_comentario_punto.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        setComentarioListener.onResponseSetComentarioListener(response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", "");
                        setComentarioListener.onResponseSetComentarioListener("Error: "+error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("texto", comentario.getTexto());
                params.put("id_punto", comentario.getIdAsociado()+"");
                params.put("id_usuario", comentario.getIdUsuario()+"");
                params.put("fecha", dt2.format(new Date()));
                return params;
            }

        };
        requestQueue.add(postRequest);

    }

    public void setComentarioMultimedia(final Comentario comentario, final SetComentarioListener setComentarioListener){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);
        String url = "http://www.pedroamas.xyz/set_comentario_multimedia.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        setComentarioListener.onResponseSetComentarioListener(response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", "");
                        setComentarioListener.onResponseSetComentarioListener("Error: "+error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("texto", comentario.getTexto());
                params.put("id_punto", comentario.getIdAsociado()+"");
                params.put("id_usuario", comentario.getIdUsuario()+"");
                params.put("fecha", dt2.format(new Date()));
                return params;
            }

        };
        requestQueue.add(postRequest);

    }

    public void getComentariosMultimedia(int idMultimedia , final GetComentariosListener getComentariosListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_comentarios_multimedia.php?" +
                                        "id_multimedia="+idMultimedia,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.e(TAG,response.toString());
                                        ArrayList<Comentario> comentarios=new ArrayList<Comentario>();

                                        Comentario comentario=null;
                                        try {
                                            int i;
                                            for(i=0;i<response.length();i++){

                                                JSONArray comentarioJSON=response.getJSONArray(i);
     //id, String texto, int idAsociado, int idUsuario, String username, Date fecha
                                                comentario=new Comentario(
                                                        comentarioJSON.getInt(0),
                                                        comentarioJSON.getString(1),
                                                        comentarioJSON.getInt(2),
                                                        comentarioJSON.getInt(3),
                                                        comentarioJSON.getString(4),

                                                        dt2.parse(comentarioJSON.getString(5))
                                                );
                                                comentarios.add(comentario);
                                                Log.e("testo comentario",comentario.getTexto());

                                            }

                                            getComentariosListener.onResponseGetComentariosListener(comentarios);

                                        }catch (Exception e){
                                            Log.e(TAG, "2 - Error Volley: " );
                                            getComentariosListener.onResponseGetComentariosListener(new ArrayList<Comentario>());
                                        }




                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "2 - Error Volley: " + error.getMessage());
                                        getComentariosListener.onResponseGetComentariosListener(new ArrayList<Comentario>());
                                    }
                                }

                        )
                );
    }

    public void eliminarComentario(int idComentario, final EliminarCometarioListener eliminarCometarioListener){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new StringRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/eliminar_comentario_multimedia.php?id_comentario=" + idComentario,
                                new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Log.e(TAG,response);
                                            eliminarCometarioListener.onResponseEliminarCometarioListener(response);

                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                            eliminarCometarioListener.onResponseEliminarCometarioListener("No se pudo eliminar correctamente");
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        eliminarCometarioListener.onResponseEliminarCometarioListener("No se pudo eliminar correctamente");
                                    }
                                }

                        )
                );
    }

    public void setEstadoImagen(int idImagen, int estado , final ActualizarEstadoImgListener actualizarEstadoImgListener){
        GestorUsuarios.getInstance(context);

        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new StringRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/set_estado_imagen.php?id_imagen=" + idImagen+
                                "&estado="+estado,
                                new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            Log.e(TAG,response);
                                            actualizarEstadoImgListener.onResponseActualizarEstadoImgListener("Ok");

                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                            actualizarEstadoImgListener.onResponseActualizarEstadoImgListener("No se pudo eliminar correctamente");
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        actualizarEstadoImgListener.onResponseActualizarEstadoImgListener("No se pudo eliminar correctamente");
                                    }
                                }

                        )
                );

    }

    public void setVideo(final Multimedia video ,final AgregarImagenSecListener agregarImagenSecListener){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //obtiene la extension
                String extension = MimeTypeMap.getFileExtensionFromUrl(video.getPath().substring(video.getPath().lastIndexOf("/")+1)  );
                String content_type  = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type),new File(video.getPath()));

                Log.e("que es estp",content_type);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id_punto",""+video.getIdPunto())
                        .addFormDataPart("titulo",video.getTitulo())
                        .addFormDataPart("descripcion",video.getDescripcion())
                        //.addFormDataPart("fecha_captura",null)
                        .addFormDataPart("type",content_type)
                        //.addFormDataPart("uploaded_file",video.getPath().substring(video.getPath().lastIndexOf("/")+1), file_body)
                        .addFormDataPart("uploaded_file",video.getPath().substring(video.getPath().lastIndexOf("/")+1), file_body)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://pedroamas.xyz/set_imagen_sec.php")
                        .post(request_body)
                        .build();

                try {
                    Log.e("","entro en try");
                    okhttp3.Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        agregarImagenSecListener.onResponseAgregarImagenSecListener("Error");
                        throw new IOException("Error : "+response);
                    }else{
                        agregarImagenSecListener.onResponseAgregarImagenSecListener("Ok");
                    }

                    Log.e("","Correcto");

                    return;

                } catch (IOException e) {
                    Log.e("","incorrecto");
                    e.printStackTrace();
                    agregarImagenSecListener.onResponseAgregarImagenSecListener("Error");
                }


            }
        });

        t.start();
    }
}
