package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.model.enums.TipoBloqueo;
import com.cisneros_guevara_lara.SkyRoute.service.AdminBloqueoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/bloqueos")
@RequiredArgsConstructor
public class AdminBloqueoController {

    private final AdminBloqueoService bloqueoService;

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("vuelos", bloqueoService.listarVuelosBloqueables());
        return "admin/bloqueos";
    }

    @PostMapping
    public String guardar(@RequestParam(required = false) TipoBloqueo tipo,
                          @RequestParam(required = false) String motivo,
                          @RequestParam(required = false) List<Long> vueloIds,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horaInicio,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horaFin,
                          Authentication auth,
                          RedirectAttributes redirect) {
        try {
            AdminBloqueoService.Resultado r = bloqueoService.bloquear(auth.getName(), tipo, motivo, vueloIds,
                    desde, hasta, fecha, horaInicio, horaFin);
            redirect.addFlashAttribute("exito", "Bloqueo guardado");
            redirect.addFlashAttribute("detalleExito", r.vuelosBloqueados() + " vuelo(s) bloqueado(s), "
                    + r.reservacionesCanceladas() + " reservación(es) cancelada(s)");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/bloqueos";
    }
}