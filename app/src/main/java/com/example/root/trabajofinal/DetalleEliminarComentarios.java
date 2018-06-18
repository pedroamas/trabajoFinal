package com.example.root.trabajofinal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Listeners.VideosListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

public class DetalleEliminarComentarios extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id";

    private android.support.v4.app.FragmentManager manager = null;
    private android.support.v4.app.FragmentTransaction ft;
    private GestorMultimedia gestorMultimedia;
    private Context context;
    private Punto punto;
    private LinearLayoutCompat.LayoutParams linLayoutParam;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_eliminar_comentarios);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        context=getApplicationContext();
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));
        GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);
        Log.e("Intent","Detalle");

        punto= gestorPuntos.getPunto(getIntent().getIntExtra(EXTRA_POSITION, 0));
        Log.e("putExtra","id: "+punto.getFoto());
        int postion= 1;

        Resources resources = getResources();

        collapsingToolbar.setTitle(punto.getTitulo());


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

        TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        //placePicutre.setImageDrawable(placePictures.getDrawable(postion % placePictures.length()));
        Bitmap fotoPortada= gestorMultimedia.cargarImagen(punto.getFoto());
        //InputStream si1=fotoPortada.;
        Log.e("Foto","Foto: "+punto.getFoto());
        if(fotoPortada==null){
            Log.e("Foto","Esta nulooo la foto");
        }

        placePicutre.setImageBitmap(fotoPortada);

        gestorMultimedia.getImagenes(punto.getId(), new ImagenesListener() {
            @Override
            public void onResponseImagenes(ArrayList<Multimedia> imagenes) {
                Log.e("Imagenes","Trajo imagnes "+imagenes.size());
                final LinearLayout layout = (LinearLayout) findViewById(R.id.content);

                Iterator<Multimedia> ite=imagenes.iterator();
                while (ite.hasNext()){

                    final Multimedia imagen =ite.next();
                    Log.e("Imagenes","path imagnes "+imagen.getPath());
                    ImageView imgGaleria=new ImageView(context);
                    linLayoutParam = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    linLayoutParam.height=LinearLayout.LayoutParams.WRAP_CONTENT;

                    Picasso.with(context).load(imagen.getPath())
                            .resize(600, 200) // resizes the image to these dimensions (in pixel)
                            .centerCrop().into(imgGaleria);
                    imgGaleria.setLayoutParams(linLayoutParam);
                    layout.addView(imgGaleria);
                    imgGaleria.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context,MostrarImagenesSecEliminarComentario.class);
                            intent.putExtra("id_imagen",imagen.getId());
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(),imagen.getId(),Toast.LENGTH_LONG).show();
                        }
                    });
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

    }

    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }
}
