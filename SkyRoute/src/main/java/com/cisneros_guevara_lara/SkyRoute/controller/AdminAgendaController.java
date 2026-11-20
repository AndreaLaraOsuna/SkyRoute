package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.service.AdminAgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/agenda")
@RequiredArgsConstructor
public class AdminAgendaController {

    private final AdminAgendaService agendaService;

    // /admin/agenda                          -> semana actual
    // /admin/agenda?semana=2026-11-22        -> esa semana (flechas)
    // /admin/agenda?vista=dia&dia=2026-11-20 -> pestaña Día con esa fecha
    @GetMapping
    public String mostrar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semana,
                          @RequestParam(required = false) String vista,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia,
                          Model model) {
        LocalDate diaSeleccionado = (dia != null) ? dia : LocalDate.now();
        LocalDate fechaSemana = (semana != null) ? semana : diaSeleccionado;

        model.addAttribute("agenda", agendaService.obtenerSemana(fechaSemana));
        model.addAttribute("vista", vista);
        model.addAttribute("dia", diaSeleccionado);
        model.addAttribute("tituloDia", agendaService.tituloDia(diaSeleccionado));
        model.addAttribute("vuelosDia", agendaService.obtenerVuelosDelDia(diaSeleccionado));
        return "admin/agenda";
    }
}