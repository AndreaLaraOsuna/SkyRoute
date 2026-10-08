package com.itson.skyroute.dominio;


public class Cliente extends Usuario {

    private String telefono;

    public Cliente() {
    }

    public Cliente(Long id, String nombreCompleto, String correo, String contrasena, String telefono) {
        super(id, nombreCompleto, correo, contrasena);
        this.telefono = telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
