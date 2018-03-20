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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorDePuntos;
import com.example.root.trabajofinal.Listeners.SetPuntoListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubirPuntoAdmin extends AppCompatActivity {
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
    LinearLayout mRlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_punto_admin);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getApplicationContext();
        idPunto = getIntent().getIntExtra("id_punto", 0);
        imagenPicasso = (ImageView) findViewById(R.id.imagenPicasso);
        mRlView = (LinearLayout) findViewById(R.id.rl_view);
        btnImagen = (Button) findViewById(R.id.btnImagen);

        //Buscar en Maps
        Button btnBuscarUbicacion=(Button)findViewById(R.id.btnBuscarUbicacion);
        btnBuscarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuscarLocalizacionPunto.class);
                startActivityForResult(intent,BUSCAR_LOCALIZACION);

            }
        });
        //Fin Buscar en Maps

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }

        enable_button();

        //Subir a Web Service
        Button btnAgregarPunto = (Button) findViewById(R.id.btnAgregarPunto);
        btnAgregarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(SubirPuntoAdmin.this);
                progress.setTitle("Subiendo");
                progress.setMessage("Espere un momento...");
                progress.show();

                String titulo,descripcion;
                Double latitud,longitud;
                Date fechaCaptura=null;
                titulo=((EditText)findViewById(R.id.edTitulo)).getEditableText().toString();
                if(titulo.isEmpty()){
                    Toast.makeText(getApplicationContext(),"El titulo es obligatorio",Toast.LENGTH_LONG).show();
                    return;
                }
                descripcion=((EditText)findViewById(R.id.edDescripcion)).getEditableText().toString();
                if(descripcion.isEmpty()){
                    Toast.makeText(getApplicationContext(),"La descripción es obligatoria", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    latitud = Double.parseDouble(((EditText) findViewById(R.id.edLatitud)).getEditableText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"La latitud es obligatoria",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    longitud=Double.parseDouble(((EditText)findViewById(R.id.edLongitud)).getEditableText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"La longitud es obligatoria",Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");

                String tituloImg=((EditText)findViewById(R.id.edTitulo)).getEditableText().toString();
                String descripcionImg=((EditText)findViewById(R.id.edDescripcionImg)).getEditableText().toString();

                try {
                    fechaCaptura = formatoDelTexto.parse(((EditText)findViewById(R.id.edFechaCaptura)).getEditableText().toString());
                } catch (ParseException ex) {}

                final Punto punto=new Punto(titulo,descripcion,latitud,longitud,f.getAbsolutePath());
                Multimedia multimedia=new Multimedia(
                        descripcionImg,
                        "",
                        tituloImg,
                        fechaCaptura,
                        new Date(),
                        0);

                punto.setImagen(multimedia);

                GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(context);

                gestorDePuntos.setPunto(punto, new SetPuntoListener() {
                    @Override
                    public void onResponseSetPunto(String response) {
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
                /*
                titulo = ((TextView) findViewById(R.id.edTitulo)).getEditableText().toString();
                descripcion = ((TextView) findViewById(R.id.edDescripcion)).getEditableText().toString();
                Multimedia imagen = new Multimedia(
                        descripcion,
                        f.getAbsolutePath(),
                        titulo,
                        null,
                        new Date(),
                        idPunto
                );*/


            }
        });
        //Fin Subir a Web Service

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
        if (requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            enable_button();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
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
                case BUSCAR_LOCALIZACION:
                    ((EditText)findViewById(R.id.edLatitud)).setText(data.getDoubleExtra("latitud",0)+"");
                    ((EditText)findViewById(R.id.edLongitud)).setText(data.getDoubleExtra("longitud",0)+"");
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(SubirPuntoAdmin.this);
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

    /*
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private final int BUSCAR_LOCALIZACION = 400;

    private ImageView imagenPicasso;
    private Button mOptionButton;
    private LinearLayout mRlView;

    private String mPath;
    private String pathImagen;
    private String nombreImagen;
    private GestorDePuntos gestorDePuntos;
    private Context context;
    private double latitud;
    private double longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_punto_admin);

        context=getApplicationContext();
        imagenPicasso = (ImageView) findViewById(R.id.imagenPicasso);
        mOptionButton = (Button) findViewById(R.id.show_options_button);
        mRlView = (LinearLayout) findViewById(R.id.rl_view);

        if(mayRequestStoragePermission())
            mOptionButton.setEnabled(true);
        else
            mOptionButton.setEnabled(false);


        mOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions();
            }
        });

        Button btnBuscarUbicacion=(Button)findViewById(R.id.btnBuscarUbicacion);
        btnBuscarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuscarLocalizacionPunto.class);
                startActivityForResult(intent,BUSCAR_LOCALIZACION);

            }
        });
        Button btnAgregarPunto=(Button)findViewById(R.id.btnAgregarPunto);
        btnAgregarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo,descripcion;
                Double latitud,longitud;
                Date fechaCaptura=null;
                titulo=((EditText)findViewById(R.id.edTitulo)).getEditableText().toString();
                if(titulo.isEmpty()){
                    Toast.makeText(getApplicationContext(),"El titulo es obligatorio",Toast.LENGTH_LONG).show();
                    return;
                }
                descripcion=((EditText)findViewById(R.id.edDescripcion)).getEditableText().toString();
                if(descripcion.isEmpty()){
                    Toast.makeText(getApplicationContext(),"La descripción es obligatoria",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    latitud = Double.parseDouble(((EditText) findViewById(R.id.edLatitud)).getEditableText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"La latitud es obligatoria",Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    longitud=Double.parseDouble(((EditText)findViewById(R.id.edLongitud)).getEditableText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"La longitud es obligatoria",Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");

                String tituloImg=((EditText)findViewById(R.id.edTitulo)).getEditableText().toString();
                String descripcionImg=((EditText)findViewById(R.id.edDescripcionImg)).getEditableText().toString();

                try {
                    fechaCaptura = formatoDelTexto.parse(((EditText)findViewById(R.id.edFechaCaptura)).getEditableText().toString());
                } catch (ParseException ex) {}

                final Punto punto=new Punto(titulo,descripcion,latitud,longitud,"");
                Multimedia multimedia=new Multimedia(
                        descripcionImg,
                        pathImagen,
                        tituloImg,
                        fechaCaptura,
                        null,
                        0);

                punto.setImagen(multimedia);

                gestorDePuntos=GestorDePuntos.getGestorDePuntos(getApplicationContext());
                gestorDePuntos.setPunto(punto, new SetPuntoListener() {
                    @Override
                    public void onResponseSetPunto(String response) {
                        Toast.makeText(getApplicationContext(),"Respuesta: "+response,Toast.LENGTH_LONG).show();
                        gestorDePuntos.actualizarPuntos(new ActualizarPuntoListener() {
                            @Override
                            public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                                if(puntos==null || puntos.size()==0){
                                    Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context,"El punto se cargó correctamente",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MenuAdmin.class);
                                    startActivity(intent);
                                    SubirPuntoAdmin.super.finish();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private boolean mayRequestStoragePermission() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }


    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(SubirPuntoAdmin.this);
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

    private void openCamera() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},100);
                return;
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
        pathImagen=mPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
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


                    Picasso.with(context)
                            .load("file:"+mPath)
                            .into(imagenPicasso);

                    break;


                case SELECT_PICTURE:
                    Log.e("","entra en selected picture");
                    Uri selectedImage = data.getData();
                    File imageFile = new File(getRealPathFromDocumentUri(this,selectedImage));
                    pathImagen = imageFile.getPath();
                    Log.e("path",pathImagen);
                    Picasso.with(context)
                            .load("file:"+pathImagen)
                            .into(imagenPicasso);
                    break;
                case BUSCAR_LOCALIZACION:
                    ((EditText)findViewById(R.id.edLatitud)).setText(data.getDoubleExtra("latitud",0)+"");
                    ((EditText)findViewById(R.id.edLongitud)).setText(data.getDoubleExtra("longitud",0)+"");

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(SubirPuntoAdmin.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SubirPuntoAdmin.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
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


    public void setLatLng(LatLng latLng){
        latitud=latLng.latitude;
        longitud=latLng.longitude;
    }
}
*/