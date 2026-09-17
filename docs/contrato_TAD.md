# Contrato TAD RepositorioLecturas

**Semana 2 - Plataforma de Monitoreo Ambiental Urbano**  
**Equipo:** Code Trinity  
**Fecha:** 2026-09-14

---

## Descripción del TAD

El `RepositorioLecturas` es un Tipo Abstracto de Dato que permite almacenar, gestionar y consultar lecturas de sensores ambientales. Proporciona operaciones para agregar, buscar, actualizar y eliminar lecturas, manteniendo la integridad de los datos mediante encapsulamiento.

---

## Operaciones Públicas

### 1. `agregar(lectura)`
- **Parámetros:**
    - `lectura`: Objeto de tipo `LecturaSensor`
- **Comportamiento esperado:**
    - Agrega la lectura al final del repositorio
    - Si el arreglo está lleno, duplica su capacidad automáticamente
    - Incrementa el contador de elementos
- **Retorna:** `true` si se agregó exitosamente
- **Situaciones inválidas:**
    - `lectura == null` → No se agrega, retorna `false`

---

### 2. `obtener(posicion)`
- **Parámetros:**
    - `posicion`: `int` (índice del elemento a recuperar)
- **Comportamiento esperado:**
    - Retorna la lectura almacenada en la posición indicada
- **Retorna:** Objeto `LecturaSensor` o `null`
- **Situaciones inválidas:**
    - `posicion < 0` → Retorna `null`
    - `posicion >= cantidad` → Retorna `null`

---

### 3. `buscarPorEstacion(idEstacion)`
- **Parámetros:**
    - `idEstacion`: `String` (identificador de la estación, ej: "EST-004")
- **Comportamiento esperado:**
    - Recorre secuencialmente el repositorio
    - Compara el ID de cada lectura con el buscado
    - Retorna la primera coincidencia encontrada
- **Retorna:** Objeto `LecturaSensor` o `null`
- **Situaciones inválidas:**
    - `idEstacion == null` → Retorna `null`
    - Estación no existe → Retorna `null`

---

### 4. `actualizar(posicion, nuevaLectura)`
- **Parámetros:**
    - `posicion`: `int` (índice del elemento a actualizar)
    - `nuevaLectura`: Objeto `LecturaSensor`
- **Comportamiento esperado:**
    - Reemplaza la lectura en la posición indicada
- **Retorna:** `void`
- **Situaciones inválidas:**
    - `posicion < 0` → No opera
    - `posicion >= cantidad` → No opera
    - `nuevaLectura == null` → No opera

---

### 5. `eliminar(posicion)`
- **Parámetros:**
    - `posicion`: `int` (índice del elemento a eliminar)
- **Comportamiento esperado:**
    - Elimina la lectura en la posición indicada
    - Mueve todos los elementos posteriores una posición a la izquierda (compactación)
    - Anula la última posición para evitar referencias duplicadas
    - Decrementa el contador de elementos
- **Retorna:** `void`
- **Situaciones inválidas:**
    - `posicion < 0` → No opera
    - `posicion >= cantidad` → No opera

---

### 6. `tamano()`
- **Parámetros:** Ninguno
- **Comportamiento esperado:**
    - Retorna el número real de lecturas almacenadas
- **Retorna:** `int` con la cantidad de elementos
- **Situaciones inválidas:** Ninguna

---

### 7. `promedioPm25()`
- **Parámetros:** Ninguno
- **Comportamiento esperado:**
    - Calcula el promedio de PM2.5 de todas las lecturas almacenadas
- **Retorna:** `double` con el promedio
- **Situaciones inválidas:**
    - Repositorio vacío → Retorna `0.0`

---

## Operaciones Privadas (Detalles de Implementación)

### 1. `redimensionar()`
- **Propósito:** Duplicar la capacidad del arreglo interno cuando está lleno
- **Comportamiento:**
    - Crea un nuevo arreglo con el doble de capacidad
    - Copia todos los elementos existentes al nuevo arreglo
    - Actualiza la referencia al nuevo arreglo
    - Incrementa el contador de redimensionamientos
- **Visibilidad:** `private` (no es parte del contrato público)

---

## Invariantes del TAD

1. **Encapsulamiento:** Los atributos `lecturas` y `cantidad` son `private`
2. **Consistencia:** `0 <= cantidad <= lecturas.length`
3. **Contigüidad:** Los elementos válidos están en las posiciones `0` a `cantidad - 1`
4. **No nulls:** Las posiciones `0` a `cantidad - 1` nunca contienen `null`
5. **Capacidad dinámica:** El arreglo crece automáticamente al doble cuando se llena

---

## Complejidad Computacional

| Operación | Complejidad | Justificación |
|-----------|-------------|---------------|
| `agregar()` | O(1) amortizado | Redimensionamiento ocasional por duplicación |
| `obtener()` | O(1) | Acceso directo por índice |
| `buscarPorEstacion()` | O(n) | Búsqueda secuencial en peor caso |
| `actualizar()` | O(1) | Acceso directo por índice |
| `eliminar()` | O(n) | Requiere compactación (mover elementos) |
| `tamano()` | O(1) | Retorna contador |
| `promedioPm25()` | O(n) | Recorre todas las lecturas |

---

## Ejemplos de Uso

```java
RepositorioLecturas repo = new RepositorioLecturas();

// Agregar lecturas
LecturaSensor l1 = new LecturaSensor("EST-001", "2026-09-07 08:00", 18.5, 75.2, 32.4);
repo.agregar(l1);

// Consultar tamaño
int n = repo.tamano();  // Retorna: 1

// Buscar por estación
LecturaSensor encontrada = repo.buscarPorEstacion("EST-001");  // Retorna l1
LecturaSensor noExiste = repo.buscarPorEstacion("EST-999");    // Retorna null

// Obtener por posición
LecturaSensor l = repo.obtener(0);  // Retorna l1
LecturaSensor fuera = repo.obtener(500);  // Retorna null

// Actualizar
LecturaSensor nueva = new LecturaSensor("EST-001", "2026-09-07 09:00", 19.0, 74.0, 33.0);
repo.actualizar(0, nueva);

// Eliminar
repo.eliminar(0);  // Compacta y reduce tamaño