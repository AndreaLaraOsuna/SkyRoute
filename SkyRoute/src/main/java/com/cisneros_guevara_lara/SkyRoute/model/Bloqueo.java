package com.cisneros_guevara_lara.SkyRoute.model;

import com.cisneros_guevara_lara.SkyRoute.model.enums.AlcanceBloqueo;
import com.cisneros_guevara_lara.SkyRoute.model.enums.TipoBloqueo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "bloqueos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bloqueo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoBloqueo tipo;

    @Enumerated(EnumType.STRING)
    private AlcanceBloqueo alcance;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;

    @ManyToOne
    @JoinColumn(name = "avion_id")
    private Avion avion;
}