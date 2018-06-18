package com.example.root.trabajofinal;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VerMapa extends FragmentActivity implements OnMapReadyCallback {

    private double latitud;
    private double longitud;
    private String titulo;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mapa);
        latitud=getIntent().getDoubleExtra("latitud",0);
        longitud=getIntent().getDoubleExtra("longitud",0);
        titulo=getIntent().getStringExtra("titulo");
        Log.e("Ver mapa","lat "+latitud);
        Log.e("Ver mapa","lon "+longitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng punto = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(punto).title(titulo));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
    }

}
