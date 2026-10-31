package sga;

import java.util.List;

/**
 * Punto de entrada de demostración del Sistema de Gestión de Solicitudes Académicas (Versión Inicial).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(">>> INICIANDO SISTEMA DE GESTIÓN DE SOLICITUDES ACADÉMICAS (SGA) <<<\n");

        GestorSolicitudes gestor = new GestorSolicitudes();

        // Responsables de distintas áreas
        Responsable respAcademica = new Responsable("RESP-01", "Dra. Laura Méndez", "Secretaría Académica", "lmendez@utn.edu.ar", 0, 2);
        Responsable respAlumnos = new Responsable("RESP-02", "Lic. Carlos Gómez", "Departamento de Alumnos", "cgomez@utn.edu.ar", 0, 3);
        Responsable respCarrera = new Responsable("RESP-03", "Ing. Martín Silva", "Dirección de Carrera", "msilva@utn.edu.ar", 1, 1);

        System.out.println("--- 1. REGISTRO DE SOLICITUDES ACADÉMICAS ---");
        // SOL-101: Estudiante con alto mérito académico (>=20 materias y promedio >=8.0) -> sube prioridad a ALTA (1)
        gestor.reg("SOL-101", "LEG-28401", "Ana Sofía Martínez", "amartinez@est.utn.edu.ar", "TUPaD",
                22, 8.75, "EQUIVALENCIA", 2, "Homologación de Programación I y II", 6, false, "EMAIL");

        // SOL-102: Trámite urgente de certificado -> sube prioridad de MEDIA (2) a ALTA (1)
        gestor.reg("SOL-102", "LEG-29110", "Lucas Ezequiel Torres", "ltorres@est.utn.edu.ar", "TUPaD",
                10, 7.20, "CERTIFICADO", 2, "Certificado de alumno regular para presentar en trabajo", 3, true, "WHATSAPP");

        // SOL-103: Excepción de correlatividad con 9 días transcurridos (SLA de 7 días vencido)
        gestor.reg("SOL-103", "LEG-30045", "Valentina Rojas", "vrojas@est.utn.edu.ar", "TUPaD",
                14, 7.50, "EXCEPCION", 2, "Cursado condicional de Metodología de Sistemas II", 9, false, "SMS");

        // SOL-104: Baja temporal de cuatrimestre
        gestor.reg("SOL-104", "LEG-31200", "Mateo Nicolás Díaz", "mdiaz@est.utn.edu.ar", "TUPaD",
                5, 6.80, "BAJA", 3, "Licencia estudiantil por viaje laboral", 2, false, "EMAIL");

        System.out.println("\n--- 2. ASIGNACIÓN DE RESPONSABLES Y VALIDACIÓN DE REGLAS ---");
        gestor.asig("SOL-101", respAcademica, "EMAIL");
        gestor.asig("SOL-102", respAlumnos, "WHATSAPP");
        // Intento inválido 1: Responsable sin cupo disponible (Ing. Martín Silva tiene 1/1)
        gestor.asig("SOL-103", respCarrera, "SMS");
        // Intento inválido 2: Área incompatible (Departamento de Alumnos no toma EXCEPCION)
        gestor.asig("SOL-103", respAlumnos, "SMS");
        // Asignación válida de SOL-103 a Secretaría Académica
        gestor.asig("SOL-103", respAcademica, "SMS");

        System.out.println("\n--- 3. CAMBIO DE ESTADOS Y CONTROL DE TRANSICIONES ---");
        gestor.camEst("SOL-101", "APROBADA", "Equivalencia otorgada según Disposición Académica N° 44/26.", "EMAIL");
        gestor.camEst("SOL-102", "APROBADA", "Certificado emitido con firma digital.", "WHATSAPP");
        // Intento inválido 1: Modificar una solicitud que ya está cerrada (APROBADA)
        gestor.camEst("SOL-101", "RECHAZADA", "Intento de cambio posterior al cierre.", "EMAIL");
        // Intento inválido 2: Aprobar una solicitud que sigue PENDIENTE sin revisión previa
        gestor.camEst("SOL-104", "APROBADA", "Aprobación directa sin pasar por revisión.", "EMAIL");

        System.out.println("\n--- 4. BÚSQUEDA Y FILTRADO DE SOLICITUDES ---");
        List<Solicitud> filtroPrioridadAlta = gestor.bus("", "", 1);
        System.out.println("Solicitudes encontradas con Prioridad ALTA: " + filtroPrioridadAlta.size());
        for (Solicitud s : filtroPrioridadAlta) {
            System.out.println("  * " + s.id + " - " + s.est.nombre + " (" + s.t + ") -> Estado: " + s.e);
        }

        List<Solicitud> filtroEnRevision = gestor.bus("", "EN_REVISION", 0);
        System.out.println("Solicitudes encontradas en estado EN_REVISION: " + filtroEnRevision.size());
        for (Solicitud s : filtroEnRevision) {
            System.out.println("  * " + s.id + " - " + s.est.nombre + " (" + s.t + ") -> Responsable: " + s.resp.nombre);
        }

        System.out.println("\n--- 5. REPORTE GENERAL Y CONTROL DE SLA ---");
        System.out.println(gestor.rep());
    }
}
