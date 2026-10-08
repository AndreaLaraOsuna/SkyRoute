package com.itson.skyroute.dominio;


public class Ruta {

    private Long id;
    private String codigo;
    private String origen;
    private String destino;
    private int duracionMinutos;
    private double tarifa;

    public Ruta() {
    }

    public Ruta(Long id, String codigo, String origen, String destino, int duracionMinutos, double tarifa) {
        this.id = id;
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.duracionMinutos = duracionMinutos;
        this.tarifa = tarifa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

   

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }
}
