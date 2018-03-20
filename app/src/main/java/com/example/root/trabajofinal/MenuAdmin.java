package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.root.trabajofinal.Gestores.GestorDePuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Objetos.Punto;

import java.util.ArrayList;

public class MenuAdmin extends AppCompatActivity {

    private static int SUBIR_PUNTO =150;
    private static int ELIMINAR_PUNTO =250;
    private static int EDITAR_PUNTO =350;
    private ProgressDialog progress;

    private GestorUsuarios gestorUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestorUsuarios=GestorUsuarios.getGestorUsuarios(getApplicationContext());
        setContentView(R.layout.activity_menu_admin);
        FloatingActionButton btnCerrarSesion=(FloatingActionButton) findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestorUsuarios.cerrarSesion();
                Intent returnIntent=new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        FloatingActionButton btnActualizar = (FloatingActionButton) findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(MenuAdmin.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorDePuntos gestorDePuntos = GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                    }
                });
            }
        });
        Button btnEditarPunto=(Button)findViewById(R.id.btnEditarPunto);
        btnEditarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MenuEditarPunto.class);
                startActivity(intent);
            }
        });

        Button btnSubirPunto=(Button)findViewById(R.id.btnSubirPunto);
        btnSubirPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SubirPuntoAdmin.class);
                startActivityForResult(intent,SUBIR_PUNTO);
            }
        });
        Button btnEliminarPunto=(Button)findViewById(R.id.btnEliminarPunto);
        btnEliminarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),EliminarPunto.class);
                startActivity(intent);
            }
        });

        Button btnEliminarComentarios=(Button)findViewById(R.id.btnEliminarComentarios);
        btnEliminarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),EliminarComentarios.class);
                startActivity(intent);
            }
        });


        Button btnValidarInfo=(Button)findViewById(R.id.btnValidarInfo);
        btnValidarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ValidarInfoUsuario.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==SUBIR_PUNTO || requestCode==ELIMINAR_PUNTO||
                    requestCode==EDITAR_PUNTO){
                final ProgressDialog progress;
                progress = new ProgressDialog(MenuAdmin.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorDePuntos gestorDePuntos = GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                    }
                });

            }
        }
    }
}
