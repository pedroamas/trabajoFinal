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

import com.bumptech.glide.Glide;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Listeners.EditarMultimediaListener;
import com.example.root.trabajofinal.Listeners.EliminarImagenSecListener;
import com.example.root.trabajofinal.Listeners.ImagenListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.TiposEnumerados.TipoMultimedia;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditarImagenSec extends AppCompatActivity {

    public static final String EXTRA_POSITION = "id";
    private Multimedia multimedia,multimediaEditado;
    private GestorMultimedia gestorMultimedia;
    private Context context;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
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
    private int idImagen;
    private int idPunto;
    private SimpleDateFormat dt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_imagen_sec);
        idImagen=getIntent().getIntExtra("id_imagen", 0);
        idPunto=getIntent().getIntExtra("id_punto", 0);
        context=getApplicationContext();
        imagenPicasso = (ImageView) findViewById(R.id.imgFotoSec);
        mOptionButton = (Button) findViewById(R.id.btnImagen);
        mRlView = (LinearLayout) findViewById(R.id.content);
        Button btnAgregarImagen=(Button)findViewById(R.id.btnAgregarImagen);
        dt1=new SimpleDateFormat("dd-MM-yyyy");

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

        llenarCampos();

        Button btnEliminarImagen=(Button)findViewById(R.id.btnEliminarImagen);
        btnEliminarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestorMultimedia = GestorMultimedia.getInstance(context);
                gestorMultimedia.eliminarImagenSec(idImagen, new EliminarImagenSecListener() {
                    @Override
                    public void onResponseEliminarImagenSecListener(String response) {
                        if(response.equals("Ok")){
                            Toast.makeText(context,"La imagen ha sido borrada correctamente",
                                    Toast.LENGTH_LONG).show();

                            Intent returnIntent=new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }else{
                            Toast.makeText(context,"Ocurrió un error al eliminar la imagen",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        Button btnEditarImagen=(Button)findViewById(R.id.btnEditarImagen);
        btnEditarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo;
                String descripcion;
                titulo=((EditText)findViewById(R.id.edTitulo)).getEditableText().toString();
                Log.e("",titulo);
                descripcion=((EditText)findViewById(R.id.edDescripcion)).getEditableText().toString();
                Log.e("",descripcion);
                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");


                Log.e("",titulo+" - "+descripcion);


                 multimediaEditado=new Multimedia(

                         idImagen,
                        descripcion,
                        pathImagen,
                        titulo,
                        null,
                        null,
                        0,
                         TipoMultimedia.imagen);

                 final ProgressDialog progress;
                progress = new ProgressDialog(EditarImagenSec.this);
                progress.setTitle("Editando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                gestorMultimedia.editarImagenSec(multimediaEditado, new EditarMultimediaListener() {
                    @Override
                    public void onResponseEditarMultimedia(String response) {
                        if(response.equals("Ok")) {
                            //Toast.makeText(context, "Editado ", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                            Intent returnIntent=new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditarImagenSec.this);
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    openCamera();

                }else if(option[which] == "Elegir de galeria"){
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
                    Glide.with(context)
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

                    Glide.with(context)
                            .load("file:"+getRealPathFromDocumentUri(this,path))
                            .into(imagenPicasso);
                    pathImagen=getRealPathFromDocumentUri(this,path);
                    //rotacion=getCameraPhotoOrientation(pathImagen);
                    Log.e("imagen rotation: ","vlor: "+rotacion);

                    /*mSetImage.setImageURI(path);*/

                    break;

            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(EditarImagenSec.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarImagenSec.this);
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

    public void llenarCampos(){
        GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
        gestorMultimedia.getImagen(idImagen, new ImagenListener() {
            @Override
            public void onResponseImagen(Multimedia multimedia1) {
                multimedia=multimedia1;
                Log.e("","Si respondio eeeeeeeeeeeeeeeeeeeeeeee");
                EditText edTitulo=(EditText)findViewById(R.id.edTitulo);
                EditText edDescripcion=(EditText)findViewById(R.id.edDescripcion);
                ImageView imgFotoSec=(ImageView)findViewById(R.id.imgFotoSec);
                Glide.with(context)
                        .load(multimedia.getPath())
                        .into(imgFotoSec);
                edTitulo.setText(multimedia1.getTitulo());
                edDescripcion.setText(multimedia1.getDescripcion());

            }
        });
    }
}

