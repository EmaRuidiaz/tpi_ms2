package sga;

/**
 * Entidad Solicitud Académica (Versión Inicial con Primitive Obsession y nombres crípticos).
 */
public class Solicitud {
    public String id;
    public Estudiante est;
    public String t;        // "EQUIVALENCIA", "CERTIFICADO", "EXCEPCION", "BAJA"
    public int p;           // 1 = Alta, 2 = Media, 3 = Baja
    public String e;        // "PENDIENTE", "EN_REVISION", "APROBADA", "RECHAZADA"
    public Responsable resp;
    public String desc;
    public String obs;
    public int dias;        // Días transcurridos desde el alta
    public boolean u;       // true = trámite urgente

    public Solicitud(String id, Estudiante est, String t, int p, String e, String desc, int dias, boolean u) {
        this.id = id;
        this.est = est;
        this.t = t;
        this.p = p;
        this.e = e;
        this.desc = desc;
        this.dias = dias;
        this.u = u;
        this.obs = "";
    }
}
