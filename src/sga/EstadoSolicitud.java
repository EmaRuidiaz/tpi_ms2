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

    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        if (nuevoEstado == null || this.estaCerrada()) {
            return false;
        }
        return !requiereRevisionPreviaPara(nuevoEstado);
    }
}
