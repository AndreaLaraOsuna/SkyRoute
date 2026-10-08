package com.itson.skyroute.dominio;


public class Administrador extends Usuario {

    public Administrador() {
    }

    public Administrador(Long id, String nombreCompleto, String correo, String contrasena) {
        super(id, nombreCompleto, correo, contrasena);
    }
}
