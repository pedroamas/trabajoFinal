package com.example.root.trabajofinal.Objetos;

import android.util.Log;

public class Punto {
    private int id;
    private String titulo;
    private String descripcion;
    private double latitud;
    private double longitud;
    private String fotoPortada;
    private String pathFotoWeb;
    private int estado_foto;
    private Multimedia imagen;
    private String fechaUltMod;

    public int getCantValidar() {
        return cantValidar;
    }

    public void setCantValidar(int cantValidar) {
        this.cantValidar = cantValidar;
    }

    private int cantValidar=0;

    public String getFechaUltMod() {
        return fechaUltMod;
    }

    public void setFechaUltMod(String fechaUltMod) {
        this.fechaUltMod = fechaUltMod;
    }

    public Punto(int id, String titulo, String descripcion, double latitud, double longitud, String foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fotoPortada = foto;
        this.descripcion=descripcion;

    }

    public Punto(int id, String titulo, String descripcion, double latitud, double longitud, String foto, String pathFotoWeb, int estado_foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fotoPortada = foto;
        this.pathFotoWeb = pathFotoWeb;
        this.estado_foto=estado_foto;
        this.descripcion=descripcion;

    }

    public Punto(String titulo, String descripcion, double latitud, double longitud, String foto) {
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fotoPortada = foto;
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
        return fotoPortada;
    }


    public void setFoto(String foto) {
        this.fotoPortada = foto;
    }

    public boolean equals(Punto puntoComp){
        Log.e("comp1",id+" - "+fechaUltMod);
        Log.e("comp2",puntoComp.getId()+" - "+puntoComp.fechaUltMod);
        return fechaUltMod.equals(puntoComp.getFechaUltMod());
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

    public String getPathFotoWeb() {
        return pathFotoWeb;
    }

    public void setPathFotoWeb(String pathFotoWeb) {
        this.pathFotoWeb = pathFotoWeb;
    }
}
