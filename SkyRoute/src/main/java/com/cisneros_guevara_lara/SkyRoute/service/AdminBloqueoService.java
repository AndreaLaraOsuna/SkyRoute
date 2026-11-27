package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.Administrador;
import com.cisneros_guevara_lara.SkyRoute.model.Bloqueo;
import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.TipoBloqueo;
import com.cisneros_guevara_lara.SkyRoute.repository.BloqueoRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.ReservacionRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.UsuarioRepository;
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
public class AdminBloqueoService {

    private static final List<EstadoReservacion> ESTADOS_ACTIVOS =
            List.of(EstadoReservacion.PENDIENTE, EstadoReservacion.CONFIRMADA);

    private final VueloRepository vueloRepository;
    private final BloqueoRepository bloqueoRepository;
    private final ReservacionRepository reservacionRepository;
    private final UsuarioRepository usuarioRepository;

    /** Lo que se le informa al admin al terminar. */
    public record Resultado(int vuelosBloqueados, int reservacionesCanceladas) {}

    @Transactional(readOnly = true)
    public List<Vuelo> listarVuelosBloqueables() {
        return vueloRepository.buscarSinBloqueoDesde(LocalDate.now());
    }

    @Transactional
    public Resultado bloquear(String correoAdmin, TipoBloqueo tipo, String motivo, List<Long> vueloIds,
                              LocalDate desde, LocalDate hasta,
                              LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {

        // ---- Validaciones generales ----
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona el tipo de bloqueo.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Escribe el motivo del bloqueo.");
        }
        if (vueloIds == null || vueloIds.isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos un vuelo.");
        }

        // ---- Validaciones del periodo según el tipo ----
        if (tipo == TipoBloqueo.DIAS_COMPLETOS) {
            if (desde == null || hasta == null) {
                throw new IllegalArgumentException("Selecciona las fechas desde y hasta.");
            }
            if (hasta.isBefore(desde)) {
                throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a 'desde'.");
            }
        } else {
            if (fecha == null || horaInicio == null || horaFin == null) {
                throw new IllegalArgumentException("Selecciona la fecha, la hora de inicio y la hora de fin.");
            }
            if (!horaFin.isAfter(horaInicio)) {
                throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
            }
        }

        Administrador admin = usuarioRepository.findByCorreo(correoAdmin)
                .filter(u -> u instanceof Administrador)
                .map(u -> (Administrador) u)
                .orElseThrow(() -> new IllegalStateException("Solo un administrador puede crear bloqueos."));

        List<Vuelo> vuelos = vueloRepository.findAllById(vueloIds);
        int canceladas = 0;

        for (Vuelo vuelo : vuelos) {
            String codigo = vuelo.getRuta().getCodigo();

            if (!estaEnPeriodo(vuelo, tipo, desde, hasta, fecha, horaInicio, horaFin)) {
                throw new IllegalArgumentException("El vuelo " + codigo + " no está dentro del periodo seleccionado.");
            }
            if (bloqueoRepository.existsByVueloId(vuelo.getId())) {
                throw new IllegalStateException("El vuelo " + codigo + " ya está bloqueado.");
            }

            Bloqueo bloqueo = new Bloqueo();
            bloqueo.setVuelo(vuelo);
            bloqueo.setAdministrador(admin);
            bloqueo.setTipo(tipo);
            bloqueo.setMotivo(motivo.trim());
            bloqueo.setFechaBloqueo(LocalDateTime.now());
            bloqueoRepository.save(bloqueo);

            // Cancela las reservaciones activas de ese vuelo
            List<Reservacion> activas = reservacionRepository.findByVueloIdAndEstadoIn(vuelo.getId(), ESTADOS_ACTIVOS);
            for (Reservacion r : activas) {
                r.setEstado(EstadoReservacion.CANCELADA);
            }
            reservacionRepository.saveAll(activas);
            canceladas += activas.size();
        }

        return new Resultado(vuelos.size(), canceladas);
    }

    private boolean estaEnPeriodo(Vuelo vuelo, TipoBloqueo tipo,
                                  LocalDate desde, LocalDate hasta,
                                  LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        LocalDate f = vuelo.getFecha();
        if (tipo == TipoBloqueo.DIAS_COMPLETOS) {
            return !f.isBefore(desde) && !f.isAfter(hasta);          // desde <= f <= hasta
        }
        LocalTime h = vuelo.getHoraSalida();
        return f.equals(fecha) && !h.isBefore(horaInicio) && !h.isAfter(horaFin);  // misma fecha, inicio <= h <= fin
    }
}