package com.cisneros_guevara_lara.SkyRoute.repository;

import com.cisneros_guevara_lara.SkyRoute.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Si q es null regresa todos los clientes
    @Query("""
       SELECT c FROM Cliente c
       WHERE :q IS NULL
          OR LOWER(c.nombreCompleto) LIKE LOWER(CONCAT('%', :q, '%'))
          OR c.telefono LIKE CONCAT('%', :q, '%')
       ORDER BY c.nombreCompleto ASC
       """)
    List<Cliente> buscarPorNombreOTelefono(@Param("q") String q);
}