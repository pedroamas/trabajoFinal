package com.example.root.trabajofinal;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.RegistrarListener;
import com.example.root.trabajofinal.Objetos.Usuario;

public class Registrarse extends AppCompatActivity {

    private Context context;
    private GestorUsuarios gestorUsuarios;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        context=getApplicationContext();
        gestorUsuarios=GestorUsuarios.getGestorUsuarios(context);

        Button btnRegistrarse=(Button)findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre=((EditText)findViewById(R.id.edNombre)).getEditableText().toString();
                String apellido=((EditText)findViewById(R.id.edApellido)).getEditableText().toString();
                String email=((EditText)findViewById(R.id.edEmail)).getEditableText().toString();
                String username=((EditText)findViewById(R.id.edUsername)).getEditableText().toString();
                String contrasena=((EditText)findViewById(R.id.edContrasena)).getEditableText().toString();
                String confContrasena=((EditText)findViewById(R.id.edConfContrasena)).getEditableText().toString();

                if(nombre.equals("")){
                    Toast.makeText(context,"Ingrese el nombre",Toast.LENGTH_LONG).show();
                }else if(apellido.equals("")){
                    Toast.makeText(context,"Ingrese el apellido",Toast.LENGTH_LONG).show();
                }else if(email.equals("")){
                    Toast.makeText(context,"Ingrese el email",Toast.LENGTH_LONG).show();
                }else if(username.equals("")){
                    Toast.makeText(context,"Ingrese el username",Toast.LENGTH_LONG).show();
                }else if(contrasena.equals("")){
                    Toast.makeText(context,"Ingrese el contraseña",Toast.LENGTH_LONG).show();
                }else if(confContrasena.equals("")){
                    Toast.makeText(context,"Confirme la contraseña",Toast.LENGTH_LONG).show();
                }else if(!confContrasena.equals(contrasena)){
                    Toast.makeText(context,"La confirmación la contraseña es incorrecta",Toast.LENGTH_LONG).show();
                }else{
                    Usuario usuario=new Usuario(
                            nombre,
                            apellido,
                            username,
                            contrasena,
                            email
                    );
                    gestorUsuarios.registrar(usuario, new RegistrarListener() {
                        @Override
                        public void onResponseRegistrarListener(String response) {
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            Log.e("",response);
                            if(response.equals("0")){
                                Toast.makeText(context,"El username ya se encuentra registrado",Toast.LENGTH_LONG).show();
                            }else  if(response.equals("-1")){
                                Toast.makeText(context,"Error de conexión",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(context,"Usuario registrado corretamente",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }

            }
        });
    }
}
