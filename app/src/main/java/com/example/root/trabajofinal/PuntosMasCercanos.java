package com.example.root.trabajofinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorBD;
import com.example.root.trabajofinal.Objetos.IndiceRtree;
import com.example.root.trabajofinal.Objetos.Punto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;

public class PuntosMasCercanos extends AppCompatActivity {

    private Context context;
    private double latitudUser=0;
    private double longitudUser=0;
    AlertDialog alert = null;
    private LocationListener listener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos_mas_cercanos);

        context=getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);

            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                    finish();
                } else {
                    permissionGranted();
                }
            }
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();

            } else {
                permissionGranted();
            }
        }

    }
    private void permissionGranted(){


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitudUser=location.getLatitude();
                longitudUser=location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("GPS", "ENABLE");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("GPS", "DISABLE");
            }

        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 0, listener);}
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, listener);
        }

        Button btnCalcularDistancia=(Button)findViewById(R.id.btnCalcularDistancia);
        btnCalcularDistancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edDistancia=(EditText)findViewById(R.id.edDistancia);
                if (edDistancia.getEditableText().toString().equals("")){
                    Toast.makeText(context,"Ingrese la distancia",Toast.LENGTH_LONG).show();
                }else {
                    double distancia;
                    try{
                        distancia=Double.parseDouble(edDistancia.getEditableText().toString());
                        distancia=distancia/1000.0;
                        GestorBD gestorBD=GestorBD.getInstance(getApplicationContext());
                        ArrayList<Punto> puntos=gestorBD.getPuntos();
                        IndiceRtree indiceRtree=new IndiceRtree(puntos);

                        double latitud;
                        double longitud;

                        latitud=latitudUser;
                        longitud=longitudUser;

                        ArrayList<Punto> puntosMasCercanos=indiceRtree.getPuntosMasCercanos(latitud,longitud,distancia);
                        Iterator<Punto> iterator=puntosMasCercanos.iterator();
                        while(iterator.hasNext()){
                            Log.e("PuntosMasCercanos","punto: "+iterator.next().getId());
                        }
                        Intent intent = new Intent(getApplicationContext(), MostrarPuntosMasCercanos.class);
                        intent.putExtra("latitud",latitud);
                        intent.putExtra("longitud",longitud);
                        intent.putExtra("distanciaKm",distancia);

                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context,"Ops! Ocurrió un error",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }
    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(locationManager!=null && listener!=null) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 0, listener);}
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, listener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null && listener!=null) {
            locationManager.removeUpdates(listener);
        }
    }

}
