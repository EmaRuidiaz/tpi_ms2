package sga;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicación encargado de coordinar los casos de uso de gestión de solicitudes académicas.
 * Refactorizado aplicando SOLID (SRP, OCP, LSP, ISP, DIP), Clean Code y las técnicas de
 * Introduce Parameter Object, Extract Method y Move Method.
 */
public class GestorSolicitudes {

    private final RepositorioSolicitudes repositorio;
    private final GeneradorReporteSolicitudes generadorReporte;
    private ServicioNotificacion servicioNotificacion;

    public GestorSolicitudes(
            RepositorioSolicitudes repositorio,
            ServicioNotificacion servicioNotificacion
    ) {
        this(repositorio, servicioNotificacion, new GeneradorReporteSolicitudes());
    }

    public GestorSolicitudes(
            RepositorioSolicitudes repositorio,
            ServicioNotificacion servicioNotificacion,
            GeneradorReporteSolicitudes generadorReporte
    ) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de solicitudes no puede ser nulo.");
        }
        if (servicioNotificacion == null) {
            throw new IllegalArgumentException("El servicio de notificación no puede ser nulo.");
        }
        if (generadorReporte == null) {
            throw new IllegalArgumentException("El generador de reportes no puede ser nulo.");
        }
        this.repositorio = repositorio;
        this.servicioNotificacion = servicioNotificacion;
        this.generadorReporte = generadorReporte;
    }

    public void setServicioNotificacion(ServicioNotificacion nuevoServicioNotificacion) {
        if (nuevoServicioNotificacion == null) {
            throw new IllegalArgumentException("El servicio de notificación no puede ser nulo.");
        }
        this.servicioNotificacion = nuevoServicioNotificacion;
    }

    /**
     * Operación 1: Registra una nueva solicitud académica recibiendo un Parameter Object validado.
     */
    public Solicitud registrarSolicitud(DatosRegistroSolicitud datos) {
        validarDatosNoNulos(datos, "Los datos de registro de la solicitud no pueden ser nulos.");

        PrioridadSolicitud prioridadEfectiva = determinarPrioridadEfectiva(datos);
        Solicitud nuevaSolicitud = crearSolicitudInicial(datos, prioridadEfectiva);

        repositorio.guardar(nuevaSolicitud);
        notificarRegistroSolicitud(nuevaSolicitud);

        return nuevaSolicitud;
    }

    /**
     * Operación 2: Asigna un responsable validando cupo y compatibilidad de área mediante el dominio.
     */
    public boolean asignarResponsable(String idSolicitud, Responsable responsable) {
        validarDatosNoNulos(responsable, "El responsable no puede ser nulo.");
        Solicitud solicitud = repositorio.buscarPorId(idSolicitud);
        if (solicitud == null) {
            return false;
        }

        if (!validarAsignacionResponsable(solicitud, responsable)) {
            return false;
        }

        solicitud.asignarResponsable(responsable);
        repositorio.actualizar(solicitud);
        notificarAsignacionResponsable(solicitud, responsable);

        return true;
    }

    /**
     * Operación 3: Cambia el estado de una solicitud verificando las reglas del ciclo de vida.
     */
    public boolean cambiarEstadoSolicitud(
            String idSolicitud,
            EstadoSolicitud nuevoEstado,
            String observacionesResolucion
    ) {
        validarDatosNoNulos(nuevoEstado, "El nuevo estado no puede ser nulo.");
        Solicitud solicitud = repositorio.buscarPorId(idSolicitud);
        if (solicitud == null) {
            return false;
        }

        if (!validarTransicionEstado(solicitud, nuevoEstado)) {
            return false;
        }

        solicitud.actualizarEstado(nuevoEstado, observacionesResolucion);
        repositorio.actualizar(solicitud);
        notificarCambioEstado(solicitud);

        return true;
    }

    /**
     * Operación 4: Busca y filtra solicitudes según un objeto de criterios combinables.
     */
    public List<Solicitud> buscarSolicitudes(CriterioBusquedaSolicitud criterio) {
        validarDatosNoNulos(criterio, "El criterio de búsqueda no puede ser nulo.");
        List<Solicitud> coincidencias = new ArrayList<>();
        for (Solicitud solicitud : repositorio.obtenerTodas()) {
            if (criterio.coincideCon(solicitud)) {
                coincidencias.add(solicitud);
            }
        }
        return coincidencias;
    }

    /**
     * Operación 5: Genera el reporte general delegando el formateo en GeneradorReporteSolicitudes.
     */
    public String generarReporteGeneral() {
        return generadorReporte.generarReporteGeneral(repositorio.obtenerTodas());
    }

    // --- Métodos extraídos (Extract Method) ---

    private PrioridadSolicitud determinarPrioridadEfectiva(DatosRegistroSolicitud datos) {
        Estudiante estudiante = datos.getEstudiante();
        if (estudiante.cumpleMeritoAcademicoPrioritario()) {
            return PrioridadSolicitud.ALTA;
        }
        if (datos.esUrgente()) {
            return datos.getPrioridadBase().elevarUnNivel();
        }
        return datos.getPrioridadBase();
    }

    private Solicitud crearSolicitudInicial(
            DatosRegistroSolicitud datos,
            PrioridadSolicitud prioridadEfectiva
    ) {
        return new Solicitud(
                datos.getIdSolicitud(),
                datos.getEstudiante(),
                datos.getTipo(),
                prioridadEfectiva,
                EstadoSolicitud.PENDIENTE,
                datos.getDescripcion(),
                datos.getDiasTranscurridos(),
                datos.esUrgente()
        );
    }

    private boolean validarAsignacionResponsable(Solicitud solicitud, Responsable responsable) {
        if (solicitud.getEstado().estaCerrada()) {
            System.out.println("[RECHAZO-ASIGNACION] La solicitud " + solicitud.getId()
                    + " ya se encuentra cerrada (" + solicitud.getEstado().name() + ").");
            return false;
        }

        if (solicitud.getResponsable() == responsable) {
            System.out.println("[RECHAZO-ASIGNACION] La solicitud " + solicitud.getId()
                    + " ya está asignada a " + responsable.getNombre() + ".");
            return false;
        }

        if (!responsable.tieneCupoDisponible()) {
            System.out.println("[RECHAZO-ASIGNACION] Responsable " + responsable.getNombre()
                    + " sin cupo disponible (" + responsable.getCargaActual()
                    + "/" + responsable.getMaximoSolicitudes() + ").");
            return false;
        }

        if (!responsable.puedeGestionarTipo(solicitud.getTipo())) {
            System.out.println("[RECHAZO-ASIGNACION] El área '" + responsable.getArea()
                    + "' no gestiona solicitudes de tipo " + solicitud.getTipo().name() + ".");
            return false;
        }

        return true;
    }

    private boolean validarTransicionEstado(Solicitud solicitud, EstadoSolicitud nuevoEstado) {
        EstadoSolicitud estadoActual = solicitud.getEstado();
        if (estadoActual.estaCerrada()) {
            System.out.println("[TRANSICION-INVALIDA] La solicitud " + solicitud.getId()
                    + " ya se encuentra cerrada (" + estadoActual.name() + ").");
            return false;
        }
        if (estadoActual.requiereRevisionPreviaPara(nuevoEstado)) {
            System.out.println("[TRANSICION-INVALIDA] La solicitud " + solicitud.getId()
                    + " debe pasar por EN_REVISION antes de resolverse.");
            return false;
        }
        if (!estadoActual.puedeTransicionarA(nuevoEstado)) {
            System.out.println("[TRANSICION-INVALIDA] La solicitud " + solicitud.getId()
                    + " no puede pasar de " + estadoActual.name() + " a " + nuevoEstado.name()
                    + " (EN_REVISION solo se alcanza al asignar un responsable).");
            return false;
        }
        return true;
    }

    private void notificarRegistroSolicitud(Solicitud solicitud) {
        String mensaje = "Solicitud " + solicitud.getId()
                + " (" + solicitud.getTipo().name() + ") registrada con estado "
                + solicitud.getEstado().name() + " y prioridad "
                + solicitud.getPrioridad().getEtiqueta() + ".";
        servicioNotificacion.enviarNotificacion(solicitud.getEstudiante(), mensaje);
    }

    private void notificarAsignacionResponsable(Solicitud solicitud, Responsable responsable) {
        String mensaje = "Solicitud " + solicitud.getId()
                + " asignada a " + responsable.getNombre()
                + " (" + responsable.getArea() + "). Estado actual: "
                + solicitud.getEstado().name() + ".";
        servicioNotificacion.enviarNotificacion(solicitud.getEstudiante(), mensaje);
    }

    private void notificarCambioEstado(Solicitud solicitud) {
        String mensaje = "Solicitud " + solicitud.getId()
                + " cambió a estado " + solicitud.getEstado().name()
                + ". Resolución: " + solicitud.getObservacionesResolucion();
        servicioNotificacion.enviarNotificacion(solicitud.getEstudiante(), mensaje);
    }

    private void validarDatosNoNulos(Object objeto, String mensajeError) {
        if (objeto == null) {
            throw new IllegalArgumentException(mensajeError);
        }
    }
}
