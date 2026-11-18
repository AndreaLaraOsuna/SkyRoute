package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Bloqueo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueoRepository extends JpaRepository<Bloqueo, Long> {

    boolean existsByVueloId(Long vueloId);

    List<Bloqueo> findAllByOrderByFechaBloqueoDesc();
}