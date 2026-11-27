package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.dto.AgendaSemana;
import com.cisneros_guevara_lara.SkyRoute.dto.AgendaSemana.Dia;
import com.cisneros_guevara_lara.SkyRoute.dto.AgendaSemana.Fila;
import com.cisneros_guevara_lara.SkyRoute.dto.AgendaSemana.VueloAgenda;
import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import com.cisneros_guevara_lara.SkyRoute.repository.ReservacionRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.VueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class AdminAgendaService {

    // Índice 0 = domingo, igual que las columnas de la agenda
    private static final String[] DIAS_CORTOS = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
    private static final String[] DIAS_LARGOS = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
    private static final String[] MESES = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
    private static final String[] MESES_CORTOS = {"ene", "feb", "mar", "abr", "may", "jun",
            "jul", "ago", "sep", "oct", "nov", "dic"};

    private final VueloRepository vueloRepository;
    private final ReservacionRepository reservacionRepository;

    /** Arma la semana (domingo a sábado) que contiene la fecha dada. */
    @Transactional(readOnly = true)
    public AgendaSemana obtenerSemana(LocalDate fecha) {
        LocalDate inicio = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate fin = inicio.plusDays(6);

        // Los 7 días
        List<Dia> dias = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = inicio.plusDays(i);
            dias.add(new Dia(i, d, DIAS_CORTOS[i], DIAS_LARGOS[i], tituloDia(d)));
        }

        // Vuelos de la semana; indiceDia = cuántos días después del domingo
        List<VueloAgenda> vuelos = vueloRepository
                .findByFechaBetweenOrderByFechaAscHoraSalidaAsc(inicio, fin).stream()
                .map(v -> aVueloAgenda(v, (int) ChronoUnit.DAYS.between(inicio, v.getFecha())))
                .toList();

        // Cuadrícula: una fila por cada hora que tenga al menos un vuelo.
        // TreeMap mantiene las horas ordenadas (6, 7, 13, 21...)
        TreeMap<Integer, List<List<VueloAgenda>>> porHora = new TreeMap<>();
        for (VueloAgenda va : vuelos) {
            int hora = va.getVuelo().getHoraSalida().getHour();
            porHora.computeIfAbsent(hora, h -> sieteCeldasVacias())
                    .get(va.getIndiceDia())
                    .add(va);
        }
        List<Fila> filas = porHora.entrySet().stream()
                .map(e -> new Fila(e.getKey() + ":00", e.getValue()))
                .toList();

        return new AgendaSemana(inicio, textoSemana(inicio, fin), dias, vuelos, filas);
    }

    /** Vuelos de un solo día (pestaña "Día" en escritorio). */
    @Transactional(readOnly = true)
    public List<VueloAgenda> obtenerVuelosDelDia(LocalDate dia) {
        int indice = dia.getDayOfWeek().getValue() % 7;   // lunes=1 ... domingo=7 -> domingo=0
        return vueloRepository.findByFechaBetweenOrderByFechaAscHoraSalidaAsc(dia, dia).stream()
                .map(v -> aVueloAgenda(v, indice))
                .toList();
    }

    /** "Domingo 27 de septiembre" */
    public String tituloDia(LocalDate d) {
        return DIAS_LARGOS[d.getDayOfWeek().getValue() % 7] + " " + d.getDayOfMonth()
                + " de " + MESES[d.getMonthValue() - 1];
    }

    // AUXILIARES
    private VueloAgenda aVueloAgenda(Vuelo vuelo, int indiceDia) {
        int capacidad = vuelo.getAvion() != null ? vuelo.getAvion().getAsientos() : 0;
        long ocupados = reservacionRepository.contarAsientosOcupados(vuelo.getId());
        return new VueloAgenda(vuelo, indiceDia, ocupados, capacidad);
    }

    private List<List<VueloAgenda>> sieteCeldasVacias() {
        List<List<VueloAgenda>> celdas = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            celdas.add(new ArrayList<>());
        }
        return celdas;
    }

    private String textoSemana(LocalDate inicio, LocalDate fin) {
        String texto = inicio.getDayOfMonth() + " " + MESES_CORTOS[inicio.getMonthValue() - 1];
        if (inicio.getYear() != fin.getYear()) {
            texto += " " + inicio.getYear();
        }
        return texto + " - " + fin.getDayOfMonth() + " " + MESES_CORTOS[fin.getMonthValue() - 1] + " " + fin.getYear();
    }
}