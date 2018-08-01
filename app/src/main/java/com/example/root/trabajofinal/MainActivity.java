package com.example.root.trabajofinal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Objetos.IndiceRtree;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private static int INICIAR_SESION = 100;
    private static int MENU_ADMIN = 200;
    private GestorUsuarios gestorUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestorUsuarios = GestorUsuarios.getInstance(this);
        Usuario usuario = gestorUsuarios.getUsuario();

        progress = new ProgressDialog(this);
        progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        progress.setCanceledOnTouchOutside(true);
        progress.setCancelable(false);
        progress.setTitle("Actualizando");
        progress.setMessage("Espere un momento...");
        progress.show();



        GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
       gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
            @Override
            public void onResponseActualizarPunto(ArrayList<Punto> puntos) {


                progress.dismiss();

            }
        });
        FloatingActionButton btnIniciarSesion = (FloatingActionButton) findViewById(R.id.btnIniciarSesion);
        FloatingActionButton btnCerrarSesion = (FloatingActionButton) findViewById(R.id.btnCerrarSesion);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(intent,INICIAR_SESION);
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestorUsuarios.cerrarSesion();
                startActivity(getIntent());
                finish();
            }
        });
        visibilidadBotones(usuario);


        Button btnAdministracion=(Button)findViewById(R.id.btnAdministracion);
        btnAdministracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuAdmin.class);
                startActivityForResult(intent,MENU_ADMIN);
            }
        });
        FloatingActionButton btnActualizar = (FloatingActionButton) findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                    }
                });
            }
        });


        Button btnRealidadAumentada = (Button) findViewById(R.id.btnRealidadAumentada);
        btnRealidadAumentada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RealidadAumentada.class);
                startActivity(intent);
            }
        });
        Button btnVistaSatelital = (Button) findViewById(R.id.btnVistaSatelital);
        btnVistaSatelital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VistaSatelital.class);
                startActivity(intent);
            }
        });
        Button btnLista = (Button) findViewById(R.id.btnLista);
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListaMaterialDesign.class);
                startActivity(intent);
            }
        });


        Button btnPuntosCercanos = (Button) findViewById(R.id.btnPuntosCercanos);
        btnPuntosCercanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PuntosMasCercanos.class);
                startActivity(intent);

                //indiceRtree.consultar(-33.2913857,90,-66.3386996,90);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == INICIAR_SESION||requestCode==MENU_ADMIN)  {
            Log.e("","2");
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Usuario usuario = gestorUsuarios.getUsuario();
                FloatingActionButton btnIniciarSesion = (FloatingActionButton) findViewById(R.id.btnIniciarSesion);
                FloatingActionButton btnCerrarSesion = (FloatingActionButton) findViewById(R.id.btnCerrarSesion);

                btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivityForResult(intent,INICIAR_SESION);
                    }
                });

                btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gestorUsuarios.cerrarSesion();
                        visibilidadBotones(null);
                    }
                });

                visibilidadBotones(usuario);

            }
        }
    }

    private void visibilidadBotones(Usuario usuario){
        FloatingActionButton btnIniciarSesion = (FloatingActionButton) findViewById(R.id.btnIniciarSesion);
        FloatingActionButton btnCerrarSesion = (FloatingActionButton) findViewById(R.id.btnCerrarSesion);
        Button btnAdministracion=(Button)findViewById(R.id.btnAdministracion);
        TextView txtUsuario=(TextView)findViewById(R.id.txtUsuario);
        if(usuario==null) {
            btnIniciarSesion.setVisibility(View.VISIBLE);
            btnCerrarSesion.setVisibility(View.INVISIBLE);
            btnAdministracion.setVisibility(View.INVISIBLE);
            txtUsuario.setVisibility(View.INVISIBLE);

        }
        else{
            btnIniciarSesion.setVisibility(View.INVISIBLE);
            btnCerrarSesion.setVisibility(View.VISIBLE);
            btnAdministracion.setVisibility(View.INVISIBLE);
            txtUsuario.setText(usuario.getUsername());
            txtUsuario.setVisibility(View.VISIBLE);
            if(usuario.isAdmin()){
                btnAdministracion.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), MenuAdmin.class);
                startActivityForResult(intent,MENU_ADMIN);
            }
        }
    }




}
