package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.dto.ResumenAdmin;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.repository.ReservacionRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminResumenService {

    private static final int TOP_RUTAS = 5;

    private final ReservacionRepository reservacionRepository;
    private final VueloRepository vueloRepository;

    @Transactional(readOnly = true)
    public ResumenAdmin obtenerResumen() {
        LocalDate hoy = LocalDate.now();

        // Semana de domingo a sábado (igual que la Agenda)
        LocalDateTime inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay();
        LocalDateTime finSemana = inicioSemana.plusDays(7);
        long reservacionesSemana = reservacionRepository.contarCreadasEntre(inicioSemana, finSemana);

        // Ocupación de vuelos próximos
        long capacidad = vueloRepository.sumarCapacidadDesde(hoy);
        long ocupados = reservacionRepository.contarPasajerosDesde(hoy);
        int porcentaje = (capacidad == 0) ? 0 : (int) Math.round(ocupados * 100.0 / capacidad);

        long pendientes = reservacionRepository.countByEstado(EstadoReservacion.PENDIENTE);
        long canceladas = reservacionRepository.countByEstado(EstadoReservacion.CANCELADA);

        List<String> rutas = reservacionRepository.findRutasMasSolicitadas(PageRequest.of(0, TOP_RUTAS));

        return new ResumenAdmin(reservacionesSemana, porcentaje, pendientes, canceladas, rutas);
    }
}