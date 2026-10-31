# Sistema de Gestión de Solicitudes Académicas (SGA)
### Trabajo Práctico Integrador (TPI) — Metodología de Sistemas II
**Universidad Tecnológica Nacional (UTN) — Tecnicatura Universitaria en Programación a Distancia (TUPaD)**

---

## 👥 Integrantes del Equipo y Participación

| Integrante | Comisión | Rol y Contribuciones Principales en el TPI |
| :--- | :---: | :--- |
| **Ruidiaz, Emanuel Facundo** | Comisión 15 | Desarrollo del prototipo base (`estado-inicial`), diagnóstico de calidad interna/externa, refactorings de dominio (*Move Method*, *Replace Type Code with Enum*, *Introduce Parameter Object*), suite de pruebas automatizadas y redacción del informe técnico. |
| **Roques Zeballos, Juan Martín** | Comisión 15 | Relevamiento de los 10 *code smells* y deuda técnica, desacoplamiento de infraestructura aplicando SOLID (*SRP, OCP, LSP, ISP, DIP* en `RepositorioSolicitudes`, `ServicioNotificacion` y `GeneradorReporteSolicitudes`), ejecución del *Code Review* cruzado, elaboración de ADRs y verificación de no-regresión. |

---

## 📌 Descripción del Caso de Estudio
El **Sistema de Gestión de Solicitudes Académicas (SGA)** modela el circuito administrativo y académico de una facultad para gestionar trámites estudiantiles (**Equivalencias**, **Certificados**, **Excepciones de Correlatividad** y **Bajas Temporales**).

### Funcionalidades Principales Verificables
1. **Registro de Solicitudes Académicas:** Alta de trámites evaluando mérito académico ($\ge 20$ materias aprobadas y promedio $\ge 8.0$) y carácter urgente para el cálculo automático de la prioridad efectiva.
2. **Asignación de Responsables:** Validación de cupo operativo disponible y compatibilidad entre el área del responsable (*Secretaría Académica*, *Departamento de Alumnos*, *Dirección de Carrera*) y el tipo de trámite, transicionando la solicitud a `EN_REVISION`.
3. **Gestión del Ciclo de Vida y Estados:** Control estricto de transiciones (`PENDIENTE` $\rightarrow$ `EN_REVISION` $\rightarrow$ `APROBADA` / `RECHAZADA`), bloqueando la modificación de trámites cerrados o la resolución sin revisión previa.
4. **Búsqueda y Filtrado Combinado:** Consulta por tipo de trámite, estado actual y prioridad mediante `CriterioBusquedaSolicitud`.
5. **Reporte General y Control de SLA:** Tablero estadístico con totales por estado y detección automática de vencimientos según el Acuerdo de Nivel de Servicio (SLA) normal o urgente de cada tipo de solicitud.
6. **Persistencia Estructurada y Notificaciones Multicanal:** Almacenamiento en archivo CSV (`solicitudes_db.csv`) e implementación en memoria para pruebas (`RepositorioSolicitudesMemoria`), junto con despacho polimórfico por `EMAIL`, `SMS` y `WHATSAPP`.

---

## 🛠️ Tecnologías Utilizadas
- **Lenguaje:** Java 21 LTS (Standard Edition, sin dependencias externas pesadas para garantizar portabilidad inmediata).
- **Persistencia:** Archivo estructurado CSV (`solicitudes_db.csv`) codificado en UTF-8 + Repositorio en memoria para testing.
- **Control de Versiones:** Git & GitHub con *Conventional Commits* y tags por etapa.

---

## 📂 Estructura del Repositorio

```text
tpi_ms2/
├── src/sga/
│   ├── Main.java                          # Demostración integral de las 5 operaciones funcionales
│   ├── PruebasVerificacionSistema.java    # Suite de 10 pruebas automatizadas en memoria (10/10 PASS)
│   ├── GestorSolicitudes.java             # Servicio de aplicación refactorizado (SRP, DIP, Clean Code)
│   ├── GeneradorReporteSolicitudes.java   # Constructor de reportes y estadísticas de SLA (SRP)
│   ├── Solicitud.java                     # Entidad raíz rica del dominio (Move Method, Guard Clauses)
│   ├── Estudiante.java                    # Entidad de dominio con evaluación de mérito académico
│   ├── Responsable.java                   # Entidad de dominio con control de cupo y compatibilidad
│   ├── DatosRegistroSolicitud.java        # Parameter Object para el alta de solicitudes
│   ├── CriterioBusquedaSolicitud.java     # Parameter Object para búsquedas y filtros combinados
│   ├── TipoSolicitud.java                 # Enum con plazos base de SLA y áreas competentes
│   ├── PrioridadSolicitud.java            # Enum con lógica de escalamiento de prioridad
│   ├── EstadoSolicitud.java               # Enum con reglas de transición de estados
│   ├── RepositorioSolicitudes.java        # Interfaz de persistencia (DIP / ISP)
│   ├── RepositorioSolicitudesArchivo.java # Implementación de persistencia en archivo CSV (LSP / DRY)
│   ├── RepositorioSolicitudesMemoria.java # Implementación en memoria para pruebas unitarias (LSP)
│   ├── ServicioNotificacion.java          # Interfaz de notificación (OCP / DIP / ISP)
│   ├── NotificacionEmail.java             # Estrategia concreta de envío por Email
│   ├── NotificacionSms.java               # Estrategia concreta de envío por SMS
│   └── NotificacionWhatsApp.java          # Estrategia concreta de envío por WhatsApp
├── docs/
│   ├── 01-diagnostico-calidad-y-smells.md # Etapas 1, 2 y 3: Calidad externa/interna y 10 code smells
│   ├── 02-pruebas-y-verificacion.md       # Etapa 6: Matriz de 10 casos de prueba y no-regresión
│   ├── 03-solid-clean-code-y-refactorings.md # Etapas 4 y 5: SOLID, Clean Code y 6 refactorings seguros
│   ├── 04-checklist-code-review.md        # Etapa 7: Checklist técnico de 12 puntos y revisión de PR
│   ├── 05-bitacora-uso-ia-y-prompts.md    # Declaración metodológica y registro detallado de prompts IA
│   ├── 06-adr-decisiones-arquitectura.md  # Architecture Decision Records (ADR-001 a ADR-003)
│   ├── diagramas/                         # Diagramas UML (antes y después) y gráfico de métricas
│   └── evidencias/                        # Código inicial, salidas comparativas y capturas de ejecución
├── solicitudes_db.csv                     # Base de datos CSV generada por la ejecución del sistema
└── README.md
```

---

## 🏷️ Tags e Historial de Hitos del Proyecto

El repositorio cuenta con los tags obligatorios exigidos por la consigna oficial para inspeccionar la evolución paso a paso:

| Tag | Etapa / Estado | Descripción del Hito |
| :--- | :--- | :--- |
| `estado-inicial` | **Versión Base Inicial** | Sistema funcional con problemas de diseño intencionales (*God Class*, *Long Parameter List*, *Feature Envy*, *Duplicated Code*, *Primitive Obsession*). |
| `etapa-diagnostico` | **Etapas 1, 2 y 3** | Relevamiento de calidad externa vs. interna, deuda técnica y catálogo de 10 *code smells* documentados. |
| `etapa-refactoring` | **Etapas 4 y 5** | Aplicación de principios SOLID (*SRP, OCP, LSP, ISP, DIP*), *Clean Code* y 6 refactorings seguros (*Introduce Parameter Object*, *Extract Method*, *Move Method*, *Replace Type Code with Enum*, *Extract Interface/Class*, *Guard Clauses*). |
| `etapa-pruebas` | **Etapa 6** | Incorporación de la suite `PruebasVerificacionSistema.java` (10 casos automatizados) y verificación de equivalencia 100% antes/después. |
| `etapa-docs` | **Etapas 7 y 8** | Checklist de *Code Review* profesional, ADRs, diagramas UML comparativos y bitácora de uso de IA. |
| `estadorefactorizado` | **Versión Final Refactorizada** | Entrega final integrada, limpia, documentada y verificada (también etiquetada como `estado-refactorizado`). |

### Cómo comparar la versión inicial y la versión refactorizada
```bash
# Inspeccionar el código de la versión inicial
git checkout estado-inicial

# Volver a la versión final refactorizada
git checkout estadorefactorizado

# Ver el diff completo o el historial gráfico de commits y tags
git log --oneline --decorate --graph --all
```

*(Nota: También se conserva una copia directa de los archivos originales de `estado-inicial` dentro de `docs/evidencias/codigo-estado-inicial/` para consulta rápida sin cambiar de rama).*

---

## 🚀 Instalación, Compilación y Ejecución

### Requisitos Previos
- **Java JDK 17 o superior** (desarrollado y verificado sobre **Java 21 LTS**).

### 1. Compilar el proyecto
```bash
javac -encoding UTF-8 -d bin src/sga/*.java
```

### 2. Ejecutar la demostración funcional completa (`Main`)
```bash
java -Dfile.encoding=UTF-8 -cp bin sga.Main
```

### 3. Ejecutar la suite de pruebas automatizadas (`PruebasVerificacionSistema`)
```bash
java -Dfile.encoding=UTF-8 -cp bin sga.PruebasVerificacionSistema
```
