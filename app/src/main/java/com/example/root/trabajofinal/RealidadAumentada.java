package com.example.root.trabajofinal;

import android.Manifest;
import android.content.Context;
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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context=getApplicationContext();


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
                Log.e("","en 1");
                startActivity(getIntent());
                finish();
                return;
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},200);
                Log.e("","en 2");
                startActivity(getIntent());
                finish();
                return;
            }
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Ocultar titulo de la ventana
            requestWindowFeature(Window.FEATURE_NO_TITLE);
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
}
