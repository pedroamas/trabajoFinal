package com.example.root.trabajofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuEditarPunto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_editar_punto);
        Button btnEditarInfo=(Button)findViewById(R.id.btnEditarInfo);
        btnEditarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditarPunto.class);
                startActivity(intent);
            }
        });

        Button btnEditarMultimedia=(Button)findViewById(R.id.btnEditarMultimedia);
        btnEditarMultimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditarMultimedia.class);
                startActivity(intent);
            }
        });
    }
}
