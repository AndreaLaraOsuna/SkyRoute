package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalTime;

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

    @Query("""
            SELECT v FROM Vuelo v
            WHERE (:origen IS NULL OR LOWER(v.ruta.origen) LIKE LOWER(CONCAT('%', :origen, '%')))
              AND (:destino IS NULL OR LOWER(v.ruta.destino) LIKE LOWER(CONCAT('%', :destino, '%')))
              AND (:fechaIda IS NULL OR v.fecha = :fechaIda)
              AND v.id NOT IN (SELECT b.vuelo.id FROM Bloqueo b)
            ORDER BY v.fecha ASC, v.horaSalida ASC
            """)
    List<Vuelo> buscarVuelosPorRuta(@Param("origen") String origen,
                                    @Param("destino") String destino);

    // Vuelos entre dos fechas, ordenados por fecha y hora
    List<Vuelo> findByFechaBetweenOrderByFechaAscHoraSalidaAsc(LocalDate inicio, LocalDate fin);

    // Suma de asientos de todos los vuelos de hoy en adelante
    @Query("SELECT COALESCE(SUM(v.avion.asientos), 0) FROM Vuelo v WHERE v.fecha >= :desde")
    long sumarCapacidadDesde(@Param("desde") LocalDate desde);

    // Vuelos de una fecha en adelante, ordenados
    List<Vuelo> findByFechaGreaterThanEqualOrderByFechaAscHoraSalidaAsc(LocalDate fecha);

    @Query("SELECT v FROM Vuelo v WHERE v.fecha >= :desde " +
            "AND v.id NOT IN (SELECT b.vuelo.id FROM Bloqueo b) " +
            "ORDER BY v.fecha ASC, v.horaSalida ASC")
    List<Vuelo> buscarSinBloqueoDesde(@Param("desde") LocalDate desde);

    boolean existsByAvionIdAndFechaAndHoraSalida(Long avionId, LocalDate fecha, LocalTime horaSalida);
}
