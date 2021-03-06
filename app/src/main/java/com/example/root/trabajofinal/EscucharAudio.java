package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.root.trabajofinal.Gestores.GestorComentarios;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Listeners.AudioListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.SetComentarioListener;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Multimedia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EscucharAudio extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id_audio";
    private int idAudio=-1;
    VideoView simpleVideoView;
    MyMediaController myMediaController;
    int progreso=-1;
    SimpleDateFormat dt1;
    private Context context;
    GestorComentarios gestorComentarios;
    private EscucharAudio escucharAudio;
    EscucharAudio.AdapterComentarios adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escuchar_audio);

        context=getApplicationContext();;
        dt1=new SimpleDateFormat("dd-MM-yyyy");
        idAudio=getIntent().getIntExtra(EXTRA_POSITION,0);
        gestorComentarios=GestorComentarios.obtenerGestorComentarios(context);

        escucharAudio=this;

        Button btnComentar=(Button)findViewById(R.id.btnComentar);
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(context);
                String texto=((EditText)findViewById(R.id.edComentario)).getEditableText().toString();
                if(texto.equals("")){
                    Toast.makeText(context,"Ingrese el comentario",Toast.LENGTH_LONG).show();
                }else if(gestorUsuarios.getUsuario()==null){
                    Toast.makeText(context,"Tiene que estar registrado",Toast.LENGTH_LONG).show();
                }else{

                    Comentario comentario=new Comentario(
                            texto,
                            idAudio,
                            gestorUsuarios.getUsuario().getId(),
                            new Date()
                    );
                    gestorComentarios.comentar(comentario, new SetComentarioListener() {
                        @Override
                        public void onResponseSetComentarioListener(String response) {
                            Log.e("comentario",response);
                            if(response.equals("Ok")){
                                Toast.makeText(context,"Gracias por tu comentario",Toast.LENGTH_LONG).show();
                                ((EditText)findViewById(R.id.edComentario)).setText("");
                                gestorComentarios.getComentarios(idAudio, new GetComentariosListener() {
                                    @Override
                                    public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                                        ListView lista;
                                        lista = (ListView)findViewById(R.id.listaComentarios);
                                        adaptador = new EscucharAudio.AdapterComentarios(escucharAudio,comentarios);
                                        lista.setAdapter(adaptador);

                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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
