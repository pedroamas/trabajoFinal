package com.example.root.trabajofinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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

import com.example.root.trabajofinal.Gestores.GestorPuntos;
import com.example.root.trabajofinal.Gestores.GestorMultimedia;
import com.example.root.trabajofinal.Objetos.Punto;

import java.util.Iterator;

/**
 * Created by pedro on 08/02/18.
 */

public class ContenidoListaEditarMultimedia extends Fragment {

    private GestorPuntos gestorPuntos;
    private GestorMultimedia gestorMultimedia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        gestorPuntos = GestorPuntos.getInstance(getActivity().getApplicationContext());
        gestorPuntos.getPuntos();
        //if(gestorPuntos.size()>0) {
        ContenidoListaEditarMultimedia.ContentAdapter adapter = new ContenidoListaEditarMultimedia.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;
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
                    intent = new Intent(context, DetalleEditarMultimedia.class);

                    intent.putExtra("id",idPunto);
                    context.startActivity(intent);



                }
            });
        }
    }
    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ContenidoListaEditarMultimedia.ViewHolder> {
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

        }

        @Override
        public ContenidoListaEditarMultimedia.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContenidoListaEditarMultimedia.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ContenidoListaEditarMultimedia.ViewHolder holder, int position) {
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

