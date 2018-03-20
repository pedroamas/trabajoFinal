package com.example.root.trabajofinal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.Gestores.GestorDePuntos;
import com.example.root.trabajofinal.Objetos.Punto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class VistaSatelital extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        View.OnClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMapWorldPlugin mGoogleMapPlugin;
    private World mWorld;
    private TextView txtInfo;
    private GestorDePuntos gestorDePuntos;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                startActivity(getIntent());
                finish();
                return;
            }
        }
        Button myLocationButton = (Button) findViewById(R.id.myLocationButton);
        myLocationButton.setVisibility(View.VISIBLE);
        myLocationButton.setOnClickListener(this);
        context=getApplicationContext();
        Button btnRealidadAumentada= (Button) findViewById(R.id.btnRealidadAumentada);
        btnRealidadAumentada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RealidadAumentada.class);
                startActivity(intent);
                finish();
            }});

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        txtInfo=(TextView)findViewById(R.id.txtInfo);
        BeyondarLocationManager
                .setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
        gestorDePuntos.getPuntos();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // To get the GeoObject that owns the marker we use the following
        // method:
        GeoObject geoObject = mGoogleMapPlugin.getGeoObjectOwner(marker);
        if (geoObject != null) {

            Punto punto=gestorDePuntos.getPunto(geoObject.getName());
            Intent intent = new Intent(getApplicationContext(), Detalle.class);
            intent.putExtra(Detalle.EXTRA_POSITION, punto.getId());
            startActivity(intent);

        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When the activity is resumed it is time to enable the
        // BeyondarLocationManager
        BeyondarLocationManager.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // To avoid unnecessary battery usage disable BeyondarLocationManager
        // when the activity goes on pause.
        BeyondarLocationManager.disable();
    }

    @Override
    public void onClick(View v) {
        // When the user clicks on the button we animate the map to the user
        // location

        txtInfo.setText(mWorld.getLatitude()+" "+mWorld.getLongitude());
        LatLng userLocation = new LatLng(mWorld.getLatitude(), mWorld.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        if (mMap == null) {
            return;
        }

        // We create the world and fill the world
        GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
        mWorld = gestorDePuntos.generarMundo(this);

        // As we want to use GoogleMaps, we are going to create the plugin and
        // attach it to the World
        mGoogleMapPlugin = new GoogleMapWorldPlugin(this);
        // Then we need to set the map in to the GoogleMapPlugin
        mGoogleMapPlugin.setGoogleMap(mMap);
        // Now that we have the plugin created let's add it to our world.
        // NOTE: It is better to load the plugins before start adding object in
        // to the world.
        mWorld.addPlugin(mGoogleMapPlugin);

        mMap.setOnMarkerClickListener(this);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGoogleMapPlugin.getLatLng(), 15));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);



        BeyondarLocationManager.addWorldLocationUpdate(mWorld);
        // Lets add the user position to the map
        GeoObject user = new GeoObject(1000l);
        user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
        txtInfo.setText(mWorld.getLatitude()+" "+mWorld.getLongitude());
        user.setImageResource(R.drawable.flag);
        user.setName("Posición actual");
        mWorld.addBeyondarObject(user);


        BeyondarLocationManager.addGeoObjectLocationUpdate(user);

        LatLng userLocation = new LatLng(mWorld.getLatitude(), mWorld.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

        // We need to set the LocationManager to the BeyondarLocationManager.

    }
}
