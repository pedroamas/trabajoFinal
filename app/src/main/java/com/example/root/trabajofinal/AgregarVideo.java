package com.example.root.trabajofinal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgregarVideo extends AppCompatActivity {

    private Button btnUpload;
    private Context context;
    private int idPunto;
    private String titulo;
    private String descripcion;
    private String fechaCaptura;
    private int year;
    private int month;
    private int day;
    private File f;
    EditText edFechaCaptura;

    static final int DATE_DIALOG_ID = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context=getApplicationContext();
        idPunto=getIntent().getIntExtra("id_punto", 0);
        btnUpload = (Button) findViewById(R.id.btnUpload);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        /*
        edFechaCaptura= (EditText) findViewById(R.id.edFechaCaptura);
        edFechaCaptura.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Toast.makeText(AgregarVideo.this, ((TextView)findViewById(R.id.edFechaCaptura)).getEditableText().toString()+"L", Toast.LENGTH_SHORT).show();
                    showDialog(DATE_DIALOG_ID);
                }
            }
        });

        edFechaCaptura.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });*/

        Button btnSubir=(Button)findViewById(R.id.btnSubir);
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(AgregarVideo.this);
                progress.setTitle("Subiendo");
                progress.setMessage("Espere un momento...");
                progress.show();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        titulo=((TextView)findViewById(R.id.edTitulo)).getEditableText().toString();
                        descripcion=((TextView)findViewById(R.id.edDescripcion)).getEditableText().toString();
                        fechaCaptura=((TextView)findViewById(R.id.edFechaCaptura)).getEditableText().toString();


                        String content_type  = getMimeType(f.getPath());

                        String file_path = f.getAbsolutePath();
                        OkHttpClient client = new OkHttpClient();
                        RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                        RequestBody request_body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id_punto",""+idPunto)
                                .addFormDataPart("titulo",titulo)
                                .addFormDataPart("descripcion",descripcion)
                                .addFormDataPart("fecha_captura",fechaCaptura)
                                .addFormDataPart("type",content_type)
                                .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://pedroamas.xyz/subir_video.php")
                                .post(request_body)
                                .build();

                        try {
                            Log.e("","entro en try");
                            Response response = client.newCall(request).execute();
                            if(!response.isSuccessful()){
                                throw new IOException("Error : "+response);
                            }
                            Log.e("","Correcto");

                            progress.dismiss();
                            Intent intent=new Intent(context,DetalleEditarMultimedia.class);
                            intent.putExtra("id",idPunto);
                            startActivity(intent);
                            finish();

                        } catch (IOException e) {
                            Log.e("","incorrecto");
                            e.printStackTrace();
                        }


                    }
                });

                t.start();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }

        enable_button();
    }

    private void enable_button() {

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(AgregarVideo.this)
                        .withRequestCode(10)
                        .start();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    ProgressDialog progress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){

            f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));

        }
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            edFechaCaptura.setText(new StringBuilder()
                    .append(String.format("%02d",day))
                    .append("-")
                    .append(String.format("%02d",month + 1))
                    .append("-")
                    .append(year));

        }
    };
}
