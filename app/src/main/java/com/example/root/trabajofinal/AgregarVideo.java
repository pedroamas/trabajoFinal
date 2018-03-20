package com.example.root.trabajofinal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.root.trabajofinal.Gestores.GestorWebService;
import com.example.root.trabajofinal.Listeners.AgregarImagenSecListener;
import com.example.root.trabajofinal.Listeners.RegistrarListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                Multimedia video=new Multimedia(
                        descripcion,
                        f.getAbsolutePath(),
                        titulo,
                        null,
                        null,
                        idPunto
                );
                GestorWebService gestorWebService=GestorWebService.getGestorWebService(context);
                gestorWebService.setVideo(video, new AgregarImagenSecListener() {
                    @Override
                    public void onResponseAgregarImagenSecListener(String response) {
                        Log.e("Resp",response);
                        progress.dismiss();
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

        enable_button();
    }

    private void enable_button() {

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), 10);


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

            Uri selectedImage = data.getData();
            f=new File(getRealPathFromDocumentUri(this,selectedImage));
            if (f.exists())
                Log.e("File path",getRealPathFromDocumentUri(this,selectedImage));

        }
    }

    public String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }
}
