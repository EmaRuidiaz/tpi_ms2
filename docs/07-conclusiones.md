# Conclusiones, Aprendizajes y Mejoras Futuras (Etapa 8)

## 1. Conclusiones Grupales

### 1.1 Calidad externa no implica calidad interna
La versión `estado-inicial` cumplía los cinco casos de uso y generaba el CSV esperado: desde la mirada del usuario "funcionaba". Sin embargo, concentraba todo en una *God Class* con 13 parámetros en `reg(...)`, I/O triplicado y condicionales de notificación rígidos. El trabajo confirmó que la calidad interna no se percibe en la interfaz, pero define cuánto cuesta cada cambio: agregar un canal de notificación o testear una regla requería tocar y releer la clase completa.

### 1.2 El refactoring seguro necesita una red de verificación
La comparación *Golden Master* de la salida de `Main` y del CSV, junto con la suite automatizada en memoria, permitió aplicar seis refactorings (*Introduce Parameter Object*, *Extract Method*, *Move Method*, *Replace Type Code with Enum*, *Extract Class/Interface* y *Guard Clauses*) con evidencia de que el comportamiento externo no cambió. Sin el DIP aplicado a `RepositorioSolicitudes` y `ServicioNotificacion`, esa suite no hubiera sido posible.

### 1.3 El code review encuentra lo que el escenario feliz no muestra
La tercera iteración de revisión (`04-checklist-code-review.md`) detectó cuatro defectos de reglas de negocio que habían pasado las iteraciones anteriores: resolución de solicitudes sin responsable, carga operativa inconsistente al reasignar, IDs duplicados y un resumen de pruebas que siempre informaba 100%. Ninguno aparecía en la salida de `Main`, porque el escenario de demostración no ejercita esos caminos. La lección es doble: un checklist marcado "SÍ" no reemplaza recorrer los caminos alternativos de cada caso de uso, y las pruebas deben poder fallar de forma visible para ser evidencia confiable.

### 1.4 Uso de IA como apoyo
La IA aceleró la estructuración del diagnóstico y del plan de refactoring, pero sus propuestas tuvieron que validarse contra el código real (líneas, redondeos del SLA, formato del CSV). La responsabilidad de comprender y defender cada decisión es del equipo.

## 2. Dificultades Encontradas
- Mantener exactamente la misma salida por consola y el mismo formato de CSV mientras se reestructuraba el código.
- Decidir hasta dónde refactorizar sin exceder el alcance del TPI (por ejemplo, persistencia con lectura real o reemplazo de `System.out` por resultados de dominio).
- Problemas de codificación de caracteres (tildes) en la consola de Windows al capturar las evidencias; se resuelve ejecutando con `-Dstdout.encoding=UTF-8`.

## 3. Límites del Trabajo
- El sistema es un prototipo de consola sin interfaz de usuario ni base de datos real.
- `RepositorioSolicitudesArchivo` escribe el CSV pero no lo lee al iniciar: no hay persistencia real entre ejecuciones.
- Las pruebas son una suite propia en Java puro, sin un framework como JUnit ni medición de cobertura.
- `diasTranscurridos` es un valor fijo; el control de SLA no usa fechas reales.

## 4. Mejoras Futuras
1. Lectura del CSV al iniciar y escape del separador en textos libres (o migración a una base de datos mediante una nueva implementación de `RepositorioSolicitudes`).
2. Reemplazar los `System.out.println` de `GestorSolicitudes` por un objeto resultado con el motivo del rechazo.
3. Asociar un canal de notificación preferido a cada estudiante y eliminar `setServicioNotificacion` (acoplamiento temporal).
4. Introducir un `enum Area` para la compatibilidad responsable–tipo de solicitud.
5. Calcular el SLA a partir de una fecha de creación (`LocalDate`).
6. Migrar la suite a JUnit 5 e incorporar análisis estático y cobertura.

---

## 5. Conclusiones Individuales

### Ruidiaz, Emanuel Facundo
Trabajar sobre la refactorización paso a paso del módulo de Solicitudes Académicas me permitió dimensionar el valor real de técnicas como *Introduce Parameter Object*, *Replace Type Code with Enum* y *Move Method*. Pasar de un método `reg(...)` con 13 parámetros primitivos y lógica dispersa a entidades de dominio ricas que validan sus propias invariantes redujo drásticamente la complejidad cognitiva y facilitó escribir pruebas automatizadas rápidas.

Además, al revisar el Pull Request de la tercera iteración de *Code Review* comprendí la importancia de evaluar no solo la estructura estática del código sino también las transiciones inválidas y los casos borde que el camino feliz de demostración no recorre. Si empezara de nuevo, diseñaría los objetos de parámetro y los tests de invariantes de negocio desde el primer momento antes de abordar los casos nominales.

### Roques Zeballos, Juan Martín
Mi aporte se concentró en el relevamiento de los code smells, el desacoplamiento de la infraestructura con SOLID (`RepositorioSolicitudes`, `ServicioNotificacion` y `GeneradorReporteSolicitudes`), los ADRs y el code review. El mayor aprendizaje fue entender en la práctica la diferencia entre calidad externa e interna: el sistema base "andaba", pero cualquier cambio obligaba a tocar una única clase gigante. Aplicar DIP no fue solo una cuestión de diseño prolijo: fue lo que hizo posible probar el sistema en memoria sin depender del archivo CSV.

Lo más difícil, y lo que más me enseñó, fue la tercera iteración del code review. Con el checklist completo en "SÍ" y las pruebas en verde, revisé los caminos que la demostración no recorre y encontré cuatro defectos reales (por ejemplo, se podía aprobar una solicitud sin responsable asignado y el resumen de pruebas informaba 100% aunque algo fallara). Me quedó claro que un checklist aprobado no garantiza que las reglas de negocio estén protegidas, y que una prueba solo es evidencia si puede fallar de forma visible. Si empezara de nuevo, escribiría pruebas para los casos alternativos desde el primer refactoring, y no solo para el escenario principal.

---

## 6. Enlaces Oficiales de Entrega y Video de Defensa
- **Video de Defensa y Demostración (Google Drive):** [https://drive.google.com/file/d/1NTLajOwqr0HAIDRuz-5_TfOsEMSKWahB/view?usp=sharing](https://drive.google.com/file/d/1NTLajOwqr0HAIDRuz-5_TfOsEMSKWahB/view?usp=sharing)
- **Repositorio GitHub Oficial:** [https://github.com/EmaRuidiaz/tpi_ms2](https://github.com/EmaRuidiaz/tpi_ms2)
- **Tag Versión Inicial (`estado-inicial`):** [https://github.com/EmaRuidiaz/tpi_ms2/tree/estado-inicial](https://github.com/EmaRuidiaz/tpi_ms2/tree/estado-inicial)
- **Tag Versión Final Refactorizada (`estadorefactorizado`):** [https://github.com/EmaRuidiaz/tpi_ms2/tree/estadorefactorizado](https://github.com/EmaRuidiaz/tpi_ms2/tree/estadorefactorizado)
- **Pull Request — Iteración 3 del Code Review:** [https://github.com/EmaRuidiaz/tpi_ms2/pull/1](https://github.com/EmaRuidiaz/tpi_ms2/pull/1)
