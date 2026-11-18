package com.cisneros_guevara_lara.SkyRoute.model;

import com.cisneros_guevara_lara.SkyRoute.model.enums.TipoBloqueo;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Column(nullable = false)
    private TipoBloqueo tipo;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "fecha_bloqueo", nullable = false)
    private LocalDateTime fechaBloqueo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vuelo_id", nullable = false)
    private Vuelo vuelo;

    @PrePersist
    public void prePersist() {
        if (this.fechaBloqueo == null) {
            this.fechaBloqueo = LocalDateTime.now();
        }
    }
}