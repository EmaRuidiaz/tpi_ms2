package sga;

import java.util.ArrayList;
import java.util.List;

/**
 * Suite de pruebas automatizadas de verificación funcional y de regresión (Etapa 6).
 * Comprueba mediante un repositorio en memoria y un espía de notificaciones (Test Double)
 * que el comportamiento del dominio y casos de uso se mantiene íntegro tras el refactoring.
 */
public class PruebasVerificacionSistema {

    private static int pruebasEjecutadas = 0;
    private static int pruebasExitosas = 0;

    /**
     * Doble de prueba (Spy) para verificar notificaciones sin depender de la consola.
     */
    private static class NotificacionSpy implements ServicioNotificacion {
        private final List<String> mensajesEnviados = new ArrayList<>();

        @Override
        public void enviarNotificacion(Estudiante destinatario, String mensaje) {
            mensajesEnviados.add(destinatario.getLegajo() + ": " + mensaje);
        }

        public int cantidadNotificaciones() {
            return mensajesEnviados.size();
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("     SUITE DE PRUEBAS AUTOMATIZADAS DE VERIFICACIÓN — SGA (TPI MS II)           ");
        System.out.println("================================================================================\n");

        ejecutarPrueba("CP-01: Registro estándar conserva prioridad base y estado PENDIENTE",
                PruebasVerificacionSistema::probarRegistroEstandar);

        ejecutarPrueba("CP-02: Prioridad ALTA automática por mérito académico (>=20 mat y prom >=8.0)",
                PruebasVerificacionSistema::probarPrioridadPorMeritoAcademico);

        ejecutarPrueba("CP-03: Escalamiento de prioridad en solicitud urgente (BAJA -> MEDIA)",
                PruebasVerificacionSistema::probarEscalamientoPorUrgencia);

        ejecutarPrueba("CP-04: Asignación válida de responsable actualiza carga y pasa a EN_REVISION",
                PruebasVerificacionSistema::probarAsignacionResponsableValida);

        ejecutarPrueba("CP-05: Rechazo de asignación cuando el responsable no tiene cupo disponible",
                PruebasVerificacionSistema::probarRechazoPorFaltaDeCupo);

        ejecutarPrueba("CP-06: Rechazo de asignación por incompatibilidad de área administrativa",
                PruebasVerificacionSistema::probarRechazoPorAreaIncompatible);

        ejecutarPrueba("CP-07: Transición válida de EN_REVISION a APROBADA registra observaciones",
                PruebasVerificacionSistema::probarTransicionEstadoValida);

        ejecutarPrueba("CP-08: Bloqueo de transiciones inválidas (solicitud cerrada o sin revisión previa)",
                PruebasVerificacionSistema::probarBloqueoTransicionesInvalidas);

        ejecutarPrueba("CP-09: Cálculo de plazos SLA (normal y urgente) y detección de vencimiento",
                PruebasVerificacionSistema::probarCalculoSlaYVencimiento);

        ejecutarPrueba("CP-10: Filtrado combinado y validaciones defensivas (Guard Clauses)",
                PruebasVerificacionSistema::probarFiltradoYValidacionesDefensivas);

        ejecutarPrueba("CP-11: PENDIENTE no pasa a EN_REVISION manualmente sin asignar responsable",
                PruebasVerificacionSistema::probarBloqueoRevisionSinResponsable);

        ejecutarPrueba("CP-12: Reasignación libera la carga del responsable anterior",
                PruebasVerificacionSistema::probarReasignacionLiberaCarga);

        ejecutarPrueba("CP-13: Rechazo de asignación sobre solicitud cerrada sin consumir cupo",
                PruebasVerificacionSistema::probarRechazoAsignacionSolicitudCerrada);

        ejecutarPrueba("CP-14: Rechazo de registro con ID de solicitud duplicado",
                PruebasVerificacionSistema::probarRechazoIdDuplicado);

        int porcentajeExito = pruebasEjecutadas == 0 ? 0 : (pruebasExitosas * 100) / pruebasEjecutadas;
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("RESUMEN DE EJECUCIÓN: %d/%d pruebas superadas exitosamente (%d%% PASS)%n",
                pruebasExitosas, pruebasEjecutadas, porcentajeExito);
        System.out.println("================================================================================");

        if (pruebasExitosas != pruebasEjecutadas) {
            System.exit(1);
        }
    }

    private static void ejecutarPrueba(String nombreCaso, Runnable casoPrueba) {
        pruebasEjecutadas++;
        try {
            casoPrueba.run();
            pruebasExitosas++;
            System.out.println("[PASS] " + nombreCaso);
        } catch (Throwable error) {
            System.out.println("[FAIL] " + nombreCaso + " -> " + error.getMessage());
        }
    }

    private static void probarRegistroEstandar() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        NotificacionSpy spy = new NotificacionSpy();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, spy);

        Estudiante est = new Estudiante("LEG-100", "Juan Pérez", "jperez@est.utn.edu.ar", "TUPaD", 10, 7.0);
        Solicitud sol = gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-01", est, TipoSolicitud.BAJA, PrioridadSolicitud.BAJA, "Baja cuatrimestral", 1, false
        ));

        verificar(sol.getEstado() == EstadoSolicitud.PENDIENTE, "El estado inicial debe ser PENDIENTE");
        verificar(sol.getPrioridad() == PrioridadSolicitud.BAJA, "La prioridad debe mantenerse en BAJA");
        verificar(spy.cantidadNotificaciones() == 1, "Debe haberse emitido 1 notificación de alta");
    }

    private static void probarPrioridadPorMeritoAcademico() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante estMerito = new Estudiante("LEG-101", "Ana Gómez", "agomez@est.utn.edu.ar", "TUPaD", 22, 8.50);
        Solicitud sol = gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-02", estMerito, TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.BAJA, "Equivalencia externa", 2, false
        ));

        verificar(sol.getPrioridad() == PrioridadSolicitud.ALTA,
                "Estudiante con >=20 materias y promedio >=8.0 debe recibir Prioridad ALTA");
    }

    private static void probarEscalamientoPorUrgencia() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-102", "Pedro Ruiz", "pruiz@est.utn.edu.ar", "TUPaD", 8, 6.90);
        Solicitud sol = gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-03", est, TipoSolicitud.CERTIFICADO, PrioridadSolicitud.BAJA, "Certificado laboral urgente", 1, true
        ));

        verificar(sol.getPrioridad() == PrioridadSolicitud.MEDIA,
                "Una solicitud urgente con prioridad base BAJA debe escalar a MEDIA");
    }

    private static void probarAsignacionResponsableValida() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-103", "Lucía Paz", "lpaz@est.utn.edu.ar", "TUPaD", 12, 7.40);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-04", est, TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.MEDIA, "Homologación", 2, false
        ));

        Responsable resp = new Responsable("R-01", "Dra. Méndez", "Secretaría Académica", "mendez@utn.edu.ar", 0, 2);
        boolean asignado = gestor.asignarResponsable("SOL-04", resp);

        Solicitud solActualizada = repo.buscarPorId("SOL-04");
        verificar(asignado, "La asignación debe resultar exitosa");
        verificar(solActualizada.getEstado() == EstadoSolicitud.EN_REVISION, "Debe pasar a EN_REVISION");
        verificar(resp.getCargaActual() == 1, "La carga del responsable debe incrementarse a 1");
    }

    private static void probarRechazoPorFaltaDeCupo() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-104", "Marcos Ríos", "mrios@est.utn.edu.ar", "TUPaD", 12, 7.10);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-05", est, TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.MEDIA, "Homologación", 2, false
        ));

        Responsable respSinCupo = new Responsable("R-02", "Ing. Silva", "Dirección de Carrera", "silva@utn.edu.ar", 2, 2);
        boolean asignado = gestor.asignarResponsable("SOL-05", respSinCupo);

        verificar(!asignado, "Debe rechazarse la asignación cuando cargaActual == maximoSolicitudes");
        verificar(repo.buscarPorId("SOL-05").getEstado() == EstadoSolicitud.PENDIENTE, "Debe seguir PENDIENTE");
    }

    private static void probarRechazoPorAreaIncompatible() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-105", "Sofía Vera", "svera@est.utn.edu.ar", "TUPaD", 15, 7.80);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-06", est, TipoSolicitud.EXCEPCION, PrioridadSolicitud.MEDIA, "Excepción correlativa", 3, false
        ));

        Responsable respAlumnos = new Responsable("R-03", "Lic. Gómez", "Departamento de Alumnos", "gomez@utn.edu.ar", 0, 3);
        boolean asignado = gestor.asignarResponsable("SOL-06", respAlumnos);

        verificar(!asignado, "Departamento de Alumnos no debe poder tomar una EXCEPCION");
    }

    private static void probarTransicionEstadoValida() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-106", "Diego Cano", "dcano@est.utn.edu.ar", "TUPaD", 18, 7.90);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-07", est, TipoSolicitud.CERTIFICADO, PrioridadSolicitud.MEDIA, "Certificado estudio", 1, false
        ));
        Responsable resp = new Responsable("R-04", "Lic. Gómez", "Departamento de Alumnos", "gomez@utn.edu.ar", 0, 3);
        gestor.asignarResponsable("SOL-07", resp);

        boolean cambioExitoso = gestor.cambiarEstadoSolicitud("SOL-07", EstadoSolicitud.APROBADA, "Emitido OK");
        Solicitud sol = repo.buscarPorId("SOL-07");

        verificar(cambioExitoso, "La transición de EN_REVISION a APROBADA debe ser válida");
        verificar(sol.getEstado() == EstadoSolicitud.APROBADA, "El estado final debe ser APROBADA");
        verificar("Emitido OK".equals(sol.getObservacionesResolucion()), "Debe guardar la observación de resolución");
    }

    private static void probarBloqueoTransicionesInvalidas() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-107", "Carla Luna", "cluna@est.utn.edu.ar", "TUPaD", 9, 7.00);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-08", est, TipoSolicitud.BAJA, PrioridadSolicitud.BAJA, "Baja temporal", 2, false
        ));

        // 1. Intento de aprobar directamente desde PENDIENTE
        boolean aprobacionDirecta = gestor.cambiarEstadoSolicitud("SOL-08", EstadoSolicitud.APROBADA, "Sin revisión");
        verificar(!aprobacionDirecta, "No debe permitir aprobar una solicitud en estado PENDIENTE");

        // 2. Asignar, rechazar y luego intentar reabrir o aprobar una solicitud cerrada
        Responsable resp = new Responsable("R-05", "Lic. Gómez", "Departamento de Alumnos", "gomez@utn.edu.ar", 0, 3);
        gestor.asignarResponsable("SOL-08", resp);
        gestor.cambiarEstadoSolicitud("SOL-08", EstadoSolicitud.RECHAZADA, "Documentación incompleta");

        boolean cambioPosteriorCierre = gestor.cambiarEstadoSolicitud("SOL-08", EstadoSolicitud.APROBADA, "Reapertura inválida");
        verificar(!cambioPosteriorCierre, "No debe permitir cambiar el estado de una solicitud cerrada");
    }

    private static void probarCalculoSlaYVencimiento() {
        Estudiante est = new Estudiante("LEG-108", "Franco Mora", "fmora@est.utn.edu.ar", "TUPaD", 14, 7.30);

        // EXCEPCION normal tiene SLA de 7 días -> con 9 días en estado EN_REVISION está vencida
        Solicitud solVencida = new Solicitud(
                "SOL-09A", est, TipoSolicitud.EXCEPCION, PrioridadSolicitud.MEDIA,
                EstadoSolicitud.EN_REVISION, "Excepción", 9, false
        );
        verificar(solVencida.calcularPlazoMaximoDias() == 7, "SLA normal de EXCEPCION debe ser 7 días");
        verificar(solVencida.tieneSlaVencido(), "Con 9 días y abierta debe marcar SLA vencido");

        // CERTIFICADO urgente reduce SLA de 3 a ceil(1.5) = 2 días, pero si está APROBADA no figura como vencida abierta
        Solicitud solCerrada = new Solicitud(
                "SOL-09B", est, TipoSolicitud.CERTIFICADO, PrioridadSolicitud.ALTA,
                EstadoSolicitud.APROBADA, "Certificado urgente", 3, true
        );
        verificar(solCerrada.calcularPlazoMaximoDias() == 2, "SLA urgente de CERTIFICADO debe ser 2 días");
        verificar(!solCerrada.tieneSlaVencido(), "Una solicitud ya APROBADA no debe contarse como pendiente vencida");
    }

    private static void probarFiltradoYValidacionesDefensivas() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-109", "Elena Paz", "epaz@est.utn.edu.ar", "TUPaD", 25, 9.10);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-10", est, TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.MEDIA, "Equivalencia", 4, false
        ));

        List<Solicitud> filtradas = gestor.buscarSolicitudes(
                new CriterioBusquedaSolicitud(TipoSolicitud.EQUIVALENCIA, EstadoSolicitud.PENDIENTE, PrioridadSolicitud.ALTA)
        );
        verificar(filtradas.size() == 1, "El filtro combinado debe encontrar exactamente 1 solicitud");

        boolean capturoExcepcionPromedio = false;
        try {
            new Estudiante("LEG-ERR", "Error", "err@utn.edu.ar", "TUPaD", 5, 12.5);
        } catch (IllegalArgumentException ex) {
            capturoExcepcionPromedio = true;
        }
        verificar(capturoExcepcionPromedio, "Guard Clause debe rechazar promedio mayor a 10.0");
    }

    private static void probarBloqueoRevisionSinResponsable() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-110", "Bruno Sosa", "bsosa@est.utn.edu.ar", "TUPaD", 11, 7.20);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-11", est, TipoSolicitud.BAJA, PrioridadSolicitud.BAJA, "Baja temporal", 1, false
        ));

        boolean pasoARevision = gestor.cambiarEstadoSolicitud("SOL-11", EstadoSolicitud.EN_REVISION, "Revisión manual");
        boolean aprobada = gestor.cambiarEstadoSolicitud("SOL-11", EstadoSolicitud.APROBADA, "Aprobación sin responsable");

        Solicitud sol = repo.buscarPorId("SOL-11");
        verificar(!pasoARevision, "No debe permitir pasar a EN_REVISION sin asignar responsable");
        verificar(!aprobada, "No debe permitir aprobar una solicitud sin responsable asignado");
        verificar(sol.getEstado() == EstadoSolicitud.PENDIENTE, "La solicitud debe seguir PENDIENTE");
    }

    private static void probarReasignacionLiberaCarga() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-111", "Irene Gil", "igil@est.utn.edu.ar", "TUPaD", 13, 7.60);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-12", est, TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.MEDIA, "Homologación", 2, false
        ));

        Responsable primero = new Responsable("R-06", "Dra. Méndez", "Secretaría Académica", "mendez@utn.edu.ar", 0, 2);
        Responsable segundo = new Responsable("R-07", "Ing. Silva", "Dirección de Carrera", "silva@utn.edu.ar", 0, 2);
        gestor.asignarResponsable("SOL-12", primero);
        boolean reasignado = gestor.asignarResponsable("SOL-12", segundo);
        boolean mismaAsignacion = gestor.asignarResponsable("SOL-12", segundo);

        verificar(reasignado, "La reasignación a otro responsable compatible debe ser exitosa");
        verificar(primero.getCargaActual() == 0, "El responsable anterior debe liberar su carga (0)");
        verificar(segundo.getCargaActual() == 1, "El nuevo responsable debe sumar la carga (1)");
        verificar(!mismaAsignacion, "Reasignar al mismo responsable no debe volver a sumar carga");
        verificar(segundo.getCargaActual() == 1, "La carga del mismo responsable debe permanecer en 1");
    }

    private static void probarRechazoAsignacionSolicitudCerrada() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, new NotificacionSpy());

        Estudiante est = new Estudiante("LEG-112", "Tomás Vidal", "tvidal@est.utn.edu.ar", "TUPaD", 16, 7.40);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-13", est, TipoSolicitud.CERTIFICADO, PrioridadSolicitud.MEDIA, "Certificado", 1, false
        ));
        Responsable resp = new Responsable("R-08", "Lic. Gómez", "Departamento de Alumnos", "gomez@utn.edu.ar", 0, 3);
        gestor.asignarResponsable("SOL-13", resp);
        gestor.cambiarEstadoSolicitud("SOL-13", EstadoSolicitud.APROBADA, "Emitido");

        Responsable otro = new Responsable("R-09", "Ing. Silva", "Dirección de Carrera", "silva@utn.edu.ar", 0, 2);
        boolean asignado = gestor.asignarResponsable("SOL-13", otro);

        verificar(!asignado, "No debe permitir asignar responsable a una solicitud APROBADA");
        verificar(otro.getCargaActual() == 0, "El responsable rechazado no debe consumir cupo");
        verificar(repo.buscarPorId("SOL-13").getResponsable() == resp, "Debe conservarse el responsable original");
    }

    private static void probarRechazoIdDuplicado() {
        RepositorioSolicitudes repo = new RepositorioSolicitudesMemoria();
        NotificacionSpy spy = new NotificacionSpy();
        GestorSolicitudes gestor = new GestorSolicitudes(repo, spy);

        Estudiante est = new Estudiante("LEG-113", "Paula Ríos", "prios@est.utn.edu.ar", "TUPaD", 7, 6.50);
        DatosRegistroSolicitud datos = new DatosRegistroSolicitud(
                "SOL-14", est, TipoSolicitud.BAJA, PrioridadSolicitud.BAJA, "Baja temporal", 1, false
        );
        gestor.registrarSolicitud(datos);

        boolean capturoDuplicado = false;
        try {
            gestor.registrarSolicitud(datos);
        } catch (IllegalStateException ex) {
            capturoDuplicado = true;
        }

        verificar(capturoDuplicado, "Debe rechazarse el registro de un ID ya existente");
        verificar(repo.obtenerTodas().size() == 1, "El repositorio debe contener una única solicitud");
        verificar(spy.cantidadNotificaciones() == 1, "No debe notificarse el registro rechazado");
    }

    private static void verificar(boolean condicion, String mensajeError) {
        if (!condicion) {
            throw new AssertionError(mensajeError);
        }
    }
}
