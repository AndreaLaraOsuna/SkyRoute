package com.cisneros_guevara_lara.SkyRoute.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorDTO {

    private String mensaje;
    private List<String> detalles;
    private LocalDateTime fecha;
}
