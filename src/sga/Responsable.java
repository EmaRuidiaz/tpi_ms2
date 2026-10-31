package sga;

/**
 * Representa a un responsable administrativo o académico (Versión Inicial - Modelo Anémico).
 */
public class Responsable {
    public String id;
    public String nombre;
    public String area;
    public String email;
    public int cargaActual;
    public int maximoSolicitudes;

    public Responsable(String id, String nombre, String area, String email, int cargaActual, int maximoSolicitudes) {
        this.id = id;
        this.nombre = nombre;
        this.area = area;
        this.email = email;
        this.cargaActual = cargaActual;
        this.maximoSolicitudes = maximoSolicitudes;
    }
}
