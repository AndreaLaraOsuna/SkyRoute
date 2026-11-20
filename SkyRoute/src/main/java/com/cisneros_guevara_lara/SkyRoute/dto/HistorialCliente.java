package com.cisneros_guevara_lara.SkyRoute.dto;

import com.cisneros_guevara_lara.SkyRoute.model.Cliente;
import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Resumen del cliente para la pantalla de Clientes.
 * No es entidad: no se guarda en la BD, solo se calcula para la vista.
 */
@Getter
@AllArgsConstructor
public class HistorialCliente {
    private Cliente cliente;
    private int totalReservaciones;
    private double totalGastado;
    private long completadas;
    private long canceladas;
    private List<Reservacion> proximas;
    private List<Reservacion> anteriores;
}