# Code Review Profesional y Criterios de Aceptación Técnica (Etapa 7)

## 1. Checklist Técnico de Code Review (Antes vs. Después)

Se elaboró y aplicó el siguiente **Checklist de Revisión Técnica** sobre el repositorio del proyecto, contrastando la versión base (`estado-inicial`) contra la versión final (`estadorefactorizado`):

| # | Categoría | Criterio Técnico Evaluado | `estado-inicial` | `estadorefactorizado` | Evidencia / Observación Técnica |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **1** | **Clean Code** | ¿Los nombres de clases, métodos y variables revelan su intención sin abreviaturas crípticas? | ❌ NO | ✅ SÍ | Se reemplazaron `reg`, `asig`, `camEst`, `bus`, `rep` y campos `t, p, e, u` por identificadores autoexplicativos (`registrarSolicitud`, `tipo`, `prioridad`, `estado`). |
| **2** | **Clean Code** | ¿Las funciones son pequeñas, cohesivas y operan en un único nivel de abstracción? | ❌ NO | ✅ SÍ | Se aplicó *Extract Method* en `GestorSolicitudes` y `GeneradorReporteSolicitudes`; ningún método supera las 20 líneas. |
| **3** | **Clean Code** | ¿Las firmas de métodos tienen una cantidad reducida de parámetros ($\le 3$)? | ❌ NO | ✅ SÍ | Se redujo `reg(...)` de 13 parámetros primitivos a 1 objeto `DatosRegistroSolicitud` (*Introduce Parameter Object*). |
| **4** | **DRY** | ¿El código está libre de bloques duplicados de persistencia o notificación? | ❌ NO | ✅ SÍ | La escritura CSV y el despacho de notificaciones estaban triplicados; ahora están centralizados en `RepositorioSolicitudesArchivo` y `ServicioNotificacion`. |
| **5** | **Dominio** | ¿Se evita *Primitive Obsession* y el uso de números/cadenas mágicas? | ❌ NO | ✅ SÍ | Se introdujeron `TipoSolicitud`, `PrioridadSolicitud`, `EstadoSolicitud` y constantes simbólicas (`MINIMO_MATERIAS_MERITO_ACADEMICO`, etc.). |
| **6** | **Cohesión** | ¿Las entidades de dominio encapsulan su estado y contienen la lógica que opera sobre sus datos (*Information Expert*)? | ❌ NO | ✅ SÍ | Se aplicó *Move Method* hacia `Estudiante.cumpleMeritoAcademicoPrioritario()`, `Responsable.tieneCupoDisponible()` y `Solicitud.tieneSlaVencido()`. |
| **7** | **SOLID (SRP)** | ¿Cada clase posee una única responsabilidad claramente delimitada? | ❌ NO | ✅ SÍ | Se desarmó la *God Class* separando negocio (`GestorSolicitudes`), reporte (`GeneradorReporteSolicitudes`), persistencia (`RepositorioSolicitudes`) y notificación (`ServicioNotificacion`). |
| **8** | **SOLID (OCP)** | ¿Es posible agregar nuevos canales de notificación sin modificar la clase gestora? | ❌ NO | ✅ SÍ | Polimorfismo mediante la interfaz `ServicioNotificacion`. |
| **9** | **SOLID (DIP/LSP)** | ¿Los componentes de alto nivel dependen de abstracciones inyectadas e intercambiables? | ❌ NO | ✅ SÍ | Inyección por constructor de `RepositorioSolicitudes` y `ServicioNotificacion`, permitiendo usar `RepositorioSolicitudesMemoria` en tests. |
| **10** | **Robustez** | ¿Existen validaciones defensivas (*Guard Clauses*) y manejo seguro de recursos I/O? | ❌ NO | ✅ SÍ | Todos los constructores y servicios validan parámetros nulos o fuera de rango; `FileWriter` usa bloque `try-with-resources` con UTF-8 explícito. |
| **11** | **Testing** | ¿Existen pruebas automatizadas que verifiquen casos nominales y casos borde sin regresiones? | ❌ NO | ✅ SÍ | Suite `PruebasVerificacionSistema` con 10/10 casos automatizados aprobados y salida comparativa 100% equivalente. |
| **12** | **Git / Trazabilidad** | ¿El historial de commits es atómico, descriptivo y cuenta con los tags requeridos? | ⚠️ PARCIAL | ✅ SÍ | Commits semánticos (*Conventional Commits*) por cada refactoring y tags `estado-inicial`, `etapa-diagnostico`, `etapa-refactoring`, `etapa-pruebas`, `etapa-docs`, `estadorefactorizado`. |

---

## 2. Registro de Revisión de Pull Request (Feedback Profesional)

### Iteración 1 — Revisión sobre `estado-inicial` (Estado: ⚠️ *Changes Requested*)
> **PR #1: Módulo de Gestión de Solicitudes Académicas (SGA)**  
> **Autores / Equipo:** Ruidiaz, Emanuel Facundo & Roques Zeballos, Juan Martín  
> **Revisores Cruzados:** Roques Zeballos, Juan Martín & Ruidiaz, Emanuel Facundo (Revisión por pares)  
> **Veredicto Inicial:** ⚠️ **Se requieren cambios antes de fusionar a `main`**
>
> **Resumen del Feedback Objetivo:**  
> El prototipo cumple correctamente con los 5 casos de uso visibles para el usuario final y genera el archivo CSV esperado (buena **calidad externa**). Sin embargo, presenta una **deuda técnica elevada** que compromete su mantenibilidad y testabilidad:
> 1. **[BLOQUEANTE - SRP / DIP / DRY] Acoplamiento y triplicación de I/O en `GestorSolicitudes`:** Las líneas 35–46, 100–111 y 154–165 abren un `FileWriter` directamente sobre `"solicitudes_db.csv"` sin `try-with-resources`. Extraer una interfaz `RepositorioSolicitudes` y mover el acceso a disco a `RepositorioSolicitudesArchivo`.
> 2. **[BLOQUEANTE - OCP] Condicionales rígidos de notificación:** Los bloques `if (canalNotif.equals("EMAIL"))` repetidos en tres métodos violan OCP. Extraer la interfaz `ServicioNotificacion` y sus estrategias concretas.
> 3. **[ALTA - Clean Code] Lista excesiva de parámetros y nombres abreviados:** El método `reg(...)` recibe 13 parámetros sueltos y utiliza nombres crípticos (`t, p, e, u`). Aplicar *Introduce Parameter Object* (`DatosRegistroSolicitud`) y renombrar métodos y atributos con vocabulario del dominio.
> 4. **[ALTA - Dominio] *Feature Envy* y *Primitive Obsession*:** Trasladar las validaciones de mérito académico a `Estudiante`, las de cupo y área a `Responsable` y el cálculo de SLA a `Solicitud` (*Move Method*), reemplazando los literales de estado, tipo y prioridad por `Enum`.

### Iteración 2 — Verificación posterior al Refactoring (Estado: ✅ *Approved*)
> **Veredicto Final:** ✅ **Aprobado para Merge (`estadorefactorizado`)**  
> Se verificó que las 4 observaciones bloqueantes y de alta prioridad fueron resueltas en commits atómicos, manteniendo el 100% de equivalencia en la salida funcional de `Main` y alcanzando **10/10 pruebas automatizadas exitosas** en `PruebasVerificacionSistema`.

### Iteración 3 — Revisión de reglas de negocio sobre `estadorefactorizado` (Estado: ⚠️ *Changes Requested* → ✅ *Approved*)
> **PR: `fix/code-review-iteracion-3` → `main`**  
> **Autor de las correcciones:** Roques Zeballos, Juan Martín  
> **Revisor:** Ruidiaz, Emanuel Facundo  
>
> **Motivo de la revisión:** Las iteraciones 1 y 2 se centraron en la estructura interna (SOLID, Clean Code, DRY). En esta revisión se recorrieron los caminos alternativos de cada caso de uso, incluso los que el escenario de `Main` no ejercita, y se encontró que el punto 10 del checklist (**Robustez**) estaba aprobado sin cubrir algunas invariantes del dominio.
>
> **Hallazgos y correcciones:**
> 1. **[BLOQUEANTE - Regla de negocio] Resolución sin revisión real** (`EstadoSolicitud.puedeTransicionarA`): se permitía `PENDIENTE → EN_REVISION` mediante `cambiarEstadoSolicitud`, sin asignar responsable, y luego aprobar la solicitud. Además, `Main` invocaba `getResponsable().getNombre()`, que lanzaría `NullPointerException` en ese escenario.  
>    **Corrección:** la única transición manual permitida es `EN_REVISION → APROBADA/RECHAZADA`; `EN_REVISION` solo se alcanza al asignar responsable. `GestorSolicitudes` informa el rechazo con `[TRANSICION-INVALIDA]` y `Main` usa `getNombreResponsableAsignado()`. **Prueba:** CP-11.
> 2. **[ALTA - Integridad] Carga operativa inconsistente al reasignar** (`Solicitud.asignarResponsable`): al reasignar se incrementaba la carga del nuevo responsable sin liberar la del anterior; reasignar al mismo responsable duplicaba su carga; y se podía asignar responsable a una solicitud ya cerrada, consumiendo cupo.  
>    **Corrección:** nuevo método `Responsable.liberarCargaOperativa()`; `Solicitud` y `GestorSolicitudes` rechazan asignaciones sobre solicitudes cerradas y reasignaciones al mismo responsable. **Pruebas:** CP-12 y CP-13.
> 3. **[ALTA - Integridad] IDs de solicitud duplicados** (`RepositorioSolicitudes*.guardar`): se aceptaban dos solicitudes con el mismo ID y `buscarPorId` devolvía siempre la primera.  
>    **Corrección:** ambas implementaciones (archivo y memoria) lanzan `IllegalStateException` ante un ID existente, manteniendo el mismo contrato (LSP); el registro rechazado no se notifica. **Prueba:** CP-14.
> 4. **[MEDIA - Confiabilidad de la evidencia] Resumen de pruebas fijo** (`PruebasVerificacionSistema`): el resumen imprimía siempre `(100% PASS)` aunque fallaran casos.  
>    **Corrección:** el porcentaje se calcula a partir de las pruebas ejecutadas y superadas.
>
> **Veredicto Final:** ✅ **Aprobado** — `14/14` pruebas `PASS` y salida de `Main` + `solicitudes_db.csv` idénticas a las evidencias previas (sin regresiones en el comportamiento externo).
>
> **Observaciones registradas como deuda técnica pendiente (no bloqueantes):**
> - `RepositorioSolicitudesArchivo` solo escribe el CSV y no lo lee al iniciar, por lo que no hay persistencia real entre ejecuciones; tampoco escapa el separador `;` en textos libres.
> - `GestorSolicitudes` informa los rechazos con `System.out.println`; convendría devolver un resultado con el motivo para separar la presentación del servicio.
> - El canal de notificación se intercambia con `setServicioNotificacion` antes de cada operación (acoplamiento temporal); podría asociarse un canal preferido a cada estudiante.
> - Las áreas se comparan como `String` con tildes (`TipoSolicitud.esAreaCompatible`); un `enum Area` evitaría errores de tipeo.

---

## 3. Criterios de Aceptación Técnica (*Definition of Done*)
Para dar por aceptada la evolución del módulo en la rama principal se verificó el cumplimiento estricto de los siguientes criterios:
1. **Cero Regresiones Funcionales:** La comparación entre `salida_estado_inicial.txt` y `salida_estado_refactorizado.txt` no presenta diferencias.
2. **Suite de Pruebas Verde:** `PruebasVerificacionSistema` ejecuta los 14 casos de prueba (nominales, bordes y excepciones; CP-11 a CP-14 agregados en la Iteración 3) con resultado `14/14 PASS`.
3. **Cumplimiento del Checklist:** 12 de 12 puntos del checklist técnico en estado `SÍ`.
4. **Cero Warnings de Compilación:** Compilación limpia con `javac -encoding UTF-8` en Java 21 LTS.
