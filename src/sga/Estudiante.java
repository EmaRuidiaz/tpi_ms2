package sga;

/**
 * Entidad de dominio que representa a un estudiante solicitante.
 * Aplica encapsulamiento, validaciones defensivas (Guard Clauses) y Move Method
 * para evaluar mérito académico sin exponer su estado interno (Information Expert).
 */
public class Estudiante {

    public static final int MINIMO_MATERIAS_MERITO_ACADEMICO = 20;
    public static final double PROMEDIO_MINIMO_MERITO_ACADEMICO = 8.0;

    private final String legajo;
    private final String nombre;
    private final String email;
    private final String carrera;
    private final int materiasAprobadas;
    private final double promedio;

    public Estudiante(
            String legajo,
            String nombre,
            String email,
            String carrera,
            int materiasAprobadas,
            double promedio
    ) {
        validarTextoObligatorio(legajo, "El legajo del estudiante no puede ser nulo ni vacío.");
        validarTextoObligatorio(nombre, "El nombre del estudiante no puede ser nulo ni vacío.");
        validarTextoObligatorio(email, "El email del estudiante no puede ser nulo ni vacío.");
        validarTextoObligatorio(carrera, "La carrera del estudiante no puede ser nula ni vacía.");
        if (materiasAprobadas < 0) {
            throw new IllegalArgumentException("La cantidad de materias aprobadas no puede ser negativa.");
        }
        if (promedio < 0.0 || promedio > 10.0) {
            throw new IllegalArgumentException("El promedio académico debe encontrarse entre 0.0 y 10.0.");
        }

        this.legajo = legajo;
        this.nombre = nombre;
        this.email = email;
        this.carrera = carrera;
        this.materiasAprobadas = materiasAprobadas;
        this.promedio = promedio;
    }

    /**
     * Move Method: Determina si el estudiante califica para prioridad alta automática
     * por mérito académico, evitando Feature Envy en el servicio gestor.
     */
    public boolean cumpleMeritoAcademicoPrioritario() {
        return this.materiasAprobadas >= MINIMO_MATERIAS_MERITO_ACADEMICO
                && this.promedio >= PROMEDIO_MINIMO_MERITO_ACADEMICO;
    }

    public String getLegajo() {
        return legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getCarrera() {
        return carrera;
    }

    public int getMateriasAprobadas() {
        return materiasAprobadas;
    }

    public double getPromedio() {
        return promedio;
    }

    private void validarTextoObligatorio(String valor, String mensajeError) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
    }
}
