package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {

    List<Reservacion> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    Optional<Reservacion> findByFolio(String folio);

    List<Reservacion> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT p.asiento FROM Pasajero p WHERE p.reservacion.vuelo.id = :vueloId " +
            "AND p.reservacion.estado <> 'CANCELADA'")
    List<String> obtenerAsientosOcupados(@Param("vueloId") Long vueloId);

    @Query("""
            SELECT r.vuelo.ruta.destino
            FROM Reservacion r
            WHERE r.estado <> :estadoCancelado
            GROUP BY r.vuelo.ruta.destino
            ORDER BY COUNT(r) DESC
            """)
    List<String> findTopDestinosMasReservados(@Param("estadoCancelado") EstadoReservacion estadoCancelado);

    @Query("SELECT COUNT(p) FROM Pasajero p WHERE p.reservacion.vuelo.id = :vueloId " +
            "AND p.reservacion.estado <> com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion.CANCELADA")
    long contarAsientosOcupados(@Param("vueloId") Long vueloId);

    // Pendientes / canceladas
    long countByEstado(EstadoReservacion estado);

    // Reservaciones creadas en un rango [inicio, fin)
    @Query("SELECT COUNT(r) FROM Reservacion r WHERE r.fechaCreacion >= :inicio AND r.fechaCreacion < :fin")
    long contarCreadasEntre(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Asientos ocupados en vuelos de hoy en adelante (sin contar canceladas)
    @Query("SELECT COUNT(p) FROM Pasajero p WHERE p.reservacion.vuelo.fecha >= :desde " +
            "AND p.reservacion.estado <> com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion.CANCELADA")
    long contarPasajerosDesde(@Param("desde") LocalDate desde);

    // Rutas con más reservaciones (sin contar canceladas)
    @Query("""
            SELECT CONCAT(ru.origen, ' → ', ru.destino)
            FROM Reservacion r JOIN r.vuelo v JOIN v.ruta ru
            WHERE r.estado <> com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion.CANCELADA
            GROUP BY ru.id, ru.origen, ru.destino
            ORDER BY COUNT(r) DESC
            """)
    List<String> findRutasMasSolicitadas(Pageable pageable);

    List<Reservacion> findByVueloIdAndEstadoIn(Long vueloId, List<EstadoReservacion> estados);

}
