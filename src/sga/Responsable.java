package sga;

/**
 * Entidad de dominio que representa a un responsable administrativo o académico.
 * Aplica Move Method para encapsular las reglas de cupo operativo y compatibilidad de área.
 */
public class Responsable {

    private final String id;
    private final String nombre;
    private final String area;
    private final String email;
    private int cargaActual;
    private final int maximoSolicitudes;

    public Responsable(
            String id,
            String nombre,
            String area,
            String email,
            int cargaActual,
            int maximoSolicitudes
    ) {
        validarTextoObligatorio(id, "El identificador del responsable es obligatorio.");
        validarTextoObligatorio(nombre, "El nombre del responsable es obligatorio.");
        validarTextoObligatorio(area, "El área del responsable es obligatoria.");
        validarTextoObligatorio(email, "El email del responsable es obligatorio.");
        if (cargaActual < 0) {
            throw new IllegalArgumentException("La carga actual no puede ser negativa.");
        }
        if (maximoSolicitudes <= 0) {
            throw new IllegalArgumentException("El cupo máximo de solicitudes debe ser mayor a cero.");
        }

        this.id = id;
        this.nombre = nombre;
        this.area = area;
        this.email = email;
        this.cargaActual = cargaActual;
        this.maximoSolicitudes = maximoSolicitudes;
    }

    /**
     * Move Method: Verifica si el responsable dispone de capacidad operativa para asumir un trámite.
     */
    public boolean tieneCupoDisponible() {
        return this.cargaActual < this.maximoSolicitudes;
    }

    /**
     * Move Method: Verifica si el área del responsable es competente para el tipo de solicitud.
     */
    public boolean puedeGestionarTipo(TipoSolicitud tipoSolicitud) {
        if (tipoSolicitud == null) {
            return false;
        }
        return tipoSolicitud.esAreaCompatible(this.area);
    }

    public void incrementarCargaOperativa() {
        if (!tieneCupoDisponible()) {
            throw new IllegalStateException("El responsable no posee cupo disponible para nuevas solicitudes.");
        }
        this.cargaActual++;
    }

    /**
     * Libera una unidad de carga cuando una solicitud se reasigna a otro responsable.
     */
    public void liberarCargaOperativa() {
        if (this.cargaActual == 0) {
            throw new IllegalStateException("El responsable no posee carga operativa para liberar.");
        }
        this.cargaActual--;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArea() {
        return area;
    }

    public String getEmail() {
        return email;
    }

    public int getCargaActual() {
        return cargaActual;
    }

    public int getMaximoSolicitudes() {
        return maximoSolicitudes;
    }

    private void validarTextoObligatorio(String valor, String mensajeError) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
    }
}
