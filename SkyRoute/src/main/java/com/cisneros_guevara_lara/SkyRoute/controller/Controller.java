package com.cisneros_guevara_lara.SkyRoute.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@org.springframework.stereotype.Controller
public class Controller{

    @GetMapping({"/", "/inicio"})
    public String mostrarInicio() {
        return "usuario/inicio";
    }

    @GetMapping("/asientos")
    public String mostrarAsientos() {
        return "usuario/asientos";
    }

    @PostMapping("/pago")
    public String procesarAsientos(@RequestParam(name = "asiento", required = false) List<String> asientosSeleccionados, Model model) {
        model.addAttribute("asientos", asientosSeleccionados);
        return "usuario/pago";
    }

    @GetMapping("/contacto")
    public String mostrarContacto() {
        return "usuario/contacto";
    }

    @GetMapping("/mis-reservas")
    public String mostrarMisReservas() {
        return "usuario/mis-reservas";
    }

    @GetMapping("/pago")
    public String mostrarPago() {
        return "usuario/pago";
    }

    @GetMapping("/reservar")
    public String mostrarReservar() {
        return "usuario/reservar";
    }

    @PostMapping("/asientos")
    public String procesarDatosPasajero(@RequestParam String nombreCompleto,
                                        @RequestParam String fechaNacimiento,
                                        @RequestParam String nacionalidad,
                                        @RequestParam String correo,
                                        @RequestParam String telefono) {
        return "usuario/asientos";
    }

    @GetMapping({"/login"})
    public String mostrarLogin() {
        return "index";
    }


}
