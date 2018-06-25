package com.example.root.trabajofinal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
                permisionGranted();
            }
        }else {
            permisionGranted();
        }




    }

    public void permisionGranted(){

        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();

        }else {
            try {
            gestorPuntos = GestorPuntos.getInstance(context);
            BeyondarLocationManager
                    .setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));

            setContentView(R.layout.camera_with_google_maps);

            mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                    R.id.beyondarFragment);

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

            BeyondarLocationManager.addWorldLocationUpdate(mWorld);
            // Lets add the user position to the map
            GeoObject user = new GeoObject(1000l);
            user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());
            user.setImageResource(R.drawable.flag);
            user.setName("Posición actual");
            mWorld.addBeyondarObject(user);

            BeyondarLocationManager.addGeoObjectLocationUpdate(user);
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
                    }
                });
        alert = builder.create();
        alert.show();
    }
}
