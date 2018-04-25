package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorComentarios;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Listeners.EliminarCometarioListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MostrarImagenesSecEliminarComentario extends AppCompatActivity {

    private Context context;
    private GestorComentarios gestorComentarios;
    public static final String EXTRA_POSITION = "id_imagen";
    public MostrarImagenesSecEliminarComentario mostrarImagenesSecEliminarComentario;
    private SimpleDateFormat dt1,dt2;
    private int idImagen;
    MostrarImagenesSecEliminarComentario.AdapterComentarios adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagenes_sec_eliminar_comentario);
        Log.e("","eliminar comentarios siiii");

        mostrarImagenesSecEliminarComentario=this;
        dt1=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        context=getApplicationContext();


        gestorComentarios=GestorComentarios.obtenerGestorComentarios(context);
        idImagen=getIntent().getIntExtra(EXTRA_POSITION,0);
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

                    //Llenar la lista de comentarios

                    llenarComentarios();

                }

            }
        });

    }

    private void llenarComentarios(){

        gestorComentarios.getComentarios(idImagen, new GetComentariosListener() {
            @Override
            public void onResponseGetComentariosListener(ArrayList<Comentario> comentarios) {
                ListView lista;

                lista = (ListView)findViewById(R.id.listaComentarios);

                adaptador = new MostrarImagenesSecEliminarComentario.AdapterComentarios(mostrarImagenesSecEliminarComentario,comentarios);

                lista.setAdapter(adaptador);
                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final Comentario comentario=(Comentario)adaptador.getItem(position);
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext());
                        }
                        builder.setTitle(Html.fromHtml("<font color='#FF0000'>Comentario de "+comentario.getUsername()+"</font>"));

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

                                .setIcon(R.drawable.ic_dialog_alert)
                                .show();

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
