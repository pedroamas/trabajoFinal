package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.example.root.trabajofinal.Gestores.GestorDePuntos;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Objetos.Punto;

import java.util.ArrayList;

public class EliminarPunto extends AppCompatActivity {

    private static int ELIMINAR_PUNTO=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_material_design);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ListaMaterialDesign.Adapter adapter = new ListaMaterialDesign.Adapter(getSupportFragmentManager());
        adapter.addFragment(new ContenidoListaEliminar(), "List");
        viewPager.setAdapter(adapter);
    }

    private void openActivity(Class<? extends Activity> ActivityClass) {
        Intent intent = new Intent(this, ActivityClass);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==ELIMINAR_PUNTO ){
                final ProgressDialog progress;
                progress = new ProgressDialog(EliminarPunto.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorDePuntos gestorDePuntos = GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                });

            }
        }
    }
}
