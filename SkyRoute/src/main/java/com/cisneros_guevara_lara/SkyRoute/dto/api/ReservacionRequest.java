package com.cisneros_guevara_lara.SkyRoute.dto.api;

import com.cisneros_guevara_lara.SkyRoute.model.enums.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReservacionRequest {

    @NotNull(message = "El vuelo es obligatorio")
    private Long vueloId;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotEmpty(message = "Debes registrar al menos un pasajero")
    @Valid
    private List<PasajeroRequest> pasajeros;
}
