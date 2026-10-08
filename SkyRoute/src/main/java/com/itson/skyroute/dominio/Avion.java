package com.itson.skyroute.dominio;

public class Avion {

    private Long id;
    private String matricula;
    private int asientos;

    public Avion() {
    }

    public Avion(Long id, String matricula, int asientos) {
        this.id = id;
        this.matricula = matricula;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getAsientos() {
        return asientos;
    }

    public void setAsientos(int asientos) {
        this.asientos = asientos;
    }
}
