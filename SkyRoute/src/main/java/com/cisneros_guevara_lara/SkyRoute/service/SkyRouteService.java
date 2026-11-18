package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.*;
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

    // ==================== USUARIOS ====================

    // @Transactional indica una transacion en la BD
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

    @Transactional(readOnly = true) // le decimos a spring boot que es solo lectura para que optimice el rendimiento
    public Usuario buscarUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    // ==================== VUELOS, RUTAS Y AVIONES ====================

    @Transactional(readOnly = true)
    public List<Vuelo> buscarVuelosDisponibles(String origen, String destino, LocalDate fecha) {
        return vueloRepository.buscarVuelosDisponibles(origen, destino, fecha);
    }

    @Transactional(readOnly = true)
    public List<String> listarOrigenes() {
        return rutaRepository.listarOrigenes();
    }

    @Transactional(readOnly = true)
    public List<String> listarDestinos() {
        return rutaRepository.listarDestinos();
    }

    // ==================== BLOQUEOS ====================

    @Transactional(readOnly = true)
    public List<Bloqueo> listarBloqueos() {
        return bloqueoRepository.findAllByOrderByFechaBloqueoDesc();
    }

    @Transactional
    public Bloqueo bloquearVuelo(Long vueloId, String correoAdmin, TipoBloqueo tipo, String motivo) {
        if (bloqueoRepository.existsByVueloId(vueloId)) {
            throw new IllegalStateException("Este vuelo ya está bloqueado.");
        }

        Vuelo vuelo = vueloRepository.findById(vueloId)
                .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado."));

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

    // ==================== RESERVACIONES ====================

    @Transactional(readOnly = true)
    public List<Reservacion> listarReservacionesCliente(Long clienteId) {
        return reservacionRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
    }

    @Transactional
    public Reservacion guardarReservacion(Reservacion reservacion) {
        if (reservacion.getFolio() == null) {
            reservacion.setFolio("SR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (reservacion.getFechaCreacion() == null) {
            reservacion.setFechaCreacion(LocalDateTime.now());
        }
        return reservacionRepository.save(reservacion);
    }
}