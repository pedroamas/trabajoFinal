/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.root.trabajofinal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Listeners.VideosListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

public class Detalle extends AppCompatActivity  {

    public static final String EXTRA_POSITION = "id";

    private android.support.v4.app.FragmentManager manager = null;
    private android.support.v4.app.FragmentTransaction ft;
    private GestorMultimedia gestorMultimedia;
    private Context context;
    private Punto punto;
    private static int LOGUEAR_USUARIO = 100;
    private LinearLayoutCompat.LayoutParams linLayoutParam;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        context=getApplicationContext();
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));
        final GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);

        punto= gestorPuntos.getPunto(getIntent().getIntExtra(EXTRA_POSITION, 0));
        Log.e("putExtra","id: "+punto.getFoto());
        int postion= 1;

        Resources resources = getResources();

        //String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(punto.getTitulo());

        //Click de agregar imagenes del usuario
        Button btnAgregarImagen=(Button)findViewById(R.id.btnAgregarImagen);
        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(context);
                if(gestorUsuarios.getUsuario()==null){
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivityForResult(intent,LOGUEAR_USUARIO);
                }else {
                    Intent intent = new Intent(context, AgregarImagenesSecUsuario.class);
                    intent.putExtra("id_punto", punto.getId());
                    startActivity(intent);
                }
            }
        });

        //String[] placeDetails = resources.getStringArray(R.array.place_details);
        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(punto.getDescripcion());
        //GestorWebService.getInstance(getApplicationContext()).obtenerDescripcion(punto.getId(),placeDetail);
        //gestorPuntos.getDescripcion(punto.getId(),placeDetail);

        //String[] placeLocations = resources.getStringArray(R.array.place_locations);
        TextView placeLocation =  (TextView) findViewById(R.id.place_location);
        placeLocation.setText("Latitud "+punto.getLatitud()+"\nLongitud "+punto.getLongitud());

        gestorMultimedia = GestorMultimedia.getInstance(context);
        Log.e("<img>","path foto: "+punto.getFoto());

        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        //placePicutre.setImageDrawable(placePictures.getDrawable(postion % placePictures.length()));
        Bitmap fotoPortada= gestorMultimedia.cargarImagen(punto.getFoto());
        //InputStream si1=fotoPortada.;
        Log.e("Foto","Foto: "+punto.getFoto());
        if(fotoPortada==null){
            Log.e("Foto","Esta nulooo la foto");
        }

        placePicutre.setImageBitmap(fotoPortada);
        linLayoutParam = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linLayoutParam.height=LinearLayout.LayoutParams.WRAP_CONTENT;

        gestorMultimedia.getImagenes(punto.getId(), new ImagenesListener() {
            @Override
            public void onResponseImagenes(ArrayList<Multimedia> imagenes) {
                final LinearLayout layout = (LinearLayout) findViewById(R.id.lytGaleria);
                layout.removeAllViews();
                int count=0;
                Iterator<Multimedia> ite=imagenes.iterator();
                while (ite.hasNext()){

                    final Multimedia imagen =ite.next();
                    Log.e("Imagenes","path imagnes "+imagen.getPath());
                    if(count==0){
                        final ImageView image=(ImageView)findViewById(R.id.image);
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(context,MostrarImagenesSec.class);
                                intent.putExtra("id_imagen",imagen.getId());
                                intent.putExtra("no_editar",1);
                                startActivity(intent);
                                //Toast.makeText(getApplicationContext(),imagen.getId(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        ImageView imgGaleria = new ImageView(context);

                        Glide.with(context).load(imagen.getPath())
                                .placeholder(R.drawable.ic_image_box)
                                .override(600, 200) // resizes the image to these dimensions (in pixel)
                                .centerCrop()
                                .crossFade()
                                .into(imgGaleria);
                        imgGaleria.setLayoutParams(linLayoutParam);
                        imgGaleria.setPadding(0, 10, 0, 10);
                        layout.addView(imgGaleria);
                        imgGaleria.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, MostrarImagenesSec.class);
                                intent.putExtra("id_imagen", imagen.getId());
                                startActivity(intent);
                                //Toast.makeText(getApplicationContext(),imagen.getId(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    count++;
                }

                GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                gestorMultimedia.getVideos(punto.getId(), new VideosListener() {
                    @Override
                    public void onResponseVideos(ArrayList<Multimedia> videos) {

                        DisplayMetrics dm = getResources().getDisplayMetrics();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, convertDpToPx(16, dm), 0, convertDpToPx(16, dm));

                        TextView galeriaVideos=new TextView(context);
                        galeriaVideos.setLayoutParams(lp);


                        galeriaVideos.setText("Galería de videos");
                        galeriaVideos.setTextSize(20);
                        galeriaVideos.setTextColor(getResources().getColor( R.color.blue));

                        if(videos.size()>0) {
                            layout.addView(galeriaVideos);
                        }
                        Iterator<Multimedia> ite=videos.iterator();

                        while (ite.hasNext()){
                            final Multimedia video=ite.next();
                            Button btnVideo=new Button(context);
                            btnVideo.setLayoutParams(linLayoutParam);
                            btnVideo.setText(video.getTitulo());
                            Drawable image = context.getResources().getDrawable(R.drawable.ic_media_play );
                            image.setBounds( 0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight() );
                            btnVideo.setCompoundDrawables( image, null, null, null );
                            btnVideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), VerVideo.class);
                                    intent.putExtra("id_video",video.getId());
                                    startActivity(intent);
                                }
                            });
                            Log.e("",video.getTitulo());
                            layout.addView(btnVideo);

                        }
                    }
                });


            }
        });
        //ak
        gestorMultimedia.getImagenesUsuarios(punto.getId(), new ImagenesListener() {
            @Override
            public void onResponseImagenes(ArrayList<Multimedia> imagenes) {
                Log.e("Imagenes","Trajo imagnes de usuarios"+imagenes.size());
                final LinearLayout layout = (LinearLayout) findViewById(R.id.lytImagenesUsuarios);

                Iterator<Multimedia> ite=imagenes.iterator();
                while (ite.hasNext()){

                    final Multimedia imagen =ite.next();
                    Log.e("Imagenes","path imagnes "+imagen.getPath());
                    ImageView imgGaleria=new ImageView(context);


                    Glide.with(context).load(imagen.getPath())
                            .placeholder(R.drawable.ic_image_box)
                            .override(600, 200) // resizes the image to these dimensions (in pixel)
                            .centerCrop()
                            .crossFade()
                            .into(imgGaleria);
                    imgGaleria.setLayoutParams(linLayoutParam);
                    imgGaleria.setPadding(0,10,0,10);
                    layout.addView(imgGaleria);
                    imgGaleria.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context,MostrarImagenesSec.class);
                            intent.putExtra("id_imagen",imagen.getId());
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(),imagen.getId(),Toast.LENGTH_LONG).show();
                        }
                    });
                }


            }
        });

        Button btnVerMapa=(Button)findViewById(R.id.btnVerMapa);
        btnVerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerMapa.class);

                intent.putExtra("latitud",punto.getLatitud());
                intent.putExtra("longitud",punto.getLongitud());
                intent.putExtra("titulo",punto.getTitulo());
                startActivity(intent);
            }
        });

    }
    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==LOGUEAR_USUARIO ){
                GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(context);
                if(gestorUsuarios.getUsuario()!=null){
                    Intent intent = new Intent(context, AgregarImagenesSecUsuario.class);
                    intent.putExtra("id_punto", punto.getId());
                    startActivity(intent);
                }

            }
        }
    }
}
