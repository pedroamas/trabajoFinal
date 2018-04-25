package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorComentarios;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Listeners.SetComentarioListener;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MostrarImagenesSec extends AppCompatActivity {

    private Context context;
    private GestorComentarios gestorComentarios;
    public static final String EXTRA_POSITION = "id_imagen";
    public MostrarImagenesSec mostrarImagenesSec;
    private SimpleDateFormat dt1,dt2;
    AdapterComentarios adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagenes_sec);

        mostrarImagenesSec=this;
        dt1=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        context=getApplicationContext();

        EditText edComentario=(EditText)findViewById(R.id.edComentario);
        edComentario.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        gestorComentarios=GestorComentarios.obtenerGestorComentarios(context);
        final int idImagen=getIntent().getIntExtra(EXTRA_POSITION,0);
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(getApplicationContext());
        gestorMultimedia.getImagen(idImagen, new ImagenListener() {
            @Override
            public void onResponseImagen(Multimedia multimedia) {
                LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content);


                if(multimedia!=null){

                    ImageView imgFoto=(ImageView)findViewById(R.id.imgFoto);
                    Picasso.with(getApplicationContext()).load(multimedia.getPath())
                            .into(imgFoto);

                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(!multimedia.getTitulo().isEmpty()){
                        TextView txtTitulo=new TextView(getApplicationContext());
                        txtTitulo.setText(multimedia.getTitulo());
                        txtTitulo.setLayoutParams(params);
                        linearLayout.addView(txtTitulo);
                        txtTitulo.setTypeface(null, Typeface.BOLD);
                        txtTitulo.setTextSize(18);
                        txtTitulo.setTextColor(Color.BLACK);
                    }
                    if(!multimedia.getDescripcion().isEmpty()){
                        TextView txtDescripcion=new TextView(getApplicationContext());
                        txtDescripcion.setText(multimedia.getDescripcion());
                        txtDescripcion.setLayoutParams(params);
                        linearLayout.addView(txtDescripcion);
                        txtDescripcion.setTextColor(Color.BLACK);
                        txtDescripcion.setTextSize(14);
                    }
                    try {
                        if (multimedia.getFechaCaptura() != null && multimedia.getFechaCaptura().after(dt2.parse("01/01/1800"))) {
                            TextView txtCaptura = new TextView(getApplicationContext());
                            txtCaptura.setText("Fecha de captura: " + dt2.format(multimedia.getFechaCaptura()));
                            txtCaptura.setLayoutParams(params);
                            linearLayout.addView(txtCaptura);
                        }
                    }catch (Exception e){}
                    if(multimedia.getFechaSubida()!=null){
                        TextView txtSubida=new TextView(getApplicationContext());
                        txtSubida.setText("Fecha de subida: "+dt2.format(multimedia.getFechaSubida()));
                        txtSubida.setLayoutParams(params);
                        txtSubida.setPadding(0,5,0,5);
                        linearLayout.addView(txtSubida);

                    }

                    if(multimedia.getUsername()!=null){
                        TextView txtUsername=new TextView(getApplicationContext());
                        txtUsername.setText("Gentileza de : "+multimedia.getUsername());
                        txtUsername.setLayoutParams(params);
                        linearLayout.addView(txtUsername);
                    }
                    //Llenar la lista de comentarios

                    gestorComentarios.getComentarios(idImagen, new GetComentariosListener() {
                        @Override
                        public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                            ListView lista;


                            lista = (ListView)findViewById(R.id.listaComentarios);

                            adaptador = new AdapterComentarios(mostrarImagenesSec,comentarios);

                            lista.setAdapter(adaptador);

                        }
                    });

                }

            }
        });

        Button btnComentar=(Button)findViewById(R.id.btnComentar);
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(context);
                String texto=((EditText)findViewById(R.id.edComentario)).getEditableText().toString();
                if(gestorUsuarios.getUsuario()==null){
                    //Toast.makeText(context,"Tiene que estar registrado",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);

                }else if(texto.equals("")){
                    Toast.makeText(context,"Ingrese el comentario",Toast.LENGTH_LONG).show();
                }else{

                    Comentario comentario=new Comentario(
                            texto,
                            idImagen,
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
                                gestorComentarios.getComentarios(idImagen, new GetComentariosListener() {
                                    @Override
                                    public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                                        ListView lista;
                                        lista = (ListView)findViewById(R.id.listaComentarios);
                                        adaptador = new AdapterComentarios(mostrarImagenesSec,comentarios);
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
