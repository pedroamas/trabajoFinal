package com.example.root.trabajofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Listeners.ActualizarEstadoImgListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class AprobarImgSecUsuarios extends AppCompatActivity {


    private Context context;
    public static final String EXTRA_POSITION = "id_imagen";
    private SimpleDateFormat dt1,dt2;
    private int idImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprobar_img_sec_usuarios);

        dt1=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        context=getApplicationContext();

        idImagen=getIntent().getIntExtra(EXTRA_POSITION,0);
        Log.e("idIMagene","id: "+idImagen);

        final GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(getApplicationContext());
        gestorMultimedia.getImagenConEstado(idImagen,0, new ImagenListener() {
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

                    Button btnAprobar=(Button)findViewById(R.id.btnAprobar);
                    btnAprobar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gestorMultimedia.setEstadoImagen(idImagen, 1, new ActualizarEstadoImgListener() {
                                @Override
                                public void onResponseActualizarEstadoImgListener(String response) {
                                    if (response.equals("Ok")){
                                        finish();
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
                            gestorMultimedia.setEstadoImagen(idImagen, 2, new ActualizarEstadoImgListener() {
                                @Override
                                public void onResponseActualizarEstadoImgListener(String response) {
                                    Log.e("respuesta car estado",response);
                                    //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
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
}