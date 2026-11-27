package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.dto.HistorialCliente;
import com.cisneros_guevara_lara.SkyRoute.model.Cliente;
import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.repository.ClienteRepository;
import com.cisneros_guevara_lara.SkyRoute.repository.ReservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminClienteService {

    private final ClienteRepository clienteRepository;
    private final ReservacionRepository reservacionRepository;

    @Transactional(readOnly = true)
    public List<Cliente> buscarClientes(String q) {
        String filtro = (q == null || q.isBlank()) ? null : q.trim();
        return clienteRepository.buscarPorNombreOTelefono(filtro);
    }

    @Transactional(readOnly = true)
    public HistorialCliente obtenerHistorial(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe."));

        List<Reservacion> reservaciones =
                reservacionRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);

        LocalDate hoy = LocalDate.now();

        List<Reservacion> proximas = reservaciones.stream()
                .filter(r -> esActiva(r) && !r.getVuelo().getFecha().isBefore(hoy))
                .toList();

        List<Reservacion> anteriores = reservaciones.stream()
                .filter(r -> !proximas.contains(r))
                .toList();

        // Total gastado: suma de todo menos lo cancelado
        double totalGastado = reservaciones.stream()
                .filter(r -> r.getEstado() != EstadoReservacion.CANCELADA)
                .mapToDouble(Reservacion::getTotal)
                .sum();

        long completadas = contarPorEstado(reservaciones, EstadoReservacion.COMPLETADA);
        long canceladas = contarPorEstado(reservaciones, EstadoReservacion.CANCELADA);

        return new HistorialCliente(cliente, reservaciones.size(), totalGastado,
                completadas, canceladas, proximas, anteriores);
    }

    private boolean esActiva(Reservacion r) {
        return r.getEstado() == EstadoReservacion.PENDIENTE
                || r.getEstado() == EstadoReservacion.CONFIRMADA;
    }

    private long contarPorEstado(List<Reservacion> reservaciones, EstadoReservacion estado) {
        return reservaciones.stream().filter(r -> r.getEstado() == estado).count();
    }
}