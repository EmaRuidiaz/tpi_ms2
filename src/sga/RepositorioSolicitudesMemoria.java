package sga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación en memoria de RepositorioSolicitudes (Principio LSP).
 * Permite ejecutar pruebas unitarias rápidas y determinísticas sin tocar el sistema de archivos.
 */
public class RepositorioSolicitudesMemoria implements RepositorioSolicitudes {

    private final List<Solicitud> solicitudes = new ArrayList<>();

    @Override
    public void guardar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("No se puede guardar una solicitud nula.");
        }
        solicitudes.add(solicitud);
    }

    @Override
    public void actualizar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("No se puede actualizar una solicitud nula.");
        }
    }

    @Override
    public Solicitud buscarPorId(String idSolicitud) {
        if (idSolicitud == null || idSolicitud.isBlank()) {
            return null;
        }
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId().equals(idSolicitud)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public List<Solicitud> obtenerTodas() {
        return Collections.unmodifiableList(solicitudes);
    }
}
