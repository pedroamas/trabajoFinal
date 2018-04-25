package com.example.root.trabajofinal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Listeners.ImagenesListener;
import com.example.root.trabajofinal.Objetos.Multimedia;

import java.util.ArrayList;
import java.util.Iterator;

public class ContenidoValidarInfoUsuario extends Fragment {

    private static int VALIDAR_INFO=700;
    private GestorMultimedia gestorMultimedia;
    private RecyclerView recyclerView;
    private ArrayList<Multimedia> imagenesPendientes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        gestorMultimedia = GestorMultimedia.getInstance(getActivity().getApplicationContext());
        Log.e("Procesame","esto");
        gestorMultimedia.getImagenesUsuariosPendientes(new ImagenesListener() {
            @Override
            public void onResponseImagenes(ArrayList<Multimedia> imagenes) {
                //gestorDePuntos.getPuntos();
                imagenesPendientes=imagenes;
                Log.e("Procesame","esto");

                //if(gestorDePuntos.size()>0) {
                ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });


        return recyclerView;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
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
                    Context context =getActivity().getApplicationContext();
                    GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);

                    intent = new Intent(context, AprobarImgSecUsuarios.class);

                    intent.putExtra("id_imagen",(imagenesPendientes.get(getAdapterPosition())).getId());
                    startActivityForResult(intent,VALIDAR_INFO);



                }
            });
        }
    }
    /**
     * Adapter to display recycler view.
     */
    public  class ContentAdapter extends RecyclerView.Adapter<ContenidoValidarInfoUsuario.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private final int LENGTH ;
        private final String[] mPlaces;
        //private final String[] mPlaceDesc;
        private final String[] mPlaceAvators;
        private GestorMultimedia gestorMultimedia;
        public ContentAdapter(Context context) {


            //GestorPuntos gestorDePuntos=GestorPuntos.getInstance(context);
            //gestorMultimedia=GestorMultimedia.getInstance(context);
            Iterator<Multimedia> iterator=imagenesPendientes.iterator();
            int cantidad=imagenesPendientes.size();
            mPlaces=new String[cantidad];
            mPlaceAvators=new String[cantidad];

            LENGTH=imagenesPendientes.size();
            int i=0;
            while (iterator.hasNext()){
                Multimedia imagen=iterator.next();
                mPlaces[i]=imagen.getUsername();
                Log.e("Lista",imagen.getPath());
                mPlaceAvators[i]=imagen.getPath() ;
                i++;
            }

        }

        @Override
        public ContenidoValidarInfoUsuario.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContenidoValidarInfoUsuario.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ContenidoValidarInfoUsuario.ViewHolder holder, int position) {
            //holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            // holder.avator.setImageBitmap(
            //        loadImage("/Pictures/testing123.jpg")
            //);
            if(position>=0) {
                holder.name.setText(mPlaces[position % mPlaces.length]);

                //Bitmap bitmap=gestorMultimedia.cargarImagen(mPlaceAvators[position % mPlaceAvators.length]);
                /*Bitmap bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mPlaceAvators[position % mPlaceAvators.length]),50,50);
                if(bitmap!=null) {
                    holder.avator.setImageBitmap(bitmap);
                }*/
            }
            //holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
        }



        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}

