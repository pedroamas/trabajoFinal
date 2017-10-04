package com.example.root.trabajofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public class Login extends AppCompatActivity implements IRespuesta{

    public static String TAG="<Web service>";
    public Usuario usuario;
    private GestorUsuarios gestorUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnAceptar=(Button)findViewById(R.id.btnAceptar);

        Log.e("ASD","ENTRAS    ");
        gestorUsuarios=GestorUsuarios.getGestorUsuarios(getApplicationContext());
        gestorUsuarios.registerView(this);
        Log.e("ASD","ENTRAS    2");
        Button btnRegistrarse=(Button)findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Registrarse.class);
                startActivity(intent);
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edUsername=(EditText)findViewById(R.id.edUsername);
                EditText edPassword=(EditText)findViewById(R.id.edPassword);
                usuario=new Usuario(edUsername.getEditableText().toString(),
                                    edPassword.getEditableText().toString());
                GestorUsuarios gestorUsuarios=GestorUsuarios.getGestorUsuarios(getApplicationContext());

                gestorUsuarios.login(usuario);

            }
        });
    }

    @Override
    public void onResponse(int idMj, TipoMensaje tipoMj, Object object) {
        if(idMj==1){
            if(tipoMj==TipoMensaje.USUARIO){
                this.usuario=(Usuario)object;
                if(usuario==null){
                    Toast.makeText(getApplicationContext(),"No esta registrado",Toast.LENGTH_LONG).show();
                }else if(usuario.isAdmin()){
                    startActivity(new Intent(getApplicationContext(), MenuAdmin.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        }
    }
}
