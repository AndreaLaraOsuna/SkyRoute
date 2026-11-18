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

    @Query("""
       SELECT v FROM Vuelo v
       WHERE (:origen IS NULL OR LOWER(v.ruta.origen) LIKE LOWER(CONCAT('%', :origen, '%')))
         AND (:destino IS NULL OR LOWER(v.ruta.destino) LIKE LOWER(CONCAT('%', :destino, '%')))
         AND (:fechaIda IS NULL OR v.fecha = :fechaIda)
       ORDER BY v.fecha ASC, v.horaSalida ASC
       """)
    List<Vuelo> buscarVuelosDisponibles(@Param("origen") String origen,
                                        @Param("destino") String destino,
                                        @Param("fechaIda") LocalDate fechaIda);

    @Query("SELECT v FROM Vuelo v WHERE v.ruta.origen = :origen AND v.ruta.destino = :destino " +
            "AND v.fecha >= CURRENT_DATE AND v.id NOT IN (SELECT b.vuelo.id FROM Bloqueo b) " +
            "ORDER BY v.fecha ASC, v.horaSalida ASC")
    List<Vuelo> buscarVuelosPorRuta(@Param("origen") String origen,
                                    @Param("destino") String destino);
}
