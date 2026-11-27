package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.service.AdminRutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rutas")
@RequiredArgsConstructor
public class AdminRutaController {

    private final AdminRutaService rutaService;

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("rutas", rutaService.listarRutas());
        model.addAttribute("aeropuertos", rutaService.listarAeropuertos());
        return "admin/rutas";
    }

    @PostMapping
    public String crear(@RequestParam String codigo,
                        @RequestParam String origen,
                        @RequestParam String destino,
                        @RequestParam int horas,
                        @RequestParam int minutos,
                        @RequestParam double tarifa,
                        RedirectAttributes redirect) {
        try {
            rutaService.crear(codigo, origen, destino, horas, minutos, tarifa);
            redirect.addFlashAttribute("exito", "Ruta creada");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/rutas";
    }
}