package com.example.root.trabajofinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorUsuarios;
import com.example.root.trabajofinal.Listeners.ActualizarPuntoListener;
import com.example.root.trabajofinal.Objetos.Punto;

import java.util.ArrayList;
import java.util.Iterator;

public class MenuAdmin extends AppCompatActivity {

    private static int SUBIR_PUNTO =150;
    private static int ELIMINAR_PUNTO =250;
    private static int EDITAR_PUNTO =350;
    private ProgressDialog progress;

    private GestorUsuarios gestorUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestorUsuarios=GestorUsuarios.getInstance(getApplicationContext());
        setContentView(R.layout.activity_menu_admin);

        //Para la lista


        FloatingActionButton btnCerrarSesion=(FloatingActionButton) findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestorUsuarios.cerrarSesion();
                Intent returnIntent=new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        FloatingActionButton btnActualizar = (FloatingActionButton) findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(MenuAdmin.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                    }
                });
            }
        });

        TextView txtUsuario=(TextView)findViewById(R.id.txtUsuario);
        txtUsuario.setText(gestorUsuarios.getUsuario().getUsername());
        txtUsuario.setTypeface(null, Typeface.BOLD);
        Drawable dr = this.getResources().getDrawable(R.drawable.ic_perm_identity_black_24dp);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(this.getResources(), Bitmap.createScaledBitmap(bitmap, 35, 35, true));

        txtUsuario.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        Button btnEditarPunto=(Button)findViewById(R.id.btnEditarPunto);
        btnEditarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MenuEditarPunto.class);
                startActivity(intent);
            }
        });

        Button btnSubirPunto=(Button)findViewById(R.id.btnSubirPunto);
        btnSubirPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SubirPuntoAdmin.class);
                startActivityForResult(intent,SUBIR_PUNTO);
            }
        });
        Button btnEliminarPunto=(Button)findViewById(R.id.btnEliminarPunto);
        btnEliminarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),EliminarPunto.class);
                startActivity(intent);
            }
        });

        Button btnEliminarComentarios=(Button)findViewById(R.id.btnEliminarComentarios);
        btnEliminarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),EliminarComentarios.class);
                startActivity(intent);
            }
        });


        Button btnValidarInfo=(Button)findViewById(R.id.btnValidarInfo);
        btnValidarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ValidarInfoUsuario.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode==SUBIR_PUNTO || requestCode==ELIMINAR_PUNTO||
                    requestCode==EDITAR_PUNTO){
                final ProgressDialog progress;
                progress = new ProgressDialog(MenuAdmin.this);
                progress.setTitle("Actualizando");
                progress.setMessage("Espere un momento...");
                progress.show();
                GestorPuntos gestorPuntos = GestorPuntos.getInstance(getApplicationContext());
                gestorPuntos.actualizarPuntos(new ActualizarPuntoListener() {
                    @Override
                    public void onResponseActualizarPunto(ArrayList<Punto> puntos) {
                        progress.dismiss();
                    }
                });

            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));

            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            //description = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    Context context = v.getContext();
                    GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);
                    Iterator<Punto> iterator= gestorPuntos.getPuntos().iterator();
                    int i=0;
                    int idPunto=-1;
                    while (i<=getAdapterPosition()){
                        idPunto=iterator.next().getId();
                        i++;
                    }
                    intent = new Intent(context, Detalle.class);

                    intent.putExtra("id",idPunto);
                    context.startActivity(intent);


                }
            });
        }
    }

    //Para la lista
    public static class ContentAdapter extends RecyclerView.Adapter<MenuAdmin.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private final int LENGTH ;
        private final String[] mPlaces;
        //private final String[] mPlaceDesc;
        private final String[] mPlaceAvators;
        private GestorMultimedia gestorMultimedia;
        public ContentAdapter(Context context) {


            GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);
            gestorMultimedia = GestorMultimedia.getInstance(context);
            Iterator<Punto> iterator= gestorPuntos.getPuntos().iterator();
            int cantidad= gestorPuntos.getPuntos().size();
            mPlaces=new String[cantidad];
            mPlaceAvators=new String[cantidad];

            LENGTH= gestorPuntos.getPuntos().size();
            int i=0;
            while (iterator.hasNext()){
                Punto punto=iterator.next();
                mPlaces[i]=punto.getTitulo();
                Log.e("Lista",punto.getFoto());
                mPlaceAvators[i]=punto.getFoto() ;
                i++;
            }

            /*
            Resources resources = context.getResources();
            mPlaces = resources.getStringArray(R.array.places);
            mPlaceDesc = resources.getStringArray(R.array.place_desc);
            TypedArray a = resources.obtainTypedArray(R.array.place_avator);
            mPlaceAvators = new Drawable[a.length()];
            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            }
            a.recycle();
            */
        }

        @Override
        public MenuAdmin.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuAdmin.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(MenuAdmin.ViewHolder holder, int position) {
            //holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            // holder.avator.setImageBitmap(
            //        loadImage("/Pictures/testing123.jpg")
            //);
            if(position>=0) {
                holder.name.setText(mPlaces[position % mPlaces.length]);

                //Bitmap bitmap=gestorMultimedia.cargarImagen(mPlaceAvators[position % mPlaceAvators.length]);
                Bitmap bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mPlaceAvators[position % mPlaceAvators.length]),50,50);
                if(bitmap!=null) {
                    holder.avator.setImageBitmap(bitmap);
                }
            }
            //holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
        }



        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
