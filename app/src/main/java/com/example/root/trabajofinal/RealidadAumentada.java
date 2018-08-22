package com.example.root.trabajofinal;

import android.Manifest;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Objetos.Punto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RealidadAumentada extends FragmentActivity implements

        OnClickBeyondarObjectListener
{

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private Button mShowMap;
    private GestorPuntos gestorPuntos;
    private Context context;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 3;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
    AlertDialog alert = null;
    private boolean primeraVez=true;
    private double latitudUser=0;
    private double longitudUser=0;
    private GeoObject user;
    private Location loc;

    private LocationListener listener;
    private LocationManager locationManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context=getApplicationContext();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                //Si alguno de los permisos no esta concedido lo solicita
                ActivityCompat.requestPermissions(this, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);

            }else{
                controlGPS();
            }
        }else {
            controlGPS();
        }




    }

    private void controlGPS(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);

            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                    finish();
                } else {
                    permisionGranted();
                }
            }
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();

            } else {
                permisionGranted();
            }
        }
    }
    public void permisionGranted(){

        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            AlertNoGps();

        }
        else {
            try {
            gestorPuntos = GestorPuntos.getInstance(context);
            setContentView(R.layout.camera_with_google_maps);

            user = new GeoObject(1000l);
            //user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
            user.setImageResource(R.drawable.flag);
            user.setName("Posición actual");
            mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                    R.id.beyondarFragment);
                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        String latitude = String.valueOf(location.getLatitude());
                        String longitud = String.valueOf(location.getLongitude());
                        Log.d("latitude", latitude);
                        Log.d("longitud", longitud);
                        // Called when a new location is found by the network location provider.
                        Log.e("Location updates",location.getLatitude()+" - "+location.getLongitude());
                        latitudUser=location.getLatitude();
                        longitudUser=location.getLongitude();
                        if(mWorld!=null&& user!=null){
                            user.setGeoPosition(latitudUser, longitudUser);
                            mWorld.addBeyondarObject(user);
                            mWorld.setLocation(location);
                        }

                        TextView txtLocalizacion=(TextView)findViewById(R.id.txtLocalizacion);
                        txtLocalizacion.setText(location.getLatitude()+" - "+location.getLongitude());


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
                            // Called when a new location is found by the network location provider.
                            Log.e("Location updates",location.getLatitude()+" - "+location.getLongitude());
                            latitudUser=location.getLatitude();
                            longitudUser=location.getLongitude();
                            if(mWorld!=null&& user!=null){
                                user.setGeoPosition(latitudUser, longitudUser);
                                mWorld.addBeyondarObject(user);
                                mWorld.setLocation(location);
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
                }catch (Exception e){
                    e.printStackTrace();
                }*/

            mShowMap = (Button) findViewById(R.id.showMapButton);
            mShowMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == mShowMap) {
                        Intent intent = new Intent(context, VistaSatelital.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            // We create the world and fill it
            mWorld = gestorPuntos.generarMundo(this);

            mBeyondarFragment.setWorld(mWorld);

            //enableLocationUpdates();          //habilita la localizacion
            mBeyondarFragment.setOnClickBeyondarObjectListener(this);

            //BeyondarLocationManager.addWorldLocationUpdate(mWorld);
            // Lets add the user position to the map

            //mWorld.addBeyondarObject(user);

            //BeyondarLocationManager.addGeoObjectLocationUpdate(user);
        }catch (Exception e){
                TextView txtAviso=new TextView(context);
                FrameLayout frameLayout=(FrameLayout)findViewById(R.id.content);
                txtAviso.setText("Es posible que su dispositivo no tenga giroscopio");
                frameLayout.addView(txtAviso);

            }
        }

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
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        if (beyondarObjects.size() > 0) {
            GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);
            Punto punto= gestorPuntos.getPunto(beyondarObjects.get(0).getName());
            Intent intent = new Intent(context, Detalle.class);
            intent.putExtra(Detalle.EXTRA_POSITION, punto.getId());
            startActivity(intent);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MULTIPLE_PERMISSIONS_REQUEST_CODE: {
                //Verifica si todos los permisos se aceptaron o no
                if (validatePermissions(grantResults)) {
                    //Si todos los permisos fueron aceptados continua con el flujo normal
                    permisionGranted();
                } else {
                    //Si algun permiso fue rechazado no se puede continuar
                    Toast.makeText(getApplicationContext(),"Se necesitan todos los permisos",Toast.LENGTH_LONG).show();
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean validatePermissions(int[] grantResults) {
        boolean allGranted = false;
        //Revisa cada uno de los permisos y si estos fueron aceptados o no
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                //Si todos los permisos fueron aceptados retorna true
                allGranted = true;
            } else {
                //Si algun permiso no fue aceptado retorna false
                allGranted = false;
                break;
            }
        }
        return allGranted;
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

    private static double valorAbsolutoNumero(double num){
        return num>=0?num:-num;
    }
}
