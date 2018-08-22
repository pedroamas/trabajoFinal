package com.example.root.trabajofinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
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
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
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
import com.google.android.gms.maps.model.MarkerOptions;

public class VistaSatelital extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        View.OnClickListener, OnMapReadyCallback {

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
    private LocationListener listener;
    private LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);

            } else {
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                    finish();
                } else {
                    cargarMapa();
                }
            }
        } else {
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();

            } else {
                cargarMapa();
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
                        finish();
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

    private void cargarMapa() {
        Button myLocationButton = (Button) findViewById(R.id.myLocationButton);
        myLocationButton.setVisibility(View.VISIBLE);
        myLocationButton.setOnClickListener(this);
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

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        BeyondarLocationManager
                .setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
        gestorPuntos.getPuntos();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitud = String.valueOf(location.getLongitude());
                Log.d("latitude", latitude);
                Log.d("longitud", longitud);
                double difLat=latitudUser-location.getLatitude();
                double difLon=longitudUser-location.getLongitude();
                Log.d("difLat", "dif lat "+valorAbsolutoNumero(difLat));
                Log.d("difLon", "dif lon "+valorAbsolutoNumero(difLon));
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


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



        //noinspection MissingPermission
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 0, listener);}
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, listener);
        }

        /*
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    //startActivity(getIntent());
                    //finish();
                    return;
                }
            }
            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.e("Location updates",location.getLatitude()+" - "+location.getLongitude());
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

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {
                    AlertNoGps();
                }
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.e("Posicion","latitud: "+loc.getLatitude());
            Log.e("Posicion","longitud: "+loc.getLongitude());
            latitudUser=loc.getLatitude();
            longitudUser=loc.getLongitude();
        }catch (Exception e){}*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // To get the GeoObject that owns the marker we use the following
        // method:
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

        mMap.setOnMarkerClickListener(this);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGoogleMapPlugin.getLatLng(), 15));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);


        //BeyondarLocationManager.addWorldLocationUpdate(mWorld);
        // Lets add the user position to the map
        user = new GeoObject(1000l);
        user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
        user.setImageResource(R.drawable.flag);
        user.setName("Posición actual");
        mWorld.addBeyondarObject(user);


        //BeyondarLocationManager.addGeoObjectLocationUpdate(user);

        LatLng userLocation = new LatLng(mWorld.getLatitude(), mWorld.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

        // We need to set the LocationManager to the BeyondarLocationManager.

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    cargarMapa();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private static double valorAbsolutoNumero(double num){
        return num>=0?num:-num;
    }

}
