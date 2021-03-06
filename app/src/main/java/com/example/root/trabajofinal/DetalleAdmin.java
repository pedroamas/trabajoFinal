package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.CantValidarListener;
import com.example.root.trabajofinal.Listeners.EliminarPuntoListener;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Listeners.VideosListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;

import java.util.ArrayList;
import java.util.Iterator;

public class DetalleAdmin extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id";
    public static int EDICION = 1000;
    public static int EDICION_IMAGENES = 2000;
    public static int EDICION_VIDEOS = 3000;
    public static int EDICION_USUARIOS = 4000;
    private GestorMultimedia gestorMultimedia;
    private Context context;
    private Punto punto;
    private LinearLayoutCompat.LayoutParams linLayoutParam;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_punto);
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

        //String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(punto.getTitulo());
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white));



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
        linLayoutParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        //linLayoutParam.setMargins(0,10,0,0);
        Button btnEditarPunto=(Button)findViewById(R.id.btnEditarPunto);
        btnEditarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DetalleEditarPunto.class);
                intent.putExtra("id",punto.getId());
                startActivityForResult(intent,EDICION);
            }
        });





        cargarImagenes();
        cargarVideos();
        cargarImagenesUsuarios();

        Button btnAgregarImagen=(Button)findViewById(R.id.btnAgregarImagen);
        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AgregarImagenesSec.class);
                intent.putExtra("id_punto",punto.getId());
                startActivityForResult(intent,EDICION_IMAGENES);
            }
        });
        Button btnAgregarVideo=(Button)findViewById(R.id.btnAgregarVideo);
        btnAgregarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AgregarVideo.class);
                intent.putExtra("id_punto", punto.getId());
                startActivityForResult(intent,EDICION_VIDEOS);
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
        Button btnEliminarPunto=(Button)findViewById(R.id.btnEliminarPunto);
        btnEliminarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(DetalleAdmin.this);

                builder.setMessage(Html.fromHtml("<font color='#000000'>¿Desea eliminar el punto de interés?</font>"));
                //builder.setTitle("Eliminar");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                        gestorPuntos.eliminarPunto(punto.getId(), new EliminarPuntoListener() {
                            @Override
                            public void onResponseEliminarPunto(String response) {

                                if (response.equals("Ok")){
                                    Toast.makeText(getApplicationContext(), "El punto se eliminó correctamente", Toast.LENGTH_LONG).show();
                                    gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                                        @Override
                                        public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                                            Intent returnIntent=new Intent();
                                            setResult(Activity.RESULT_OK,returnIntent);
                                            finish();
                                        }
                                    });
                                }else{
                                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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
                bq.setTextColor(getResources().getColor(R.color.red));
                Log.e("TEMA",""+getThemeName());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
        Usuario usuario=gestorUsuarios.getUsuario();
        if(usuario==null){
            finish();
            return;
        }else if(!usuario.isAdmin()){
            finish();
            return;
        }

        if(resultCode == RESULT_OK){
            if(requestCode==EDICION ){
                final ProgressDialog progress;
                progress = new ProgressDialog(DetalleAdmin.this);
                progress.setTitle("Actualizando");
            progress.setCancelable(false);
            progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                progress.setMessage("Espere un momento...");
                progress.show();
                GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                        startActivity(getIntent());
                        setResult(Activity.RESULT_OK,getIntent());
                        finish();
                    }
                });

            }if(requestCode==EDICION_IMAGENES ){
                cargarImagenes();
            }if(requestCode==EDICION_VIDEOS ){
                cargarVideos();
            }if(requestCode==EDICION_USUARIOS ){
                setResult(Activity.RESULT_OK,getIntent());
                cargarImagenesUsuarios();
            }
        }
    }
    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
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

    public void cargarImagenes(){
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
                                Intent intent=new Intent(context,MostrarImagenesSecEliminarComentario.class);
                                intent.putExtra("id_imagen",imagen.getId());
                                intent.putExtra("no_editar",1);
                                startActivityForResult(intent,EDICION_IMAGENES);
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
                                Intent intent = new Intent(context, MostrarImagenesSecEliminarComentario.class);
                                intent.putExtra("id_imagen", imagen.getId());
                                startActivityForResult(intent, EDICION_IMAGENES);
                                //Toast.makeText(getApplicationContext(),imagen.getId(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    count++;
                }

            }
        });

    }

    public void cargarVideos(){
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
        gestorMultimedia.getVideos(punto.getId(), new VideosListener() {
            @Override
            public void onResponseVideos(ArrayList<Multimedia> videos) {

                DisplayMetrics dm = getResources().getDisplayMetrics();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, convertDpToPx(16, dm), 0, convertDpToPx(16, dm));


                Iterator<Multimedia> ite=videos.iterator();
                LinearLayout layoutVideos=(LinearLayout)findViewById(R.id.lytGaleriaVideos);
                layoutVideos.removeAllViews();
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
                            Intent intent = new Intent(getApplicationContext(), VerVideoEliminarComentario.class);
                            intent.putExtra("id_video",video.getId());
                            startActivityForResult(intent,EDICION_VIDEOS);
                        }
                    });
                    Log.e("",video.getTitulo());
                    layoutVideos.addView(btnVideo);

                }
            }
        });
    }

    public void cargarImagenesUsuarios(){
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);

        GestorPuntos gestorPuntos=GestorPuntos.getInstance(context);
        gestorPuntos.getCantValidar(punto.getId(), new CantValidarListener() {
            @Override
            public void onResponseCantValidar(int cantidad) {
                Button btnValidarInfo=(Button)findViewById(R.id.btnValidarInfo);
                btnValidarInfo.setText("Validar ("+cantidad+")");
                if(cantidad==0){
                    btnValidarInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context,"No hay imágenes para validar",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    btnValidarInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getApplicationContext(),ValidarInfoUsuario.class);
                            intent.putExtra("id",punto.getId());
                            startActivityForResult(intent,EDICION_USUARIOS);
                        }
                    });
                }
            }
        });
        gestorMultimedia.getImagenesUsuarios(punto.getId(), new ImagenesListener() {
            @Override
            public void onResponseImagenes(ArrayList<Multimedia> imagenes) {
                Log.e("Imagenes","Trajo imagnes de usuarios"+imagenes.size());
                final LinearLayout layout = (LinearLayout) findViewById(R.id.lytImagenesUsuarios);
                layout.removeAllViews();
                Iterator<Multimedia> ite=imagenes.iterator();
                while (ite.hasNext()){

                    final Multimedia imagen =ite.next();
                    Log.e("Imagenes","path imagnes "+imagen.getPath());
                    ImageView imgGaleria=new ImageView(context);
                    linLayoutParam = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    linLayoutParam.height=LinearLayout.LayoutParams.WRAP_CONTENT;

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
                            Intent intent=new Intent(context,MostrarImagenesSecEliminarComentario.class);
                            intent.putExtra("id_imagen",imagen.getId());
                            startActivityForResult(intent,EDICION_USUARIOS);
                            //Toast.makeText(getApplicationContext(),imagen.getId(),Toast.LENGTH_LONG).show();
                        }
                    });
                }


            }
        });
    }
}



