package com.example.root.trabajofinal.Objetos;

/**
 * Created by root on 25/07/17.
 */

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String username;
    private String contrasena;
    private String email;
    private boolean admin;

    public Usuario(String nombre, String apellido, String username, String contrasena, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.contrasena = contrasena;
        this.email = email;
    }

    public Usuario(int id, String nombre, String apellido, String username, String contrasena, String email,boolean admin) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.contrasena = contrasena;
        this.email = email;
        this.admin=admin;
    }

    public Usuario(String username, String contrasena) {
        this.username = username;
        this.contrasena = contrasena;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getUsername() {
        return username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return admin;
    }
}
