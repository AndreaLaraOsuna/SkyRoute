package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.Avion;
import com.cisneros_guevara_lara.SkyRoute.model.Ruta;
import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import com.cisneros_guevara_lara.SkyRoute.repository.AvionRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.RutaRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    private final RutaRepository rutaRepository;
    private final AvionRepository avionRepository;

    @Transactional(readOnly = true)
    public List<Vuelo> listarVuelosProximos() {
        return vueloRepository.findByFechaGreaterThanEqualOrderByFechaAscHoraSalidaAsc(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Ruta> listarRutas() {
        return rutaRepository.findAll(Sort.by("codigo"));
    }

    @Transactional(readOnly = true)
    public List<Avion> listarAviones() {
        return avionRepository.findAll(Sort.by("matricula"));
    }

    @Transactional
    public Vuelo programar(Long rutaId, Long avionId, LocalDate fecha, LocalTime hora) {
        if (rutaId == null) {
            throw new IllegalArgumentException("Selecciona una ruta.");
        }
        if (avionId == null) {
            throw new IllegalArgumentException("Selecciona un avión.");
        }
        validarFechaFutura(fecha, hora);

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("La ruta no existe."));
        Avion avion = avionRepository.findById(avionId)
                .orElseThrow(() -> new IllegalArgumentException("El avión no existe."));

        // Un avión no puede salir dos veces a la misma fecha y hora
        if (vueloRepository.existsByAvionIdAndFechaAndHoraSalida(avionId, fecha, hora)) {
            throw new IllegalStateException("El avión " + avion.getMatricula()
                    + " ya tiene un vuelo programado a esa fecha y hora.");
        }

        Vuelo vuelo = new Vuelo();
        vuelo.setRuta(ruta);
        vuelo.setAvion(avion);
        vuelo.setFecha(fecha);
        vuelo.setHoraSalida(hora);
        return vueloRepository.save(vuelo);
    }


    /** Pone la misma fecha y hora nueva a todos los vuelos seleccionados. Regresa cuántos cambió. */
    @Transactional
    public int reprogramar(List<Long> vueloIds, LocalDate fecha, LocalTime hora) {
        if (vueloIds == null || vueloIds.isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos un vuelo.");
        }
        validarFechaFutura(fecha, hora);

        List<Vuelo> vuelos = vueloRepository.findAllById(vueloIds);
        for (Vuelo vuelo : vuelos) {
            vuelo.setFecha(fecha);
            vuelo.setHoraSalida(hora);
        }
        vueloRepository.saveAll(vuelos);
        return vuelos.size();
    }

    // AUXILIARES
    private void validarFechaFutura(LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Selecciona la fecha y la hora.");
        }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora deben ser posteriores a este momento.");
        }
    }
}