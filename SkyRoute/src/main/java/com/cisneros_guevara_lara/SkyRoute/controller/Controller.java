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
import com.cisneros_guevara_lara.SkyRoute.controller.admin.AdminReservacionController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // true si quien navega es un admin (está haciendo una reserva a nombre de un cliente)
    @ModelAttribute("modoAdmin")
    public boolean esModoAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
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

    @GetMapping("/vuelos")
    public String mostrarVuelos(@RequestParam(required = false) String origen,
                                @RequestParam(required = false) String destino,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIda,
                                @RequestParam(defaultValue = "1") int pasajeros,
                                Model model) {
        List<Vuelo> vuelos = skyRouteService.buscarVuelos(origen, destino, fechaIda);
        model.addAttribute("vuelos", vuelos);
        model.addAttribute("origen", origen);
        model.addAttribute("destino", destino);
        model.addAttribute("fechaIda", fechaIda);
        model.addAttribute("pasajeros", pasajeros);
        return "usuario/vuelos";
    }

    @GetMapping("/reservar")
    public String mostrarReservar(@RequestParam Long vueloId,
                                  @RequestParam(defaultValue = "1") int pasajeros,
                                  Authentication auth,
                                  HttpSession session,
                                  Model model) {
        Vuelo vuelo = skyRouteService.obtenerVueloPorId(vueloId);
        session.setAttribute("reservaVueloId", vueloId);
        session.setAttribute("reservaCantidadPasajeros", pasajeros);

        Cliente cliente = skyRouteService.obtenerClienteParaReserva(auth.getName(),
                (Long) session.getAttribute(AdminReservacionController.SESION_CLIENTE_ID));
        if (cliente == null) {
            // Admin que no eligió cliente: lo mandamos a elegirlo
            return "redirect:/admin/reservaciones/nueva";
        }
        model.addAttribute("vuelo", vuelo);
        model.addAttribute("cliente", cliente);
        model.addAttribute("pasajeros", pasajeros);
        return "usuario/reservar";
    }

    @PostMapping("/asientos")
    public String procesarDatosPasajero(@RequestParam List<String> nombresCompletos,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fechasNacimiento,
                                        @RequestParam List<String> nacionalidades,
                                        HttpSession session,
                                        Model model) {
        Long vueloId = (Long) session.getAttribute("reservaVueloId");
        if (vueloId == null) return "redirect:/inicio";

        session.setAttribute("pasajerosNombres", nombresCompletos);
        session.setAttribute("pasajerosNacimientos", fechasNacimiento);
        session.setAttribute("pasajerosNacionalidades", nacionalidades);

        Vuelo vuelo = skyRouteService.obtenerVueloPorId(vueloId);
        List<String> asientosOcupados = skyRouteService.obtenerAsientosOcupados(vueloId);

        model.addAttribute("vuelo", vuelo);
        model.addAttribute("asientosOcupados", asientosOcupados);
        model.addAttribute("cantidadPasajeros", nombresCompletos.size());
        return "usuario/asientos";
    }

    @PostMapping("/pago")
    public String procesarAsiento(@RequestParam("asiento") List<String> asientos,
                                  HttpSession session,
                                  Model model) {
        Long vueloId = (Long) session.getAttribute("reservaVueloId");
        if (vueloId == null) return "redirect:/inicio";

        session.setAttribute("pasajerosAsientos", asientos);

        Vuelo vuelo = skyRouteService.obtenerVueloPorId(vueloId);
        double subtotal = vuelo.getRuta().getTarifa() * asientos.size();
        double impuestos = Math.round(subtotal * 0.16 * 100.0) / 100.0;
        double total = subtotal + impuestos;

        model.addAttribute("vuelo", vuelo);
        model.addAttribute("asiento", String.join(", ", asientos));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("total", total);
        return "usuario/pago";
    }

    @PostMapping("/confirmar-reserva")
    @SuppressWarnings("unchecked")
    public String confirmarReserva(@RequestParam MetodoPago metodoPago,
                                   Authentication auth,
                                   HttpSession session,
                                   RedirectAttributes redirect) {
        Long vueloId = (Long) session.getAttribute("reservaVueloId");
        List<String> nombres = (List<String>) session.getAttribute("pasajerosNombres");
        List<LocalDate> nacimientos = (List<LocalDate>) session.getAttribute("pasajerosNacimientos");
        List<String> nacionalidades = (List<String>) session.getAttribute("pasajerosNacionalidades");
        List<String> asientos = (List<String>) session.getAttribute("pasajerosAsientos");

        if (vueloId == null || asientos == null || asientos.isEmpty()) {
            return "redirect:/inicio";
        }

        Cliente cliente = skyRouteService.obtenerClienteParaReserva(auth.getName(),
                (Long) session.getAttribute(AdminReservacionController.SESION_CLIENTE_ID));
        if (cliente == null) {
            return "redirect:/admin/reservaciones/nueva";
        }

        Reservacion reservacion = skyRouteService.crearReservacionMultiple(
                cliente.getCorreo(), vueloId, nombres, nacimientos, nacionalidades, asientos, metodoPago
        );

        session.removeAttribute("reservaVueloId");
        session.removeAttribute("reservaCantidadPasajeros");
        session.removeAttribute("pasajerosNombres");
        session.removeAttribute("pasajerosNacimientos");
        session.removeAttribute("pasajerosNacionalidades");
        session.removeAttribute("pasajerosAsientos");
        session.removeAttribute(AdminReservacionController.SESION_CLIENTE_ID);

        redirect.addFlashAttribute("folio", reservacion.getFolio());
        return "redirect:/reserva-confirmada";
    }

    @GetMapping("/reserva-confirmada")
    public String mostrarReservaConfirmada() {
        return "usuario/reserva-confirmada";
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