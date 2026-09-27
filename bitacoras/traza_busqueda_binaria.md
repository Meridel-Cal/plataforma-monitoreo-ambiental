# Traza de Búsqueda Binaria - Antes de la corrección

## Contexto
Esta traza documenta el comportamiento de mi algoritmo de búsqueda binaria
**antes** de corregir la condición del ciclo `while`.

El propósito de esta traza es demostrar que puedo razonar sobre el
comportamiento de un algoritmo antes de modificarlo, identificando casos
límite donde falla.

## Escenario de prueba

- **Arreglo:** 3 elementos ordenados ascendentemente por timestamp
- **Datos:** `["0000000010", "0000000020", "0000000030"]`
- **Índices:** posición 0, posición 1, posición 2
- **Objetivo buscado:** `"0000000030"` (el último elemento del arreglo)
- **Condición con error:** `while (inicio < fin)`
- **Fórmula del medio:** `medio = (inicio + fin) / 2`

## Traza paso a paso

### Paso 1

| Variable | Valor |
| :--- | :--- |
| `inicio` | 0 |
| `fin` | 2 |
| `medio` | (0 + 2) / 2 = **1** |
| Valor en `datos[1]` | `"0000000020"` |
| Comparación | `"0000000020".compareTo("0000000030")` → negativo |
| Acción | `inicio = medio + 1` → `inicio = 2` |

**Análisis:** El objetivo es mayor que el valor del medio, por lo que descarto
la mitad izquierda. El nuevo rango de búsqueda es `[2, 2]`.

### Paso 2

| Variable | Valor |
| :--- | :--- |
| `inicio` | 2 |
| `fin` | 2 |
| `medio` | **No se calcula** |
| Valor en medio | **No se evalúa** |
| Comparación | **No se realiza** |
| Acción | **El ciclo termina porque `2 < 2` es FALSO** |

**Análisis:** El rango de búsqueda colapsó a un solo elemento (posición 2),
que es exactamente donde está el valor buscado. Sin embargo, mi condición
`inicio < fin` excluye el caso donde ambos índices son iguales, por lo que
el algoritmo abandona la búsqueda sin siquiera mirar ese elemento.

### Resultado final

El método retorna **`-1`** (no encontrado), aunque el dato `"0000000030"`
existe claramente en la posición 2 del arreglo.

## Causa raíz del error

La condición `while (inicio < fin)` es incorrecta porque no permite evaluar
el último elemento candidato cuando el rango de búsqueda se reduce a una
sola posición. En ese momento, `inicio == fin`, y la condición se vuelve
falsa prematuramente.

## Corrección aplicada

Cambie la condición del ciclo a:

```java
while (inicio <= fin)