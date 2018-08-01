package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorComentarios;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.EliminarCometarioListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.SetComentarioListener;
import com.example.root.trabajofinal.Listeners.VideoListener;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pedro on 31/07/18.
 */

public class VerVideoEliminarComentario extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id_video";
    private int idVideo=-1;
    SimpleDateFormat dt1,dt2;
    private Context context;
    GestorComentarios gestorComentarios;
    VerVideoEliminarComentario.AdapterComentarios adaptador;
    private VerVideoEliminarComentario verVideoEliminarComentario;
    public static final String API_KEY = "AIzaSyCe6tORd9Ch4lx-9Ku5SQ476uS9OtZYsWA";
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youTubePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_video_eliminar);

        context = getApplicationContext();
        verVideoEliminarComentario = this;
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
                    adaptador = new VerVideoEliminarComentario.AdapterComentarios(verVideoEliminarComentario, comentarios);
                    lista.setAdapter(adaptador);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final Comentario comentario=(Comentario)adaptador.getItem(position);
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext(), android.R.style.Theme_Material_Dialog);
                            } else {
                                builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext());
                            }
                            builder.setTitle(Html.fromHtml("<font color='#3F51B5'>Comentario de "+comentario.getUsername()+"</font>"));

                            builder.setMessage(Html.fromHtml("<font color='#000000'>¿Desea eliminar el mensaje?</font>"));
                            //builder.setTitle("Eliminar");
                            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    gestorComentarios.eliminarComentario(comentario.getId(), new EliminarCometarioListener() {
                                        @Override
                                        public void onResponseEliminarCometarioListener(String response) {

                                            if(response.equals("Ok")) {
                                                llenarComentarios();
                                            }else{
                                                Toast.makeText(context,
                                                        response,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })

                                    .setIcon(R.drawable.ic_dialog_alert);

                            AlertDialog a=builder.create();

                            a.show();
                            Button bq = a.getButton(DialogInterface.BUTTON_POSITIVE);
                            bq.setBackgroundColor(Color.RED);
                            bq.setTextColor(getResources().getColor(R.color.white));

                        }
                    });
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



        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void llenarComentarios(){

        gestorComentarios.getComentarios(idVideo, new GetComentariosListener() {
            @Override
            public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                ListView lista;

                lista = (ListView)findViewById(R.id.listaComentarios);

                adaptador = new VerVideoEliminarComentario.AdapterComentarios(verVideoEliminarComentario,comentarios);

                lista.setAdapter(adaptador);
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final Comentario comentario=(Comentario)adaptador.getItem(position);
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext(), android.R.style.Theme_Material_Dialog);
                        } else {
                            builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext());
                        }
                        builder.setTitle(Html.fromHtml("<font color='#3F51B5'>Comentario de "+comentario.getUsername()+"</font>"));

                        builder.setMessage(Html.fromHtml("<font color='#000000'>¿Desea eliminar el mensaje?</font>"));
                        //builder.setTitle("Eliminar");
                        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                gestorComentarios.eliminarComentario(comentario.getId(), new EliminarCometarioListener() {
                                    @Override
                                    public void onResponseEliminarCometarioListener(String response) {

                                        if(response.equals("Ok")) {
                                            llenarComentarios();
                                        }else{
                                            Toast.makeText(context,
                                                    response,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })

                                .setIcon(R.drawable.ic_dialog_alert);

                        AlertDialog a=builder.create();

                        a.show();
                        Button bq = a.getButton(DialogInterface.BUTTON_POSITIVE);
                        bq.setBackgroundColor(Color.RED);
                        bq.setTextColor(getResources().getColor(R.color.white));

                    }
                });

            }
        });

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