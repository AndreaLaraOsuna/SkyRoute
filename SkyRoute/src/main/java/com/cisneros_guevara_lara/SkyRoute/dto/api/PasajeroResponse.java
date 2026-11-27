package com.cisneros_guevara_lara.SkyRoute.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasajeroResponse {

    private String nombreCompleto;
    private String nacionalidad;
    private String asiento;
}
