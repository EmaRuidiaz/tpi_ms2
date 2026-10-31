package sga;

import java.util.List;

/**
 * Punto de entrada de demostración del Sistema de Gestión de Solicitudes Académicas (Versión Refactorizada).
 * Conserva exactamente el mismo comportamiento externo observable que la versión inicial.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(">>> INICIANDO SISTEMA DE GESTIÓN DE SOLICITUDES ACADÉMICAS (SGA) <<<\n");

        RepositorioSolicitudes repositorio = new RepositorioSolicitudesArchivo("solicitudes_db.csv");
        ServicioNotificacion canalEmail = new NotificacionEmail();
        ServicioNotificacion canalWhatsApp = new NotificacionWhatsApp();
        ServicioNotificacion canalSms = new NotificacionSms();

        GestorSolicitudes gestor = new GestorSolicitudes(repositorio, canalEmail);

        // Responsables de distintas áreas
        Responsable respAcademica = new Responsable("RESP-01", "Dra. Laura Méndez", "Secretaría Académica", "lmendez@utn.edu.ar", 0, 2);
        Responsable respAlumnos = new Responsable("RESP-02", "Lic. Carlos Gómez", "Departamento de Alumnos", "cgomez@utn.edu.ar", 0, 3);
        Responsable respCarrera = new Responsable("RESP-03", "Ing. Martín Silva", "Dirección de Carrera", "msilva@utn.edu.ar", 1, 1);

        System.out.println("--- 1. REGISTRO DE SOLICITUDES ACADÉMICAS ---");
        // SOL-101: Estudiante con alto mérito académico (>=20 materias y promedio >=8.0) -> sube prioridad a ALTA
        Estudiante estMartinez = new Estudiante("LEG-28401", "Ana Sofía Martínez", "amartinez@est.utn.edu.ar", "TUPaD", 22, 8.75);
        gestor.setServicioNotificacion(canalEmail);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-101", estMartinez, TipoSolicitud.EQUIVALENCIA, PrioridadSolicitud.MEDIA,
                "Homologación de Programación I y II", 6, false
        ));

        // SOL-102: Trámite urgente de certificado -> sube prioridad de MEDIA a ALTA
        Estudiante estTorres = new Estudiante("LEG-29110", "Lucas Ezequiel Torres", "ltorres@est.utn.edu.ar", "TUPaD", 10, 7.20);
        gestor.setServicioNotificacion(canalWhatsApp);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-102", estTorres, TipoSolicitud.CERTIFICADO, PrioridadSolicitud.MEDIA,
                "Certificado de alumno regular para presentar en trabajo", 3, true
        ));

        // SOL-103: Excepción de correlatividad con 9 días transcurridos (SLA de 7 días vencido)
        Estudiante estRojas = new Estudiante("LEG-30045", "Valentina Rojas", "vrojas@est.utn.edu.ar", "TUPaD", 14, 7.50);
        gestor.setServicioNotificacion(canalSms);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-103", estRojas, TipoSolicitud.EXCEPCION, PrioridadSolicitud.MEDIA,
                "Cursado condicional de Metodología de Sistemas II", 9, false
        ));

        // SOL-104: Baja temporal de cuatrimestre
        Estudiante estDiaz = new Estudiante("LEG-31200", "Mateo Nicolás Díaz", "mdiaz@est.utn.edu.ar", "TUPaD", 5, 6.80);
        gestor.setServicioNotificacion(canalEmail);
        gestor.registrarSolicitud(new DatosRegistroSolicitud(
                "SOL-104", estDiaz, TipoSolicitud.BAJA, PrioridadSolicitud.BAJA,
                "Licencia estudiantil por viaje laboral", 2, false
        ));

        System.out.println("\n--- 2. ASIGNACIÓN DE RESPONSABLES Y VALIDACIÓN DE REGLAS ---");
        gestor.setServicioNotificacion(canalEmail);
        gestor.asignarResponsable("SOL-101", respAcademica);

        gestor.setServicioNotificacion(canalWhatsApp);
        gestor.asignarResponsable("SOL-102", respAlumnos);

        gestor.setServicioNotificacion(canalSms);
        // Intento inválido 1: Responsable sin cupo disponible (Ing. Martín Silva tiene 1/1)
        gestor.asignarResponsable("SOL-103", respCarrera);
        // Intento inválido 2: Área incompatible (Departamento de Alumnos no toma EXCEPCION)
        gestor.asignarResponsable("SOL-103", respAlumnos);
        // Asignación válida de SOL-103 a Secretaría Académica
        gestor.asignarResponsable("SOL-103", respAcademica);

        System.out.println("\n--- 3. CAMBIO DE ESTADOS Y CONTROL DE TRANSICIONES ---");
        gestor.setServicioNotificacion(canalEmail);
        gestor.cambiarEstadoSolicitud("SOL-101", EstadoSolicitud.APROBADA, "Equivalencia otorgada según Disposición Académica N° 44/26.");

        gestor.setServicioNotificacion(canalWhatsApp);
        gestor.cambiarEstadoSolicitud("SOL-102", EstadoSolicitud.APROBADA, "Certificado emitido con firma digital.");

        gestor.setServicioNotificacion(canalEmail);
        // Intento inválido 1: Modificar una solicitud que ya está cerrada (APROBADA)
        gestor.cambiarEstadoSolicitud("SOL-101", EstadoSolicitud.RECHAZADA, "Intento de cambio posterior al cierre.");
        // Intento inválido 2: Aprobar una solicitud que sigue PENDIENTE sin revisión previa
        gestor.cambiarEstadoSolicitud("SOL-104", EstadoSolicitud.APROBADA, "Aprobación directa sin pasar por revisión.");

        System.out.println("\n--- 4. BÚSQUEDA Y FILTRADO DE SOLICITUDES ---");
        List<Solicitud> filtroPrioridadAlta = gestor.buscarSolicitudes(
                CriterioBusquedaSolicitud.porPrioridad(PrioridadSolicitud.ALTA)
        );
        System.out.println("Solicitudes encontradas con Prioridad ALTA: " + filtroPrioridadAlta.size());
        for (Solicitud s : filtroPrioridadAlta) {
            System.out.println("  * " + s.getId() + " - " + s.getEstudiante().getNombre()
                    + " (" + s.getTipo().name() + ") -> Estado: " + s.getEstado().name());
        }

        List<Solicitud> filtroEnRevision = gestor.buscarSolicitudes(
                CriterioBusquedaSolicitud.porEstado(EstadoSolicitud.EN_REVISION)
        );
        System.out.println("Solicitudes encontradas en estado EN_REVISION: " + filtroEnRevision.size());
        for (Solicitud s : filtroEnRevision) {
            System.out.println("  * " + s.getId() + " - " + s.getEstudiante().getNombre()
                    + " (" + s.getTipo().name() + ") -> Responsable: " + s.getResponsable().getNombre());
        }

        System.out.println("\n--- 5. REPORTE GENERAL Y CONTROL DE SLA ---");
        System.out.println(gestor.generarReporteGeneral());
    }
}
