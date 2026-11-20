package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import com.cisneros_guevara_lara.SkyRoute.repository.ReservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cisneros_guevara_lara.SkyRoute.model.Cliente;
import com.cisneros_guevara_lara.SkyRoute.repository.ClienteRepository;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservacionService {

    private final ReservacionRepository reservacionRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Reservacion> listarReservaciones() {
        return reservacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarClientes(String q) {
        String filtro = (q == null || q.isBlank()) ? null : q.trim();
        return clienteRepository.buscarPorNombreOTelefono(filtro);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerCliente(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecciona un cliente.");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe."));
    }

    @Transactional(readOnly = true)
    public Reservacion obtenerReservacion(Long id) {
        return reservacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reservación no existe."));
    }

    // Check in: solo una reservación PENDIENTE puede pasar a CONFIRMADA
    @Transactional
    public void hacerCheckIn(Long id) {
        Reservacion reservacion = obtenerReservacion(id);
        if (reservacion.getEstado() != EstadoReservacion.PENDIENTE) {
            throw new IllegalStateException("Solo se puede hacer check in a reservaciones pendientes.");
        }
        reservacion.setEstado(EstadoReservacion.CONFIRMADA);
        reservacionRepository.save(reservacion);
    }

    // Cancelar: solo si está PENDIENTE o CONFIRMADA (no una ya cancelada o completada)
    @Transactional
    public void cancelar(Long id) {
        Reservacion reservacion = obtenerReservacion(id);
        EstadoReservacion estado = reservacion.getEstado();
        if (estado != EstadoReservacion.PENDIENTE && estado != EstadoReservacion.CONFIRMADA) {
            throw new IllegalStateException("Esta reservación ya no se puede cancelar.");
        }
        reservacion.setEstado(EstadoReservacion.CANCELADA);
        reservacionRepository.save(reservacion);
    }
}