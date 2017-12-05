package com.example.root.trabajofinal;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalleEliminarPunto extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id";

    private Punto punto;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_punto);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        // collapsingToolbar.setTitle(getString(R.string.item_title));
        GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
        Log.e("Intent","Detalle");

        punto=gestorDePuntos.getPunto(getIntent().getIntExtra(EXTRA_POSITION, 0));
        Log.e("putExtra","id: "+punto.getFoto());
        int postion= 1;

        Resources resources = getResources();

        //String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(punto.getTitulo());


        //String[] placeDetails = resources.getStringArray(R.array.place_details);
        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(punto.getDescripcion());
        //GestorWebService.getGestorWebService(getApplicationContext()).obtenerDescripcion(punto.getId(),placeDetail);
        //gestorDePuntos.getDescripcion(punto.getId(),placeDetail);

        //String[] placeLocations = resources.getStringArray(R.array.place_locations);
        TextView placeLocation =  (TextView) findViewById(R.id.place_location);
        placeLocation.setText("Latitud "+punto.getLatitud()+"\nLongitud "+punto.getLongitud());

        GestorImagenes gestorImagenes=GestorImagenes.obtenerGestorImagenes(getApplicationContext());
        Log.e("<img>","path foto: "+punto.getFoto());

        TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        //placePicutre.setImageDrawable(placePictures.getDrawable(postion % placePictures.length()));
        Bitmap fotoPortada=gestorImagenes.cargarImagen(punto.getFoto());
        //InputStream si1=fotoPortada.;
        Log.e("Foto","Foto: "+punto.getFoto());
        if(fotoPortada==null){
            Log.e("Foto","Esta nulooo la foto");
        }

        placePicutre.setImageBitmap(fotoPortada);
        //placePictures.recycle();

        Button btnEliminarPunto=(Button)findViewById(R.id.btnEliminarPunto);
        btnEliminarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.eliminarPunto(punto.getId());
            }
        });

    }
}
