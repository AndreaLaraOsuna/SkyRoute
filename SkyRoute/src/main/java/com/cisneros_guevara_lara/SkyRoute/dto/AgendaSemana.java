package com.cisneros_guevara_lara.SkyRoute.dto;

import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/** Todo lo que necesita la pantalla de Agenda para una semana (domingo a sábado). */
@Getter
@AllArgsConstructor
public class AgendaSemana {

    private LocalDate inicio;          // domingo de la semana
    private String textoSemana;
    private List<Dia> dias;            // los 7 días
    private List<VueloAgenda> vuelos;  // vuelos de la semana (vista móvil)
    private List<Fila> filas;          // cuadrícula por hora (vista escritorio)

    @Getter
    @AllArgsConstructor
    public static class Dia {
        private int indice;            // 0 = domingo ... 6 = sábado
        private LocalDate fecha;
        private String nombreCorto;    // "Dom"
        private String nombreLargo;    // "Domingo"
        private String titulo;         // "Domingo 27 de septiembre"
    }

    @Getter
    @AllArgsConstructor
    public static class VueloAgenda {
        private Vuelo vuelo;
        private int indiceDia;         // en qué columna/día va
        private long ocupados;
        private int capacidad;
    }

    @Getter
    @AllArgsConstructor
    public static class Fila {
        private String hora;                     // "7:00"
        private List<List<VueloAgenda>> celdas;  // 7 celdas, una por día
    }
}