package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorWebService;
import com.example.root.trabajofinal.Listeners.AgregarImagenSecListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgregarVideo extends AppCompatActivity {

    private Context context;
    private int idPunto;
    private String titulo;
    private String descripcion;
    private String path;
    private Date fechaCaptura;
    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    public static final String API_KEY = "AIzaSyCe6tORd9Ch4lx-9Ku5SQ476uS9OtZYsWA";
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youTubePlayerFragment;

    static final int DATE_DIALOG_ID = 999;
    SimpleDateFormat dt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button btnVisualizar=(Button)findViewById(R.id.btnVisualizar);
        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizarVideo();
            }
        });
        context=getApplicationContext();
        dt2=new SimpleDateFormat("dd/MM/yyyy");
        idPunto=getIntent().getIntExtra("id_punto", 0);
        EditText edFechaCaptura=(EditText)findViewById(R.id.edFechaCaptura);
        edFechaCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        });
        edFechaCaptura.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    obtenerFecha();
                }
            }
        });
        Button btnSubir=(Button)findViewById(R.id.btnSubir);
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(AgregarVideo.this);
                progress.setTitle("Subiendo");
                progress.setMessage("Espere un momento...");
                progress.show();
                titulo=((TextView)findViewById(R.id.edTitulo)).getEditableText().toString();
                descripcion=((TextView)findViewById(R.id.edDescripcion)).getEditableText().toString();
                path=((TextView)findViewById(R.id.edPath)).getEditableText().toString();
                path=path.replace("https://www.youtube.com/watch?v=","");
                path=path.replace("https://youtu.be/","");
                try{
                    EditText edFechaCaptura=(EditText)findViewById(R.id.edFechaCaptura);
                    fechaCaptura=dt2.parse(edFechaCaptura.toString());
                    Log.e("","captura "+fechaCaptura);
                }catch (Exception e){
                    fechaCaptura=null;
                }
                Multimedia video=new Multimedia(
                        descripcion,
                        path,
                        titulo,
                        fechaCaptura,
                        null,
                        idPunto
                );
                GestorWebService gestorWebService=GestorWebService.getInstance(context);
                gestorWebService.setVideo(video, new AgregarImagenSecListener() {
                    @Override
                    public void onResponseAgregarImagenSecListener(String response) {
                        Log.e("respuesta",response);
                        progress.dismiss();
                            //Toast.makeText(context,"La imagen se subió correctamente",Toast.LENGTH_LONG).show();
                            Intent returnIntent=new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();

                    }
                });

            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }


    }


    ProgressDialog progress;


    private void visualizarVideo(){
        String pathYoutube=((EditText)findViewById(R.id.edPath)).getEditableText().toString();
        pathYoutube=pathYoutube.replace("https://www.youtube.com/watch?v=","");
        pathYoutube=pathYoutube.replace("https://youtu.be/","");
        final String pathVideo=pathYoutube;
        Log.e("pathvideo",pathVideo);
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtubeplayerfragment);
        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                try {
                    if (!wasRestored) {
                        youTubePlayer.cueVideo(pathVideo);
                    }

                } catch (Exception e) {}
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                } else {
                    String error = errorReason.toString();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;

                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);

                EditText edFechaCaptura=(EditText)findViewById(R.id.edFechaCaptura);
                edFechaCaptura.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

                try {
                    fechaCaptura=dt2.parse(edFechaCaptura.toString());
                    Log.e("fecha","captura "+fechaCaptura);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        },anio, mes, dia);

        recogerFecha.show();

    }
}
