package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.root.trabajofinal.Gestores.GestorDePuntos;

import java.util.ArrayList;

public class RealidadAumentada extends FragmentActivity implements

        OnClickBeyondarObjectListener
{

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private Button mShowMap;
    private GestorDePuntos gestorDePuntos;
    private Context context;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context=getApplicationContext();
        // Ocultar titulo de la ventana
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        gestorDePuntos=GestorDePuntos.getGestorDePuntos(context);
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
            }});

        // We create the world and fill it
        mWorld = gestorDePuntos.generarMundo(this);

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
            GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(context);
            Punto punto=gestorDePuntos.getPunto(beyondarObjects.get(0).getName());
            Intent intent = new Intent(context, Detalle.class);
            intent.putExtra(Detalle.EXTRA_POSITION, punto.getId());
            startActivity(intent);

        }
    }
}
