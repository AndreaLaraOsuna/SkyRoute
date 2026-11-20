package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.service.AdminClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/clientes")
@RequiredArgsConstructor
public class AdminClienteController {

    private final AdminClienteService clienteService;

    @GetMapping
    public String mostrar(@RequestParam(required = false) String q,
                          @RequestParam(required = false) Long id,
                          Model model) {
        model.addAttribute("q", q);
        model.addAttribute("clientes", clienteService.buscarClientes(q));
        model.addAttribute("idSeleccionado", id);

        if (id != null) {
            try {
                model.addAttribute("historial", clienteService.obtenerHistorial(id));
            } catch (IllegalArgumentException e) {
                // Si el id no existe, se muestra solo la lista
                model.addAttribute("idSeleccionado", null);
            }
        }
        return "admin/clientes";
    }
}