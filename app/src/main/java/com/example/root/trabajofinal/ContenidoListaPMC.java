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
import com.example.root.trabajofinal.Objetos.IndiceRtree;
import com.example.root.trabajofinal.Objetos.Punto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class ContenidoListaPMC extends Fragment {

    private GestorPuntos gestorPuntos;
    private ArrayList<Punto> puntosMasCercanos;
    private double latitud;
    private double longitud;
    private double distanciaKm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        gestorPuntos = GestorPuntos.getInstance(getActivity().getApplicationContext());
        IndiceRtree rtree=new IndiceRtree(gestorPuntos.getPuntos());

        latitud=getActivity().getIntent().getDoubleExtra("latitud", 0);
        longitud=getActivity().getIntent().getDoubleExtra("longitud", 0);
        distanciaKm=getActivity().getIntent().getDoubleExtra("distanciaKm", 0);
        puntosMasCercanos=rtree.getPuntosMasCercanos(latitud,longitud,distanciaKm);
        //if(gestorPuntos.size()>0) {
        ContenidoListaPMC.ContentAdapter adapter = new ContenidoListaPMC.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
            description = (TextView) itemView.findViewById(R.id.list_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    Context context = v.getContext();
                    Iterator<Punto> iterator=puntosMasCercanos.iterator();
                    int i=0;
                    int idPunto=-1;
                    while (i<=getAdapterPosition()){
                        idPunto=iterator.next().getId();
                        i++;
                    }
                    intent = new Intent(context, Detalle.class);

                    intent.putExtra("id",idPunto);
                    getActivity().startActivity(intent);



                }
            });
        }
    }
    /**
     * Adapter to display recycler view.
     */
    public  class ContentAdapter extends RecyclerView.Adapter<ContenidoListaPMC.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private final int LENGTH ;
        private final String[] mPlaces;
        private final String[] mPlaceDesc;
        private final String[] mPlaceAvators;
        private GestorMultimedia gestorMultimedia;
        public ContentAdapter(Context context) {


            GestorPuntos gestorPuntos = GestorPuntos.getInstance(context);
            gestorMultimedia = GestorMultimedia.getInstance(context);
            Iterator<Punto> iterator=puntosMasCercanos.iterator();
            int cantidad=puntosMasCercanos.size();
            mPlaces=new String[cantidad];
            mPlaceDesc=new String[cantidad];
            mPlaceAvators=new String[cantidad];

            LENGTH=puntosMasCercanos.size();
            int i=0;
            while (iterator.hasNext()){
                Punto punto=iterator.next();
                mPlaces[i]=punto.getTitulo();
                DecimalFormat formato = new DecimalFormat("0.00");
                mPlaceDesc[i]=formato.format(IndiceRtree.distance(latitud,longitud,punto.getLatitud(),punto.getLongitud()))+" Km";
                Log.e("Lista",punto.getFoto());
                mPlaceAvators[i]=punto.getFoto() ;
                i++;
            }

        }

        @Override
        public ContenidoListaPMC.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContenidoListaPMC.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ContenidoListaPMC.ViewHolder holder, int position) {
            //holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            // holder.avator.setImageBitmap(
            //        loadImage("/Pictures/testing123.jpg")
            //);
            if(position>=0) {
                holder.name.setText(mPlaces[position % mPlaces.length]);
                holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
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
