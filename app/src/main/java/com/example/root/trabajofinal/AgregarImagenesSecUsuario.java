package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Listeners.AgregarImagenSecListener;
import com.example.root.trabajofinal.Objetos.Multimedia;
import com.example.root.trabajofinal.Objetos.Punto;
import com.example.root.trabajofinal.Objetos.Usuario;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgregarImagenesSecUsuario extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";
    private Button btnImagen;
    private Context context;
    private int idPunto;
    private String titulo;
    private String descripcion;
    private ImageView imagenPicasso;
    private File f;
    private String mPath;
    private String pathImagen;

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private final int BUSCAR_LOCALIZACION = 400;
    private NavigationView navigationView;
    private ProgressDialog progress;
    private static int PANTALLA_CUALQUIERA = 3000;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.barra_menu);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            LinearLayout contenido=(LinearLayout) findViewById(R.id.contenido);
            contenido.addView(getLayoutInflater().inflate(R.layout.activity_agregar_imagenes_sec, null));
            configurarMenu();

            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            context=getApplicationContext();
            idPunto=getIntent().getIntExtra("id_punto", 0);
            imagenPicasso = (ImageView) findViewById(R.id.imgFotoSec);
            btnImagen = (Button) findViewById(R.id.btnImagen);

            //Subir al Web Service
            Button btnAgregarImagen=(Button)findViewById(R.id.btnAgregarImagen);
            btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (f == null) {
                        Toast.makeText(context, "Ingrese una imagen", Toast.LENGTH_LONG).show();
                    }else if (!AgregarImagenesSecUsuario.isOnline(AgregarImagenesSecUsuario.this)){
                        Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_LONG).show();
                    }else {
                        AlertDialog.Builder builder;

                        builder = new AlertDialog.Builder(getSupportActionBar().getThemedContext());

                        //builder.setTitle(Html.fromHtml("<font color='#3F51B5'>Comentario de</font>"));

                        builder.setMessage(Html.fromHtml("<font color='#000000'>La imagen debe ser aprobada por el administrador</font>"));
                        //builder.setTitle("Eliminar");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                progress = new ProgressDialog(AgregarImagenesSecUsuario.this);
                                progress.setTitle("Subiendo");
                                progress.setMessage("Espere un momento...");
                                progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                progress.show();
                                titulo = ((TextView) findViewById(R.id.edTitulo)).getEditableText().toString();
                                descripcion = ((TextView) findViewById(R.id.edDescripcion)).getEditableText().toString();
                                Multimedia imagen = new Multimedia(
                                        descripcion,
                                        f.getAbsolutePath(),
                                        titulo,
                                        null,
                                        new Date(),
                                        idPunto
                                );
                                GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance(context);
                                Usuario usuario = gestorUsuarios.getUsuario();
                                imagen.setIdUsuario(usuario.getId());
                                GestorMultimedia gestorMultimedia = GestorMultimedia.getInstance(context);
                                gestorMultimedia.setImagenSecUsuario(imagen, new AgregarImagenSecListener() {
                                    @Override
                                    public void onResponseAgregarImagenSecListener(String response) {
                                        Log.e("Resp", response);
                                        progress.dismiss();
                                        if (response.equals("Ok")) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(AgregarImagenesSecUsuario.this, "La imágen se subió correctamente", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            //Toast.makeText(context,"La imagen se subió correctamente",Toast.LENGTH_LONG).show();
                                            Intent returnIntent = new Intent();
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        }else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(context,"Ha ocurrido un error. Intente nuevamente",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        })
                                .show();


                }

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

        @Override
        protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
            GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
            Usuario usuario=gestorUsuarios.getUsuario();
            if(usuario==null){
                finish();
                return;
            }
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
                        Glide.with(context)
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



                            Glide.with(context)
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
            final CharSequence[] option = {"Tomar foto", "Elegir de galería", "Cancelar"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarImagenesSecUsuario.this);
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


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnActualizar) {

            progress = new ProgressDialog(AgregarImagenesSecUsuario.this);
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
                }
            });
            return true;
        }else if(id == R.id.navRealidadAumentada){

            Toast.makeText(getApplicationContext(),"RA",Toast.LENGTH_LONG).show();
            return true;
        }

        else {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.navRealidadAumentada:
                intent = new Intent(getApplicationContext(), RealidadAumentada.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navVistaSatelital:
                intent = new Intent(getApplicationContext(), VistaSatelital.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navLista:
                intent = new Intent(getApplicationContext(), ListaMaterialDesign.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navPuntosCercanos:
                intent = new Intent(getApplicationContext(), PuntosCercanos.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navCerrarSesion:
                GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
                gestorUsuarios.cerrarSesion();
                finish();
                break;
            case R.id.navAgregarPunto:
                intent = new Intent(getApplicationContext(), SubirPuntoAdmin.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.navEdicion:
                intent = new Intent(getApplicationContext(), MenuAdmin.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
            case R.id.itm_iniciar_sesion:
                intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(intent,PANTALLA_CUALQUIERA);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void configurarMenu(){
        GestorUsuarios gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
        Usuario usuario=gestorUsuarios.getUsuario();
        TextView txtUsername=(TextView)navigationView.getHeaderView(0).findViewById(R.id.txtUsernameMain);
        if(navigationView!=null) {
            Menu navMenu = navigationView.getMenu();
            if (usuario == null) {
                navMenu.getItem(0).setVisible(true);
                navMenu.getItem(1).setVisible(false);
                navMenu.getItem(3).setVisible(false);
            }else if(usuario.isAdmin()){
                txtUsername.setText(usuario.getUsername());
                navMenu.getItem(0).setVisible(false);
                navMenu.getItem(1).setVisible(true);
                navMenu.getItem(3).setVisible(true);
            }else{
                txtUsername.setText(usuario.getUsername());
                navMenu.getItem(0).setVisible(false);
                navMenu.getItem(1).setVisible(true);
                navMenu.getItem(3).setVisible(false);
            }
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    }

