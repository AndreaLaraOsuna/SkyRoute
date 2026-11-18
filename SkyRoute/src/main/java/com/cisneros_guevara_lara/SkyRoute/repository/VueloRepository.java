package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    List<Vuelo> findAllByOrderByFechaAscHoraSalidaAsc();

    @Query("SELECT v FROM Vuelo v WHERE v.ruta.origen = :origen AND v.ruta.destino = :destino " +
            "AND v.fecha = :fecha AND v.id NOT IN (SELECT b.vuelo.id FROM Bloqueo b) " +
            "ORDER BY v.horaSalida ASC")
    List<Vuelo> buscarVuelosDisponibles(@Param("origen") String origen,
                                        @Param("destino") String destino,
                                        @Param("fecha") LocalDate fecha);
}
