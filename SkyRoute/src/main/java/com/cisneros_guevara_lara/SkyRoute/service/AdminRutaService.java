package com.cisneros_guevara_lara.SkyRoute.service;

import com.cisneros_guevara_lara.SkyRoute.model.Ruta;
import com.cisneros_guevara_lara.SkyRoute.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class AdminRutaService {

    // Ciudades que ofrece la aerolínea (opciones de Origen y Destino)
    private static final List<String> CIUDADES = List.of(
            "Buenos Aires", "Cancún", "Ciudad de México", "Ciudad Obregón", "Guadalajara",
            "Hermosillo", "Los Mochis", "Madrid", "Mazatlán", "Monterrey", "Tijuana"
    );

    private final RutaRepository rutaRepository;

    @Transactional(readOnly = true)
    public List<Ruta> listarRutas() {
        return rutaRepository.findAll(Sort.by("codigo"));
    }

    @Transactional(readOnly = true)
    public List<String> listarAeropuertos() {
        TreeSet<String> ciudades = new TreeSet<>(CIUDADES);
        ciudades.addAll(rutaRepository.listarOrigenes());
        ciudades.addAll(rutaRepository.listarDestinos());
        return new ArrayList<>(ciudades);
    }

    @Transactional
    public Ruta crear(String codigo, String origen, String destino,
                      int horas, int minutos, double tarifa) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de vuelo es obligatorio.");
        }
        String codigoLimpio = codigo.trim().toUpperCase();

        if (rutaRepository.findByCodigo(codigoLimpio).isPresent()) {
            throw new IllegalArgumentException("Ya existe una ruta con el código " + codigoLimpio + ".");
        }
        if (origen == null || destino == null || origen.isBlank() || destino.isBlank()) {
            throw new IllegalArgumentException("El origen y el destino son obligatorios.");
        }
        origen = origen.trim();
        destino = destino.trim();

        if (origen.equalsIgnoreCase(destino)) {
            throw new IllegalArgumentException("El origen y el destino deben ser diferentes.");
        }
        if (rutaRepository.existsByOrigenIgnoreCaseAndDestinoIgnoreCase(origen, destino)) {
            throw new IllegalArgumentException("Ya existe una ruta de " + origen + " a " + destino + ".");
        }
        if (horas < 0 || minutos < 0 || minutos > 59) {
            throw new IllegalArgumentException("La duración no es válida.");
        }
        int duracion = horas * 60 + minutos;
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0.");
        }
        if (tarifa <= 0) {
            throw new IllegalArgumentException("La tarifa debe ser mayor a 0.");
        }

        Ruta ruta = new Ruta();
        ruta.setCodigo(codigoLimpio);
        ruta.setOrigen(origen);
        ruta.setDestino(destino);
        ruta.setDuracionMinutos(duracion);
        ruta.setTarifa(tarifa);
        return rutaRepository.save(ruta);
    }
}