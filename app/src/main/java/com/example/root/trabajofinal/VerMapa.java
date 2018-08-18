package com.example.root.trabajofinal;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VerMapa extends FragmentActivity implements
         OnMapReadyCallback {

    private double latitud;
    private double longitud;
    private String titulo;
    private GoogleMap mMap;
    private GoogleMapWorldPlugin mGoogleMapPlugin;
    private World mWorld;
    private GestorPuntos gestorPuntos;
    private Context context;
    private Location loc;
    private boolean primeraVez=true;
    private double latitudUser=0;
    private double longitudUser=0;
    AlertDialog alert = null;
    LocationManager manager;
    private static LocationManager oLoc;
    private Location currentLoc;
    private GeoObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mapa);
        latitud=getIntent().getDoubleExtra("latitud",0);
        longitud=getIntent().getDoubleExtra("longitud",0);
        titulo=getIntent().getStringExtra("titulo");

        //lo demas el vista satelital
        Log.e("Ver mapa","lat "+latitud);
        Log.e("Ver mapa","lon "+longitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng punto = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(punto).title(titulo));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
    }
    */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap == null) {
            return;
        }

        // We create the world and fill the world
        GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
        mWorld = gestorPuntos.generarMundo(this);

        // As we want to use GoogleMaps, we are going to create the plugin and
        // attach it to the World
        mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
        // Then we need to set the map in to the GoogleMapPlugin
        mGoogleMapPlugin.setGoogleMap(mMap);
        // Now that we have the plugin created let's add it to our world.
        // NOTE: It is better to load the plugins before start adding object in
        // to the world.
        mWorld.addPlugin(mGoogleMapPlugin);


        LatLng userLocation = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        // We need to set the LocationManager to the BeyondarLocationManager.

    }
}
