# Bitácora individual - Semana 04

## 1. Datos de la actividad

- **Estudiante:** Meridel Calderon
- **Desarrollo:** Individual
- **Semana:** 04
- **Fecha del laboratorio:** 2026-09-28
- **Fecha del taller:** 2026-10-01
- **Tema principal:** Algoritmos de ordenamiento (Burbuja, Selección, Inserción, MergeSort, HeapSort, QuickSort), comparación empírica de eficiencia, complejidad O(n²) vs O(n log n), efecto colateral del ordenamiento sobre la búsqueda binaria.
- **Pregunta de la semana:** Si ordenar es necesario para buscar rápidamente, ¿cuánto cuesta ordenar y qué consecuencias tiene hacerlo sobre el resto del sistema?

## 2. Predicción antes de ejecutar

Antes de abrir o ejecutar el programa, respondo:

1. **¿Qué creo que va a ocurrir?**
   Predigo que los tres algoritmos simples (Burbuja, Selección, Inserción) tendrán un rendimiento similar con datos desordenados (~50 millones de comparaciones para 10.000 elementos), pero que **Inserción se comportará drásticamente mejor** con datos ya ordenados (como los que llegan de la red de sensores), reduciendo sus comparaciones a ~9.999. También predigo que QuickSort con pivote fijo en el primer elemento fallará con `StackOverflowError` cuando los datos estén ordenados cronológicamente, porque las particiones quedarán desbalanceadas.

2. **¿Qué parte del programa o del algoritmo puede fallar?**
   Sospecho que el Experimento 5 (efecto colateral) demostrará que ordenar por PM2.5 rompe la búsqueda binaria por timestamp. El arreglo perderá su precondición de ordenamiento cronológico, y la búsqueda binaria devolverá `-1` para datos que sí existen. También espero que el Experimento 3 muestre cómo Inserción se vuelve inviable con 100.000 datos desordenados (crecimiento O(n²)), mientras MergeSort y HeapSort mantienen un crecimiento manejable (O(n log n)).

3. **¿Cómo comprobaré mi predicción?**
   Ejecutaré los cinco experimentos del `BancoDeOrdenamiento` en orden, anotando comparaciones, intercambios y tiempo en cada uno. Compararé los resultados con las predicciones teóricas de complejidad asintótica.

## 3. Evidencia del laboratorio

### Resultado observado

**Experimento 1 (10.000 datos desordenados):**
| Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
| :--- | :---: | :---: | :---: |
| Burbuja | 49.995.000 | 24.928.244 | ~350 |
| Selección | 49.995.000 | 9.994 | ~180 |
| Inserción | 24.938.233 | 24.928.244 | ~316 |

**Experimento 2 (10.000 datos ordenados, después del TODO 1):**
| Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
| :--- | :---: | :---: | :---: |
| Burbuja (con bandera) | 9.999 | 0 | 1 |
| Selección | 49.995.000 | 0 | ~180 |
| Inserción | 9.999 | 0 | 1 |

**Experimento 3 (crecimiento):**
| n | Algoritmo | Comparaciones | Tiempo (ms) |
| :---: | :--- | :---: | :---: |
| 1.000 | Inserción | 8.684 | 1 |
| 1.000 | MergeSort | 16.786 | 1 |
| 1.000 | HeapSort | ~17.000 | 1 |
| 10.000 | Inserción | 24.938.233 | 316 |
| 10.000 | MergeSort | 120.396 | 4 |
| 10.000 | HeapSort | 235.434 | 4 |
| 100.000 | Inserción | ~2.500.000.000 | >30.000 |
| 100.000 | MergeSort | ~1.500.000 | 45 |
| 100.000 | HeapSort | ~3.000.000 | 60 |

**Experimento 4 (QuickSort):**
- Caso A (50.000 desordenados): ~900.318 comparaciones, ~145 ms ✅
- Caso B (50.000 ordenados, antes del fix): `StackOverflowError` ❌
- Caso B (después del fix con mediana de tres): ~900.000 comparaciones, ~150 ms ✅

**Experimento 5 (efecto colateral):**
- Paso 1: Búsqueda binaria por timestamp → posición 73.412 ✅ (17 comparaciones)
- Paso 2: Ordenar por PM2.5 → ranking generado ✅
- Paso 3: Búsqueda binaria por timestamp → **-1 (falla silenciosa)** ❌
- Verificación lineal: encuentra el dato en posición 73.412 ✅

### Diferencia entre la predicción y el resultado

Mis predicciones fueron **mayoritariamente correctas**, pero subestimé la magnitud de algunos fenómenos:

1. **Acerté** en que Inserción sería excelente con datos ordenados (~9.999 comparaciones), pero no anticipé que Burbuja con la bandera de corte temprano lograría el mismo rendimiento. Ambos algoritmos "simples" se volvieron O(n) con datos ordenados.

2. **Acerté** en que QuickSort explotaría con datos ordenados, pero no esperaba que el `StackOverflowError` ocurriera tan rápido (con solo 50.000 elementos). Esto me hizo consciente de que la recursión profunda tiene límites físicos reales, no solo teóricos.

3. **Subestimé** el crecimiento de Inserción: de 10.000 a 100.000 elementos (multiplicar n por 10), sus comparaciones se multiplicaron por ~100 (de 24 millones a 2.500 millones), confirmando brutalmente la complejidad O(n²). En cambio, MergeSort solo multiplicó sus comparaciones por ~12, confirmando O(n log n).

4. **El Experimento 5 fue el más revelador:** la búsqueda binaria no lanzó ninguna excepción, simplemente devolvió `-1`. Este "fallo silencioso" es más peligroso que un error explícito porque el sistema seguiría funcionando normalmente, reportando datos inexistentes.

### Error o comportamiento inesperado

- **¿Qué ocurrió?** Al integrar `BancoDeOrdenamiento` en `IngestaSensores.java`, IntelliJ marcó en rojo las llamadas a `banco.experimentoUno()`, `experimentoDos()`, etc., con el error: *"cannot find symbol"*.

- **¿Por qué ocurrió?** Los métodos `experimentoUno()` a `experimentoCinco()` en `BancoDeOrdenamiento.java` estaban declarados como `private static`. En Java, los métodos `private` solo son accesibles desde la misma clase. Como `IngestaSensores` es una clase diferente, no podía invocarlos.

- **¿Cómo lo corregí?** Creé una rama `fix/visibilidad-metodos-banco-ordenamiento` desde `feature/semana-4-ordenamientos`, cambié los cinco métodos de `private static void` a `public static void`, verifiqué que compilara, hice commit con el mensaje `fix: cambiar métodos de BancoDeOrdenamiento de private a public para integración con IngestaSensores`, y mergié el fix de vuelta a la rama feature con `--no-ff`.

- **¿Qué aprendí?** La visibilidad (`private`, `public`, `protected`) no es solo una convención de estilo: es una barrera técnica real que impide la integración entre módulos. Diseñar una API pública clara es parte fundamental del trabajo de ingeniería.

## 4. Explicación en lenguaje llano

> Imagina que tengo una biblioteca con 10.000 libros desordenados. Si quiero encontrar un libro específico, tengo tres opciones:
>
> **Burbuja:** Comparo cada libro con su vecino y los voy intercambiando hasta que todos estén en orden. Es como revisar cada par de libros adyacentes una y otra vez. Muy lento, pero simple.
>
> **Selección:** Busco el libro más pequeño de toda la biblioteca, lo pongo al principio. Luego busco el siguiente más pequeño, y así. Hago muchas comparaciones, pero muevo pocos libros.
>
> **Inserción:** Tomo un libro y lo inserto en su lugar correcto entre los que ya ordené. Si los libros ya vienen casi ordenados (como llegan de la red de sensores), este método es rapidísimo.
>
> **MergeSort:** Divido la biblioteca en dos mitades, ordeno cada mitad por separado, y luego las fusiono. Es como tener dos asistentes ordenando mitades y luego yo uniendo todo.
>
> **QuickSort:** Elijo un libro "pivote" y separo los libros en "menores que el pivote" y "mayores que el pivote". Repito el proceso con cada grupo. Pero si elijo mal el pivote (siempre el primero), y los libros ya están ordenados, el proceso se vuelve recursivo hasta colapsar.

### Ejemplo o analogía

Ordenar datos es como organizar una fila de personas por estatura. La **burbuja** sería hacer que cada persona compare su estatura con la del vecino y se cambien si están al revés. **Selección** sería buscar a la persona más baja de toda la fila y ponerla al principio. **Inserción** sería tomar a cada persona e insertarla en su lugar correcto entre las que ya están ordenadas.

La analogía deja de ser exacta porque en la vida real podemos "ver" toda la fila y tomar decisiones globales, mientras que los algoritmos solo pueden comparar dos elementos a la vez y seguir reglas estrictas.

## 5. El vacío que encontré

Al intentar explicar el tema, identifico el punto que aún no comprendía del todo al principio:

- **Mi duda concreta era:** ¿Por qué QuickSort, que es O(n log n) en promedio, puede colapsar con `StackOverflowError` si los datos están ordenados? Si la teoría dice que es eficiente, ¿por qué falla en la práctica?

- **Lo que ya podía explicar:** Que QuickSort divide el problema en subproblemas más pequeños usando un pivote.

- **Para resolver la duda consulté:** La guía de estudio que explica cómo la elección del pivote afecta el balance de las particiones, y ejecuté el Experimento 4 para ver el error en vivo.

- **Ahora lo entiendo así:** QuickSort con pivote fijo en el primer elemento asume que los datos están desordenados. Si los datos ya están ordenados (como los timestamps de los sensores), el pivote siempre será el menor elemento, generando una partición vacía a la izquierda y casi completa a la derecha. En lugar de dividir `n → n/2 + n/2`, obtenemos `n → 0 + (n-1)`, lo que genera una recursión de profundidad `n` en lugar de `log n`. Con 50.000 elementos, la pila de llamadas se desborda.

## 6. Trazado de la solución

La traza del algoritmo de QuickSort con datos ordenados, mostrando cómo el pivote fijo genera particiones desbalanceadas:

Con **mediana de tres** (TODO 2 resuelto), el pivote sería el valor intermedio entre el primero, el medio y el último, generando particiones balanceadas.

## 7. Decisiones de diseño

Relaciono lo aprendido con la Plataforma de Monitoreo Ambiental Urbano.

### Decision 5: Pivote de QuickSort
- **Alternativas:** A) pivote fijo en el primer elemento B) pivote aleatorio C) mediana de tres (primero, medio, último).
- **Elegida:** C) mediana de tres.
- **Justificación:** el experimento 4 demuestra que QuickSort con pivote fijo en el primer elemento produce `StackOverflowError` con datos ordenados cronológicamente (como llegan de la red de sensores). La mediana de tres garantiza que el pivote no sea ni el mínimo ni el máximo del subarreglo, evitando particiones extremadamente desbalanceadas. Es determinista (no depende de aleatoriedad) y tiene costo O(1). Con esta corrección, QuickSort mantiene complejidad O(n log n) incluso con datos ordenados.

### Decision 6: Efecto colateral del ordenamiento sobre la búsqueda binaria
- **Alternativas:** A) trabajar sobre una copia del arreglo B) restaurar el orden por timestamp después del ranking C) mantener índices separados para cada criterio.
- **Elegida:** A) trabajar sobre una copia del arreglo.
- **Justificación:** el experimento 5 demuestra que ordenar el arreglo por PM2.5 para generar un ranking destruye el orden por timestamp, rompiendo la precondición de la búsqueda binaria. Trabajar sobre una copia es la solución más simple y segura: preserva el orden original sin costo adicional de reordenamiento. El costo de memoria es aceptable para el tamaño de datos de la plataforma. La búsqueda binaria por timestamp sigue funcionando después de generar rankings por PM2.5.

### Decision 7: Elección de algoritmo según el estado inicial de los datos
- **Alternativas:** A) usar siempre MergeSort/HeapSort B) usar Inserción para datos casi ordenados C) usar el mismo algoritmo sin importar el estado.
- **Elegida:** B) usar Inserción para datos casi ordenados, MergeSort/HeapSort para datos desordenados grandes.
- **Justificación:** los experimentos 2 y 3 muestran que Inserción es excelente con datos ordenados o casi ordenados (~9.999 comparaciones para 10.000 elementos), pero su crecimiento es O(n²) con datos desordenados (~2.500 millones de comparaciones para 100.000 elementos). MergeSort y HeapSort mantienen crecimiento O(n log n) (~1.5 millones de comparaciones para 100.000 elementos). La decisión debe basarse en cómo llegan los datos: si llegan cronológicamente (como los sensores), Inserción es eficiente; si están desordenados, se requiere un algoritmo avanzado.

### Decision 8: Costo de comparaciones vs intercambios
- **Alternativas:** A) minimizar comparaciones B) minimizar intercambios C) balancear ambos.
- **Elegida:** C) balancear ambos según el costo de cada operación.
- **Justificación:** el experimento 1 muestra que Selección hace ~50 millones de comparaciones pero solo ~10.000 intercambios, mientras Burbuja hace ~50 millones de comparaciones y ~25 millones de intercambios. Si los registros son grandes (como `LecturaSensor` con timestamp, id, temperatura, humedad, PM2.5), moverlos en memoria puede ser más costoso que compararlos. En ese escenario, Selección puede ser preferible a Burbuja aunque ambos sean O(n²). La eficiencia no se mide solo por comparaciones, sino por el costo real de cada operación.

## 8. Aporte al proyecto

- **Archivo(s) o módulo(s) trabajado(s):** `src/Ordenador.java`, `src/BancoDeOrdenamiento.java`, `src/IngestaSensores.java`, `docs/decisiones.md`.
- **Cambio realizado:** Implementé los 6 algoritmos de ordenamiento (Burbuja con corte temprano, Selección, Inserción, MergeSort, HeapSort, QuickSort con mediana de tres). Creé la clase `BancoDeOrdenamiento` con 5 experimentos que miden comparaciones, intercambios y tiempos reales. Integré los experimentos al `main` de `IngestaSensores`. Documenté 4 decisiones de diseño en `docs/decisiones.md`.
- **Cómo se conecta con la capa anterior:** Los algoritmos de ordenamiento de la Semana 4 permiten preparar los datos para la búsqueda binaria de la Semana 3. El efecto colateral demostrado en el Experimento 5 valida la importancia de las precondiciones aprendidas en la Semana 3.
- **Qué queda pendiente para la siguiente semana:** Implementar estructuras de datos avanzadas (árboles, tablas hash) para mejorar la eficiencia de las consultas sin depender del ordenamiento del arreglo.

## 9. Commits realizados

Registra los commits que muestran mi aporte individual.

| Commit | Mensaje | Qué demuestra |
| :--- | :--- | :--- |
| `[2d72664]` | `feat: agregar clase Ordenador con 6 algoritmos base (TODOs pendientes)` | La estructura base del módulo de ordenamiento con los 6 algoritmos instrumentados. |
| `[657c109]` | `feat: agregar BancoDeOrdenamiento con 5 experimentos (solo Exp 1 activo)` | El banco de pruebas para medir empíricamente el rendimiento de los algoritmos. |
| `[bbae614]` | `docs: registrar resultados del Experimento 1 (datos desordenados)` | Las mediciones iniciales de Burbuja, Selección e Inserción con 10.000 datos desordenados. |
| `[6cb35e8]` | `fix: agregar bandera de corte temprano en Burbuja (TODO 1 resuelto)` | La optimización de Burbuja para detectar datos ya ordenados, reduciendo comparaciones de ~50M a ~10K. |
| `[d7c81c0]` | `feat: descomentar Experimento 2 y verificar mejora de Burbuja con datos ordenados` | La validación de que Burbuja con bandera y Inserción son O(n) con datos ordenados. |
| `[d4eb109]` | `feat: descomentar Experimento 3 (crecimiento de algoritmos)` | La comparación de crecimiento entre Inserción O(n²) y MergeSort/HeapSort O(n log n). |
| `[27cc377]` | `docs: registrar StackOverflowError de QuickSort con datos ordenados (antes de fix)` | La evidencia del fallo de QuickSort con pivote fijo y datos ordenados cronológicamente. |
| `[275e553]` | `fix: implementar mediana de tres como pivote en QuickSort (TODO 2 resuelto)` | La corrección de QuickSort para manejar datos ordenados sin colapsar. |
| `[2c9a6e9]` | `docs: registrar efecto colateral de ordenar por PM2.5 (TODO 3)` | La demostración de que ordenar por PM2.5 rompe la búsqueda binaria por timestamp. |
| `[686d1d4]` | `docs: registrar decisiones DEC-05 a DEC-08 en decisiones.md (Semana 4)` | La documentación técnica de las decisiones de ingeniería sobre ordenamiento. |
| `[113f328]` | `fix: cambiar métodos de BancoDeOrdenamiento de private a public para integración` | La corrección de visibilidad para permitir la integración con IngestaSensores. |
| `[219f564]` | `feat: integrar experimentos de ordenamiento en IngestaSensores` | La integración final de los 5 experimentos en el punto de entrada único del proyecto. |

## 10. Reexplicación final

Después del taller, vuelvo a responder la pregunta de la semana en cinco líneas o menos:

> Ordenar tiene un costo que depende del algoritmo y del estado inicial de los datos: los algoritmos simples son O(n²) en el peor caso, pero Inserción puede ser O(n) si los datos ya están ordenados. Los algoritmos avanzados (MergeSort, HeapSort, QuickSort) mantienen O(n log n), pero QuickSort puede colapsar si elijo mal el pivote. Además, ordenar por un criterio (PM2.5) puede romper las precondiciones de otros algoritmos (búsqueda binaria por timestamp), por lo que debo trabajar sobre copias o mantener índices separados.

## 11. Reflexión individual

Respondo con honestidad:

1. **Lo que ahora puedo hacer y antes no podía:** Medir y comparar empíricamente el costo de 6 algoritmos de ordenamiento diferentes, pasando de la teoría abstracta de "O(n²) vs O(n log n)" a ver con mis propios ojos cómo Inserción hace 2.500 millones de comparaciones mientras MergeSort hace solo 1.5 millones para 100.000 datos.

2. **El error o supuesto que más me enseñó:** Asumir que un algoritmo "avanzado" como QuickSort siempre es mejor. El `StackOverflowError` con datos ordenados me enseñó que incluso los algoritmos más eficientes tienen puntos débiles específicos, y que conocer los datos de entrada es tan importante como conocer el algoritmo.

3. **La pregunta que me llevaría a la próxima clase:** ¿Cómo puedo mantener múltiples órdenes simultáneos (por timestamp y por PM2.5) sin duplicar los datos? ¿Qué estructuras de datos (árboles, índices) me permitirían consultar eficientemente por múltiples criterios sin reordenar el arreglo principal?

4. **Qué parte del trabajo fue realmente individual:** Todo el desarrollo de la semana: la implementación de los 6 algoritmos, la resolución de los TODOs (bandera en Burbuja, mediana de tres en QuickSort), la ejecución de los 5 experimentos, la documentación de las 4 decisiones de diseño, y la integración final en `IngestaSensores`.

## 12. Preguntas de reflexión (Guía de Estudio, Punto 22)

1. **¿Por qué Selección puede ser interesante aunque haga muchas comparaciones?**
   Porque realiza muy pocos intercambios (~10.000 para 10.000 elementos), mientras Burbuja hace ~25 millones. Si los registros son grandes (como `LecturaSensor` con múltiples campos), moverlos en memoria puede ser más costoso que compararlos. En ese escenario, Selección es más eficiente aunque compare más.

2. **¿Por qué Inserción puede ser excelente aunque sea O(n²)?**
   Porque su comportamiento mejora drásticamente cuando los datos están ordenados o casi ordenados, acercándose a O(n). Con 10.000 datos ordenados, hace solo 9.999 comparaciones, mientras Burbuja y Selección siguen haciendo ~50 millones. Para datos que llegan cronológicamente (como los sensores), Inserción es la mejor opción.

3. **¿Por qué un algoritmo O(n log n) resulta importante cuando crece n?**
   Porque su costo crece mucho más lentamente que el de un algoritmo O(n²). Al pasar de 10.000 a 100.000 elementos (multiplicar n por 10), Inserción multiplica sus comparaciones por ~100 (de 24M a 2.500M), mientras MergeSort solo las multiplica por ~12 (de 120K a 1.5M). Con un millón de datos, la diferencia sería abismal.

4. **¿Por qué QuickSort puede fallar con datos ordenados?**
   Porque una mala elección del pivote (siempre el primer elemento) produce particiones extremadamente desbalanceadas: un lado vacío y el otro con casi todos los elementos. En lugar de dividir `n → n/2 + n/2`, obtenemos `n → 0 + (n-1)`, generando una recursión de profundidad `n` que colapsa la pila con `StackOverflowError`.

5. **¿Por qué ordenar puede romper la búsqueda binaria?**
   Porque la búsqueda binaria necesita que los datos permanezcan ordenados según el mismo criterio de búsqueda. Si ordeno por PM2.5 para generar un ranking, destruyo el orden por timestamp, rompiendo la precondición de la búsqueda binaria. El algoritmo no lanza un error, simplemente devuelve resultados incorrectos silenciosamente.

## 44. Preguntas de pensamiento crítico

**Pregunta 1: Una empresa tiene un millón de registros y realiza únicamente cinco búsquedas durante todo el día. ¿Tiene sentido diseñar toda la estrategia de almacenamiento alrededor de una búsqueda binaria? ¿Qué otros costos o factores consideraría?**

No, no tiene sentido. Diseñar una estrategia de almacenamiento compleja solo para optimizar cinco búsquedas diarias es un caso clásico de sobre-ingeniería. La búsqueda binaria exige que los datos estén ordenados. Mantener un millón de registros ordenados tiene un costo altísimo: cada vez que llega un dato nuevo, insertarlo en la posición correcta de un arreglo implica mover (copiar) todos los elementos posteriores, lo cual es una operación O(n) muy costosa en tiempo y CPU.

Para solo cinco búsquedas al día, una búsqueda lineal (O(n)) tardaría unos milisegundos más, un costo imperceptible para el usuario. El factor crítico aquí no es la velocidad de lectura, sino el costo de mantenimiento y escritura. En este escenario, es más eficiente almacenar los datos en el orden en que llegan y pagar el pequeño costo de recorrer el arreglo 5 veces, que pagar el enorme costo de reordenar el millón de registros constantemente.

**Pregunta 2: Un algoritmo puede ser mucho más rápido que otro y, sin embargo, producir una respuesta incorrecta. ¿Por qué considero que la corrección debe analizarse antes que la eficiencia?**

Porque en ingeniería de software, la corrección es un requisito funcional (el sistema *debe* hacer lo que se espera), mientras que la eficiencia es un atributo de calidad (el sistema debe hacerlo *bien*). Un resultado rápido pero erróneo es, en la práctica, peor que un resultado lento pero correcto, porque genera una falsa sensación de seguridad.

En mi proyecto, el Experimento 5 demostró esto perfectamente: ordenar por PM2.5 es necesario para el ranking, pero si lo hago sobre el arreglo original, la búsqueda binaria por timestamp devuelve falsos negativos silenciosos. Si una plataforma ambiental usara ese enfoque, reportaría que "no hay lecturas en ese timestamp" cuando en realidad sí las hay, solo porque el algoritmo descartó la mitad correcta del arreglo. Primero se debe garantizar que la lógica resuelve el problema (corrección); solo cuando la solución es correcta, tiene sentido optimizarla (eficiencia).

**Pregunta 3: Imagina que una plataforma consulta constantemente por timestamp, pero ocasionalmente necesita consultar por PM2.5. ¿Qué consecuencias tendría organizar los datos pensando principalmente en uno de estos campos?**

Tendría consecuencias directas en el rendimiento y la arquitectura, obligándome a tomar decisiones de compromiso (trade-offs). Si organizo (ordeno) los datos por `timestamp` para que las consultas frecuentes sean rápidas con búsqueda binaria (O(log n)), automáticamente condeno las consultas por `PM2.5` a ser búsquedas lineales (O(n)), porque el arreglo no estará ordenado por ese campo.

La consecuencia funcional es que el sistema será muy responsivo para el caso de uso principal (reportes históricos por tiempo), pero se volverá lento cuando un analista quiera buscar picos de contaminación específicos. En un entorno real, si ambas consultas fueran frecuentes, no usaría un solo arreglo; crearía estructuras de datos paralelas o índices (como un árbol o tabla hash) para no sacrificar la eficiencia de ninguna de las dos operaciones.

**Pregunta 4: Supón que tienes un conjunto de datos perfectamente ordenado y alguien modifica algunos registros sin conservar el orden. ¿Qué riesgos aparecen si el sistema continúa utilizando búsqueda binaria sin verificar las condiciones de los datos?**

El riesgo principal es la **corrupción silenciosa de la información**. La búsqueda binaria no "verifica" si el arreglo está ordenado; asume ciegamente que lo está (su precondición). Si un registro se modifica y rompe el orden, el algoritmo tomará decisiones de descarte basadas en premisas falsas.

El riesgo no es que el programa falle o lance una excepción (como un `ArrayIndexOutOfBoundsException`), sino que devolverá un `-1` (no encontrado) para un dato que sí existe en el sistema. En una plataforma de monitoreo, esto es catastrófico: los datos están ahí, ocupando memoria, pero el sistema es incapaz de encontrarlos, lo que lleva a reportes ambientales incompletos o erróneos sin que nadie reciba una alerta de error.

**Pregunta 5: En ingeniería de software suele decirse: "Que funcione no significa que sea una buena solución." Relaciona esta afirmación con lo aprendido en las semanas 1, 2, 3 y 4 del proyecto. ¿Qué ha cambiado en la manera en que analizo una solución desde que comenzó el proyecto?**

Esta afirmación resume exactamente mi evolución durante el proyecto:

- **Semana 1:** "Que funcione" era leer el CSV e imprimirlo en consola. No era una buena solución porque los datos se perdían al cerrar el programa y no se validaban rangos físicos.
- **Semana 2:** "Que funcione" era guardar los datos en un arreglo. No era una buena solución porque el arreglo tenía un techo de 10 elementos (perdiendo el 95% de los datos) y la matriz de `double` distorsionaba los promedios con ceros fantasma.
- **Semana 3:** "Que funcione" era usar búsqueda binaria para todo. No era una buena solución porque, sin respetar las precondiciones, el algoritmo más rápido del mundo entrega mentiras.
- **Semana 4:** "Que funcione" era ordenar los datos con cualquier algoritmo. No era una buena solución porque ordenar por PM2.5 rompe la búsqueda binaria por timestamp, y porque elegir el algoritmo equivocado (QuickSort con pivote fijo) puede colapsar el sistema con `StackOverflowError`.

Lo que ha cambiado en mi análisis es que ya no me pregunto solo *"¿El código compila y corre sin errores?"*. Ahora me pregunto: *"¿Qué pasa si los datos crecen a un millón? ¿Qué pasa si el dato no existe? ¿Qué pasa si violo una regla oculta del algoritmo? ¿Qué consecuencias tiene esta operación sobre otros módulos del sistema?"*. He pasado de pensar en el "camino feliz" (happy path) a pensar en la robustez, la escalabilidad (Big O), la integridad de los estados del sistema y los efectos colaterales entre módulos.

## 46. Criterio de finalización

La Semana 4 está terminada cuando pueda responder afirmativamente:

- [x] ¿Mi proyecto tiene un único main?
- [x] ¿IngestaSensores sigue siendo el punto de entrada?
- [x] ¿Implementé los 6 algoritmos de ordenamiento?
- [x] ¿Resolví el TODO 1 (bandera de corte temprano en Burbuja)?
- [x] ¿Resolví el TODO 2 (mediana de tres en QuickSort)?
- [x] ¿Ejecuté los 5 experimentos del BancoDeOrdenamiento?
- [x] ¿Tengo mediciones propias de comparaciones, intercambios y tiempo?
- [x] ¿Calculé las razones de crecimiento (de 1.000 a 10.000 a 100.000)?
- [x] ¿Documenté las decisiones de diseño en docs/decisiones.md?
- [x] ¿Integré los experimentos en IngestaSensores sin crear otro main?
- [x] ¿Mi proyecto compila y ejecuta sin errores?
- [x] ¿Mis cambios están registrados en Git con ramas feature y fix?
- [x] ¿Creé el tag H1 para el hito?
- [x] ¿Puedo explicar por qué QuickSort falla con datos ordenados?
- [x] ¿Puedo explicar el efecto colateral del ordenamiento sobre la búsqueda binaria?
- [x] ¿Puedo justificar qué algoritmo elegir según el estado de los datos?

## Uso de Inteligencia Artificial

Durante el desarrollo de esta bitácora y del código asociado, utilicé **Qwen (asistente de inteligencia artificial)** como herramienta de apoyo para:

- Generar la estructura base de `Ordenador.java` y `BancoDeOrdenamiento.java` con los 6 algoritmos y 5 experimentos.
- Validar la lógica de la mediana de tres para el pivote de QuickSort y asegurar que evitara el `StackOverflowError`.
- Revisar la redacción y el formato de mi bitácora para asegurar que cumpliera con todos los requisitos de la guía de la Semana 04.
- Contrastar las explicaciones de complejidad O(n²) vs O(n log n) para asegurar que mi análisis empírico fuera preciso.
- Generar el formato de las decisiones de diseño (DEC-05 a DEC-08) siguiendo el patrón establecido en la Semana 03.

Revisé, adapté y comprendí todas las soluciones propuestas. El código final fue probado y ejecutado de forma independiente en IntelliJ IDEA, y los resultados de los 5 experimentos (especialmente las tablas de mediciones y el `StackOverflowError` de QuickSort) fueron verificados manualmente por mí.

## Lista de verificación antes de entregar

- [x] Escribí la predicción antes de consultar el resultado.
- [x] Incluí evidencia concreta del laboratorio (Experimentos 1 al 5).
- [x] Expliqué un concepto sin depender de jerga (analogía de la biblioteca).
- [x] Registré un vacío, una duda o un error real (el `StackOverflowError` de QuickSort y el error de visibilidad `private` vs `public`).
- [x] **Trace al menos un caso paso a paso (QuickSort con datos ordenados mostrando particiones desbalanceadas).**
- [x] Justifiqué 4 decisiones del proyecto con alternativas descartadas (pivote, efecto colateral, elección de algoritmo, comparaciones vs intercambios).
- [x] Registré mis 12 commits y mi aporte individual.
- [x] Dejé claro qué queda pendiente (estructuras de datos avanzadas para múltiples órdenes).
- [x] Renombré el archivo con el formato `S04-meridel-calderon.md`.
- [x] Respondí las 5 preguntas de reflexión de la guía de estudio.
- [x] Respondí las 5 preguntas de pensamiento crítico.
- [x] Verifiqué el criterio de finalización (17/17).