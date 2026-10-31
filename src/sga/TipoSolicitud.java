package sga;

/**
 * Representa los tipos válidos de solicitudes académicas junto con su plazo base de SLA
 * y su área administrativa/académica competente.
 * Reemplaza los códigos de texto mágicos (Primitive Obsession).
 */
public enum TipoSolicitud {
    EQUIVALENCIA(15, "Secretaría Académica"),
    CERTIFICADO(3, "Departamento de Alumnos"),
    EXCEPCION(7, "Secretaría Académica"),
    BAJA(5, "Departamento de Alumnos");

    private static final String AREA_DIRECCION_CARRERA = "Dirección de Carrera";

    private final int plazoMaximoDiasBase;
    private final String areaCompetente;

    TipoSolicitud(int plazoMaximoDiasBase, String areaCompetente) {
        this.plazoMaximoDiasBase = plazoMaximoDiasBase;
        this.areaCompetente = areaCompetente;
    }

    public int getPlazoMaximoDiasBase() {
        return plazoMaximoDiasBase;
    }

    public String getAreaCompetente() {
        return areaCompetente;
    }

    public boolean esAreaCompatible(String area) {
        if (area == null || area.isBlank()) {
            return false;
        }
        return areaCompetente.equals(area) || AREA_DIRECCION_CARRERA.equals(area);
    }
}
