package com.example.root.trabajofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
                                "https://apptesis.000webhostapp.com/get_usuario.php?" +
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
                                "https://apptesis.000webhostapp.com/get_puntos.php?" ,
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

    public void obtenerDescripcion(int idPunto, final TextView textView){
        GestorUsuarios.getGestorUsuarios(context);

        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "https://apptesis.000webhostapp.com/get_descripcion.php?id=" + idPunto ,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        try {
                                            Log.e("Descripcion",response.getString(0));
                                            textView.setText(response.getString(0));

                                        }catch (Exception e){
                                            Log.d(TAG, "Error Volley: " );
                                        }


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

    public void actualizarPuntos(){
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonArrayRequest(
                                Request.Method.GET,
                                "https://apptesis.000webhostapp.com/get_puntos.php?" ,
                                new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {

                                        try {
                                            int i;
                                            ArrayList<Punto> puntos=new ArrayList<Punto>();

                                            for(i=0;i<response.length();i++){

                                                JSONArray puntoJSON=response.getJSONArray(i);
                                                File file=new File(puntoJSON.getString(5));
                                                Punto puntoWB=new Punto(
                                                        puntoJSON.getInt(0),        //id
                                                        puntoJSON.getString(1),     //titulo
                                                        puntoJSON.getString(2),     //descripcion
                                                        Double.parseDouble(puntoJSON.getString(3)),       //latitud
                                                        Double.parseDouble(puntoJSON.getString(4)),       //longitud
                                                        "/data/data/com.example.root.trabajofinal/app_imageDir/"+file.getName(),
                                                        puntoJSON.getString(5),             //foto_web
                                                        0                                 //estado_foto
                                                );
                                                GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(context);
                                                gestorImagenes.descargarImagen(puntoJSON.getString(5),file.getName(),puntoJSON.getInt(0));
                                                puntos.add(puntoWB);

                                            }
                                            gestorDePuntos.respActualizarPuntos(puntos);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley(ActualizarPuntos): " + error.getMessage());

                                    }
                                }

                        )
                );
    }

    public void setPunto(final Punto punto){
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(context);
        Log.e("SetPunto","Entro por lo menos");
        String url = "https://apptesis.000webhostapp.com/set_punto.php";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        Toast.makeText(context, "El punto se agregó con éxito", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", "");
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
                params.put("descripcion", "l");

                GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(context);

                params.put("base64Img", gestorImagenes.getImagenBase64(punto.getImagen().getPath()));
                params.put("nombre_imagenImg",punto.getImagen().getNombreArchivo());
                params.put("tituloImg", punto.getImagen().getTitulo());
                params.put("descripcionImg", punto.getImagen().getDescripcion());
                params.put("fecha_capturaImg", ""+punto.getImagen().getFechaCaptura());
                params.put("fecha_subidaImg", ""+punto.getImagen().getFechaSubida());
                return params;
            }

        };
        requestQueue.add(postRequest);

    }

    public void enviarImagen(final String image, final Multimedia multimedia) {

        Log.e("<GestorWebService>",multimedia.getPath());
        final StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://apptesis.000webhostapp.com/subir_imagen.php",
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




}
