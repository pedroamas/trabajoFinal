package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.root.trabajofinal.Gestores.GestorComentarios;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.EliminarCometarioListener;
import com.example.root.trabajofinal.Listeners.EliminarImagenSecListener;
import com.example.root.trabajofinal.Listeners.GetComentariosListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Objetos.Comentario;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
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
    public static int EDITAR_MULTIMEDIA=5000;
    public int noEditar;
    MostrarImagenesSecEliminarComentario.AdapterComentarios adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagenes_sec_eliminar_comentario);

        mostrarImagenesSecEliminarComentario=this;
        dt1=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        context=getApplicationContext();


        gestorComentarios=GestorComentarios.obtenerGestorComentarios(context);
        idImagen=getIntent().getIntExtra(EXTRA_POSITION,0);
        noEditar=getIntent().getIntExtra("no_editar",0);
        final GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(getApplicationContext());
        gestorMultimedia.getImagen(idImagen, new ImagenListener() {
            @Override
            public void onResponseImagen(Multimedia multimedia) {
                LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content);


                if(multimedia!=null){

                    ImageView imgFoto=(ImageView)findViewById(R.id.imgFoto);
                    Glide.with(getApplicationContext()).load(multimedia.getPath())
                            .crossFade()
                            .placeholder(R.drawable.ic_image_box)
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

        Button btnEditarImagen=(Button)findViewById(R.id.btnEditarImagen);
        btnEditarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditarImagenSec.class);
                intent.putExtra("id_imagen", idImagen);
                startActivityForResult(intent, EDITAR_MULTIMEDIA);
            }
        });
        Button btnEliminarImagen=(Button)findViewById(R.id.btnEliminarImagen);
        btnEliminarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext(),R.style.AppTheme);
                } else {
                    builder = new AlertDialog.Builder(MostrarImagenesSecEliminarComentario.this,R.style.Theme_AppCompat_Dialog);
                }
                builder.setMessage(Html.fromHtml("<font color='#000000'>¿Desea eliminar el punto de interés?</font>"));
                //builder.setTitle("Eliminar");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gestorMultimedia.eliminarImagenSec(idImagen, new EliminarImagenSecListener() {
                            @Override
                            public void onResponseEliminarImagenSecListener(String response) {
                                if(response.equals("Ok")){
                                    Toast.makeText(context,"La imagen ha sido borrada correctamente",
                                            Toast.LENGTH_LONG).show();

                                    Intent returnIntent=new Intent();
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();
                                }else{
                                    Toast.makeText(context,"Ocurrió un error al eliminar la imagen",
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

        if(noEditar==1){
            btnEditarImagen.setVisibility(View.GONE);
            btnEliminarImagen.setVisibility(View.GONE);
        }

    }
    public String getThemeName()
    {
        PackageInfo packageInfo;
        try
        {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            int themeResId = packageInfo.applicationInfo.theme;
            return getResources().getResourceEntryName(themeResId);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }
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
                            builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext(), android.R.style.Theme_Material_Dialog);
                        } else {
                            builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext());
                        }
                        Log.e("TEMA",""+getThemeName());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==EDITAR_MULTIMEDIA ){
                final ProgressDialog progress;
                progress = new ProgressDialog(MostrarImagenesSecEliminarComentario.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                        setResult(Activity.RESULT_OK,getIntent());
                        startActivity(getIntent());
                        finish();
                    }
                });

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
