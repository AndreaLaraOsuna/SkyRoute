package com.cisneros_guevara_lara.SkyRoute.dto.api;

import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReservacionResponse {

    private Long id;
    private String folio;
    private EstadoReservacion estado;
    private LocalDateTime fechaCreacion;
    private MetodoPago metodoPago;
    private double subtotal;
    private double impuestos;
    private double total;
    private VueloResponse vuelo;
    private List<PasajeroResponse> pasajeros;

    public static ReservacionResponse de(Reservacion r) {
        return new ReservacionResponse(r.getId(), r.getFolio(), r.getEstado(), r.getFechaCreacion(), r.getMetodoPago(),
                r.getSubtotal(), r.getImpuestos(), r.getTotal(), VueloResponse.de(r.getVuelo()),
                r.getPasajeros().stream()
                        .map(p -> new PasajeroResponse(p.getNombreCompleto(), p.getNacionalidad(), p.getAsiento()))
                        .toList());
    }
}
