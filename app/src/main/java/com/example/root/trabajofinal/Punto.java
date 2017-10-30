package com.example.root.trabajofinal;

import android.widget.TextView;

public class Punto {
    private int id;
    private String titulo;
    private double latitud;
    private double longitud;
    private String foto;
    private String foto_web;
    private int estado_foto;

    public Punto(int id, String titulo, double latitud, double longitud, String foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;

    }

    public Punto(int id, String titulo, double latitud, double longitud, String foto,String foto_web, int estado_foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.foto_web=foto_web;
        this.estado_foto=estado_foto;

    }

    public Punto(String titulo, double latitud, double longitud, String foto) {
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
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
        if(latitud!=puntoComp.getLatitud()) return false;
        if(longitud!=puntoComp.getLongitud()) return false;
        if(!foto.equals(puntoComp.getFoto())) return false;
        return true;
    }

}
