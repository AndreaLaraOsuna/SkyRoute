package com.itson.skyroute.dominio;

import java.time.LocalDate;
import java.time.LocalTime;


public class Vuelo {

    private Long id;
    private Ruta ruta;
    private Avion avion;
    private LocalDate fecha;
    private LocalTime horaSalida;

    public Vuelo() {
    }

    public Vuelo(Long id, Ruta ruta, Avion avion, LocalDate fecha, LocalTime horaSalida) {
        this.id = id;
        this.ruta = ruta;
        this.avion = avion;
        this.fecha = fecha;
        this.horaSalida = horaSalida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }
}
