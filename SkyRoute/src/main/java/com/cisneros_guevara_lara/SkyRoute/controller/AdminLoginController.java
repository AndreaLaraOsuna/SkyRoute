package com.cisneros_guevara_lara.SkyRoute.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    // Muestra el formulario de login del panel.
    // El POST de /admin/login lo procesa Spring Security (SecurityConfig).
    @GetMapping("/login")
    public String mostrarLogin() {
        return "admin/login";
    }

    // Si escriben solo /admin, los manda a reservaciones
    @GetMapping({"", "/"})
    public String inicio() {
        return "redirect:/admin/login";
    }
}