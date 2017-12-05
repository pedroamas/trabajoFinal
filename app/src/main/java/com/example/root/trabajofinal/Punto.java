package com.example.root.trabajofinal;

import android.widget.TextView;

public class Punto {
    private int id;
    private String titulo;
    private String descripcion;
    private double latitud;
    private double longitud;
    private String foto;
    private String fotoWeb;
    private int estado_foto;
    private Multimedia imagen;



    public Punto(int id, String titulo,String descripcion, double latitud, double longitud, String foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.descripcion=descripcion;

    }

    public Punto(int id, String titulo, String descripcion, double latitud, double longitud, String foto,String fotoWeb, int estado_foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.fotoWeb=fotoWeb;
        this.estado_foto=estado_foto;
        this.descripcion=descripcion;

    }

    public Punto(String titulo, String descripcion, double latitud, double longitud, String foto) {
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.descripcion=descripcion;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getFoto() {
        return foto;
    }


    public boolean equals(Punto puntoComp){
        if(id!=puntoComp.getId()) return false;
        if(!titulo.equals(puntoComp.getTitulo())) return false;
        if(!descripcion.equals(puntoComp.getDescripcion())) return false;
        if(latitud!=puntoComp.getLatitud()) return false;
        if(longitud!=puntoComp.getLongitud()) return false;
        if(!foto.equals(puntoComp.getFoto())) return false;
        if(!fotoWeb.equals(puntoComp.getFotoWeb())) return false;
        return true;
    }

    public void setImagen(Multimedia imagen) {
        this.imagen = imagen;
    }

    public Multimedia getImagen() {
        return imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFotoWeb() {
        return fotoWeb;
    }

    public void setFotoWeb(String fotoWeb) {
        this.fotoWeb = fotoWeb;
    }
}
