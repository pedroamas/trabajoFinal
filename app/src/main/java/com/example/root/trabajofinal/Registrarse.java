package com.example.root.trabajofinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registrarse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        Button btnRegistrarse=(Button)findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edNombre=(EditText)findViewById(R.id.edNombre);
                EditText edApellido=(EditText)findViewById(R.id.edApellido);
                EditText edEmail=(EditText)findViewById(R.id.edEmail);
                EditText edContrasena=(EditText)findViewById(R.id.edContrasena);
                EditText edConfContrasena=(EditText)findViewById(R.id.edConfContrasena);
                EditText edUsername=(EditText)findViewById(R.id.edUsername);
                if(edNombre.getEditableText().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingrese el nombre",Toast.LENGTH_LONG).show();
                }else if(edApellido.getText().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingrese el apellido",Toast.LENGTH_LONG).show();
                }else if(edEmail.getText().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingrese el email",Toast.LENGTH_LONG).show();
                }else if(edContrasena.getText().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingrese el contraseña",Toast.LENGTH_LONG).show();
                }else if(edConfContrasena.getText().equals("")){
                    Toast.makeText(getApplicationContext(),"Confirme la contraseña",Toast.LENGTH_LONG).show();
                }else if(edUsername.getText().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingrese el username",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
