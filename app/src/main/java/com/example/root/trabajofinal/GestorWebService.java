package com.example.root.trabajofinal;

import android.content.Context;
import android.util.Log;
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
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.EliminarPuntoListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Listeners.SetPuntoListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Listeners.VideosListener;
import com.example.root.trabajofinal.TiposEnumerados.TipoMultimedia;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class GestorWebService {

    public Usuario usuario;
    private Context context;
    private static GestorWebService gestorWebService;
    private GestorUsuarios gestorUsuarios;
    private GestorDePuntos gestorDePuntos;
    public static String TAG="<GestorWebService>";

    private GestorWebService(Context context){

        this.context=context;
        this.gestorUsuarios=GestorUsuarios.getGestorUsuarios(context);
        this.gestorDePuntos=GestorDePuntos.getGestorDePuntos(context);
    }

    public static GestorWebService getGestorWebService(Context context){
        if(gestorWebService==null){
            gestorWebService=new GestorWebService(context);
        }
        return gestorWebService;
    }

    public void login(Usuario usuario){
        GestorUsuarios.getGestorUsuarios(context);

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

                                        gestorUsuarios.notifyViews(usuarioComleto);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        gestorUsuarios.notifyViews(null);
                                    }
                                }

                        )
                );

    }

    public void obtenerPuntos(int idMensaje){
        GestorUsuarios.getGestorUsuarios(context);

        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "http://www.pedroamas.xyz/get_puntos.php?" ,
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

                                        gestorUsuarios.notifyViews(usuarioComleto);

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                        gestorUsuarios.notifyViews(null);
                                    }
                                }

                        )
                );

    }

    public void eliminarPunto(int idPunto , final EliminarPuntoListener eliminarPuntoListener){
        GestorUsuarios.getGestorUsuarios(context);

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
                                "http://www.pedroamas.xyz/get_puntos.php?" ,
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
                                                    null,
                                                    null,
                                                    imagenJSON.getInt(6),
                                                    TipoMultimedia.imagen
                                            );

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
    public void setPunto(final Punto punto, final SetPuntoListener setPuntoListener){
        Log.e(TAG,"este es el ws");
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);
        String url = "http://www.pedroamas.xyz/set_punto.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        setPuntoListener.onResponseSetPunto("OK");

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", "");
                        setPuntoListener.onResponseSetPunto("Error: "+error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("titulo", punto.getTitulo());
                params.put("latitud", ""+punto.getLatitud());
                params.put("longitud", ""+punto.getLongitud());
                Log.e("LatLong","latidud: "+punto.getLatitud()+" Longitud: "+punto.getLongitud());
                params.put("descripcion", punto.getDescripcion());

                GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(context);

                params.put("base64Img", gestorImagenes.getImagenBase64(punto.getImagen().getPath()));

                Log.e(TAG, gestorImagenes.getImagenBase64(punto.getImagen().getPath()));
                params.put("nombre_imagenImg",punto.getImagen().getNombreArchivo());
                params.put("tituloImg", punto.getImagen().getTitulo());
                params.put("descripcionImg", punto.getImagen().getDescripcion());
                params.put("fecha_capturaImg", ""+punto.getImagen().getFechaCaptura());
                params.put("fecha_subidaImg", "");
                return params;
            }

        };
        requestQueue.add(postRequest);

    }

    public void enviarImagen(final String image, final Multimedia multimedia) {

        Log.e("<GestorWebService>",multimedia.getPath());
        final StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://www.pedroamas.xyz/subir_imagen.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("enviarImagen",response);
                        Toast.makeText(context, "La imagen se envió con éxito", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("errorEnviarImg","errorazo");
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();

                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("base64", image);
                params.put("nombre_imagen",multimedia.getNombreArchivo());
                params.put("titulo", multimedia.getTitulo());
                params.put("descripcion", multimedia.getDescripcion());
                params.put("fecha_captura", ""+multimedia.getFechaCaptura());
                params.put("fecha_subida", ""+multimedia.getFechaSubida());

                return params;
            }
        };
        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
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
                                        SimpleDateFormat dt1=new SimpleDateFormat("yyyy-MM-dd");
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

                                        ArrayList<Multimedia> imagenes=new ArrayList<Multimedia>();
                                        Log.e(TAG,response.toString());
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
                                                        null,
                                                        null,
                                                        imagenJSON.getInt(6),
                                                        TipoMultimedia.imagen
                                                );
                                                imagenes.add(imagen);
                                                Log.e(TAG,"path WS: "+imagen.getPath());

                                            }


                                        }catch (Exception e){
                                            Log.e(TAG, "Error Volley: " );
                                        }

                                        imagenesListener.onResponseImagenes(imagenes);


                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
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




}
