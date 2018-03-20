package com.example.root.trabajofinal.Objetos;

import android.util.Log;

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
    private String fechaUltMod;

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


    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean equals(Punto puntoComp){
        Log.e("comp1",id+" - "+fechaUltMod);
        Log.e("comp2",puntoComp.getId()+" - "+puntoComp.fechaUltMod);
        return fechaUltMod.equals(puntoComp.getFechaUltMod());
        /*if(puntoComp==null) return false;
        Log.e("","is nul");
        if(id!=puntoComp.getId()) return false;
        Log.e("","en id");
        if(!titulo.equals(puntoComp.getTitulo())) return false;
        Log.e("","en tit");
        if(!descripcion.equals(puntoComp.getDescripcion())) return false;
        Log.e("","en desc");
        if(latitud!=puntoComp.getLatitud()) return false;
        Log.e("","en lat");
        if(longitud!=puntoComp.getLongitud()) return false;
        Log.e("","en lon");
        if(!fotoWeb.equals(puntoComp.getFotoWeb())) return false;
        Log.e("","en fot");
        return true;*/

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
