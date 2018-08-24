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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Objetos.Punto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class VistaSatelital extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMapWorldPlugin mGoogleMapPlugin;
    private World mWorld;
    private GestorPuntos gestorPuntos;
    private Context context;
    private boolean primeraVez=true;
    private double latitudUser=0;
    private double longitudUser=0;
    AlertDialog alert = null;
    private GeoObject user;
    private LocationListener listener;
    private LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google);
        context = getApplicationContext();
        Button btnRealidadAumentada = (Button) findViewById(R.id.btnRealidadAumentada);
        btnRealidadAumentada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RealidadAumentada.class);
                startActivity(intent);
                finish();
            }
        });

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double difLat=latitudUser-location.getLatitude();
                double difLon=longitudUser-location.getLongitude();
                if(valorAbsolutoNumero(difLat)>0.002 || valorAbsolutoNumero(difLon)>0.002){
                    primeraVez=true;
                }
                latitudUser=location.getLatitude();
                longitudUser=location.getLongitude();
                if(primeraVez && mWorld!=null&& user!=null){
                    LatLng userLocation = new LatLng(latitudUser, longitudUser);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
                    primeraVez=false;
                }
                if(mWorld!=null&& user!=null){
                    user.setGeoPosition(latitudUser, longitudUser);
                    mWorld.addBeyondarObject(user);
                }
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

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);

            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                    finish();
                } else {
                    obtenerUbicacion();
                }
            }
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();

            } else {
                obtenerUbicacion();
            }
        }


    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void obtenerUbicacion() {
        Button myLocationButton = (Button) findViewById(R.id.myLocationButton);
        myLocationButton.setVisibility(View.VISIBLE);
        myLocationButton.setOnClickListener(this);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
         if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 0, listener);}
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, listener);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GeoObject geoObject = mGoogleMapPlugin.getGeoObjectOwner(marker);
        if (geoObject != null) {
            Punto punto = gestorPuntos.getPunto(geoObject.getName());
            Intent intent = new Intent(getApplicationContext(), Detalle.class);
            intent.putExtra(Detalle.EXTRA_POSITION, punto.getId());
            startActivity(intent);

        }
        return false;
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

    @Override
    public void onClick(View v) {


        user.setGeoPosition(latitudUser, longitudUser);
        mWorld.addBeyondarObject(user);
        LatLng userLocation = new LatLng(latitudUser,longitudUser);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap == null) {
            return;
        }
        GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
        gestorPuntos.getPuntos();
        mWorld = gestorPuntos.generarMundo(this);
        mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
        mGoogleMapPlugin.setGoogleMap(mMap);
        mWorld.addPlugin(mGoogleMapPlugin);
        mMap.setOnMarkerClickListener(this);
        user = new GeoObject(1000l);
        user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
        user.setImageResource(R.drawable.flag);
        user.setName("Posición actual");
        mWorld.addBeyondarObject(user);

        if(latitudUser!=0 && longitudUser!=0) {
            LatLng userLocation = new LatLng(mWorld.getLatitude(), mWorld.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    obtenerUbicacion();

                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private static double valorAbsolutoNumero(double num){
        return num>=0?num:-num;
    }

}
