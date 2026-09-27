# Bitácora individual - Semana 03

## 1. Datos de la actividad

- **Estudiante:** Meridel Calderon
- **Desarrollo:** Individual
- **Semana:** 03
- **Fecha del laboratorio:** 2026-09-21
- **Fecha del taller:** 2026-09-24
- **Tema principal:** Algoritmos de búsqueda (lineal y binaria), precondiciones, medición empírica de complejidad (comparaciones y tiempo real) y trazado de algoritmos.
- **Pregunta de la semana:** ¿Cómo encuentro un dato específico entre miles de lecturas sin recorrer todo el arreglo, y qué consecuencias tiene ignorar las reglas (precondiciones) del algoritmo?

## 2. Predicción antes de ejecutar

Antes de abrir o ejecutar el programa, respondo:

1. **¿Qué creo que va a ocurrir?**
   Predigo que la búsqueda lineal aumentará sus comparaciones de forma directamente proporcional al tamaño del arreglo (O(n)), mientras que la búsqueda binaria mantendrá un número de comparaciones muy bajo y estable (O(log n)), siempre que busque por `timestamp`, ya que el `GeneradorDatos` garantiza que este campo esté ordenado.

2. **¿Qué parte del programa o del algoritmo puede fallar?**
   Sospecho que el Experimento 4 (búsqueda binaria por PM2.5) fallará silenciosamente. Como el generador de datos no ordena el arreglo por el campo `pm25`, la búsqueda binaria violará su precondición. Esto hará que el algoritmo descarte mitades del arreglo incorrectamente, devolviendo `-1` (no encontrado) para datos que sí existen en el arreglo.

3. **¿Cómo comprobaré mi predicción?**
   Ejecutaré el `main` de `IngestaSensores` y observaré la salida del Experimento 4. Si la búsqueda lineal encuentra los 20 valores de prueba, pero la búsqueda binaria encuentra solo unos pocos (o ninguno), se confirmará que ignorar la precondición de ordenamiento rompe la lógica del algoritmo.

## 3. Evidencia del laboratorio

### Resultado observado
Al ejecutar los experimentos, la búsqueda binaria por `timestamp` demostró ser abismalmente más eficiente: para 100,000 datos, la lineal requirió 100,000 comparaciones, mientras que la binaria requirió solo 17. En el Experimento 4, al buscar valores de PM2.5 en un arreglo no ordenado, la búsqueda lineal tuvo 20/20 aciertos, mientras que la binaria solo tuvo 2/20 aciertos, validando que el algoritmo "se perdió" al tomar decisiones basadas en un orden que no existía.

### Diferencia entre la predicción y el resultado
Las predicciones fueron exactas. La diferencia de rendimiento entre O(n) y O(log n) fue evidente en la tabla de mediciones. El fallo silencioso de la búsqueda binaria en el Experimento 4 ocurrió tal como anticipé, sirviendo como una demostración práctica de por qué las precondiciones no son sugerencias, sino requisitos obligatorios.

### Error o comportamiento inesperado
- **¿Qué ocurrió?** En una primera versión del método `busquedaBinariaPorTimestamp`, el ciclo `while` usaba la condición `inicio < fin`. Al probar con un arreglo pequeño y buscar el último elemento, el algoritmo retornaba `-1` (no encontrado) aunque el dato existía.
- **¿Por qué ocurrió?** Cuando el rango de búsqueda se reduce a un solo elemento, `inicio` y `fin` son iguales (ej. `inicio = 2`, `fin = 2`). La condición `2 < 2` es falsa, por lo que el ciclo terminaba sin evaluar ese último elemento restante.
- **¿Cómo lo corregí?** Cambié la condición del ciclo a `while (inicio <= fin)`. Esto garantiza que, cuando el rango colapsa a un solo elemento, este sea evaluado (`medio` será igual a `inicio` y `fin`) antes de descartarlo.

## 4. Explicación en lenguaje llano

> Imagina que busco una palabra en un diccionario. No empiezo en la página 1 y leo una por una (búsqueda lineal). Abro el libro por la mitad. Si la palabra que busco empieza por "M" y abrí en la "S", sé que debo buscar en la primera mitad y descarto la segunda por completo. Repito este proceso de dividir a la mitad hasta encontrarla. Pero este truco solo funciona si el diccionario está ordenado alfabéticamente; si las páginas estuvieran revueltas, este método me llevaría a la página equivocada y nunca la encontraría.

### Ejemplo o analogía
La búsqueda binaria es como jugar "Adivina el número" (estoy pensando en un número del 1 al 100). En lugar de adivinar 1, 2, 3..., siempre digo "50". Si me dicen "es mayor", descarto instantáneamente 50 números y mi siguiente apuesta es 75. La analogía deja de ser exacta porque en la vida real a veces "trampo" y adivino por intuición, mientras que el algoritmo es estrictamente matemático y siempre divide el rango exacto a la mitad.

## 5. El vacío que encontré

Al intentar explicar el tema, identifico el punto que aún no comprendía del todo al principio:

- **Mi duda concreta era:** ¿Por qué la búsqueda binaria a veces hace 17 comparaciones y a veces 16 para 100,000 datos, si la fórmula teórica dice que es log₂(n)?
- **Lo que ya podía explicar:** Que log₂(100,000) es aproximadamente 16.6, por lo que el número de comparaciones es un entero que varía.
- **Para resolver la duda consulté:** La traza manual del algoritmo (ver archivo `traza_busqueda_binaria.md`) y la explicación de la IA sobre el "mejor caso" vs. "peor caso".
- **Ahora lo entiendo así:** La complejidad O(log n) describe el *peor caso* (cuando el elemento está en los extremos o no existe). El *mejor caso* ocurre cuando el elemento está justo en el medio del arreglo desde el primer intento, lo que toma solo **1 comparación**. Por eso el contador varía ligeramente dependiendo de la posición exacta del dato.

## 6. Trazado de la solución (Requisito 39)

La traza completa del algoritmo de búsqueda binaria, realizada **antes de corregir el ciclo**, se encuentra en el archivo separado:

📄 **`bitacoras/traza_busqueda_binaria.md`**

Esta traza documenta el comportamiento del algoritmo con la condición errónea `while (inicio < fin)`, demostrando paso a paso por qué el último elemento del arreglo nunca era evaluado. La evidencia de esta traza fue clave para identificar y corregir el bug mencionado en la sección 3.

## 7. Decisión de diseño

Relaciono lo aprendido con la Plataforma de Monitoreo Ambiental Urbano.

- **Problema que debía resolver:** Necesitaba consultar lecturas específicas en grandes volúmenes de datos sin que el tiempo de respuesta creciera linealmente, pero debía garantizar la precisión de los resultados.
- **Estructura, algoritmo o estrategia elegida:** Búsqueda binaria para consultas por `timestamp` (aprovechando que el generador los crea ordenados) y búsqueda lineal para campos sin orden garantizado (como `pm25`).
- **Alternativa descartada:** Aplicar búsqueda binaria universalmente a todos los campos para "ahorrar tiempo".
- **Por qué elegí la primera:** Porque las precondiciones son innegociables. El Experimento 4 demostró empíricamente que usar binaria en datos desordenados produce falsos negativos silenciosos. Es preferible una búsqueda lineal lenta pero correcta, que una binaria rápida pero errónea.
- **Qué evidencia respalda la decisión:** La salida del Experimento 4, donde la búsqueda lineal acertó 20/20 veces en datos de PM2.5, mientras que la binaria falló en 18 de 20 intentos por la falta de ordenamiento.

## 8. Aporte al proyecto

- **Archivo(s) o módulo(s) trabajado(s):** `src/BuscadorLecturas.java`, `src/BancoDePruebas.java` y `src/IngestaSensores.java`.
- **Cambio realizado:** Implementé los métodos `busquedaLinealPorTimestamp`, `busquedaBinariaPorTimestamp` y `busquedaBinariaPorPm25`. Creé la clase `BancoDePruebas` con 6 experimentos que miden comparaciones y tiempos reales, y los integré al `main` de `IngestaSensores`.
- **Cómo se conecta con la capa anterior:** El repositorio de la Semana 2 ahora puede ser consultado eficientemente. La ingesta de datos (Semana 1) ahora no solo carga y analiza, sino que valida la capacidad de búsqueda del sistema.
- **Qué queda pendiente para la siguiente semana:** Implementar un algoritmo de ordenamiento (ej. Merge Sort o Quick Sort) para poder aplicar la búsqueda binaria de manera segura sobre campos como `pm25` o `idSensor`.

## 9. Commits realizados

Registra los commits que muestran mi aporte individual.

| Commit | Mensaje | Qué demuestra |
| :--- | :--- | :--- |
| `[6b2dd6f]` | `test: agregar experimento 6 con tabla de mediciones de tiempo y comparaciones` | La medición empírica de rendimiento con tiempos reales en milisegundos para los tamaños 1.000, 100.000 y 1.000.000. |
| `[7144ec3]` | `test: agregar experimento 5 con casos de prueba mínimos para búsqueda binaria` | La validación rigurosa de los casos límite (primer elemento, intermedio, último, existente, inexistente) en arreglos pequeños y grandes. |
| `[abde466]` | `docs: actualizar bitácora técnica con decisiones de diseño de la Semana 3` | La documentación técnica de las decisiones de ingeniería sobre algoritmos de búsqueda y precondiciones. |
| `[9931f95]` | `feat: integrar Experimento 4 (binaria sobre datos no ordenados) en BancoDePruebas y main` | La demostración empírica de que la búsqueda binaria falla silenciosamente cuando se viola la precondición de ordenamiento. |
| `[6ea4b96]` | `feat: integrar Experimento 3 (dato inexistente) en BancoDePruebas y main` | La comparación del peor caso: búsqueda lineal recorre todo el arreglo (100.000 comparaciones) vs binaria (~17 comparaciones). |
| `[9110404]` | `feat: integrar Experimento 2 (lineal vs binaria) en BancoDePruebas y main` | La comparación directa de ambos algoritmos mostrando la relación de eficiencia a diferentes tamaños de datos. |
| `[9b83d11]` | `feat: implementar búsqueda binaria por timestamp en BuscadorLecturas` | La implementación del algoritmo O(log n) con contador de comparaciones y precondición de ordenamiento. |
| `[79f7e18]` | `feat: agregar búsqueda lineal por ID de estación con contador de comparaciones` | La extensión del buscador para consultar por campo `idSensor` manteniendo la métrica de comparaciones. |
| `[1a9ed50]` | `feat: integrar BancoDePruebas y ejecutar experimento de búsqueda en IngestaSensores` | La orquestación de las pruebas empíricas dentro del flujo principal del programa. |
| `[95474cf]` | `feat: implementar GeneradorDatos para crear lecturas sintéticas reproducibles` | La fuente de datos ordenados con semilla fija, necesaria para validar correctamente la búsqueda binaria. |
| `[2901301]` | `feat: implementar clase BuscadorLecturas con búsqueda lineal por timestamp` | La base del algoritmo de búsqueda y el contador estático de comparaciones para medición empírica. |

## 10. Reexplicación final

Después del taller, vuelvo a responder la pregunta de la semana en cinco líneas o menos:

> Encuentro datos eficientemente usando búsqueda binaria, que descarta mitades del arreglo en cada paso, reduciendo el costo de O(n) a O(log n). Sin embargo, este algoritmo exige estrictamente que los datos estén ordenados (precondición); si ignoro esta regla, el algoritmo falla silenciosamente, por lo que debo usar búsqueda lineal en campos desordenados hasta que implemente un método de ordenamiento.

## 11. Reflexión individual

Respondo con honestidad:

1. **Lo que ahora puedo hacer y antes no podía:** Medir y demostrar empíricamente el costo computacional de un algoritmo, pasando de la teoría abstracta de "O(log n)" a ver con mis propios ojos que 100,000 datos se resuelven en 17 pasos.
2. **El error o supuesto que más me enseñó:** Asumir que un algoritmo "más avanzado" (como la binaria) siempre es mejor. La traza manual me enseñó que un algoritmo avanzado aplicado en el contexto equivocado (datos desordenados) es peor que uno simple y correcto.
3. **La pregunta que me llevaría a la próxima clase:** ¿Cuál es el punto de equilibrio exacto? (¿A partir de qué cantidad de datos vale la pena el costo de *ordenar* el arreglo para luego usar búsqueda binaria, en lugar de simplemente usar búsqueda lineal una sola vez?).
4. **Qué parte del trabajo fue realmente individual:** Todo el desarrollo de la semana: el trazado manual del algoritmo para detectar el bug del ciclo `while`, la redacción de los experimentos en `BancoDePruebas` y la integración final en el `main`.

## 12. Preguntas específicas de la guía (Adaptadas a Semana 3)

1. **Explica la diferencia entre O(n) y O(log n) usando los números de tu Experimento 6.**
   En mi tabla, para 1,000,000 de datos, la búsqueda lineal requirió 1,000,000 de comparaciones (O(n), crece igual que los datos). La búsqueda binaria requirió solo 20 comparaciones (O(log n), crece de forma logarítmica). La binaria es 50,000 veces más eficiente en este caso.

2. **¿Qué es una precondición y por qué el Experimento 4 es crucial para entenderla?**
   Una precondición es un requisito que debe cumplirse *antes* de ejecutar un algoritmo para que este funcione correctamente. El Experimento 4 es crucial porque demuestra que si aplico búsqueda binaria a PM2.5 (que no está ordenado), el algoritmo no lanza un error, sino que devuelve resultados falsos silenciosamente, lo cual es mucho más peligroso en un sistema de monitoreo ambiental.

3. **¿Por qué mido "comparaciones" además de "tiempo en milisegundos"?**
   El tiempo en milisegundos depende de la velocidad de mi computadora, de otros programas abiertos o del calentamiento de la JVM. El número de comparaciones es una métrica absoluta y matemática que refleja la eficiencia real del algoritmo, independiente del hardware.

4. **En el Experimento 3 (dato inexistente), ¿por qué la búsqueda lineal hace 100,000 comparaciones y la binaria solo 17?**
   Porque la lineal debe recorrer cada elemento uno por uno hasta el final para confirmar que el dato no está. La binaria, en cambio, descarta la mitad del arreglo en cada paso; en 17 pasos, ya ha descartado matemáticamente las 100,000 posiciones posibles.

5. **Tu método `buscarPorEstacion()` es lineal. Si la ciudad crece a 8,000 estaciones, ¿qué harías para mejorarlo?**
   Primero, implementaría un algoritmo de ordenamiento (como planeo en la Semana 4) para ordenar el repositorio por `idSensor`. Una vez ordenado, reemplazaría la llamada a búsqueda lineal por una búsqueda binaria adaptada al ID, reduciendo las comparaciones de 8,000 a aproximadamente 13.

## 44. Preguntas de pensamiento crítico

**Pregunta 1: Una empresa tiene un millón de registros y realiza únicamente cinco búsquedas durante todo el día. ¿Tiene sentido diseñar toda la estrategia de almacenamiento alrededor de una búsqueda binaria? ¿Qué otros costos o factores consideraría?**

No, no tiene sentido. Diseñar una estrategia de almacenamiento compleja solo para optimizar cinco búsquedas diarias es un caso clásico de sobre-ingeniería. La búsqueda binaria exige que los datos estén ordenados. Mantener un millón de registros ordenados tiene un costo altísimo: cada vez que llega un dato nuevo, insertarlo en la posición correcta de un arreglo implica mover (copiar) todos los elementos posteriores, lo cual es una operación O(n) muy costosa en tiempo y CPU.
Para solo cinco búsquedas al día, una búsqueda lineal (O(n)) tardaría unos milisegundos más, un costo imperceptible para el usuario. El factor crítico aquí no es la velocidad de lectura, sino el costo de mantenimiento y escritura. En este escenario, es más eficiente almacenar los datos en el orden en que llegan y pagar el pequeño costo de recorrer el arreglo 5 veces, que pagar el enorme costo de reordenar el millón de registros constantemente.

**Pregunta 2: Un algoritmo puede ser mucho más rápido que otro y, sin embargo, producir una respuesta incorrecta. ¿Por qué considero que la corrección debe analizarse antes que la eficiencia?**

Porque en ingeniería de software, la corrección es un requisito funcional (el sistema *debe* hacer lo que se espera), mientras que la eficiencia es un atributo de calidad (el sistema debe hacerlo *bien*). Un resultado rápido pero erróneo es, en la práctica, peor que un resultado lento pero correcto, porque genera una falsa sensación de seguridad.
En mi proyecto, el Experimento 4 demostró esto perfectamente: la búsqueda binaria sobre PM2.5 es rapidísima (O(log n)), pero devuelve falsos negativos silenciosos porque ignora su precondición. Si una plataforma ambiental usara ese algoritmo, reportaría que "no hay contaminación" cuando en realidad sí la hay, solo porque el algoritmo descartó la mitad correcta del arreglo. Primero se debe garantizar que la lógica resuelve el problema (corrección); solo cuando la solución es correcta, tiene sentido optimizarla (eficiencia).

**Pregunta 3: Imagina que una plataforma consulta constantemente por timestamp, pero ocasionalmente necesita consultar por PM2.5. ¿Qué consecuencias tendría organizar los datos pensando principalmente en uno de estos campos?**

Tendría consecuencias directas en el rendimiento y la arquitectura, obligándome a tomar decisiones de compromiso (trade-offs). Si organizo (ordeno) los datos por `timestamp` para que las consultas frecuentes sean rápidas con búsqueda binaria (O(log n)), automáticamente condeno las consultas por `PM2.5` a ser búsquedas lineales (O(n)), porque el arreglo no estará ordenado por ese campo.
La consecuencia funcional es que el sistema será muy responsivo para el caso de uso principal (reportes históricos por tiempo), pero se volverá lento cuando un analista quiera buscar picos de contaminación específicos. En un entorno real, si ambas consultas fueran frecuentes, no usaría un solo arreglo; crearía estructuras de datos paralelas o índices (como un árbol o tabla hash) para no sacrificar la eficiencia de ninguna de las dos operaciones.

**Pregunta 4: Supón que tienes un conjunto de datos perfectamente ordenado y alguien modifica algunos registros sin conservar el orden. ¿Qué riesgos aparecen si el sistema continúa utilizando búsqueda binaria sin verificar las condiciones de los datos?**

El riesgo principal es la **corrupción silenciosa de la información**. La búsqueda binaria no "verifica" si el arreglo está ordenado; asume ciegamente que lo está (su precondición). Si un registro se modifica y rompe el orden, el algoritmo tomará decisiones de descarte basadas en premisas falsas.
El riesgo no es que el programa falle o lance una excepción (como un `ArrayIndexOutOfBoundsException`), sino que devolverá un `-1` (no encontrado) para un dato que sí existe en el sistema. En una plataforma de monitoreo, esto es catastrófico: los datos están ahí, ocupando memoria, pero el sistema es incapaz de encontrarlos, lo que lleva a reportes ambientales incompletos o erróneos sin que nadie reciba una alerta de error.

**Pregunta 5: En ingeniería de software suele decirse: "Que funcione no significa que sea una buena solución." Relaciona esta afirmación con lo aprendido en las semanas 1, 2 y 3 del proyecto. ¿Qué ha cambiado en la manera en que analizo una solución desde que comenzó el proyecto?**

Esta afirmación resume exactamente mi evolución durante el proyecto:
*   **Semana 1:** "Que funcione" era leer el CSV e imprimirlo en consola. No era una buena solución porque los datos se perdían al cerrar el programa y no se validaban rangos físicos.
*   **Semana 2:** "Que funcione" era guardar los datos en un arreglo. No era una buena solución porque el arreglo tenía un techo de 10 elementos (perdiendo el 95% de los datos) y la matriz de `double` distorsionaba los promedios con ceros fantasma.
*   **Semana 3:** "Que funcione" era usar búsqueda binaria para todo. No era una buena solución porque, sin respetar las precondiciones, el algoritmo más rápido del mundo entrega mentiras.

Lo que ha cambiado en mi análisis es que ya no me pregunto solo *"¿El código compila y corre sin errores?"*. Ahora me pregunto: *"¿Qué pasa si los datos crecen a un millón? ¿Qué pasa si el dato no existe? ¿Qué pasa si violo una regla oculta del algoritmo?"*. He pasado de pensar en el "camino feliz" (happy path) a pensar en la robustez, la escalabilidad (Big O) y la integridad de los estados del sistema.

## 46. Criterio de finalización

La Semana 3 está terminada cuando pueda responder afirmativamente:

- [x] ¿Mi proyecto tiene un único main?
- [x] ¿IngestaSensores sigue siendo el punto de entrada?
- [x] ¿Puedo buscar linealmente por timestamp?
- [x] ¿Puedo buscar binariamente por timestamp?
- [x] ¿Sé explicar por qué la binaria requiere orden?
- [x] ¿Sé explicar O(n) y O(log n)?
- [x] ¿Sé explicar el bug del ciclo while que corregí (`inicio <= fin`)?
- [x] ¿Sé explicar por qué String se compara con `equals()`?
- [x] ¿Puedo demostrar el problema de PM2.5?
- [x] ¿Tengo mediciones propias?
- [x] ¿Tengo la traza de la búsqueda binaria? (Ver archivo `traza_busqueda_binaria.md`)
- [x] ¿Documenté la decisión de diseño?
- [x] ¿Mi proyecto compila?
- [x] ¿Mis cambios están registrados en Git?
- [x] ¿Puedo explicar qué agregué a la aplicación sin decir que construí "otro programa"?

## Uso de Inteligencia Artificial

Durante el desarrollo de esta bitácora y del código asociado, utilicé **Qwen (asistente de inteligencia artificial)** como herramienta de apoyo para:

- Generar la estructura de la tabla de casos de prueba mínimos (Experimento 5) y asegurar que cubriera todos los escenarios límite.
- Validar la lógica de mi traza manual del algoritmo de búsqueda binaria para identificar correctamente el error de la condición `inicio < fin`.
- Revisar la redacción y el formato de mi bitácora para asegurar que cumpliera con todos los requisitos de la guía de la Semana 03.
- Contrastar la explicación de complejidad O(log n) para asegurar que mi explicación en "lenguaje llano" fuera precisa.

Revisé, adapté y comprendí todas las soluciones propuestas. El código final fue probado y ejecutado de forma independiente en IntelliJ IDEA, y los resultados de la consola (especialmente la tabla de mediciones) fueron verificados manualmente por mí.

## Lista de verificación antes de entregar

- [x] Escribí la predicción antes de consultar el resultado.
- [x] Incluí evidencia concreta del laboratorio (Experimentos 1 al 6).
- [x] Expliqué un concepto sin depender de jerga (analogía del diccionario).
- [x] Registré un vacío, una duda o un error real (el bug de `inicio < fin`).
- [x] **Trace al menos un caso paso a paso (Requisito 39: traza de búsqueda binaria antes de corregir, en archivo separado).**
- [x] Justifiqué una decisión del proyecto y una alternativa descartada (no usar binaria en PM2.5).
- [x] Registré mis commits y mi aporte individual.
- [x] Dejé claro qué queda pendiente (algoritmos de ordenamiento).
- [x] Renombré el archivo con el formato `s03-meridel-calderon.md`.
- [x] Respondí las 5 preguntas obligatorias adaptadas a la guía de la Semana 03.