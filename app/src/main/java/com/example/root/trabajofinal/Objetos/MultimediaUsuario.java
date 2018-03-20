package com.example.root.trabajofinal.Objetos;

import com.example.root.trabajofinal.TiposEnumerados.TipoMultimedia;

import java.util.Date;

/**
 * Created by pedro on 23/02/18.
 */

public class MultimediaUsuario {
    private int id;
    private String descripcion;
    private String path;
    private String titulo;
    private Date fechaCaptura;
    private Date fechaSubida;
    private int idPunto;
    private TipoMultimedia tipo;

    public MultimediaUsuario(int id, String descripcion, String path, String titulo, Date fechaCaptura, Date fechaSubida, int idPunto,TipoMultimedia tipo) {
        this.id = id;
        this.descripcion = descripcion;
        this.path = path;
        this.titulo = titulo;
        this.fechaCaptura = fechaCaptura;
        this.fechaSubida = fechaSubida;
        this.idPunto = idPunto;
        this.tipo=tipo;
    }

    public MultimediaUsuario(String descripcion, String path, String titulo, Date fechaCaptura, Date fechaSubida, int idPunto) {
        this.descripcion = descripcion;
        this.path = path;
        this.titulo = titulo;
        this.fechaCaptura = fechaCaptura;
        this.fechaSubida = fechaSubida;
        this.idPunto = idPunto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFechaCaptura(Date fechaCaptura) {
        this.fechaCaptura = fechaCaptura;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPath() {
        return path;
    }

    public String getTitulo() {
        return titulo;
    }

    public Date getFechaCaptura() {
        return fechaCaptura;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public int getIdPunto() {
        return idPunto;
    }

    public String getNombreArchivo(){
        String file = path.substring(path.lastIndexOf('/') + 1);
        return file;
    }
}