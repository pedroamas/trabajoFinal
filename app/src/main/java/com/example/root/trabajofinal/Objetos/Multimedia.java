package com.example.root.trabajofinal.Objetos;

import com.example.root.trabajofinal.TiposEnumerados.TipoMultimedia;

import java.util.Date;

/**
 * Created by root on 06/11/17.
 */

public class Multimedia {
    private int id;
    private String descripcion;
    private String path;
    private String titulo;
    private Date fechaCaptura;
    private Date fechaSubida;
    private int idPunto;
    private int idUsuario;
    private String username;
    private String tituloPunto;



    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private TipoMultimedia tipo;


    public Multimedia(int id, String descripcion, String path, String titulo, Date fechaCaptura, Date fechaSubida, int idPunto,TipoMultimedia tipo) {
        this.id = id;
        this.descripcion = descripcion;
        this.path = path;
        this.titulo = titulo;
        this.fechaCaptura = fechaCaptura;
        this.fechaSubida = fechaSubida;
        this.idPunto = idPunto;
        this.tipo=tipo;
    }

    public Multimedia(String descripcion, String path, String titulo, Date fechaCaptura, Date fechaSubida, int idPunto) {
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

    public String getTituloPunto() {
        return tituloPunto;
    }

    public void setTituloPunto(String tituloPunto) {
        this.tituloPunto = tituloPunto;
    }

    public String getNombreArchivo(){
        String file = path.substring(path.lastIndexOf('/') + 1);
        return file;
    }
}
