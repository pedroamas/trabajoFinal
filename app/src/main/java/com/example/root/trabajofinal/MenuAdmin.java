package com.example.root.trabajofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuAdmin extends AppCompatActivity {

    private GestorUsuarios gestorUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestorUsuarios=GestorUsuarios.getGestorUsuarios(getApplicationContext());
        setContentView(R.layout.activity_menu_admin);
        Button btnCerrarSesion=(Button)findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestorUsuarios.cerrarSesion();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
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
                startActivity(intent);
            }
        });


    }
}
