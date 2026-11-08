package sga;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación de RepositorioSolicitudes que persiste los datos en un archivo CSV estructurado.
 * Elimina el código triplicado de escritura en disco presente en la versión inicial (DRY).
 */
public class RepositorioSolicitudesArchivo implements RepositorioSolicitudes {

    private static final String ENCABEZADO_CSV =
            "ID;LEGAJO;ESTUDIANTE;TIPO;PRIORIDAD;ESTADO;RESPONSABLE;DIAS;URGENTE;OBSERVACIONES\n";
    private static final String SEPARADOR_CSV = ";";

    private final String rutaArchivo;
    private final List<Solicitud> almacenamientoInterno = new ArrayList<>();

    public RepositorioSolicitudesArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de persistencia no puede ser nula ni vacía.");
        }
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("No se puede guardar una solicitud nula.");
        }
        if (buscarPorId(solicitud.getId()) != null) {
            throw new IllegalStateException("Ya existe una solicitud registrada con el ID " + solicitud.getId() + ".");
        }
        almacenamientoInterno.add(solicitud);
        sincronizarArchivoCsv();
    }

    @Override
    public void actualizar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("No se puede actualizar una solicitud nula.");
        }
        sincronizarArchivoCsv();
    }

    @Override
    public Solicitud buscarPorId(String idSolicitud) {
        if (idSolicitud == null || idSolicitud.isBlank()) {
            return null;
        }
        for (Solicitud solicitud : almacenamientoInterno) {
            if (solicitud.getId().equals(idSolicitud)) {
                return solicitud;
            }
        }
        return null;
    }

    @Override
    public List<Solicitud> obtenerTodas() {
        return Collections.unmodifiableList(almacenamientoInterno);
    }

    private void sincronizarArchivoCsv() {
        try (FileWriter escritor = new FileWriter(rutaArchivo, StandardCharsets.UTF_8, false)) {
            escritor.write(ENCABEZADO_CSV);
            for (Solicitud solicitud : almacenamientoInterno) {
                escritor.write(formatearFilaCsv(solicitud));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Error al sincronizar el archivo de solicitudes: " + ex.getMessage(), ex);
        }
    }

    private String formatearFilaCsv(Solicitud solicitud) {
        Estudiante est = solicitud.getEstudiante();
        return solicitud.getId() + SEPARADOR_CSV
                + est.getLegajo() + SEPARADOR_CSV
                + est.getNombre() + SEPARADOR_CSV
                + solicitud.getTipo().name() + SEPARADOR_CSV
                + solicitud.getPrioridad().getCodigoNumerico() + SEPARADOR_CSV
                + solicitud.getEstado().name() + SEPARADOR_CSV
                + solicitud.getNombreResponsableAsignado() + SEPARADOR_CSV
                + solicitud.getDiasTranscurridos() + SEPARADOR_CSV
                + solicitud.esUrgente() + SEPARADOR_CSV
                + solicitud.getObservacionesResolucion() + "\n";
    }
}
