package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorComentarios;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.SetComentarioListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VerVideo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_POSITION = "id_video";
    private int idVideo=-1;
    SimpleDateFormat dt1,dt2;
    private Context context;
    GestorComentarios gestorComentarios;
    VerVideo.AdapterComentarios adaptador;
    private VerVideo verVideo;
    public static final String API_KEY = "AIzaSyCe6tORd9Ch4lx-9Ku5SQ476uS9OtZYsWA";
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youTubePlayerFragment;

    private NavigationView navigationView;
    private ProgressDialog progress;
    private static int PANTALLA_CUALQUIERA = 3000;

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
        contenido.addView(getLayoutInflater().inflate(R.layout.activity_ver_video, null));
        configurarMenu();

        context = getApplicationContext();
        verVideo = this;
        dt1 = new SimpleDateFormat("dd-MM-yyyy");
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        idVideo = getIntent().getIntExtra(EXTRA_POSITION, 0);
        gestorComentarios = GestorComentarios.obtenerGestorComentarios(context);

        gestorComentarios.getComentarios(idVideo, new GetComentariosListener() {
            @Override
            public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                try {
                    ListView lista;
                    lista = (ListView) findViewById(R.id.listaComentarios);
                    adaptador = new VerVideo.AdapterComentarios(verVideo, comentarios);
                    lista.setAdapter(adaptador);
                }catch (Exception e){}

            }
        });
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(getApplicationContext());
        gestorMultimedia.getVideo(idVideo, new VideoListener() {
            @Override
            public void onResponseVideo(final Multimedia video) {

                youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                        .findFragmentById(R.id.youtubeplayerfragment);
                youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                        try {
                            if (!wasRestored) {
                                youTubePlayer.cueVideo(video.getPath()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
                            }


                        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content);
                        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(!video.getTitulo().isEmpty()){
                            TextView txtTitulo=new TextView(getApplicationContext());
                            txtTitulo.setText(video.getTitulo());
                            txtTitulo.setLayoutParams(params);
                            linearLayout.addView(txtTitulo);
                            txtTitulo.setTypeface(null, Typeface.BOLD);
                            txtTitulo.setTextSize(18);
                            txtTitulo.setTextColor(Color.BLACK);
                        }
                        if(!video.getDescripcion().isEmpty()){
                            TextView txtDescripcion=new TextView(getApplicationContext());
                            txtDescripcion.setText(video.getDescripcion());
                            txtDescripcion.setLayoutParams(params);
                            linearLayout.addView(txtDescripcion);
                            txtDescripcion.setTextColor(Color.BLACK);
                            txtDescripcion.setTextSize(14);
                        }
                        try {
                            if (video.getFechaCaptura() != null && video.getFechaCaptura().after(dt2.parse("01/01/1800"))) {
                                TextView txtCaptura = new TextView(getApplicationContext());
                                txtCaptura.setText("Fecha de captura: " + dt2.format(video.getFechaCaptura()));
                                txtCaptura.setLayoutParams(params);
                                linearLayout.addView(txtCaptura);
                            }
                        }catch (Exception e){}
                        if(video.getFechaSubida()!=null){
                            TextView txtSubida=new TextView(getApplicationContext());
                            txtSubida.setText("Fecha de subida: "+dt2.format(video.getFechaSubida()));
                            txtSubida.setLayoutParams(params);
                            txtSubida.setPadding(0,5,0,5);
                            linearLayout.addView(txtSubida);

                        }

                        if(video.getUsername()!=null){
                            TextView txtUsername=new TextView(getApplicationContext());
                            txtUsername.setText("Gentileza de : "+video.getUsername());
                            txtUsername.setLayoutParams(params);
                            linearLayout.addView(txtUsername);
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
        });
        try {
            EditText edComentario = (EditText) findViewById(R.id.edComentario);
            edComentario.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    return false;
                }
            });

            Button btnComentar = (Button) findViewById(R.id.btnComentar);
            btnComentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(context);
                    String texto = ((EditText) findViewById(R.id.edComentario)).getEditableText().toString();
                    if (texto.equals("")) {
                        Toast.makeText(context, "Ingrese el comentario", Toast.LENGTH_LONG).show();
                    } else if (gestorUsuarios.getUsuario() == null) {
                        Toast.makeText(context, "Tiene que estar registrado", Toast.LENGTH_LONG).show();
                    } else {

                        Comentario comentario = new Comentario(
                                texto,
                                idVideo,
                                gestorUsuarios.getUsuario().getId(),
                                new Date()
                        );
                        gestorComentarios.comentar(comentario, new SetComentarioListener() {
                            @Override
                            public void onResponseSetComentarioListener(String response) {
                                Log.e("comentario", response);
                                if (response.equals("Ok")) {
                                    Toast.makeText(context, "Gracias por tu comentario", Toast.LENGTH_LONG).show();
                                    ((EditText) findViewById(R.id.edComentario)).setText("");
                                    gestorComentarios.getComentarios(idVideo, new GetComentariosListener() {
                                        @Override
                                        public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                                            ListView lista;
                                            lista = (ListView) findViewById(R.id.listaComentarios);
                                            adaptador = new VerVideo.AdapterComentarios(verVideo, comentarios);
                                            lista.setAdapter(adaptador);

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
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

            progress = new ProgressDialog(VerVideo.this);
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
                    startActivity(getIntent());
                    finish();
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
                startActivity(getIntent());
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




    public class AdapterComentarios extends BaseAdapter {

        protected Activity activity;
        protected ArrayList<Comentario> comentarios;

        public AdapterComentarios (Activity activity, ArrayList<Comentario> comentarios) {
            this.activity = activity;
            this.comentarios = comentarios;
        }

        @Override
        public int getCount() {
            return comentarios.size();
        }

        public void clear() {
            comentarios.clear();
        }

        public void addAll(ArrayList<Comentario> comentarios) {
            for (int i = 0; i < comentarios.size(); i++) {
                comentarios.add(comentarios.get(i));
            }
        }

        @Override
        public Object getItem(int arg0) {
            return comentarios.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inf.inflate(R.layout.comentario_personalizado, parent,false);
            }


            Comentario dir = comentarios.get(position);

            TextView txtTexto = (TextView) v.findViewById(R.id.txtTexto);
            txtTexto.setText(dir.getTexto());

            TextView txtUsername= (TextView) v.findViewById(R.id.txtUsername);
            txtUsername.setText(dir.getUsername());


            TextView txtFecha = (TextView) v.findViewById(R.id.txtFecha);
            if(dir.getFecha()!=null) {
                txtFecha.setText(dt1.format(dir.getFecha()));
            }

            return v;
        }
    }
}
