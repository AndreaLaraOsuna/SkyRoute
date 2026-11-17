package com.cisneros_guevara_lara.SkyRoute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "rutas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    private String origen;
    private String destino;
    private int duracionMinutos;
    private double tarifa;

    @OneToMany(mappedBy = "ruta")
    private List<Vuelo> vuelos;
}