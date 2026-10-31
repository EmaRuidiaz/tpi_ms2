package sga;

/**
 * Introduce Parameter Object: Agrupa de forma cohesiva e inmutable los datos requeridos
 * para dar de alta una solicitud académica, eliminando la lista extensa de 13 parámetros
 * primitivos (Long Parameter List) del método original reg(...).
 */
public class DatosRegistroSolicitud {

    private final String idSolicitud;
    private final Estudiante estudiante;
    private final TipoSolicitud tipo;
    private final PrioridadSolicitud prioridadBase;
    private final String descripcion;
    private final int diasTranscurridos;
    private final boolean urgente;

    public DatosRegistroSolicitud(
            String idSolicitud,
            Estudiante estudiante,
            TipoSolicitud tipo,
            PrioridadSolicitud prioridadBase,
            String descripcion,
            int diasTranscurridos,
            boolean urgente
    ) {
        if (idSolicitud == null || idSolicitud.isBlank()) {
            throw new IllegalArgumentException("El ID de la solicitud es obligatorio.");
        }
        if (estudiante == null) {
            throw new IllegalArgumentException("Los datos del estudiante son obligatorios.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de solicitud es obligatorio.");
        }
        if (prioridadBase == null) {
            throw new IllegalArgumentException("La prioridad base es obligatoria.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del trámite es obligatoria.");
        }
        if (diasTranscurridos < 0) {
            throw new IllegalArgumentException("Los días transcurridos no pueden ser negativos.");
        }

        this.idSolicitud = idSolicitud;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridadBase = prioridadBase;
        this.descripcion = descripcion;
        this.diasTranscurridos = diasTranscurridos;
        this.urgente = urgente;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public PrioridadSolicitud getPrioridadBase() {
        return prioridadBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDiasTranscurridos() {
        return diasTranscurridos;
    }

    public boolean esUrgente() {
        return urgente;
    }
}
