# SysVentas + Padrón de Ciudadanos — Guía para IntelliJ IDEA (Windows)

Probado para: **IntelliJ IDEA 2026.2.x en Windows**, **JDK 21**, **Maven** (el proyecto trae Maven Wrapper, no necesitas instalar Maven).

## 1. Requisito: JDK 21
El `pom.xml` compila con Java 21, así que usa **JDK 21** (no 25 ni 26: Lombok puede fallar con JDK muy nuevos).

Si no lo tienes, IntelliJ lo descarga solo:
`File → Project Structure… (Ctrl+Alt+Mayús+S) → Project → SDK → Add SDK → Download JDK…` → Version **21** (Temurin / Oracle OpenJDK / Corretto, cualquiera).

## 2. Abrir el proyecto
1. Descomprime el ZIP.
2. `File → Open…` y elige la carpeta **`SysVentas`** (la que contiene `pom.xml`), **no** la carpeta de afuera `POO2026-2-G2-dev-Sesion07`.
3. Pulsa **Trust Project**.
4. Espera a que termine la barra de progreso inferior (IntelliJ descarga JavaFX, Lombok y Hibernate Validator con Maven).
   Si no empieza: panel **Maven** (derecha) → botón **↻ Reload All Maven Projects**.
5. Confirma el JDK: `File → Project Structure → Project → SDK` = **21**.

## 3. Activar Lombok (una sola vez)
- El plugin **Lombok** ya viene incluido en IntelliJ. Verifícalo en `File → Settings → Plugins → Installed` (debe estar activado).
- `File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors` → marca **Enable annotation processing**.

## 4. Ejecutar
**Opción A (recomendada):** arriba a la derecha selecciona la configuración **`SysVentas (javafx:run)`** y pulsa ▶.
(Ejecuta `clean javafx:run` con el Maven del proyecto.)

**Opción B:** panel **Maven → SysVentas → Plugins → javafx → javafx:run** (doble clic).

**Opción C (terminal de IntelliJ, `Alt+F12`):**
```
.\mvnw.cmd clean javafx:run
```

**Opción D:** clic derecho en `App.java` → **Run 'App.main()'**.

## 5. Usar el Ejercicio 1
En la ventana: menú **File → Padrón de Ciudadanos**.

| Acción | Cómo |
|---|---|
| Agregar | Llena el formulario → **Agregar** |
| Consultar | Escribe nombre o CURP → **Consultar** (o Enter). **Ver todos** limpia el filtro |
| Modificar | Clic en una fila (se carga en el formulario) → cambia datos → **Modificar** |
| Eliminar | Clic en una fila → **Eliminar** (pide confirmación), o el icono de la papelera de la fila |
| Limpiar | **Limpiar** vacía el formulario y vuelve al modo "nuevo" |

CURP de ejemplo válida: `GOPJ900515HDFMRN08` (18 caracteres, se escribe en mayúsculas automáticamente y no se puede repetir).

## 6. Problemas frecuentes
| Síntoma | Solución |
|---|---|
| `Cannot resolve symbol 'builder'` / `getNombreCompleto` en rojo | Falta Lombok: revisa el paso 3 y luego `Build → Rebuild Project` |
| `Project JDK is not defined` | Paso 1 y 2.5: elige el JDK 21 |
| `release version 21 not supported` | Estás usando un JDK menor a 21 |
| `JavaFX runtime components are missing` | No ejecutes una clase que extiende `Application` directamente; usa la opción A, B, C o D |
| Todo en rojo y no importa dependencias | Necesitas internet la primera vez; luego panel Maven → **Reload All Maven Projects** |
| Caracteres raros (tildes) | `File → Settings → Editor → File Encodings` → todo en **UTF-8** |
