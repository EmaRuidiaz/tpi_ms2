package sga;

import java.util.List;

/**
 * Abstracción de persistencia para las solicitudes académicas (Principios SRP, LSP, ISP y DIP).
 * Desacopla el dominio del medio físico de almacenamiento (archivo CSV, memoria o base de datos).
 */
public interface RepositorioSolicitudes {
    void guardar(Solicitud solicitud);
    void actualizar(Solicitud solicitud);
    Solicitud buscarPorId(String idSolicitud);
    List<Solicitud> obtenerTodas();
}
