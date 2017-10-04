package com.example.root.trabajofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;


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
                                                File file=new File(puntoJSON.getString(4));
                                                Punto puntoWB=new Punto(
                                                        puntoJSON.getInt(0),        //id
                                                        puntoJSON.getString(1),     //titulo
                                                        Double.parseDouble(puntoJSON.getString(2)),       //latitud
                                                        Double.parseDouble(puntoJSON.getString(3)),       //longitud
                                                        "/data/data/com.example.root.trabajofinal/app_imageDir/"+file.getName()
                                                );
                                                GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(context);
                                                gestorImagenes.descargarImagen(puntoJSON.getString(4),file.getName());
                                                Log.e("<archivo>",file.getName());
                                                puntos.add(puntoWB);
                                                Log.e("<WEBSER>","Lat - Long: "+puntoJSON.getString(1)+" "+ Double.parseDouble(puntoJSON.getString(2))+ " "+Double.parseDouble(puntoJSON.getString(3)));
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
                                        Log.d(TAG, "Error Volley: " + error.getMessage());

                                    }
                                }

                        )
                );
    }




}
