package sga;

/**
 * Estrategia concreta de notificación por correo electrónico institucional.
 */
public class NotificacionEmail implements ServicioNotificacion {
    @Override
    public void enviarNotificacion(Estudiante destinatario, String mensaje) {
        if (destinatario == null || mensaje == null) {
            throw new IllegalArgumentException("El destinatario y el mensaje no pueden ser nulos.");
        }
        System.out.println("[NOTIF-EMAIL -> " + destinatario.getEmail() + "] " + mensaje);
    }
}
