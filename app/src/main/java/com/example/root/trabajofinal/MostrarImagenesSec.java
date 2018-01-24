package com.example.root.trabajofinal;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.squareup.picasso.Picasso;

public class MostrarImagenesSec extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id_imagen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagenes_sec);

        int idImagen=getIntent().getIntExtra(EXTRA_POSITION,0);
        GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(getApplicationContext());
        gestorImagenes.getImagen(idImagen, new ImagenListener() {
            @Override
            public void onResponseImagen(Multimedia multimedia) {
                LinearLayout linearLayout=(LinearLayout)findViewById(R.id.content);
                TextView txt1 = new TextView(getApplicationContext());
                txt1.setText("HOLAALLALALALALALALAL");
                linearLayout.setBackgroundColor(Color.TRANSPARENT);
                linearLayout.addView(txt1);
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
                        Log.e("titulo imagen",multimedia.getTitulo());
                    }
                    if(!multimedia.getDescripcion().isEmpty()){
                        TextView txtDescripcion=new TextView(getApplicationContext());
                        txtDescripcion.setText("Descripción: "+multimedia.getDescripcion());
                        txtDescripcion.setLayoutParams(params);
                        linearLayout.addView(txtDescripcion);
                        Log.e("descripcion imagen",multimedia.getDescripcion());
                    }
                    if(multimedia.getFechaCaptura()!=null){
                        TextView txtCaptura=new TextView(getApplicationContext());
                        txtCaptura.setText("Fecha de captura: "+multimedia.getFechaCaptura());
                        txtCaptura.setLayoutParams(params);
                        linearLayout.addView(txtCaptura);
                        Log.e("captura imagen",""+multimedia.getFechaCaptura());
                    }
                    if(multimedia.getFechaSubida()!=null){
                        TextView txtSubida=new TextView(getApplicationContext());
                        txtSubida.setText("Fecha de subida: "+multimedia.getFechaSubida());
                        txtSubida.setLayoutParams(params);
                        linearLayout.addView(txtSubida);
                        Log.e("subida imagen",""+multimedia.getFechaSubida());
                    }

                }

            }
        });



    }
}
