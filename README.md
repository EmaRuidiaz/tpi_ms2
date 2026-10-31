# Sistema de Gestión de Solicitudes Académicas (SGA) — TPI Metodología de Sistemas II

Prototipo base del **Sistema de Gestión de Solicitudes Académicas (SGA)** desarrollado para el Trabajo Práctico Integrador (TPI) de **Metodología de Sistemas II** (Tecnicatura Universitaria en Programación a Distancia — UTN).

- **Estudiante:** Ruidiaz, Emanuel Facundo
- **Comisión:** Comisión 15
- **Modalidad:** Individual

## Estado del Proyecto (`estado-inicial`)
Esta versión corresponde al **sistema base funcional** previo al proceso de diagnóstico de deuda técnica, aplicación de principios SOLID/Clean Code y refactoring seguro.

### Funcionalidades Implementadas
1. **Registro de solicitudes académicas** (`EQUIVALENCIA`, `CERTIFICADO`, `EXCEPCION`, `BAJA`) con cálculo de prioridad según mérito académico o urgencia.
2. **Asignación de responsables** validando disponibilidad de cupo y compatibilidad de área administrativa/académica.
3. **Cambio y control de estados** (`PENDIENTE` -> `EN_REVISION` -> `APROBADA` / `RECHAZADA`).
4. **Búsqueda y filtrado** combinable por tipo, estado y prioridad.
5. **Generación de reporte general** con indicadores de gestión y control de plazos SLA.
6. **Persistencia en archivo** (`solicitudes_db.csv`) y **notificaciones** por canal (`EMAIL`, `SMS`, `WHATSAPP`).

## Ejecución
```bash
javac -encoding UTF-8 -d bin src/sga/*.java
java -Dfile.encoding=UTF-8 -cp bin sga.Main
```
