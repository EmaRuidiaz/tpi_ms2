# Declaración y Bitácora de Uso de Inteligencia Artificial (Sección 7)

## 1. Declaración Metodológica de Uso de IA
De acuerdo con los lineamientos de la cátedra para el Trabajo Práctico Integrador de **Metodología de Sistemas II**:
- **Herramienta utilizada:** Antigravity (Asistente de IA generativa para ingeniería de software).
- **Finalidad y grado de intervención:** Asistencia como copiloto técnico para: (1) estructurar la matriz diagnóstica de los 10 *code smells* del sistema base; (2) revisar alternativas de diseño aplicando principios SOLID y refactorings seguros de catálogo (*Introduce Parameter Object*, *Extract Method*, *Move Method*, *Replace Type Code with Enum*); (3) generar casos borde para la suite de pruebas automatizadas; y (4) revisar la claridad estilística de la documentación técnica.
- **Validación humana y autoría:** Todo el código fuente (inicial y refactorizado), las pruebas ejecutadas, los commits y las justificaciones técnicas fueron revisados críticamente, compilados, verificados y validados por los integrantes del equipo (**Ruidiaz, Emanuel Facundo** y **Roques Zeballos, Juan Martín**), quienes comprenden y asumen la responsabilidad académica total sobre la entrega y su defensa oral.

---

## 2. Registro Ordenado de Prompts e Interacciones con IA

### Interacción 1 — Diagnóstico de Deuda Técnica y Code Smells (Etapas 2 y 3)
- **Fecha:** 31/10/2026
- **Prompt utilizado:**
  > *"Actuá como Arquitecto de Software Senior y docente de Metodología de Sistemas II. Analizá la clase `GestorSolicitudes` y las entidades `Solicitud`, `Estudiante` y `Responsable` de la versión base (`estado-inicial`) de nuestro Sistema de Gestión de Solicitudes Académicas en Java. Identificá al menos 8 code smells del catálogo de Martin Fowler / Refactoring, indicando para cada uno: nombre técnico del smell, ubicación exacta (clase y líneas), fragmento de evidencia, principio de diseño comprometido (SOLID/DRY/Clean Code), riesgo para el mantenimiento futuro y prioridad de corrección (Alta/Media/Baja) en formato tabla."*
- **Resultado obtenido de la IA (Resumen):** La herramienta listó inicialmente 8 problemas centrados en *God Class*, *Long Method*, *Long Parameter List*, *Primitive Obsession*, *Feature Envy*, *Duplicated Code*, *Magic Numbers* y condicionales `if/else` de notificación.
- **Validación humana y ajuste realizado:** Se verificaron las líneas exactas contra el archivo `GestorSolicitudes.java` del tag `estado-inicial` y se amplió el diagnóstico a **10 code smells**, incorporando explícitamente el **acoplamiento directo a infraestructura de archivos (`FileWriter` sin cierre seguro `try-with-resources`)** y la **ausencia de cláusulas de guarda (*Guard Clauses*)**, ya que ambos afectaban de forma directa la posibilidad de escribir pruebas unitarias en memoria.

### Interacción 2 — Diseño del Refactoring Seguro y Principios SOLID (Etapas 4 y 5)
- **Fecha:** 31/10/2026
- **Prompt utilizado:**
  > *"Necesito planificar una secuencia de refactorings seguros paso a paso sobre `GestorSolicitudes` en Java 21 que conserve exactamente la misma salida por consola y el mismo formato del archivo `solicitudes_db.csv`, pero elimine los 10 code smells detectados. Debe incluir obligatoriamente: (1) Introduce Parameter Object para los 13 parámetros de `reg(...)`, (2) Move Method para trasladar las validaciones de mérito académico, cupo de responsable y cálculo de SLA a las entidades correspondientes, (3) Extract Method para descomponer `reg` y `rep`, y (4) aplicación de SRP, OCP y DIP para desacoplar las notificaciones y la persistencia CSV. Proponé la división en commits atómicos siguiendo Conventional Commits."*
- **Resultado obtenido de la IA (Resumen):** Propuso crear los objetos `DatosRegistroSolicitud` y `CriterioBusquedaSolicitud`, los enums `TipoSolicitud`, `PrioridadSolicitud` y `EstadoSolicitud`, las interfaces `RepositorioSolicitudes` y `ServicioNotificacion`, y separar `GeneradorReporteSolicitudes`.
- **Validación humana y ajuste realizado:** Se revisó que el método `calcularPlazoMaximoDias()` en `Solicitud` mantuviera el redondeo `Math.ceil(plazoBase * 0.50)` para trámites urgentes y que una solicitud ya cerrada (`APROBADA` o `RECHAZADA`) no se contabilizara como SLA vencido, preservando el 100% de compatibilidad con el comportamiento externo del sistema base.

### Interacción 3 — Diseño de Pruebas de Regresión y Checklist de Code Review (Etapas 6 y 7)
- **Fecha:** 31/10/2026
- **Prompt utilizado:**
  > *"Diseñá una clase de pruebas automatizadas en Java puro (`PruebasVerificacionSistema.java`) que utilice `RepositorioSolicitudesMemoria` y un Test Double (`NotificacionSpy`) para verificar 10 casos de prueba (nominales, bordes de SLA, rechazo por cupo/área y bloqueo de transiciones inválidas de estado). Además, estructurá un Checklist de Code Review de 12 puntos comparando `estado-inicial` vs. `estadorefactorizado` y simulando el feedback técnico de un Pull Request."*
- **Resultado obtenido de la IA (Resumen):** Generó la estructura de `PruebasVerificacionSistema` con 10 métodos de prueba independientes y la tabla de revisión técnica.
- **Validación humana y ajuste realizado:** Se ejecutó la suite con Java 21 (`10/10 PASS`) y se contrastó con `Compare-Object` en PowerShell que la ejecución de `Main.java` antes y después del refactoring produjera exactamente 0 diferencias de salida.
