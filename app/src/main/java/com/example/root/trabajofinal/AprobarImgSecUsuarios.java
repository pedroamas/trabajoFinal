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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarEstadoImgListener;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AprobarImgSecUsuarios extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private ProgressDialog progress;
    private static int PANTALLA_CUALQUIERA = 3000;


    private Context context;
    public static final String EXTRA_POSITION = "id_imagen";
    private SimpleDateFormat dt1,dt2;
    private int idImagen;

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
        contenido.addView(getLayoutInflater().inflate(R.layout.activity_aprobar_img_sec_usuarios, null));
        configurarMenu();
        dt1=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        context=getApplicationContext();

        idImagen=getIntent().getIntExtra(EXTRA_POSITION,0);
        Log.e("idIMagene","id: "+idImagen);

        final GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(getApplicationContext());
        gestorMultimedia.getImagenConEstado(idImagen,0, new ImagenListener() {
            @Override
            public void onResponseImagen(final Multimedia multimedia) {
                LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content);

                if(multimedia!=null){

                    ImageView imgFoto=(ImageView)findViewById(R.id.imgFoto);
                    Glide.with(getApplicationContext()).load(multimedia.getPath())
                            .placeholder(R.drawable.ic_image_box)
                            .into(imgFoto);

                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(!multimedia.getTitulo().isEmpty()){
                        TextView txtTitulo=new TextView(getApplicationContext());
                        txtTitulo.setText(multimedia.getTitulo());
                        txtTitulo.setLayoutParams(params);
                        linearLayout.addView(txtTitulo);
                    }
                    if(!multimedia.getDescripcion().isEmpty()){
                        TextView txtDescripcion=new TextView(getApplicationContext());
                        txtDescripcion.setText("Descripción: "+multimedia.getDescripcion());
                        txtDescripcion.setLayoutParams(params);
                        linearLayout.addView(txtDescripcion);
                    }
                    if(multimedia.getFechaCaptura()!=null){
                        TextView txtCaptura=new TextView(getApplicationContext());
                        txtCaptura.setText("Fecha de captura: "+dt2.format(multimedia.getFechaCaptura()));
                        txtCaptura.setLayoutParams(params);
                        linearLayout.addView(txtCaptura);
                    }
                    if(multimedia.getFechaSubida()!=null){
                        TextView txtSubida=new TextView(getApplicationContext());
                        txtSubida.setText("Fecha de subida: "+dt2.format(multimedia.getFechaSubida()));
                        txtSubida.setLayoutParams(params);
                        linearLayout.addView(txtSubida);
                    }

                    if(multimedia.getUsername()!=null){
                        TextView txtUsername=new TextView(getApplicationContext());
                        txtUsername.setText("Gentileza de : "+multimedia.getUsername());
                        txtUsername.setLayoutParams(params);
                        linearLayout.addView(txtUsername);
                    }
                    if(multimedia.getTituloPunto()!=null){
                        TextView txtTituloPunto=new TextView(getApplicationContext());
                        txtTituloPunto.setText("Punto de referencia : "+multimedia.getTituloPunto());
                        txtTituloPunto.setLayoutParams(params);
                        linearLayout.addView(txtTituloPunto);
                    }

                    Button btnAprobar=(Button)findViewById(R.id.btnAprobar);
                    btnAprobar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progress = new ProgressDialog(AprobarImgSecUsuarios.this);
                            progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            progress.setCanceledOnTouchOutside(true);
                            progress.setCancelable(false);
                            progress.setTitle("Procesando");
                            progress.setMessage("Espere un momento...");
                            progress.show();
                            gestorMultimedia.setEstadoImagen(idImagen, 1, new ActualizarEstadoImgListener() {
                                @Override
                                public void onResponseActualizarEstadoImgListener(String response) {
                                    progress.dismiss();
                                    if (response.equals("Ok")){

                                        Intent returnIntent=new Intent();
                                        setResult(Activity.RESULT_OK,returnIntent);
                                        finish();
                                    }
                                }
                            });
                        }
                    });

                    Button btnRechazar=(Button)findViewById(R.id.btnRechazar);
                    btnRechazar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progress = new ProgressDialog(AprobarImgSecUsuarios.this);
                            progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            progress.setCanceledOnTouchOutside(true);
                            progress.setCancelable(false);
                            progress.setTitle("Procesando");
                            progress.setMessage("Espere un momento...");
                            progress.show();
                            gestorMultimedia.setEstadoImagen(idImagen, 2, new ActualizarEstadoImgListener() {
                                @Override
                                public void onResponseActualizarEstadoImgListener(String response) {
                                    Log.e("respuesta car estado",response);
                                    //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                                    progress.dismiss();
                                    if (response.equals("Ok")){
                                        Intent returnIntent=new Intent();
                                        setResult(Activity.RESULT_OK,returnIntent);
                                        finish();
                                    }


                                }
                            });
                        }
                    });

                }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progress!=null){
            progress.dismiss();
            progress=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(progress!=null){
            progress.dismiss();
            progress=null;
        }
        invalidateOptionsMenu();
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

            progress = new ProgressDialog(AprobarImgSecUsuarios.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
        Usuario usuario=gestorUsuarios.getUsuario();
        if(usuario==null){
            finish();
            return;
        }else if(!usuario.isAdmin()) {
            finish();
            return;
        }
    }

}