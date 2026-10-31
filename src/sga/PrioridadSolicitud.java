package sga;

/**
 * Representa los niveles de prioridad de una solicitud académica.
 * Encapsula la lógica de escalamiento de prioridad sin usar enteros mágicos.
 */
public enum PrioridadSolicitud {
    ALTA(1, "ALTA"),
    MEDIA(2, "MEDIA"),
    BAJA(3, "BAJA");

    private final int codigoNumerico;
    private final String etiqueta;

    PrioridadSolicitud(int codigoNumerico, String etiqueta) {
        this.codigoNumerico = codigoNumerico;
        this.etiqueta = etiqueta;
    }

    public int getCodigoNumerico() {
        return codigoNumerico;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Escala un nivel la prioridad cuando una solicitud reviste carácter urgente.
     */
    public PrioridadSolicitud elevarUnNivel() {
        if (this == BAJA) {
            return MEDIA;
        }
        return ALTA;
    }
}
