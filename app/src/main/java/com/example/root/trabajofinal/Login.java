package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.LoginListener;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public Usuario usuario;
    private GestorUsuarios gestorUsuarios;
    private Context context;
    private NavigationView navigationView;
    private ProgressDialog progress;
    private static int PANTALLA_CUALQUIERA = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barra_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout contenido=(LinearLayout) findViewById(R.id.contenido);
        contenido.addView(getLayoutInflater().inflate(R.layout.activity_login, null));
        configurarMenu();

        //fin menu

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
                if(usuario.getUsername().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingrese el nombre de usuario",Toast.LENGTH_LONG).show();
                    return;
                }else if(usuario.getContrasena().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingrese la contraseña",Toast.LENGTH_LONG).show();
                    return;
                }
                progress = new ProgressDialog(Login.this);
                progress.setTitle("Verificando");
                progress.setCancelable(false);
                progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                progress.setMessage("Espere un momento...");
                progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                progress.show();
                gestorUsuarios.login(usuario, new LoginListener() {
                    @Override
                    public void onResponseLoginListener(Usuario usuario) {
                        gestorUsuarios.setUsuario(usuario);
                        progress.dismiss();
                        Log.e("","manda algo el login");
                        if(usuario==null){
                            Toast.makeText(getApplicationContext(),"No está registrado",Toast.LENGTH_LONG).show();
                        }else {
                            if (usuario.isAdmin()) {
                                Log.e("", "siiii es admin");
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("resultado", "Ok");
                                setResult(Activity.RESULT_OK, new Intent());
                                finish();

                            } else {
                                Log.e("", "solo es usuario registrado");
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("resultado", "Ok");
                                setResult(Activity.RESULT_OK, new Intent());
                                finish();
                            }

                        }
                    }
                });

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if(progress!=null){
            progress.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnActualizar) {

            progress = new ProgressDialog(Login.this);
            progress.setTitle("Actualizando");
            progress.setCancelable(false);
            progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            progress.setMessage("Espere un momento...");
            progress.show();
            GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
            gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                @Override
                public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                    progress.dismiss();
                }
            });
            return true;
        }else if(id == R.id.navRealidadAumentada){

            Toast.makeText(getApplicationContext(),"RA",Toast.LENGTH_LONG).show();
            return true;
        }

        else {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.navRealidadAumentada:
                intent = new Intent(getApplicationContext(), RealidadAumentada.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navVistaSatelital:
                intent = new Intent(getApplicationContext(), VistaSatelital.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navLista:
                intent = new Intent(getApplicationContext(), ListaMaterialDesign.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navPuntosCercanos:
                intent = new Intent(getApplicationContext(), PuntosCercanos.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navCerrarSesion:
                GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
                gestorUsuarios.cerrarSesion();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navAgregarPunto:
                intent = new Intent(getApplicationContext(), SubirPuntoAdmin.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navEdicion:
                intent = new Intent(getApplicationContext(), MenuAdmin.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.itm_iniciar_sesion:
                intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    public void configurarMenu(){
        GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
        Usuario usuario=gestorUsuarios.getUsuario();
        TextView txtUsername=(TextView)navigationView.getHeaderView(0).findViewById(R.id.txtUsernameMain);
        if(navigationView!=null) {
            Menu navMenu = navigationView.getMenu();
            if (usuario == null) {
                navMenu.getItem(0).setVisible(true);
                navMenu.getItem(1).setVisible(false);
                navMenu.getItem(3).setVisible(false);
            }else if(usuario.isAdmin()){
                txtUsername.setText(usuario.getUsername());
                navMenu.getItem(0).setVisible(false);
                navMenu.getItem(1).setVisible(true);
                navMenu.getItem(3).setVisible(true);
            }else{
                txtUsername.setText(usuario.getUsername());
                navMenu.getItem(0).setVisible(false);
                navMenu.getItem(1).setVisible(true);
                navMenu.getItem(3).setVisible(false);
            }
        }
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
