package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    Optional<Ruta> findByCodigo(String codigo);

    @Query("SELECT DISTINCT r.origen FROM Ruta r ORDER BY r.origen ASC")
    List<String> listarOrigenes();

    @Query("SELECT DISTINCT r.destino FROM Ruta r ORDER BY r.destino ASC")
    List<String> listarDestinos();

    boolean existsByOrigenIgnoreCaseAndDestinoIgnoreCase(String origen, String destino);
}