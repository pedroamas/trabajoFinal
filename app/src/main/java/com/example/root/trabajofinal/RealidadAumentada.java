package com.example.root.trabajofinal;

import android.Manifest;
import android.support.v7.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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

public class RealidadAumentada extends AppCompatActivity implements

        OnClickBeyondarObjectListener
{

    private static int CODE_GPS=8000;
    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private Button mShowMap;
    private GestorPuntos gestorPuntos;
    private Context context;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 3;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
    AlertDialog a = null;
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

        setContentView(R.layout.camera_with_google_maps);
        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.beyondarFragment);
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);

        this.mBeyondarFragment.setMaxDistanceToRender(100);
        this.mBeyondarFragment.setPullCloserDistance(0);

        user = new GeoObject(1000l);
        user.setImageResource(R.drawable.flag);
        user.setName("Posición actual");

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

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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

        cargarPuntos();

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
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(RealidadAumentada.this);

        builder.setMessage(Html.fromHtml("<font color='#000000'>El sistema GPS esta desactivado, ¿Desea activarlo?</font>"));
        builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),CODE_GPS);

            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })

                .setIcon(R.drawable.ic_dialog_alert);

        a=builder.create();
        a.show();
        Button bq = a.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void cargarPuntos(){
        gestorPuntos = GestorPuntos.getInstance(context);
        try{
        mWorld = gestorPuntos.generarMundo(this);
        mBeyondarFragment.setWorld(mWorld);
        }catch (Exception e){
            e.printStackTrace();
            TextView txtAviso=new TextView(context);
            FrameLayout frameLayout=(FrameLayout)findViewById(R.id.content);
            txtAviso.setText("Su dispositivo debe poseer acelerómetro y sensor magnético");
            frameLayout.addView(txtAviso);
        }
    }
}
