package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import com.cisneros_guevara_lara.SkyRoute.service.AdminResumenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/resumen")
@RequiredArgsConstructor
public class AdminResumenController {

    private final AdminResumenService resumenService;

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("resumen", resumenService.obtenerResumen());
        return "admin/resumen";
    }
}