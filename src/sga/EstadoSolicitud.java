package sga;

/**
 * Representa el ciclo de vida formal de una solicitud académica y sus reglas de transición.
 */
public enum EstadoSolicitud {
    PENDIENTE,
    EN_REVISION,
    APROBADA,
    RECHAZADA;

    public boolean estaCerrada() {
        return this == APROBADA || this == RECHAZADA;
    }

    public boolean requiereRevisionPreviaPara(EstadoSolicitud nuevoEstado) {
        return this == PENDIENTE && (nuevoEstado == APROBADA || nuevoEstado == RECHAZADA);
    }

    /**
     * Transiciones manuales permitidas: únicamente EN_REVISION -> APROBADA / RECHAZADA.
     * El paso PENDIENTE -> EN_REVISION solo ocurre al asignar un responsable
     * (Solicitud.asignarResponsable), garantizando que toda solicitud resuelta fue revisada por alguien.
     */
    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        return this == EN_REVISION && nuevoEstado != null && nuevoEstado.estaCerrada();
    }
}
