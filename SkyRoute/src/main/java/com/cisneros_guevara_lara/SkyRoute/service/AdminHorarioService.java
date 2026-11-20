package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import com.cisneros_guevara_lara.SkyRoute.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminHorarioService {

    private final VueloRepository vueloRepository;

    @Transactional(readOnly = true)
    public List<Vuelo> listarVuelosProximos() {
        return vueloRepository.findByFechaGreaterThanEqualOrderByFechaAscHoraSalidaAsc(LocalDate.now());
    }

    /** Pone la misma fecha y hora nueva a todos los vuelos seleccionados. Regresa cuántos cambió. */
    @Transactional
    public int reprogramar(List<Long> vueloIds, LocalDate fecha, LocalTime hora) {
        if (vueloIds == null || vueloIds.isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos un vuelo.");
        }
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Selecciona la fecha y la hora.");
        }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora deben ser posteriores a este momento.");
        }

        List<Vuelo> vuelos = vueloRepository.findAllById(vueloIds);
        for (Vuelo vuelo : vuelos) {
            vuelo.setFecha(fecha);
            vuelo.setHoraSalida(hora);
        }
        vueloRepository.saveAll(vuelos);
        return vuelos.size();
    }
}