package sga;

/**
 * Abstracción para el envío de notificaciones a los estudiantes (Principios OCP, ISP y DIP).
 * Permite incorporar nuevos canales de comunicación sin modificar la lógica de negocio.
 */
public interface ServicioNotificacion {
    void enviarNotificacion(Estudiante destinatario, String mensaje);
}
