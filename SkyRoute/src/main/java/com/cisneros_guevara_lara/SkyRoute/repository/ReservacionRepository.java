package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {

    List<Reservacion> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    Optional<Reservacion> findByFolio(String folio);

    List<Reservacion> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT p.asiento FROM Pasajero p WHERE p.reservacion.vuelo.id = :vueloId " +
            "AND p.reservacion.estado <> 'CANCELADO'")
    List<String> obtenerAsientosOcupados(@Param("vueloId") Long vueloId);

    @Query("""
       SELECT r.vuelo.ruta.destino
       FROM Reservacion r
       WHERE r.estado <> :estadoCancelado
       GROUP BY r.vuelo.ruta.destino
       ORDER BY COUNT(r) DESC
       """)
    List<String> findTopDestinosMasReservados(@Param("estadoCancelado") EstadoReservacion estadoCancelado);
}
