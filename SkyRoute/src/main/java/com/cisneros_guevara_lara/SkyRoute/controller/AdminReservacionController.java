package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.model.enums.EstadoReservacion;
import com.cisneros_guevara_lara.SkyRoute.service.AdminReservacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.cisneros_guevara_lara.SkyRoute.model.Cliente;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cisneros_guevara_lara.SkyRoute.model.Reservacion;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.util.List;

@Controller
@RequestMapping("/admin/reservaciones")
@RequiredArgsConstructor
public class AdminReservacionController {
    public static final String SESION_CLIENTE_ID = "adminClienteId";
    private final AdminReservacionService reservacionService;

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(name = "estado", required = false) List<EstadoReservacion> estados,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                         Model model) {
        List<EstadoReservacion> seleccionados = (estados != null) ? estados : List.of();

        try {
            model.addAttribute("reservaciones",
                    reservacionService.listarReservaciones(q, seleccionados, fechaInicio, fechaFin));
        } catch (IllegalArgumentException e) {
            model.addAttribute("reservaciones", List.of());
            model.addAttribute("error", e.getMessage());
        }

        // Se regresan para que el formulario conserve lo que el usuario eligió
        model.addAttribute("estados", EstadoReservacion.values());
        model.addAttribute("estadosSeleccionados", seleccionados);
        model.addAttribute("q", q);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        return "admin/reservaciones";
    }

    // Muestra la pantalla: buscador + lista de clientes. Al hacer clic en uno llega con clienteId.
    @GetMapping("/nueva")
    public String mostrarNueva(@RequestParam(required = false) String q,
                               @RequestParam(required = false) Long clienteId,
                               Model model) {
        model.addAttribute("q", q);
        model.addAttribute("clientes", reservacionService.buscarClientes(q));
        if (clienteId != null) {
            model.addAttribute("clienteSeleccionado", reservacionService.obtenerCliente(clienteId));
        }
        return "admin/nueva-reservacion";
    }

    // "Realizar reserva": guarda al cliente en la sesión y manda al admin al Inicio del cliente.
    @PostMapping("/nueva")
    public String iniciarReserva(@RequestParam(required = false) Long clienteId,
                                 HttpSession session,
                                 RedirectAttributes redirect) {
        try {
            Cliente cliente = reservacionService.obtenerCliente(clienteId);
            session.setAttribute(SESION_CLIENTE_ID, cliente.getId());
            return "redirect:/inicio";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/reservaciones/nueva";
        }
    }

    // Muestra el detalle
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            Reservacion reservacion = reservacionService.obtenerReservacion(id);
            model.addAttribute("reservacion", reservacion);
            model.addAttribute("tituloDia", formatearDia(reservacion.getVuelo().getFecha()));
            return "admin/detalle-reservacion";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/reservaciones";
        }
    }

    @PostMapping("/{id}/checkin")
    public String checkIn(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            reservacionService.hacerCheckIn(id);
            redirect.addFlashAttribute("exito", "checkin");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservaciones/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            reservacionService.cancelar(id);
            redirect.addFlashAttribute("exito", "cancelada");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservaciones/" + id;
    }

    private String formatearDia(LocalDate fecha) {
        if (fecha == null) return "";
        String texto = fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es-MX")));
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}