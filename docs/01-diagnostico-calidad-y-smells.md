# Diagnóstico de Calidad Externa vs. Interna, Deuda Técnica y Code Smells

**Proyecto:** Sistema de Gestión de Solicitudes Académicas (SGA)  
**Materia:** Metodología de Sistemas II — Tecnicatura Universitaria en Programación a Distancia (UTN)  
**Integrantes:** Ruidiaz, Emanuel Facundo | Roques Zeballos, Juan Martín (Comisión 15)  
**Versión analizada:** Tag `estado-inicial`

---

## 1. Descripción del Sistema Base (Etapa 1)

### 1.1 Dominio y Alcance
El **Sistema de Gestión de Solicitudes Académicas (SGA)** modela el circuito administrativo y académico mediante el cual los estudiantes de una facultad gestionan trámites formales (homologaciones/equivalencias, certificados de alumno regular, excepciones de correlatividad y bajas temporales de cuatrimestre).

### 1.2 Actores y Entidades Principales
- **Estudiante (`Estudiante`):** Solicitante que inicia trámites y posee historial académico (legajo, carrera, materias aprobadas y promedio general).
- **Responsable Administrativo/Académico (`Responsable`):** Personal asignado según área de competencia (*Secretaría Académica*, *Departamento de Alumnos*, *Dirección de Carrera*) con un cupo máximo de trámites simultáneos.
- **Solicitud Académica (`Solicitud`):** Entidad central que registra el tipo de trámite, prioridad, estado del flujo, días transcurridos, urgencia, responsable asignado y resolución.

### 1.3 Operaciones Funcionales Verificables
1. **Registro de Solicitud:** Alta de trámite con recálculo automático de prioridad según mérito académico ($\ge 20$ materias y promedio $\ge 8.0$) o carácter urgente.
2. **Asignación de Responsable:** Vinculación de un agente verificando cupo operativo disponible y compatibilidad entre el área del responsable y el tipo de solicitud, pasando el trámite a `EN_REVISION`.
3. **Cambio y Control de Estado:** Transición controlada de estados (`PENDIENTE` $\rightarrow$ `EN_REVISION` $\rightarrow$ `APROBADA` / `RECHAZADA`), impidiendo reabrir trámites cerrados o aprobar sin revisión previa.
4. **Búsqueda y Filtrado:** Consulta combinada de solicitudes por tipo, estado actual y nivel de prioridad.
5. **Reporte General y Control de SLA:** Generación de tablero de control con conteo por estado y detección de vencimientos según el Acuerdo de Nivel de Servicio (SLA) de cada tipo de trámite.
6. **Persistencia y Notificación:** Almacenamiento estructurado en archivo CSV (`solicitudes_db.csv`) y despacho de avisos por canal (`EMAIL`, `SMS`, `WHATSAPP`).

---

## 2. Calidad Externa vs. Calidad Interna (Etapa 2)

| Dimensión | Calidad Externa (Perspectiva del Usuario) | Calidad Interna (Perspectiva de Ingeniería de Software) |
| :--- | :--- | :--- |
| **Observación directa** | El sistema cumple las 5 operaciones funcionales, persiste el archivo `solicitudes_db.csv` sin errores, valida reglas de negocio y emite reportes exactos. | El diseño concentra toda la lógica en una única clase monolítica (`GestorSolicitudes`), abusa de tipos primitivos, repite bloques de código y acopla el dominio al sistema de archivos. |
| **Correctitud funcional** | **Alta:** Los resultados visibles para los casos de prueba nominales y bordes funcionales responden a lo esperado. | **Frágil:** Cualquier modificación en el formato de archivo o en un canal de notificación obliga a editar múltiples métodos, elevando el riesgo de regresión. |
| **Mantenibilidad y Legibilidad** | Transparente para el operador final. | **Baja:** Nombres abreviados (`reg`, `asig`, `camEst`, `bus`, `rep`, `t`, `p`, `e`, `u`) exigen leer la implementación completa para deducir su propósito. |
| **Testabilidad** | Se puede probar manualmente ejecutando `Main`. | **Muy Baja:** No es posible ejecutar pruebas unitarias en memoria sin sobrescribir el archivo físico `solicitudes_db.csv` ni imprimir por consola. |
| **Extensibilidad (Costo del Cambio)** | Agregar un nuevo tipo de trámite o canal parece trivial desde la interfaz. | **Alto Costo:** Incorporar un canal (ej. *Telegram*) o un nuevo estado exige intervenir `reg`, `asig`, `camEst` y `rep` simultáneamente (violación de OCP y DRY). |

---

## 3. Catálogo de Deuda Técnica y Code Smells Detectados (Etapa 3)

Se identificaron **10 code smells y debilidades técnicas** en la versión base (`estado-inicial`):

| ID | Code Smell / Deuda Técnica | Ubicación (`estado-inicial`) | Evidencia en el Código | Impacto y Riesgo de Mantenimiento | Prioridad |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CS-01** | **God Class / Large Class** | `GestorSolicitudes.java` (L12–L238) | La clase gestiona reglas de prioridad, asignación de cupos, máquina de estados, persistencia CSV, envío de notificaciones, filtrado y armado de reportes. | **Viola SRP.** Concentra múltiples razones de cambio en un solo archivo; alto riesgo de conflictos de fusión (*merge conflicts*) y regresiones cruzadas. | **ALTA** |
| **CS-02** | **Long Parameter List** | `GestorSolicitudes.reg(...)` (L18–L20) | Firma con **13 parámetros sueltos**: `(String id, String leg, String nom, String mail, String car, int mat, double prom, String t, int p, String desc, int dias, boolean u, String canalNotif)`. | Firma ilegible y propensa a errores por inversión accidental de parámetros del mismo tipo (`String`, `int`). Dificulta evolucionar los datos de entrada. | **ALTA** |
| **CS-03** | **Long Method** | `GestorSolicitudes.reg(...)` (L18–L60) y `rep()` (L188–L236) | Métodos de más de 40–50 líneas que mezclan distintos niveles de abstracción: instanciación, reglas de negocio, escritura en disco con `FileWriter` y formateo de salida. | Eleva la complejidad ciclomática y cognitiva; impide reutilizar o testear de manera aislada los subpasos del algoritmo. | **ALTA** |
| **CS-04** | **Primitive Obsession** | `Solicitud.java` (L8–L15) y `GestorSolicitudes.java` | Uso de `String t` para tipo, `int p` (`1, 2, 3`) para prioridad, `String e` para estado y `String canalNotif` para el medio de notificación. | No existe validación en tiempo de compilación: un error tipográfico como `"APROVADA"` o prioridad `99` corrompe el estado del sistema silenciosamente. | **ALTA** |
| **CS-05** | **Feature Envy (Envidia de Atributos)** | `GestorSolicitudes.reg` (L25–L29) y `asig` (L75–L92) | `if (est.materiasAprobadas >= 20 && est.promedio >= 8.0)` y `if (r.cargaActual >= r.maximoSolicitudes)` evaluados fuera de `Estudiante` y `Responsable`. | Desaprovecha el principio *Information Expert*; convierte a `Estudiante`, `Responsable` y `Solicitud` en estructuras anémicas con atributos públicos expuestos. | **ALTA** |
| **CS-06** | **Duplicated Code (Violación de DRY)** | `GestorSolicitudes.java` (L35–L57, L100–L122, L154–L176) | El bloque `try { FileWriter fw = new FileWriter(archivoDb...) }` y el bloque `if (canalNotif.equals("EMAIL")) ...` están triplicados exactamente en `reg`, `asig` y `camEst`. | Si cambia el delimitador del CSV o el formato del mensaje, hay que recordar actualizar 3 lugares distintos, multiplicando por 3 la probabilidad de bugs. | **ALTA** |
| **CS-07** | **Magic Numbers & Magic Strings** | `GestorSolicitudes.java` (L25, L142–L149, L204–L211) | Literales rígidos sin contexto semántico: `20`, `8.0`, `1`, `2`, `3`, `15`, `3`, `7`, `5`, `0.5`, `"PENDIENTE"`, `"EN_REVISION"`, `"APROBADA"`. | Dificulta comprender qué regla institucional representa cada número y obliga a rastrear literales dispersos ante cualquier cambio reglamentario. | **MEDIA** |
| **CS-08** | **Switch / If-Else Rígidos (Falta de Polimorfismo)** | `GestorSolicitudes.java` (L49–L57, L80–L88, L205–L208) | Cadenas condicionales `if/else` preguntando por el canal de notificación y por el tipo de trámite para calcular plazos de SLA. | **Viola OCP.** El código está cerrado a la extensión y abierto a la modificación cada vez que se incorpora un canal de mensajería o tipo de solicitud. | **ALTA** |
| **CS-09** | **Acoplamiento Directo a Infraestructura (I/O)** | `GestorSolicitudes.java` (L15, L36, L101, L155) | Instanciación directa de `new FileWriter("solicitudes_db.csv")` dentro de los métodos de negocio de la clase gestora. | **Viola DIP.** El módulo de alto nivel depende de detalles de bajo nivel (sistema de archivos local), impidiendo inyectar un repositorio en memoria para tests. | **ALTA** |
| **CS-10** | **Nombres Crípticos y Ausencia de Manejo Defensivo de Errores** | `Solicitud.java` (`t, p, e, u, est, resp`) y `GestorSolicitudes.java` (`reg, asig, camEst, bus, rep`) | Abreviaturas de una o tres letras; ausencia de *guard clauses* ante parámetros `null`, promedios fuera de rango o días negativos; captura genérica de `IOException` con `System.out`. | Degrada la comunicabilidad del código (*Clean Code*) y expone el sistema a `NullPointerException` o estados inconsistentes en tiempo de ejecución. | **MEDIA** |
