package com.example.root.trabajofinal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Listeners.EditarPuntoListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DetalleEditarPunto extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id";
    private Punto punto,puntoEditado;
    private GestorMultimedia gestorMultimedia;
    private Context context;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private final int BUSCAR_LOCALIZACION = 400;
    //private ImageView mSetImage;
    private Button mOptionButton;
    private LinearLayout mRlView;
    private String mPath;
    private String pathImagen;
    private String nombreImagen;
    private GestorPuntos gestorPuntos;
    //private ImageView imgFotoActual;
    private ImageView imagenPicasso;
    private int rotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_editar_punto);
        context=getApplicationContext();
        imagenPicasso=(ImageView)findViewById(R.id.imagenPicasso);
        //mSetImage = (ImageView) findViewById(R.id.set_picture);
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
        gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
        punto= gestorPuntos.getPunto(getIntent().getIntExtra(EXTRA_POSITION, 0));

        llenarCampos();

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

        Button btnEditarPunto=(Button)findViewById(R.id.btnEditarPunto);
        btnEditarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo,descripcion;
                Double latitud,longitud;
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
                puntoEditado=new Punto(
                        punto.getId(),
                        titulo,
                        descripcion,
                        latitud,
                        longitud,
                        ""

                );
                String tituloImg=((EditText)findViewById(R.id.edTituloImg)).getEditableText().toString();
                String descripcionImg=((EditText)findViewById(R.id.edDescripcionImg)).getEditableText().toString();



                 Multimedia imagenEditada=new Multimedia(
                        descripcionImg,
                        pathImagen,
                        tituloImg,
                        null,
                        new Date(),
                        punto.getId()
                );
                puntoEditado.setImagen(imagenEditada);
                final ProgressDialog progress;
                progress = new ProgressDialog(DetalleEditarPunto.this);
                progress.setTitle("Subiendo");
                progress.setMessage("Espere un momento...");
                progress.show();
                gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                gestorPuntos.editarPunto(puntoEditado, new EditarPuntoListener() {
                    @Override
                    public void onResponseEditarPunto(String response) {
                        progress.dismiss();
                        if(response.equals("Ok")){
                            Intent returnIntent=new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }else {
                            Toast.makeText(context,"El punto no se cargó correctamente",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void llenarCampos(){
        EditText edTitulo=(EditText)findViewById(R.id.edTitulo);
        EditText edDescripcion=(EditText)findViewById(R.id.edDescripcion);
        EditText edLatitud=(EditText)findViewById(R.id.edLatitud);
        EditText edLongitud=(EditText)findViewById(R.id.edLongitud);
        //imgFotoActual=(ImageView)findViewById(R.id.set_picture);

        edTitulo.setText(punto.getTitulo());
        edDescripcion.setText(punto.getDescripcion());
        edLatitud.setText(punto.getLatitud()+"");
        edLongitud.setText(punto.getLongitud()+"");

        File imgFile = new  File(punto.getFoto());
        if(imgFile.exists()){
            Picasso.with(context).load("file:"+punto.getFoto())
                    .into(imagenPicasso);
        }
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
        gestorMultimedia.getImagenPortada(punto.getId(), new ImagenListener() {
            @Override
            public void onResponseImagen(Multimedia multimedia) {
                SimpleDateFormat dt1=new SimpleDateFormat("dd-MM-yyyy");
                EditText edTituloImg=(EditText)findViewById(R.id.edTituloImg);
                EditText edDescripcionImg=(EditText)findViewById(R.id.edDescripcionImg);
                try {
                    edTituloImg.setText(multimedia.getTitulo());
                }catch(Exception e){}
                try {
                    edDescripcionImg.setText(multimedia.getDescripcion());
                }catch(Exception e){}


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
        final CharSequence[] option = {"Tomar foto", "Elegir de galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(DetalleEditarPunto.this);
        builder.setTitle("Elegir una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    openCamera();

                }else if(option[which] == "Elegir de galería"){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(getApplicationContext());
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


                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    //mSetImage.setImageBitmap(gestorMultimedia.rotarImagen(bitmap));
                    Picasso.with(context)
                            .load("file:"+mPath)
                            .into(imagenPicasso);

                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();

                    Log.e("Subir punto","path "+getRealPathFromDocumentUri(this,path));

                    /*mSetImage.setImageBitmap(
                            //gestorMultimedia.rotarImagen(
                                    gestorMultimedia.cargarImagen(getRealPathFromDocumentUri(this,path))
                      //)
                    );*/
                    Log.e("","file:"+path);

                    Picasso.with(context)
                            .load("file:"+getRealPathFromDocumentUri(this,path))
                            .into(imagenPicasso);
                    pathImagen=getRealPathFromDocumentUri(this,path);
                    rotacion=getCameraPhotoOrientation(pathImagen);
                    Log.e("imagen rotation: ","vlor: "+rotacion);

                    /*mSetImage.setImageURI(path);*/

                    break;

                case BUSCAR_LOCALIZACION:
                    ((EditText)findViewById(R.id.edLatitud)).setText(data.getDoubleExtra("latitud",0)+"");
                    ((EditText)findViewById(R.id.edLongitud)).setText(data.getDoubleExtra("longitud",0)+"");
                    break;
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(DetalleEditarPunto.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetalleEditarPunto.this);
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

    public int getCameraPhotoOrientation( String imagePath){
        int rotate = 0;
        try {
            //context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}

