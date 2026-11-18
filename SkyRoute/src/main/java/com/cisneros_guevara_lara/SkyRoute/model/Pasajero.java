package com.cisneros_guevara_lara.SkyRoute.model;

import com.cisneros_guevara_lara.SkyRoute.model.enums.TipoPasajero;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "pasajeros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pasajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String asiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPasajero tipo;

    @ManyToOne
    @JoinColumn(name = "reservacion_id", nullable = false)
    private Reservacion reservacion;
}