# Aplicación de Principios SOLID, Clean Code y Refactoring Seguro (Etapas 4 y 5)

## 1. Aplicación de Principios SOLID (Etapa 4)

| Principio | Problema en `estado-inicial` | Mejora Aplicada en `estadorefactorizado` | Clases Involucradas |
| :--- | :--- | :--- | :--- |
| **SRP** (*Single Responsibility Principle*) | `GestorSolicitudes` concentraba reglas de dominio, persistencia CSV (`FileWriter`), envío de notificaciones y armado de reportes por consola. | Cada clase tiene una única razón de cambio: `GestorSolicitudes` coordina casos de uso; `RepositorioSolicitudesArchivo` persiste en CSV; `GeneradorReporteSolicitudes` formatea reportes; `ServicioNotificacion` despacha avisos. | `GestorSolicitudes`, `GeneradorReporteSolicitudes`, `RepositorioSolicitudesArchivo` |
| **OCP** (*Open/Closed Principle*) | Incorporar un nuevo canal de notificación o un nuevo tipo de trámite obligaba a modificar cadenas `if/else` en `reg`, `asig`, `camEst` y `rep`. | Se introdujo el polimorfismo mediante `ServicioNotificacion` y se encapsularon las reglas por tipo dentro del enum `TipoSolicitud`. Agregar *Telegram* o *Push* solo requiere crear una nueva clase sin tocar `GestorSolicitudes`. | `ServicioNotificacion`, `NotificacionEmail`, `NotificacionSms`, `NotificacionWhatsApp`, `TipoSolicitud` |
| **LSP** (*Liskov Substitution Principle*) | No existían abstracciones intercambiables; el código dependía rígidamente del archivo físico `solicitudes_db.csv`. | `RepositorioSolicitudesArchivo` y `RepositorioSolicitudesMemoria` cumplen exactamente el mismo contrato de `RepositorioSolicitudes`, pudiendo sustituirse de forma transparente entre producción y testing. | `RepositorioSolicitudes`, `RepositorioSolicitudesArchivo`, `RepositorioSolicitudesMemoria` |
| **ISP** (*Interface Segregation Principle*) | Acoplamiento monolítico sin interfaces segregadas por rol. | Interfaces pequeñas, cohesivas y orientadas al cliente: `ServicioNotificacion` (1 método) y `RepositorioSolicitudes` (4 operaciones CRUD de dominio). | `ServicioNotificacion`, `RepositorioSolicitudes` |
| **DIP** (*Dependency Inversion Principle*) | El módulo de alto nivel (`GestorSolicitudes`) instanciaba directamente detalles de bajo nivel (`new FileWriter(...)` y `System.out.println`). | `GestorSolicitudes` recibe `RepositorioSolicitudes` y `ServicioNotificacion` por **inyección de dependencias en el constructor**, dependiendo solo de abstracciones. | `GestorSolicitudes`, `Main`, `PruebasVerificacionSistema` |

---

## 2. Mejoras de Clean Code Aplicadas

1. **Nombres Reveladores de Intención:**
   - Métodos renombrados: `reg` $\rightarrow$ `registrarSolicitud`, `asig` $\rightarrow$ `asignarResponsable`, `camEst` $\rightarrow$ `cambiarEstadoSolicitud`, `bus` $\rightarrow$ `buscarSolicitudes`, `rep` $\rightarrow$ `generarReporteGeneral`.
   - Atributos renombrados en `Solicitud`: `t` $\rightarrow$ `tipo`, `p` $\rightarrow$ `prioridad`, `e` $\rightarrow$ `estado`, `u` $\rightarrow$ `urgente`, `est` $\rightarrow$ `estudiante`, `resp` $\rightarrow$ `responsable`, `obs` $\rightarrow$ `observacionesResolucion`.
2. **Funciones Pequeñas y de Un Solo Nivel de Abstracción:**
   - Ningún método supera las 20 líneas; los métodos públicos expresan el flujo de negocio de alto nivel y delegan los detalles en métodos privados claramente nombrados.
3. **Eliminación de Números y Textos Mágicos:**
   - Se definieron constantes simbólicas (`MINIMO_MATERIAS_MERITO_ACADEMICO = 20`, `PROMEDIO_MINIMO_MERITO_ACADEMICO = 8.0`, `FACTOR_REDUCCION_SLA_URGENTE = 0.50`) y tipos enumerados (`TipoSolicitud`, `PrioridadSolicitud`, `EstadoSolicitud`).
4. **Manejo Defensivo de Errores (*Guard Clauses*) y Gestión de Recursos:**
   - Validación temprana de nulidad, cadenas vacías y rangos numéricos mediante `IllegalArgumentException` e `IllegalStateException`, junto con cierre seguro `try-with-resources` en `RepositorioSolicitudesArchivo`.

---

## 3. Catálogo de Refactorings Seguros Documentados (Etapa 5)

Se aplicaron **6 técnicas formales de refactoring seguro** (superando el mínimo de 4 exigido por la cátedra):

### Refactoring 1: *Introduce Parameter Object*
- **Smell que resuelve:** *Long Parameter List* (CS-02) en `GestorSolicitudes.reg(...)` y valores mágicos en `bus(...)`.
- **Antes (`estado-inicial`):**
  ```java
  public Solicitud reg(String id, String leg, String nom, String mail, String car,
                       int mat, double prom, String t, int p, String desc,
                       int dias, boolean u, String canalNotif)
  ```
- **Después (`estadorefactorizado`):**
  ```java
  public Solicitud registrarSolicitud(DatosRegistroSolicitud datos)
  ```
- **Beneficio técnico:** Reduce la firma de 13 parámetros a 1 único objeto de dominio inmutable y validado (`DatosRegistroSolicitud`), previniendo errores por inversión de argumentos y facilitando la evolución futura.

### Refactoring 2: *Extract Method*
- **Smell que resuelve:** *Long Method* (CS-03) y mezcla de niveles de abstracción en `reg(...)`, `asig(...)` y `rep()`.
- **Antes (`estado-inicial`):** El cálculo de prioridad, la validación de cupos, el formateo de cada renglón del reporte y el conteo de vencimientos estaban incrustados en métodos largos.
- **Después (`estadorefactorizado`):**
  ```java
  public Solicitud registrarSolicitud(DatosRegistroSolicitud datos) {
      validarDatosNoNulos(datos, "Los datos de registro de la solicitud no pueden ser nulos.");
      PrioridadSolicitud prioridadEfectiva = determinarPrioridadEfectiva(datos);
      Solicitud nuevaSolicitud = crearSolicitudInicial(datos, prioridadEfectiva);
      repositorio.guardar(nuevaSolicitud);
      notificarRegistroSolicitud(nuevaSolicitud);
      return nuevaSolicitud;
  }
  ```
- **Beneficio técnico:** Disminuye drásticamente la complejidad ciclomática, hace que el método principal se lea como prosa técnica y permite aislar cada subpaso.

### Refactoring 3: *Move Method* (Reubicación de Responsabilidad / *Information Expert*)
- **Smell que resuelve:** *Feature Envy* (CS-05) y *Anemic Domain Model* en `Estudiante`, `Responsable` y `Solicitud`.
- **Antes (`estado-inicial`):**
  ```java
  // En GestorSolicitudes.java:
  if (est.materiasAprobadas >= 20 && est.promedio >= 8.0) { prioFinal = 1; }
  if (r.cargaActual >= r.maximoSolicitudes) { return false; }
  ```
- **Después (`estadorefactorizado`):**
  ```java
  // En Estudiante.java:
  public boolean cumpleMeritoAcademicoPrioritario() {
      return this.materiasAprobadas >= MINIMO_MATERIAS_MERITO_ACADEMICO
              && this.promedio >= PROMEDIO_MINIMO_MERITO_ACADEMICO;
  }
  // En Responsable.java:
  public boolean tieneCupoDisponible() { return this.cargaActual < this.maximoSolicitudes; }
  public boolean puedeGestionarTipo(TipoSolicitud tipo) { return tipo != null && tipo.esAreaCompatible(this.area); }
  // En Solicitud.java:
  public int calcularPlazoMaximoDias() { ... }
  public boolean tieneSlaVencido() { return !estado.estaCerrada() && diasTranscurridos > calcularPlazoMaximoDias(); }
  ```
- **Beneficio técnico:** Ubica cada regla de negocio en la clase experta en la información, aumentando la cohesión del modelo de dominio y encapsulando los atributos como `private`.

### Refactoring 4: *Replace Type Code with Enum / State Behavior*
- **Smell que resuelve:** *Primitive Obsession* (CS-04) y *Magic Numbers / Strings* (CS-07).
- **Antes (`estado-inicial`):** Uso de `String t = "EQUIVALENCIA"`, `int p = 1`, `String e = "PENDIENTE"`.
- **Después (`estadorefactorizado`):** Creación de `TipoSolicitud`, `PrioridadSolicitud` y `EstadoSolicitud` con comportamiento propio (`elevarUnNivel()`, `estaCerrada()`, `puedeTransicionarA(...)`).
- **Beneficio técnico:** Garantiza seguridad de tipos en tiempo de compilación (*Type Safety*) e impide estados o tipos inexistentes.

### Refactoring 5: *Extract Class & Extract Interface* (Patrón Repositorio y Estrategia de Notificación)
- **Smell que resuelve:** *God Class* (CS-01), *Duplicated Code* (CS-06), *Switch/If-Else rígidos* (CS-08) y *Acoplamiento a I/O* (CS-09).
- **Antes (`estado-inicial`):** Tres bloques idénticos con `FileWriter` y tres cadenas `if (canalNotif.equals("EMAIL"))` dentro de `GestorSolicitudes`.
- **Después (`estadorefactorizado`):** Extracción de `RepositorioSolicitudes` (`RepositorioSolicitudesArchivo`, `RepositorioSolicitudesMemoria`), `ServicioNotificacion` (`NotificacionEmail`, `NotificacionSms`, `NotificacionWhatsApp`) y `GeneradorReporteSolicitudes`.
- **Beneficio técnico:** Elimina el 100% del código duplicado de persistencia y mensajería, desacopla la lógica de negocio del disco y habilita las pruebas unitarias en memoria.

### Refactoring 6: *Replace Magic Numbers with Symbolic Constants & Introduce Guard Clauses*
- **Smell que resuelve:** *Magic Numbers* (CS-07) y *Ausencia de Manejo de Errores* (CS-10).
- **Beneficio técnico:** Documenta el significado de cada umbral reglamentario y protege las invariantes de todas las entidades desde su construcción.
