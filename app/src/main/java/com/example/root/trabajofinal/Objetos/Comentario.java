package com.example.root.trabajofinal.Objetos;

import java.util.Date;

/**
 * Created by pedro on 19/02/18.
 */

public class Comentario {
    private int id;
    private String texto;
    private int idAsociado;
    private int idUsuario;
    private String username;
    private Date fecha;

    public Comentario( String texto, int idAsociado, int idUsuario, Date fecha) {
        this.texto = texto;
        this.idAsociado = idAsociado;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
    }

    public Comentario(int id, String texto, int idAsociado, int idUsuario, String username, Date fecha) {
        this.id = id;
        this.texto = texto;
        this.idAsociado = idAsociado;
        this.idUsuario = idUsuario;
        this.username = username;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getIdAsociado() {
        return idAsociado;
    }

    public void setIdAsociado(int idAsociado) {
        this.idAsociado = idAsociado;
    }

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
