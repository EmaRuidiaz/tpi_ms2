package sga;

/**
 * Entidad raíz del dominio que modela una Solicitud Académica.
 * Elimina Primitive Obsession, reemplaza nombres crípticos por identificadores expresivos (Clean Code)
 * y aplica Move Method para el cálculo de SLA y control de estado.
 */
public class Solicitud {

    public static final double FACTOR_REDUCCION_SLA_URGENTE = 0.50;
    public static final String RESPONSABLE_SIN_ASIGNAR = "SIN_ASIGNAR";

    private final String id;
    private final Estudiante estudiante;
    private final TipoSolicitud tipo;
    private final PrioridadSolicitud prioridad;
    private EstadoSolicitud estado;
    private Responsable responsable;
    private final String descripcion;
    private String observacionesResolucion;
    private final int diasTranscurridos;
    private final boolean urgente;

    public Solicitud(
            String id,
            Estudiante estudiante,
            TipoSolicitud tipo,
            PrioridadSolicitud prioridad,
            EstadoSolicitud estadoInicial,
            String descripcion,
            int diasTranscurridos,
            boolean urgente
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador de la solicitud no puede ser nulo ni vacío.");
        }
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante asociado a la solicitud no puede ser nulo.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser nulo.");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad de la solicitud no puede ser nula.");
        }
        if (estadoInicial == null) {
            throw new IllegalArgumentException("El estado inicial de la solicitud no puede ser nulo.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción de la solicitud no puede ser nula ni vacía.");
        }
        if (diasTranscurridos < 0) {
            throw new IllegalArgumentException("Los días transcurridos no pueden ser negativos.");
        }

        this.id = id;
        this.estudiante = estudiante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.estado = estadoInicial;
        this.descripcion = descripcion;
        this.diasTranscurridos = diasTranscurridos;
        this.urgente = urgente;
        this.observacionesResolucion = "";
    }

    /**
     * Move Method: Calcula el plazo máximo de resolución en días hábiles según el tipo de trámite
     * y si reviste carácter urgente.
     */
    public int calcularPlazoMaximoDias() {
        int plazoBase = tipo.getPlazoMaximoDiasBase();
        if (urgente) {
            return (int) Math.ceil(plazoBase * FACTOR_REDUCCION_SLA_URGENTE);
        }
        return plazoBase;
    }

    /**
     * Move Method: Determina si la solicitud superó su acuerdo de nivel de servicio (SLA)
     * encontrándose aún abierta.
     */
    public boolean tieneSlaVencido() {
        return !estado.estaCerrada() && diasTranscurridos > calcularPlazoMaximoDias();
    }

    public void asignarResponsable(Responsable nuevoResponsable) {
        if (nuevoResponsable == null) {
            throw new IllegalArgumentException("El responsable a asignar no puede ser nulo.");
        }
        this.responsable = nuevoResponsable;
        nuevoResponsable.incrementarCargaOperativa();
        if (this.estado == EstadoSolicitud.PENDIENTE) {
            this.estado = EstadoSolicitud.EN_REVISION;
        }
    }

    public void actualizarEstado(EstadoSolicitud nuevoEstado, String observaciones) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición de estado inválida desde " + this.estado + " hacia " + nuevoEstado
            );
        }
        this.estado = nuevoEstado;
        this.observacionesResolucion = observaciones != null ? observaciones : "";
    }

    public String getNombreResponsableAsignado() {
        return responsable != null ? responsable.getNombre() : RESPONSABLE_SIN_ASIGNAR;
    }

    public String getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public PrioridadSolicitud getPrioridad() {
        return prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getObservacionesResolucion() {
        return observacionesResolucion;
    }

    public int getDiasTranscurridos() {
        return diasTranscurridos;
    }

    public boolean esUrgente() {
        return urgente;
    }
}
