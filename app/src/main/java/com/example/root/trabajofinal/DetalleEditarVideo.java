package com.example.root.trabajofinal;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Gestores.GestorWebService;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.EditarMultimediaListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;
import com.example.root.trabajofinal.TiposEnumerados.TipoMultimedia;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetalleEditarVideo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Context context;
    private int idVideo;
    private String titulo;
    private String descripcion;
    private String path;
    private Date fechaCaptura;
    private static final String CERO = "0";
    private static final String BARRA = "/";
    private Multimedia multimedia;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    private NavigationView navigationView;
    private ProgressDialog progress;
    private static int PANTALLA_CUALQUIERA = 3000;

    public static final String API_KEY = "AIzaSyCe6tORd9Ch4lx-9Ku5SQ476uS9OtZYsWA";
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youTubePlayerFragment;

    static final int DATE_DIALOG_ID = 999;
    SimpleDateFormat dt2;

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
        contenido.addView(getLayoutInflater().inflate(R.layout.activity_editar_video, null));
        configurarMenu();

        Button btnVisualizar=(Button)findViewById(R.id.btnVisualizar);
        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarVideo();
            }
        });
        context=getApplicationContext();
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        idVideo =getIntent().getIntExtra("id", 0);
        EditText edFechaCaptura=(EditText)findViewById(R.id.edFechaCaptura);
        edFechaCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        });
        edFechaCaptura.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    obtenerFecha();
                }
            }
        });
        llenarCampos();
        Button btnEditarVideo=(Button)findViewById(R.id.btnEditarVideo);
        btnEditarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(DetalleEditarVideo.this);
                progress.setTitle("Procesando");
                progress.setMessage("Espere un momento...");
                progress.show();
                titulo=((TextView)findViewById(R.id.edTitulo)).getEditableText().toString();
                descripcion=((TextView)findViewById(R.id.edDescripcion)).getEditableText().toString();
                path=((TextView)findViewById(R.id.edPath)).getEditableText().toString();
                path=path.replace("https://www.youtube.com/watch?v=","");
                path=path.replace("https://youtu.be/","");
                try{
                    EditText edFechaCaptura=(EditText)findViewById(R.id.edFechaCaptura);
                    fechaCaptura=dt2.parse(edFechaCaptura.toString());
                    Log.e("","captura "+fechaCaptura);
                }catch (Exception e){
                    fechaCaptura=null;
                }

                Multimedia video=new Multimedia(
                        idVideo,
                        descripcion,
                        path,
                        titulo,
                        fechaCaptura,
                        null,
                        0,
                        TipoMultimedia.video
                );
                if(video.getPath().isEmpty()){
                    progress.dismiss();
                    Toast.makeText(context,"El link del video es obligatorio",Toast.LENGTH_LONG).show();
                }else {
                    GestorWebService gestorWebService = GestorWebService.getInstance(context);
                    gestorWebService.editarVideo(video, new EditarMultimediaListener() {
                        @Override
                        public void onResponseEditarMultimedia(String response) {
                            Log.e("respuesta", response);
                            progress.dismiss();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    });

                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }


        }


    }


    private void visualizarVideo(){
        String pathYoutube=((EditText)findViewById(R.id.edPath)).getEditableText().toString();
        pathYoutube=pathYoutube.replace("https://www.youtube.com/watch?v=","");
        pathYoutube=pathYoutube.replace("https://youtu.be/","");
        final String pathVideo=pathYoutube;
        Log.e("pathvideo",pathVideo);
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtubeplayerfragment);
        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                try {
                    if (!wasRestored) {
                        youTubePlayer.cueVideo(pathVideo);
                    }

                } catch (Exception e) {}
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                } else {
                    String error = errorReason.toString();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;

                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);

                EditText edFechaCaptura=(EditText)findViewById(R.id.edFechaCaptura);
                edFechaCaptura.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

                try {
                    fechaCaptura=dt2.parse(edFechaCaptura.toString());
                    Log.e("fecha","captura "+fechaCaptura);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        },anio, mes, dia);

        recogerFecha.show();

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

            progress = new ProgressDialog(DetalleEditarVideo.this);
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
                finish();
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

    public void llenarCampos(){
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
        gestorMultimedia.getVideo(idVideo, new VideoListener() {
            @Override
            public void onResponseVideo(Multimedia multimedia1) {
                multimedia=multimedia1;
                if(multimedia1!=null) {
                    EditText edTitulo = (EditText) findViewById(R.id.edTitulo);
                    EditText edDescripcion = (EditText) findViewById(R.id.edDescripcion);
                    EditText edPath = (EditText) findViewById(R.id.edPath);
                    EditText edFechaCaptura = (EditText) findViewById(R.id.edFechaCaptura);
                    edTitulo.setText(multimedia1.getTitulo());
                    edDescripcion.setText(multimedia1.getDescripcion());
                    edPath.setText(multimedia1.getPath());
                    edFechaCaptura.setText(dt2.format(multimedia1.getFechaCaptura()));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
        Usuario usuario=gestorUsuarios.getUsuario();
        if(usuario==null){
            finish();
            return;
        }else if(!usuario.isAdmin()){
            finish();
            return;
        }
    }



}
