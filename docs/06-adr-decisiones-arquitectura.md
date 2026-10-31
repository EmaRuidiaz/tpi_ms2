# Architecture Decision Records (ADRs) — Decisiones Técnicas de Rediseño

## ADR-001: Desacoplamiento de Persistencia mediante Patrón Repositorio (DIP)
- **Estado:** Aceptado (Aplicado en commit `1ca81ba`)
- **Contexto:** En la versión inicial (`estado-inicial`), `GestorSolicitudes` instanciaba directamente `FileWriter("solicitudes_db.csv")` en tres métodos distintos (`reg`, `asig`, `camEst`), violando SRP, DRY y DIP e impidiendo ejecutar pruebas unitarias sin sobrescribir el disco.
- **Decisión:** Extraer la interfaz `RepositorioSolicitudes` y proveer dos implementaciones intercambiables bajo el Principio de Sustitución de Liskov (LSP): `RepositorioSolicitudesArchivo` (para persistencia CSV en producción) y `RepositorioSolicitudesMemoria` (para pruebas automatizadas en memoria).
- **Consecuencias:**
  - *Positivas:* Elimina por completo la triplicación de código I/O; permite ejecutar 10 pruebas unitarias en menos de 50 ms en memoria; facilita migrar en el futuro a SQL/JPA sin tocar `GestorSolicitudes`.
  - *Trade-off:* Incrementa en 3 la cantidad de archivos del paquete, costo ampliamente compensado por la ganancia en mantenibilidad y testabilidad.

---

## ADR-002: Reemplazo de Condicionales de Notificación por Estrategia Polimórfica (OCP)
- **Estado:** Aceptado (Aplicado en commit `1ca81ba`)
- **Contexto:** El envío de avisos utilizaba bloques `if/else` comparando `String canalNotif` contra `"EMAIL"`, `"SMS"` y `"WHATSAPP"` en cada operación del gestor.
- **Decisión:** Definir la interfaz `ServicioNotificacion` e implementar las clases concretas `NotificacionEmail`, `NotificacionSms` y `NotificacionWhatsApp`, inyectadas en `GestorSolicitudes`.
- **Consecuencias:**
  - *Positivas:* Cumple con el Principio Abierto/Cerrado (OCP) y permite inyectar un objeto espía (`NotificacionSpy`) durante las pruebas automatizadas para verificar cuántos mensajes se enviaron sin acoplarse a `System.out`.

---

## ADR-003: Enriquecimiento del Modelo de Dominio y Eliminación de Primitive Obsession
- **Estado:** Aceptado (Aplicado en commits `29023c0` y `ef57e96`)
- **Contexto:** Las entidades `Estudiante`, `Responsable` y `Solicitud` eran estructuras anémicas con atributos públicos y tipos primitivos (`String t`, `int p`, `String e`), mientras `GestorSolicitudes` sufría de *Feature Envy*.
- **Decisión:** Introducir los enums `TipoSolicitud`, `PrioridadSolicitud` y `EstadoSolicitud`, encapsular los atributos de las entidades y trasladar las reglas de negocio mediante *Move Method* a la clase experta en la información (*Information Expert*).
- **Consecuencias:**
  - *Positivas:* Garantiza invariantes de dominio desde el constructor (*Guard Clauses*), previene estados inválidos en tiempo de compilación y reduce la complejidad de `GestorSolicitudes`.
