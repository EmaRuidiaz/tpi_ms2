package sga;

import java.util.List;

/**
 * Clase especializada en la construcción y formato de reportes de solicitudes académicas.
 * Extraída de GestorSolicitudes para cumplir con el Principio de Responsabilidad Única (SRP)
 * y descompuesta mediante Extract Method para reducir la complejidad ciclomática.
 */
public class GeneradorReporteSolicitudes {

    private static final String LINEA_SEPARADORA_DOBLE =
            "================================================================================";
    private static final String LINEA_SEPARADORA_SIMPLE =
            "--------------------------------------------------------------------------------";

    public String generarReporteGeneral(List<Solicitud> solicitudes) {
        if (solicitudes == null) {
            throw new IllegalArgumentException("La lista de solicitudes a reportar no puede ser nula.");
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append(LINEA_SEPARADORA_DOBLE).append("\n");
        reporte.append("              REPORTE GENERAL DE SOLICITUDES ACADÉMICAS (SGA)                   \n");
        reporte.append(LINEA_SEPARADORA_DOBLE).append("\n");

        for (Solicitud solicitud : solicitudes) {
            reporte.append(formatearLineaSolicitud(solicitud));
        }

        reporte.append(LINEA_SEPARADORA_SIMPLE).append("\n");
        reporte.append(formatearResumenEstadistico(solicitudes)).append("\n");
        reporte.append(LINEA_SEPARADORA_DOBLE);
        return reporte.toString();
    }

    /**
     * Extract Method: Formatea el renglón individual de una solicitud en el reporte.
     */
    private String formatearLineaSolicitud(Solicitud solicitud) {
        Estudiante estudiante = solicitud.getEstudiante();
        String indicadorSla = formatearIndicadorSla(solicitud);

        return String.format(
                "- [%s] %-13s | Est: %-14s (%s) | Prio: %-5s | Estado: %-11s | Resp: %-16s |%s\n",
                solicitud.getId(),
                solicitud.getTipo().name(),
                estudiante.getNombre(),
                estudiante.getLegajo(),
                solicitud.getPrioridad().getEtiqueta(),
                solicitud.getEstado().name(),
                solicitud.getNombreResponsableAsignado(),
                indicadorSla
        );
    }

    /**
     * Extract Method: Construye la etiqueta de estado del SLA para una solicitud.
     */
    private String formatearIndicadorSla(Solicitud solicitud) {
        int diasTranscurridos = solicitud.getDiasTranscurridos();
        int plazoMaximo = solicitud.calcularPlazoMaximoDias();

        if (solicitud.tieneSlaVencido()) {
            return " [SLA VENCIDO: " + diasTranscurridos + "/" + plazoMaximo + " días]";
        }
        return " [SLA OK: " + diasTranscurridos + "/" + plazoMaximo + " días]";
    }

    /**
     * Extract Method: Calcula los totales por estado y SLA vencidos y construye el pie estadístico.
     */
    private String formatearResumenEstadistico(List<Solicitud> solicitudes) {
        int total = solicitudes.size();
        int pendientes = contarPorEstado(solicitudes, EstadoSolicitud.PENDIENTE);
        int enRevision = contarPorEstado(solicitudes, EstadoSolicitud.EN_REVISION);
        int aprobadas = contarPorEstado(solicitudes, EstadoSolicitud.APROBADA);
        int rechazadas = contarPorEstado(solicitudes, EstadoSolicitud.RECHAZADA);
        int slaVencidos = contarSlaVencidos(solicitudes);

        return String.format(
                "TOTAL: %d | PENDIENTES: %d | EN_REVISION: %d | APROBADAS: %d | RECHAZADAS: %d | SLA VENCIDOS: %d",
                total, pendientes, enRevision, aprobadas, rechazadas, slaVencidos
        );
    }

    private int contarPorEstado(List<Solicitud> solicitudes, EstadoSolicitud estadoBuscado) {
        int contador = 0;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getEstado() == estadoBuscado) {
                contador++;
            }
        }
        return contador;
    }

    private int contarSlaVencidos(List<Solicitud> solicitudes) {
        int contador = 0;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.tieneSlaVencido()) {
                contador++;
            }
        }
        return contador;
    }
}
