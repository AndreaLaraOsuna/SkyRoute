package com.cisneros_guevara_lara.SkyRoute.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** Para la pantalla Resumen. */
@Getter
@AllArgsConstructor
public class ResumenAdmin {
    private long reservacionesSemana;
    private int porcentajeOcupacion;
    private long pendientes;
    private long canceladas;
    private List<String> rutasMasSolicitadas;
}