package com.cisneros_guevara_lara.SkyRoute.controller;

import com.cisneros_guevara_lara.SkyRoute.model.*;
import com.cisneros_guevara_lara.SkyRoute.model.enums.MetodoPago;
import com.cisneros_guevara_lara.SkyRoute.service.SkyRouteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {

    @Autowired
    SkyRouteService skyRouteService;

    // Pone automáticamente los datos del usuario logueado en todas las vistas (header y sidebar)
    @ModelAttribute("usuarioActual")
    public Usuario agregarUsuarioActual(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return skyRouteService.buscarUsuarioPorCorreo(auth.getName());
        }
        return null;
    }

    @GetMapping({"/", "/login"})
    public String mostrarLogin() {
        return "index";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String registrarCliente(@RequestParam String nombreCompleto,
                                   @RequestParam String correo,
                                   @RequestParam String telefono,
                                   @RequestParam String contrasenia,
                                   @RequestParam String confirmarContrasenia,
                                   Model model) {
        if (!contrasenia.equals(confirmarContrasenia)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "usuario/registro";
        }
        try {
            skyRouteService.registrarCliente(nombreCompleto, correo, telefono, contrasenia);
            return "redirect:/login?registrado=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "usuario/registro";
        }
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model) {
        model.addAttribute("origenes", skyRouteService.listarOrigenes());
        model.addAttribute("destinos", skyRouteService.listarDestinos());
        model.addAttribute("topDestinos", skyRouteService.obtenerTopDestinos());
        return "usuario/inicio";
    }

    @GetMapping("/contacto")
    public String mostrarContacto() {
        return "usuario/contacto";
    }

    // Buscar vuelos reales en BD
    @GetMapping("/vuelos")
    public String mostrarVuelos(@RequestParam(required = false) String origen,
                                @RequestParam(required = false) String destino,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIda,
                                Model model) {
        List<Vuelo> vuelos = skyRouteService.buscarVuelos(origen, destino, fechaIda);
        model.addAttribute("vuelos", vuelos);
        model.addAttribute("origen", origen);
        model.addAttribute("destino", destino);
        model.addAttribute("fechaIda", fechaIda);
        return "usuario/vuelos";
    }

    // Datos del pasajero para el vuelo elegido
    @GetMapping("/reservar")
    public String mostrarReservar(@RequestParam Long vueloId,
                                  Authentication auth,
                                  HttpSession session,
                                  Model model) {
        Vuelo vuelo = skyRouteService.obtenerVueloPorId(vueloId);
        session.setAttribute("reservaVueloId", vueloId);

        Usuario usuario = skyRouteService.buscarUsuarioPorCorreo(auth.getName());
        model.addAttribute("vuelo", vuelo);
        model.addAttribute("cliente", usuario);
        return "usuario/reservar";
    }

    // Guardar datos del pasajero en sesión y cargar asientos ocupados desde BD
    @PostMapping("/asientos")
    public String procesarDatosPasajero(@RequestParam String nombreCompleto,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
                                        @RequestParam String nacionalidad,
                                        HttpSession session,
                                        Model model) {
        Long vueloId = (Long) session.getAttribute("reservaVueloId");
        if (vueloId == null) return "redirect:/inicio";

        session.setAttribute("pasajeroNombre", nombreCompleto);
        session.setAttribute("pasajeroNacimiento", fechaNacimiento);
        session.setAttribute("pasajeroNacionalidad", nacionalidad);

        Vuelo vuelo = skyRouteService.obtenerVueloPorId(vueloId);
        List<String> asientosOcupados = skyRouteService.obtenerAsientosOcupados(vueloId);

        model.addAttribute("vuelo", vuelo);
        model.addAttribute("asientosOcupados", asientosOcupados);
        return "usuario/asientos";
    }

    // Guardar asiento elegido en sesión y mostrar resumen de pago real
    @PostMapping("/pago")
    public String procesarAsiento(@RequestParam String asiento,
                                  HttpSession session,
                                  Model model) {
        Long vueloId = (Long) session.getAttribute("reservaVueloId");
        if (vueloId == null) return "redirect:/inicio";

        session.setAttribute("pasajeroAsiento", asiento);

        Vuelo vuelo = skyRouteService.obtenerVueloPorId(vueloId);
        double subtotal = vuelo.getRuta().getTarifa();
        double impuestos = Math.round(subtotal * 0.16 * 100.0) / 100.0;
        double total = subtotal + impuestos;

        model.addAttribute("vuelo", vuelo);
        model.addAttribute("asiento", asiento);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("total", total);
        return "usuario/pago";
    }

    // Persistir la reservación con SQL
    @PostMapping("/confirmar-reserva")
    public String confirmarReserva(@RequestParam MetodoPago metodoPago,
                                   Authentication auth,
                                   HttpSession session) {
        Long vueloId = (Long) session.getAttribute("reservaVueloId");
        String nombre = (String) session.getAttribute("pasajeroNombre");
        LocalDate nacimiento = (LocalDate) session.getAttribute("pasajeroNacimiento");
        String nacionalidad = (String) session.getAttribute("pasajeroNacionalidad");
        String asiento = (String) session.getAttribute("pasajeroAsiento");

        if (vueloId == null || asiento == null) {
            return "redirect:/inicio";
        }

        skyRouteService.crearReservacion(
                auth.getName(), vueloId, nombre, nacimiento, nacionalidad, asiento, metodoPago
        );

        // Limpiar datos temporales de sesión
        session.removeAttribute("reservaVueloId");
        session.removeAttribute("pasajeroNombre");
        session.removeAttribute("pasajeroNacimiento");
        session.removeAttribute("pasajeroNacionalidad");
        session.removeAttribute("pasajeroAsiento");

        return "redirect:/mis-reservas?confirmada=true";
    }

    @GetMapping("/mis-reservas")
    public String mostrarMisReservas(Authentication auth, Model model) {
        List<Reservacion> reservaciones = skyRouteService.listarReservacionesDeCliente(auth.getName());
        model.addAttribute("reservaciones", reservaciones);
        return "usuario/mis-reservas";
    }

    @PostMapping("/mis-reservas/{id}/checkin")
    public String hacerCheckIn(@PathVariable Long id, Authentication auth) {
        skyRouteService.hacerCheckIn(id, auth.getName());
        return "redirect:/mis-reservas?checkin=true";
    }

    @PostMapping("/mis-reservas/{id}/cancelar")
    public String cancelarReservacion(@PathVariable Long id, Authentication auth) {
        skyRouteService.cancelarReservacion(id, auth.getName());
        return "redirect:/mis-reservas?cancelada=true";
    }
}