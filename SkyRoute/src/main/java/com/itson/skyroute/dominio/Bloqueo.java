package com.itson.skyroute.dominio;

import java.time.LocalDate;
import java.time.LocalTime;


public class Bloqueo {

    private Long id;
    private TipoBloqueo tipo;
    private AlcanceBloqueo alcance;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivo;
    private Ruta ruta;
    private Avion avion;

    public Bloqueo() {
    }

    public Bloqueo(Long id, TipoBloqueo tipo, AlcanceBloqueo alcance, LocalDate fechaInicio, LocalDate fechaFin, LocalTime horaInicio, LocalTime horaFin, String motivo, Ruta ruta, Avion avion) {
        this.id = id;
        this.tipo = tipo;
        this.alcance = alcance;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
        this.ruta = ruta;
        this.avion = avion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoBloqueo getTipo() {
        return tipo;
    }

    public void setTipo(TipoBloqueo tipo) {
        this.tipo = tipo;
    }

    public AlcanceBloqueo getAlcance() {
        return alcance;
    }

    public void setAlcance(AlcanceBloqueo alcance) {
        this.alcance = alcance;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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
}
