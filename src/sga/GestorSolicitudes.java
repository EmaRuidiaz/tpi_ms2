package sga;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase central del sistema base (Versión Inicial con problemas de diseño intencionales:
 * God Class, Long Method, Long Parameter List, Feature Envy, Duplicated Code, Magic Numbers,
 * Primitive Obsession y acoplamiento directo a infraestructura).
 */
public class GestorSolicitudes {

    public List<Solicitud> lista = new ArrayList<>();
    public String archivoDb = "solicitudes_db.csv";

    // Operación 1: Registrar solicitud (Long Parameter List: 12 parámetros + Long Method + Feature Envy)
    public Solicitud reg(String id, String leg, String nom, String mail, String car,
                         int mat, double prom, String t, int p, String desc,
                         int dias, boolean u, String canalNotif) {

        Estudiante est = new Estudiante(leg, nom, mail, car, mat, prom);

        // Feature Envy + Magic Numbers: evalúa mérito académico accediendo a atributos internos de Estudiante
        int prioFinal = p;
        if (est.materiasAprobadas >= 20 && est.promedio >= 8.0) {
            prioFinal = 1; // 1 = Prioridad Alta por mérito académico
        } else if (u && prioFinal > 1) {
            prioFinal = prioFinal - 1;
        }

        Solicitud s = new Solicitud(id, est, t, prioFinal, "PENDIENTE", desc, dias, u);
        lista.add(s);

        // Duplicated Code (Bloque 1): Persistencia directa acoplada a FileWriter
        try {
            FileWriter fw = new FileWriter(archivoDb, false);
            fw.write("ID;LEGAJO;ESTUDIANTE;TIPO;PRIORIDAD;ESTADO;RESPONSABLE;DIAS;URGENTE;OBSERVACIONES\n");
            for (Solicitud item : lista) {
                String nomResp = item.resp != null ? item.resp.nombre : "SIN_ASIGNAR";
                fw.write(item.id + ";" + item.est.legajo + ";" + item.est.nombre + ";"
                        + item.t + ";" + item.p + ";" + item.e + ";"
                        + nomResp + ";" + item.dias + ";" + item.u + ";" + item.obs + "\n");
            }
            fw.close();
        } catch (IOException ex) {
            System.out.println("Error guardando archivo: " + ex.getMessage());
        }

        // Duplicated Code (Bloque 1): Notificación rígida con cadena if/else (viola OCP y DIP)
        String msg = "Solicitud " + s.id + " (" + s.t + ") registrada con estado " + s.e + " y prioridad " + obtenerTextoPrioridad(s.p) + ".";
        if (canalNotif.equals("EMAIL")) {
            System.out.println("[NOTIF-EMAIL -> " + est.email + "] " + msg);
        } else if (canalNotif.equals("SMS")) {
            System.out.println("[NOTIF-SMS -> " + est.nombre + "] " + msg);
        } else if (canalNotif.equals("WHATSAPP")) {
            System.out.println("[NOTIF-WHATSAPP -> " + est.nombre + "] " + msg);
        }

        return s;
    }

    // Operación 2: Asignar responsable (Feature Envy + Duplicated Code)
    public boolean asig(String idSol, Responsable r, String canalNotif) {
        Solicitud s = null;
        for (Solicitud item : lista) {
            if (item.id.equals(idSol)) {
                s = item;
                break;
            }
        }
        if (s == null) {
            return false;
        }

        // Feature Envy: valida cupo y área inspeccionando directamente los campos de Responsable
        if (r.cargaActual >= r.maximoSolicitudes) {
            System.out.println("[RECHAZO-ASIGNACION] Responsable " + r.nombre + " sin cupo disponible (" + r.cargaActual + "/" + r.maximoSolicitudes + ").");
            return false;
        }

        boolean areaCompatible = false;
        if ((s.t.equals("EQUIVALENCIA") || s.t.equals("EXCEPCION")) && r.area.equals("Secretaría Académica")) {
            areaCompatible = true;
        } else if ((s.t.equals("CERTIFICADO") || s.t.equals("BAJA")) && r.area.equals("Departamento de Alumnos")) {
            areaCompatible = true;
        } else if (r.area.equals("Dirección de Carrera")) {
            areaCompatible = true;
        }

        if (!areaCompatible) {
            System.out.println("[RECHAZO-ASIGNACION] El área '" + r.area + "' no gestiona solicitudes de tipo " + s.t + ".");
            return false;
        }

        s.resp = r;
        r.cargaActual = r.cargaActual + 1;
        if (s.e.equals("PENDIENTE")) {
            s.e = "EN_REVISION";
        }

        // Duplicated Code (Bloque 2): Persistencia repetida
        try {
            FileWriter fw = new FileWriter(archivoDb, false);
            fw.write("ID;LEGAJO;ESTUDIANTE;TIPO;PRIORIDAD;ESTADO;RESPONSABLE;DIAS;URGENTE;OBSERVACIONES\n");
            for (Solicitud item : lista) {
                String nomResp = item.resp != null ? item.resp.nombre : "SIN_ASIGNAR";
                fw.write(item.id + ";" + item.est.legajo + ";" + item.est.nombre + ";"
                        + item.t + ";" + item.p + ";" + item.e + ";"
                        + nomResp + ";" + item.dias + ";" + item.u + ";" + item.obs + "\n");
            }
            fw.close();
        } catch (IOException ex) {
            System.out.println("Error guardando archivo: " + ex.getMessage());
        }

        // Duplicated Code (Bloque 2): Notificación repetida
        String msg = "Solicitud " + s.id + " asignada a " + r.nombre + " (" + r.area + "). Estado actual: " + s.e + ".";
        if (canalNotif.equals("EMAIL")) {
            System.out.println("[NOTIF-EMAIL -> " + s.est.email + "] " + msg);
        } else if (canalNotif.equals("SMS")) {
            System.out.println("[NOTIF-SMS -> " + s.est.nombre + "] " + msg);
        } else if (canalNotif.equals("WHATSAPP")) {
            System.out.println("[NOTIF-WHATSAPP -> " + s.est.nombre + "] " + msg);
        }

        return true;
    }

    // Operación 3: Cambiar estado de solicitud (Primitive Obsession + Duplicated Code)
    public boolean camEst(String idSol, String nuevoEst, String obs, String canalNotif) {
        Solicitud s = null;
        for (Solicitud item : lista) {
            if (item.id.equals(idSol)) {
                s = item;
                break;
            }
        }
        if (s == null) {
            return false;
        }

        // Reglas de transición con Strings mágicos
        if (s.e.equals("APROBADA") || s.e.equals("RECHAZADA")) {
            System.out.println("[TRANSICION-INVALIDA] La solicitud " + s.id + " ya se encuentra cerrada (" + s.e + ").");
            return false;
        }
        if (s.e.equals("PENDIENTE") && (nuevoEst.equals("APROBADA") || nuevoEst.equals("RECHAZADA"))) {
            System.out.println("[TRANSICION-INVALIDA] La solicitud " + s.id + " debe pasar por EN_REVISION antes de resolverse.");
            return false;
        }

        s.e = nuevoEst;
        s.obs = obs;

        // Duplicated Code (Bloque 3): Persistencia repetida
        try {
            FileWriter fw = new FileWriter(archivoDb, false);
            fw.write("ID;LEGAJO;ESTUDIANTE;TIPO;PRIORIDAD;ESTADO;RESPONSABLE;DIAS;URGENTE;OBSERVACIONES\n");
            for (Solicitud item : lista) {
                String nomResp = item.resp != null ? item.resp.nombre : "SIN_ASIGNAR";
                fw.write(item.id + ";" + item.est.legajo + ";" + item.est.nombre + ";"
                        + item.t + ";" + item.p + ";" + item.e + ";"
                        + nomResp + ";" + item.dias + ";" + item.u + ";" + item.obs + "\n");
            }
            fw.close();
        } catch (IOException ex) {
            System.out.println("Error guardando archivo: " + ex.getMessage());
        }

        // Duplicated Code (Bloque 3): Notificación repetida
        String msg = "Solicitud " + s.id + " cambió a estado " + s.e + ". Resolución: " + s.obs;
        if (canalNotif.equals("EMAIL")) {
            System.out.println("[NOTIF-EMAIL -> " + s.est.email + "] " + msg);
        } else if (canalNotif.equals("SMS")) {
            System.out.println("[NOTIF-SMS -> " + s.est.nombre + "] " + msg);
        } else if (canalNotif.equals("WHATSAPP")) {
            System.out.println("[NOTIF-WHATSAPP -> " + s.est.nombre + "] " + msg);
        }

        return true;
    }

    // Operación 4: Buscar / filtrar solicitudes por criterios combinables
    public List<Solicitud> bus(String filtroTipo, String filtroEstado, int filtroPrio) {
        List<Solicitud> res = new ArrayList<>();
        for (Solicitud s : lista) {
            boolean okTipo = (filtroTipo == null || filtroTipo.equals("") || s.t.equals(filtroTipo));
            boolean okEst = (filtroEstado == null || filtroEstado.equals("") || s.e.equals(filtroEstado));
            boolean okPrio = (filtroPrio == 0 || s.p == filtroPrio);
            if (okTipo && okEst && okPrio) {
                res.add(s);
            }
        }
        return res;
    }

    // Operación 5: Generar reporte básico y control de SLA (Long Method + Magic Numbers)
    public String rep() {
        int tot = lista.size();
        int pend = 0;
        int rev = 0;
        int aprob = 0;
        int rech = 0;
        int venc = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("              REPORTE GENERAL DE SOLICITUDES ACADÉMICAS (SGA)                   \n");
        sb.append("================================================================================\n");

        for (Solicitud s : lista) {
            if (s.e.equals("PENDIENTE")) pend++;
            if (s.e.equals("EN_REVISION")) rev++;
            if (s.e.equals("APROBADA")) aprob++;
            if (s.e.equals("RECHAZADA")) rech++;

            // Magic Numbers para SLA según tipo y urgencia
            int maxDias = 10;
            if (s.t.equals("EQUIVALENCIA")) maxDias = 15;
            else if (s.t.equals("CERTIFICADO")) maxDias = 3;
            else if (s.t.equals("EXCEPCION")) maxDias = 7;
            else if (s.t.equals("BAJA")) maxDias = 5;

            if (s.u) {
                maxDias = (int) Math.ceil(maxDias * 0.5);
            }

            boolean estaVencida = (s.dias > maxDias) && (!s.e.equals("APROBADA") && !s.e.equals("RECHAZADA"));
            if (estaVencida) {
                venc++;
            }

            String nomResp = s.resp != null ? s.resp.nombre : "SIN_ASIGNAR";
            String alertaSla = estaVencida ? " [SLA VENCIDO: " + s.dias + "/" + maxDias + " días]" : " [SLA OK: " + s.dias + "/" + maxDias + " días]";

            sb.append(String.format("- [%s] %-13s | Est: %-14s (%s) | Prio: %-5s | Estado: %-11s | Resp: %-16s |%s\n",
                    s.id, s.t, s.est.nombre, s.est.legajo, obtenerTextoPrioridad(s.p), s.e, nomResp, alertaSla));
        }

        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("TOTAL: %d | PENDIENTES: %d | EN_REVISION: %d | APROBADAS: %d | RECHAZADAS: %d | SLA VENCIDOS: %d\n",
                tot, pend, rev, aprob, rech, venc));
        sb.append("================================================================================");
        return sb.toString();
    }

    private String obtenerTextoPrioridad(int p) {
        if (p == 1) return "ALTA";
        if (p == 2) return "MEDIA";
        return "BAJA";
    }
}
