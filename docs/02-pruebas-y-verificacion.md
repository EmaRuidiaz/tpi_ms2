# Pruebas y Verificación del Comportamiento (Etapa 6)

## 1. Estrategia de Verificación de No-Regresión
Para demostrar científicamente que el proceso de refactoring mejoró la **calidad interna** sin alterar negativamente el **comportamiento externo** del sistema, se aplicó una estrategia de verificación en dos niveles:

1. **Verificación de Equivalencia de Salida (Golden Master / Comparación Antes vs. Después):**
   Se ejecutó el escenario integral de `Main.java` tanto sobre el tag `estado-inicial` como sobre el tag `etapa-refactoring`, capturando la salida estándar (`docs/evidencias/salida_estado_inicial.txt` y `docs/evidencias/salida_estado_refactorizado.txt`) y el archivo persistido `solicitudes_db.csv`. La comparación línea por línea (`Compare-Object` / `diff`) arrojó **0 diferencias (100% idéntico)**.
2. **Suite de Pruebas Automatizadas de Dominio y Casos de Uso (`PruebasVerificacionSistema.java`):**
   Gracias a la aplicación del **Principio de Inversión de Dependencias (DIP)** y la extracción de `RepositorioSolicitudes` (`RepositorioSolicitudesMemoria`) y `ServicioNotificacion` (`NotificacionSpy`), se implementaron **10 casos de prueba automatizados** que se ejecutan en memoria en milisegundos sin acoplamiento al disco.

---

## 2. Matriz de Casos de Prueba Automatizados

| ID | Caso de Prueba | Precondiciones / Entrada | Resultado Esperado | Resultado Obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **CP-01** | Registro estándar de solicitud | Estudiante con 10 materias y promedio 7.0; solicitud `BAJA` prioridad `BAJA`, no urgente. | Solicitud creada en estado `PENDIENTE`, prioridad `BAJA` y 1 notificación emitida. | Estado `PENDIENTE`, prioridad `BAJA`, 1 notificación registrada en `NotificacionSpy`. | ✅ **PASS** |
| **CP-02** | Escalamiento automático por mérito académico | Estudiante con 22 materias ($\ge 20$) y promedio 8.50 ($\ge 8.0$); prioridad solicitada `BAJA`. | El sistema asigna automáticamente `PrioridadSolicitud.ALTA`. | Prioridad efectiva calculada = `ALTA`. | ✅ **PASS** |
| **CP-03** | Escalamiento de prioridad por trámite urgente | Solicitud de `CERTIFICADO` con prioridad base `BAJA` y flag `urgente = true`. | La prioridad escala un nivel hasta `PrioridadSolicitud.MEDIA`. | Prioridad efectiva calculada = `MEDIA`. | ✅ **PASS** |
| **CP-04** | Asignación válida de responsable | Solicitud `EQUIVALENCIA` en `PENDIENTE`; responsable de *Secretaría Académica* con carga `0/2`. | Retorna `true`, incrementa carga a `1/2` y transiciona estado a `EN_REVISION`. | Asignación exitosa, estado `EN_REVISION`, carga `1/2`. | ✅ **PASS** |
| **CP-05** | Rechazo de asignación por falta de cupo | Responsable con `cargaActual = 2` y `maximoSolicitudes = 2`. | Retorna `false`, mantiene la solicitud en `PENDIENTE` sin alterar la carga. | Asignación rechazada (`false`), solicitud sigue `PENDIENTE`. | ✅ **PASS** |
| **CP-06** | Rechazo de asignación por área incompatible | Solicitud de tipo `EXCEPCION` asignada a *Departamento de Alumnos*. | Retorna `false` por incompatibilidad de competencia administrativa. | Asignación rechazada (`false`). | ✅ **PASS** |
| **CP-07** | Transición válida de estado a `APROBADA` | Solicitud en estado `EN_REVISION`; cambio hacia `APROBADA` con observación. | Retorna `true`, actualiza estado a `APROBADA` y persiste la resolución. | Estado `APROBADA` y observación guardada. | ✅ **PASS** |
| **CP-08** | Bloqueo de transiciones de estado inválidas | (a) Solicitud `PENDIENTE` $\rightarrow$ `APROBADA`; (b) Solicitud `RECHAZADA` $\rightarrow$ `APROBADA`. | Ambas operaciones retornan `false`, protegiendo la invariante del ciclo de vida. | Transiciones inválidas bloqueadas (`false`). | ✅ **PASS** |
| **CP-09** | Cálculo de SLA (normal vs. urgente) y vencimiento | `EXCEPCION` normal con 9 días (SLA 7); `CERTIFICADO` urgente aprobado con 3 días (SLA reducido a 2). | `EXCEPCION` abierta reporta SLA vencido (`true`); `CERTIFICADO` cerrado no figura como pendiente vencido (`false`). | Plazos 7 y 2 días calculados; estado de vencimiento exacto. | ✅ **PASS** |
| **CP-10** | Búsqueda combinada y *Guard Clauses* | Filtro combinado `(EQUIVALENCIA, PENDIENTE, ALTA)` e intento de crear `Estudiante` con promedio `12.5`. | El filtro devuelve 1 coincidencia exacta; el constructor lanza `IllegalArgumentException`. | 1 coincidencia hallada; excepción capturada correctamente. | ✅ **PASS** |

---

## 3. Comando de Ejecución de las Pruebas
```bash
javac -encoding UTF-8 -d bin src/sga/*.java
java -Dfile.encoding=UTF-8 -cp bin sga.PruebasVerificacionSistema
```
