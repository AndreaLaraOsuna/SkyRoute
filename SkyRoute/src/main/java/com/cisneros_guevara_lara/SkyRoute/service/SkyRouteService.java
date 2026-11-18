package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.*;
import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.model.enums.MetodoPago;
import com.cisneros_guevara_lara.SkyRoute.model.enums.TipoBloqueo;
import com.cisneros_guevara_lara.SkyRoute.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkyRouteService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    VueloRepository vueloRepository;

    @Autowired
    ReservacionRepository reservacionRepository;

    @Autowired
    BloqueoRepository bloqueoRepository;

    @Autowired
    RutaRepository rutaRepository;

    @Autowired
    AvionRepository avionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    public Cliente registrarCliente(String nombreCompleto, String correo, String telefono, String contrasenia) {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        Cliente cliente = new Cliente();
        cliente.setNombreCompleto(nombreCompleto);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);
        cliente.setContrasenia(passwordEncoder.encode(contrasenia));
        return usuarioRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    // ==================== VUELOS Y RUTAS ====================

    @Transactional(readOnly = true)
    public List<String> listarOrigenes() {
        return rutaRepository.listarOrigenes();
    }

    @Transactional(readOnly = true)
    public List<String> listarDestinos() {
        return rutaRepository.listarDestinos();
    }

    @Transactional(readOnly = true)
    public List<Vuelo> buscarVuelos(String origen, String destino, LocalDate fecha) {
        if (origen != null && !origen.isBlank() && destino != null && !destino.isBlank()) {
            if (fecha != null) {
                return vueloRepository.buscarVuelosDisponibles(origen, destino, fecha);
            }
            return vueloRepository.buscarVuelosPorRuta(origen, destino);
        }
        // Si entra sin filtros, muestra todos los vuelos no bloqueados
        return vueloRepository.findAllByOrderByFechaAscHoraSalidaAsc().stream()
                .filter(v -> !bloqueoRepository.existsByVueloId(v.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Vuelo obtenerVueloPorId(Long vueloId) {
        return vueloRepository.findById(vueloId)
                .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<String> obtenerAsientosOcupados(Long vueloId) {
        return reservacionRepository.obtenerAsientosOcupados(vueloId);
    }

    // ==================== RESERVACIONES ====================

    @Transactional
    public Reservacion crearReservacion(String correoCliente,
                                        Long vueloId,
                                        String nombrePasajero,
                                        LocalDate fechaNacimiento,
                                        String nacionalidad,
                                        String asiento,
                                        MetodoPago metodoPago) {
        Usuario usuario = buscarUsuarioPorCorreo(correoCliente);
        if (!(usuario instanceof Cliente cliente)) {
            throw new IllegalStateException("Solo un cliente puede realizar reservaciones.");
        }

        Vuelo vuelo = obtenerVueloPorId(vueloId);

        if (bloqueoRepository.existsByVueloId(vueloId)) {
            throw new IllegalStateException("Este vuelo fue bloqueado y ya no admite reservas.");
        }

        List<String> ocupados = reservacionRepository.obtenerAsientosOcupados(vueloId);
        if (ocupados.contains(asiento)) {
            throw new IllegalStateException("El asiento " + asiento + " ya fue ocupado.");
        }

        double subtotal = vuelo.getRuta().getTarifa();
        double cargoEquipaje = 0.0;
        double impuestos = Math.round(subtotal * 0.16 * 100.0) / 100.0;
        double total = subtotal + cargoEquipaje + impuestos;

        Reservacion reservacion = new Reservacion();
        reservacion.setFolio("SR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        reservacion.setFechaCreacion(LocalDateTime.now());
        reservacion.setSubtotal(subtotal);
        reservacion.setCargoEquipaje(cargoEquipaje);
        reservacion.setImpuestos(impuestos);
        reservacion.setTotal(total);
        reservacion.setMetodoPago(metodoPago);
        reservacion.setEstado(EstadoReservacion.PENDIENTE);
        reservacion.setCliente(cliente);
        reservacion.setVuelo(vuelo);

        Pasajero pasajero = new Pasajero();
        pasajero.setNombreCompleto(nombrePasajero);
        pasajero.setFechaNacimiento(fechaNacimiento);
        pasajero.setNacionalidad(nacionalidad);
        pasajero.setAsiento(asiento);
        pasajero.setTipo("ADULTO");
        pasajero.setReservacion(reservacion);

        reservacion.getPasajeros().add(pasajero);

        return reservacionRepository.save(reservacion);
    }

    @Transactional(readOnly = true)
    public List<Reservacion> listarReservacionesDeCliente(String correoCliente) {
        Usuario usuario = buscarUsuarioPorCorreo(correoCliente);
        return reservacionRepository.findByClienteIdOrderByFechaCreacionDesc(usuario.getId());
    }

    @Transactional
    public void hacerCheckIn(Long reservacionId, String correoCliente) {
        Reservacion reservacion = reservacionRepository.findById(reservacionId)
                .orElseThrow(() -> new IllegalArgumentException("Reservación no encontrada."));
        if (!reservacion.getCliente().getCorreo().equals(correoCliente)) {
            throw new SecurityException("No tienes permiso sobre esta reservación.");
        }
        reservacion.setEstado(EstadoReservacion.CONFIRMADA);
        reservacionRepository.save(reservacion);
    }

    @Transactional
    public void cancelarReservacion(Long reservacionId, String correoCliente) {
        Reservacion reservacion = reservacionRepository.findById(reservacionId)
                .orElseThrow(() -> new IllegalArgumentException("Reservación no encontrada."));
        if (!reservacion.getCliente().getCorreo().equals(correoCliente)) {
            throw new SecurityException("No tienes permiso sobre esta reservación.");
        }
        reservacion.setEstado(EstadoReservacion.CANCELADA);
        reservacionRepository.save(reservacion);
    }

    // ==================== BLOQUEOS (ADMIN) ====================

    @Transactional(readOnly = true)
    public List<Bloqueo> listarBloqueos() {
        return bloqueoRepository.findAllByOrderByFechaBloqueoDesc();
    }

    @Transactional
    public Bloqueo bloquearVuelo(Long vueloId, String correoAdmin, TipoBloqueo tipo, String motivo) {
        if (bloqueoRepository.existsByVueloId(vueloId)) {
            throw new IllegalStateException("Este vuelo ya está bloqueado.");
        }
        Vuelo vuelo = obtenerVueloPorId(vueloId);
        Usuario usuario = buscarUsuarioPorCorreo(correoAdmin);
        if (!(usuario instanceof Administrador admin)) {
            throw new SecurityException("Solo un administrador puede bloquear vuelos.");
        }

        Bloqueo bloqueo = new Bloqueo();
        bloqueo.setVuelo(vuelo);
        bloqueo.setAdministrador(admin);
        bloqueo.setTipo(tipo);
        bloqueo.setMotivo(motivo);
        bloqueo.setFechaBloqueo(LocalDateTime.now());
        return bloqueoRepository.save(bloqueo);
    }

    @Transactional
    public void desbloquearVuelo(Long bloqueoId) {
        bloqueoRepository.deleteById(bloqueoId);
    }
}