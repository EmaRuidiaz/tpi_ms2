package sga;

/**
 * Representa a un estudiante solicitante (Versión Inicial - Modelo Anémico).
 */
public class Estudiante {
    public String legajo;
    public String nombre;
    public String email;
    public String carrera;
    public int materiasAprobadas;
    public double promedio;

    public Estudiante(String legajo, String nombre, String email, String carrera, int materiasAprobadas, double promedio) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.email = email;
        this.carrera = carrera;
        this.materiasAprobadas = materiasAprobadas;
        this.promedio = promedio;
    }
}
