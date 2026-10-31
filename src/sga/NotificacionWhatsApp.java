package sga;

/**
 * Estrategia concreta de notificación por mensajería instantánea WhatsApp.
 */
public class NotificacionWhatsApp implements ServicioNotificacion {
    @Override
    public void enviarNotificacion(Estudiante destinatario, String mensaje) {
        if (destinatario == null || mensaje == null) {
            throw new IllegalArgumentException("El destinatario y el mensaje no pueden ser nulos.");
        }
        System.out.println("[NOTIF-WHATSAPP -> " + destinatario.getNombre() + "] " + mensaje);
    }
}
