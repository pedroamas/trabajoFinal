package com.example.root.trabajofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GestorUsuarios gestorUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gestorUsuarios=GestorUsuarios.getGestorUsuarios(this);
        Usuario usuario=gestorUsuarios.getUsuario();
        Button btnIniciarSesion=(Button)findViewById(R.id.btnIniciarSesion);
        if(usuario!=null){
            btnIniciarSesion.setText("Cerrar sesión");
            btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gestorUsuarios.cerrarSesion();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            });
            if(usuario.isAdmin()){
                Toast.makeText(this,"Es administrador",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Usuario registrado",Toast.LENGTH_LONG).show();
            }
        }else {
            btnIniciarSesion.setText("Iniciar sesión");
            btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                }
            });
        }

        Button btnActualizar=(Button)findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.actualizarPuntos();
            }
        });

        Button btnLogMostrar=(Button)findViewById(R.id.btnLogMostrar);
        btnLogMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.logMostrarPuntos();
            }
        });
        Button btnRealidadAumentada=(Button)findViewById(R.id.btnRealidadAumentada);
        btnRealidadAumentada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RealidadAumentada.class);
                startActivity(intent);
            }
        });
        Button btnVistaSatelital=(Button)findViewById(R.id.btnVistaSatelital);
        btnVistaSatelital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VistaSatelital.class);
                startActivity(intent);
            }
        });
        Button btnBorrarPuntos=(Button)findViewById(R.id.btnBorrarPuntos);
        btnBorrarPuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorBD gestorBD=GestorBD.getGestorBD(getApplicationContext());
                gestorBD.borrarPuntos();
            }
        });
        Button btnLista=(Button)findViewById(R.id.btnLista);
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListaMaterialDesign.class);
                startActivity(intent);
            }
        });

        Button btnProbar=(Button)findViewById(R.id.btnProbar);
        btnProbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuAdmin.class);
                startActivity(intent);
            }
        });
        Button btnEnviarImagen=(Button)findViewById(R.id.btnEnviarImagen);
        btnEnviarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button btnMalDescargados=(Button)findViewById(R.id.btnMalDescargados);
        btnMalDescargados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.puntosMalDescargados();

            }
        });


    }

}
