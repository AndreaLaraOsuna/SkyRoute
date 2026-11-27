package com.cisneros_guevara_lara.SkyRoute.dto.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 10, message = "El teléfono no puede exceder 10 dígitos")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasenia;

    @NotBlank(message = "Debes confirmar la contraseña")
    private String confirmarContrasenia;
}
