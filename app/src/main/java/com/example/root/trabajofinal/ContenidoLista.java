package com.example.root.trabajofinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class ContenidoLista extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        //if(gestorDePuntos.size()>0) {
            ContenidoLista.ContentAdapter adapter = new ContenidoLista.ContentAdapter(recyclerView.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //}
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            //avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            //description = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    Context context = v.getContext();
                    GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(context);
                    Iterator<Punto> iterator=gestorDePuntos.getPuntos().iterator();
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
    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ContenidoLista.ViewHolder> {
        // Set numbers of List in RecyclerView.
        private final int LENGTH ;
        private final String[] mPlaces;
        //private final String[] mPlaceDesc;
         private final String[] mPlaceAvators;
        public ContentAdapter(Context context) {


            GestorDePuntos gestorDePuntos=GestorDePuntos.getGestorDePuntos(context);

            Iterator<Punto> iterator=gestorDePuntos.getPuntos().iterator();
            mPlaces=new String[gestorDePuntos.getPuntos().size()];
            mPlaceAvators=new String[gestorDePuntos.getPuntos().size()];

            LENGTH=gestorDePuntos.getPuntos().size();
            int i=0;
            while (iterator.hasNext()){
                Punto punto=iterator.next();
                mPlaces[i]=punto.getTitulo();
                mPlaceAvators[i++]=punto.getFoto();
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
        public ContenidoLista.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ContenidoLista.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ContenidoLista.ViewHolder holder, int position) {
            //holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
           // holder.avator.setImageBitmap(
            //        loadImage("/Pictures/testing123.jpg")
            //);
            if(position>=0) {
                holder.name.setText(mPlaces[position % mPlaces.length]);
                //holder.avator.setImageBitmap(GestorImagenes.cargarImagen(mPlaceAvators[0]));
            }
            //holder.description.setText(mPlaceDesc[position % mPlaceDesc.length]);
        }

        private Bitmap loadImage(String image_location) {
            URL imageURL = null;
            Bitmap bitmap=null;
            if (image_location != null) {
                try {
                    imageURL = new URL(image_location);
                    HttpURLConnection connection = (HttpURLConnection) imageURL
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);// Convert to bitmap
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //set any default
            }
            return bitmap;
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
