package com.cisneros_guevara_lara.SkyRoute.dto.api;

import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class VueloResponse {

    private Long id;
    private String codigoRuta;
    private String origen;
    private String destino;
    private LocalDate fecha;
    private LocalTime horaSalida;
    private int duracionMinutos;
    private double tarifa;
    private int asientos;

    public static VueloResponse de(Vuelo v) {
        return new VueloResponse(v.getId(), v.getRuta().getCodigo(), v.getRuta().getOrigen(), v.getRuta().getDestino(),
                v.getFecha(), v.getHoraSalida(), v.getRuta().getDuracionMinutos(), v.getRuta().getTarifa(),
                v.getAvion().getAsientos());
    }
}
