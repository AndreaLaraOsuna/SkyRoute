package com.itson.skyroute.dominio;

import java.time.LocalDateTime;
import java.util.List;


public class Reservacion {

    private Long id;
    private String folio;
    private LocalDateTime fechaCreacion;
    private EstadoReservacion estado;
    private Cliente cliente;
    private Vuelo vuelo;
    private List<Pasajero> pasajeros;
    private MetodoPago metodoPago;
    private double subtotal;
    private double impuestos;
    private double cargoEquipaje;
    private double total;

    public Reservacion() {
    }

    public Reservacion(Long id, String folio, LocalDateTime fechaCreacion, EstadoReservacion estado, Cliente cliente, Vuelo vuelo, List<Pasajero> pasajeros, MetodoPago metodoPago, double subtotal, double impuestos, double cargoEquipaje, double total) {
        this.id = id;
        this.folio = folio;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.cliente = cliente;
        this.vuelo = vuelo;
        this.pasajeros = pasajeros;
        this.metodoPago = metodoPago;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.cargoEquipaje = cargoEquipaje;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoReservacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoReservacion estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public List<Pasajero> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<Pasajero> pasajeros) {
        this.pasajeros = pasajeros;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }

    public double getCargoEquipaje() {
        return cargoEquipaje;
    }

    public void setCargoEquipaje(double cargoEquipaje) {
        this.cargoEquipaje = cargoEquipaje;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
