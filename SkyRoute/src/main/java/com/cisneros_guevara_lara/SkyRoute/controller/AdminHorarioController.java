package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.service.AdminHorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/admin/horarios")
@RequiredArgsConstructor
public class AdminHorarioController {

    private final AdminHorarioService horarioService;

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("rutas", horarioService.listarRutas());
        model.addAttribute("aviones", horarioService.listarAviones());
        model.addAttribute("vuelos", horarioService.listarVuelosProximos());
        return "admin/horarios";
    }

    // Pestaña "Programar vuelo": crea un vuelo nuevo
    @PostMapping("/programar")
    public String programar(@RequestParam(required = false) Long rutaId,
                            @RequestParam(required = false) Long avionId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime hora,
                            RedirectAttributes redirect) {
        try {
            horarioService.programar(rutaId, avionId, fecha, hora);
            redirect.addFlashAttribute("exito", "Vuelo programado");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/horarios";
    }

    // Pestaña "Actualizar vuelo": cambia fecha y hora de los vuelos seleccionados
    @PostMapping
    public String reprogramar(@RequestParam(required = false) List<Long> vueloIds,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime hora,
                              RedirectAttributes redirect) {
        try {
            int cambiados = horarioService.reprogramar(vueloIds, fecha, hora);
            redirect.addFlashAttribute("exito", "Horario guardado");
            redirect.addFlashAttribute("detalleExito", cambiados + " vuelo(s) actualizado(s)");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        redirect.addFlashAttribute("pestana", "actualizar");
        return "redirect:/admin/horarios";
    }
}