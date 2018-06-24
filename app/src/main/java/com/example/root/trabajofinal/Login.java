package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.LoginListener;
import com.example.root.trabajofinal.Objetos.Usuario;

public class Login extends AppCompatActivity{

    public Usuario usuario;
    private GestorUsuarios gestorUsuarios;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=getApplicationContext();
        gestorUsuarios=GestorUsuarios.getInstance(context);

        Button btnAceptar=(Button)findViewById(R.id.btnAceptar);
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
                Log.e("","clickea");
                usuario=new Usuario(edUsername.getEditableText().toString(),
                                    edPassword.getEditableText().toString());
                gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());

                gestorUsuarios.login(usuario, new LoginListener() {
                    @Override
                    public void onResponseLoginListener(Usuario usuario) {
                        gestorUsuarios.setUsuario(usuario);
                        Log.e("","manda algo el login");
                        if(usuario==null){
                            Toast.makeText(getApplicationContext(),"No esta registrado",Toast.LENGTH_LONG).show();
                        }else if(usuario.isAdmin()){
                            Log.e("","siiii es admin");
                            Intent returnIntent=new Intent();
                            returnIntent.putExtra("resultado","Ok");
                            setResult(Activity.RESULT_OK,new Intent());
                            finish();

                        }else{
                            Log.e("","solo es usuario registrado");
                            Intent returnIntent=new Intent();
                            returnIntent.putExtra("resultado","Ok");
                            setResult(Activity.RESULT_OK,new Intent());
                            finish();
                        }
                    }
                });

            }
        });


    }

    /*
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
    */
}
