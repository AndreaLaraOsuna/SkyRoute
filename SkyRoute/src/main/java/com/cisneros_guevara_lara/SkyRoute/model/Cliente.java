package com.cisneros_guevara_lara.SkyRoute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    private String telefono;

    @OneToMany(mappedBy = "cliente")
    private List<Reservacion> reservaciones;
}