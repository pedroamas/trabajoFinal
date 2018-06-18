package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.AgregarImagenSecListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Usuario;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgregarImagenesSecUsuario extends AppCompatActivity {

        private static String APP_DIRECTORY = "MyPictureApp/";
        private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";
        private Button btnImagen;
        private Context context;
        private int idPunto;
        private String titulo;
        private String descripcion;
        private String fechaCaptura;
        private ImageView imagenPicasso;
        private File f;
        private String mPath;
        private String pathImagen;

        private final int MY_PERMISSIONS = 100;
        private final int PHOTO_CODE = 200;
        private final int SELECT_PICTURE = 300;
        private final int BUSCAR_LOCALIZACION = 400;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_agregar_imagenes_sec);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            context=getApplicationContext();
            idPunto=getIntent().getIntExtra("id_punto", 0);
            imagenPicasso = (ImageView) findViewById(R.id.imgFotoSec);
            btnImagen = (Button) findViewById(R.id.btnImagen);

            //Subir al Web Service
            Button btnAgregarImagen=(Button)findViewById(R.id.btnAgregarImagen);
            btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progress = new ProgressDialog(AgregarImagenesSecUsuario.this);
                    progress.setTitle("Subiendo");
                    progress.setMessage("Espere un momento...");
                    progress.show();
                    titulo=((TextView)findViewById(R.id.edTitulo)).getEditableText().toString();
                    descripcion=((TextView)findViewById(R.id.edDescripcion)).getEditableText().toString();
                    Multimedia imagen=new Multimedia(
                            descripcion,
                            f.getAbsolutePath(),
                            titulo,
                            null,
                            new Date(),
                            idPunto
                    );
                    GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(context);
                    Usuario usuario=gestorUsuarios.getUsuario();
                    imagen.setIdUsuario(usuario.getId());
                    GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                    gestorMultimedia.setImagenSecUsuario(imagen, new AgregarImagenSecListener() {
                        @Override
                        public void onResponseAgregarImagenSecListener(String response) {
                            Log.e("Resp",response);
                            progress.dismiss();
                            if(response.equals("Ok")){
                                //Toast.makeText(context,"La imagen se subió correctamente",Toast.LENGTH_LONG).show();
                                Intent returnIntent=new Intent();
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                        }
                    });

                }
            });
            //Fin Subir al Web Service

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
                    return;
                }
            }

            enable_button();
        }

        private void enable_button() {

            btnImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showOptions();


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
            if(resultCode == RESULT_OK){
                switch (requestCode) {
                    case PHOTO_CODE:
                        MediaScannerConnection.scanFile(this,
                                new String[]{mPath}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> Uri = " + uri);
                                    }
                                });

                        f=new File(mPath);
                        Picasso.with(context)
                                .load("file:" + mPath)
                                .into(imagenPicasso);

                        break;
                    case SELECT_PICTURE:
                        Uri selectedImage = data.getData();
                        String realPath = getRealPathFromDocumentUri(this, selectedImage);
                        f = new File(realPath);
                        if (f.exists()) {
                            Log.e("File path", "file:" + f.getAbsoluteFile());
                            f = new File(realPath);



                            Picasso.with(context)
                                    .load("file:" + f.getAbsolutePath())
                                    .into(imagenPicasso);
                        }
                        break;
                }

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

        private void openCamera() {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},100);
                    return;
                }
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                    return;
                }
            }
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            boolean isDirectoryCreated = file.exists();

            if(!isDirectoryCreated)
                //Crea la carpeta
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated){
                Long timestamp = System.currentTimeMillis() / 1000;
                //setea la imagen como jpg
                String imageName = timestamp.toString() + ".jpg";

                mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                        + File.separator + imageName;
                pathImagen=mPath;
                File newFile = new File(mPath);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
                startActivityForResult(intent, PHOTO_CODE);
            }
        }


        private void showOptions() {
            final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarImagenesSecUsuario.this);
            builder.setTitle("Eleige una opción");
            builder.setItems(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(option[which] == "Tomar foto"){
                        openCamera();
                    }else if(option[which] == "Elegir de galeria"){
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                    }else {
                        dialog.dismiss();
                    }
                }
            });

            builder.show();
        }

    }

