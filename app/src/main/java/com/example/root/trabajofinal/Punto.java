package com.example.root.trabajofinal;

public class Punto {
    private int id;
    private String titulo;
    private double latitud;
    private double longitud;
    private String foto;

    public Punto(int id, String titulo, double latitud, double longitud, String foto) {
        this.id = id;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;

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

    public String getDescripcion(){return "Deberia ser la descripcion de internet";}

    public boolean equals(Punto puntoComp){
        if(id!=puntoComp.getId()) return false;
        if(!titulo.equals(puntoComp.getTitulo())) return false;
        if(latitud!=puntoComp.getLatitud()) return false;
        if(longitud!=puntoComp.getLongitud()) return false;
        if(!foto.equals(puntoComp.getFoto())) return false;
        return true;
    }

}
