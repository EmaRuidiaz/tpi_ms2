package sga;

/**
 * Introduce Parameter Object: Encapsula los filtros combinables de búsqueda de solicitudes,
 * eliminando el uso de valores mágicos ("", 0) en las consultas.
 */
public class CriterioBusquedaSolicitud {

    private final TipoSolicitud tipo;
    private final EstadoSolicitud estado;
    private final PrioridadSolicitud prioridad;

    public CriterioBusquedaSolicitud(
            TipoSolicitud tipo,
            EstadoSolicitud estado,
            PrioridadSolicitud prioridad
    ) {
        this.tipo = tipo;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public static CriterioBusquedaSolicitud porPrioridad(PrioridadSolicitud prioridad) {
        return new CriterioBusquedaSolicitud(null, null, prioridad);
    }

    public static CriterioBusquedaSolicitud porEstado(EstadoSolicitud estado) {
        return new CriterioBusquedaSolicitud(null, estado, null);
    }

    public static CriterioBusquedaSolicitud porTipo(TipoSolicitud tipo) {
        return new CriterioBusquedaSolicitud(tipo, null, null);
    }

    public boolean coincideCon(Solicitud solicitud) {
        if (solicitud == null) {
            return false;
        }
        boolean coincideTipo = (tipo == null || solicitud.getTipo() == tipo);
        boolean coincideEstado = (estado == null || solicitud.getEstado() == estado);
        boolean coincidePrioridad = (prioridad == null || solicitud.getPrioridad() == prioridad);
        return coincideTipo && coincideEstado && coincidePrioridad;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public PrioridadSolicitud getPrioridad() {
        return prioridad;
    }
}
