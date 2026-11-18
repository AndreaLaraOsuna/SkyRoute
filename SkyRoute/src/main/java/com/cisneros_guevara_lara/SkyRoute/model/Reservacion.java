package com.cisneros_guevara_lara.SkyRoute.model;

import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String folio;

    private LocalDateTime fechaCreacion;
    private double subtotal;
    private double cargoEquipaje;
    private double impuestos;
    private double total;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    private EstadoReservacion estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @OneToMany(mappedBy = "reservacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pasajero> pasajeros = new ArrayList<>();
}