# Bitácora individual - Semana 03

## 1. Datos de la actividad

- **Estudiante:** Meridel Calderon
- **Equipo:** Code Trinity 
- **Semana:** 03
- **Fecha del laboratorio:** 2026-09-21
- **Fecha del taller:** 2026-09-24
- **Tema principal:** Algoritmos de búsqueda (lineal y binaria), precondiciones, medición empírica de complejidad (comparaciones y tiempo real) y trazado de algoritmos.
- **Pregunta de la semana:** ¿Cómo encontramos un dato específico entre miles de lecturas sin recorrer todo el arreglo, y qué consecuencias tiene ignorar las reglas (precondiciones) del algoritmo?

## 2. Predicción antes de ejecutar

Antes de abrir o ejecutar el programa, respondo:

1. **¿Qué creo que va a ocurrir?**
   Predigo que la búsqueda lineal aumentará sus comparaciones de forma directamente proporcional al tamaño del arreglo (O(n)), mientras que la búsqueda binaria mantendrá un número de comparaciones muy bajo y estable (O(log n)), siempre que se busque por `timestamp`, ya que el `GeneradorDatos` garantiza que este campo esté ordenado.

2. **¿Qué parte del programa o del algoritmo puede fallar?**
   Sospecho que el Experimento 4 (búsqueda binaria por PM2.5) fallará silenciosamente. Como el generador de datos no ordena el arreglo por el campo `pm25`, la búsqueda binaria violará su precondición. Esto hará que el algoritmo descarte mitades del arreglo incorrectamente, devolviendo `-1` (no encontrado) para datos que sí existen en el arreglo.

3. **¿Cómo comprobaré mi predicción?**
   Ejecutaré el `main` de `IngestaSensores` y observaré la salida del Experimento 4. Si la búsqueda lineal encuentra los 20 valores de prueba, pero la búsqueda binaria encuentra solo unos pocos (o ninguno), se confirmará que ignorar la precondición de ordenamiento rompe la lógica del algoritmo.

## 3. Evidencia del laboratorio

### Resultado observado
Al ejecutar los experimentos, la búsqueda binaria por `timestamp` demostró ser abismalmente más eficiente: para 100,000 datos, la lineal requirió 100,000 comparaciones, mientras que la binaria requirió solo 17. En el Experimento 4, al buscar valores de PM2.5 en un arreglo no ordenado, la búsqueda lineal tuvo 20/20 aciertos, mientras que la binaria solo tuvo 2/20 aciertos, validando que el algoritmo "se perdió" al tomar decisiones basadas en un orden que no existía.

### Diferencia entre la predicción y el resultado
Las predicciones fueron exactas. La diferencia de rendimiento entre O(n) y O(log n) fue evidente en la tabla de mediciones. El fallo silencioso de la búsqueda binaria en el Experimento 4 ocurrió tal como se anticipó, sirviendo como una demostración práctica de por qué las precondiciones no son sugerencias, sino requisitos obligatorios.

### Error o comportamiento inesperado
- **¿Qué ocurrió?** En una primera versión del método `busquedaBinariaPorTimestamp`, el ciclo `while` usaba la condición `inicio < fin`. Al probar con un arreglo pequeño y buscar el último elemento, el algoritmo retornaba `-1` (no encontrado) aunque el dato existía.
- **¿Por qué ocurrió?** Cuando el rango de búsqueda se reduce a un solo elemento, `inicio` y `fin` son iguales (ej. `inicio = 2`, `fin = 2`). La condición `2 < 2` es falsa, por lo que el ciclo terminaba sin evaluar ese último elemento restante.
- **¿Cómo lo corregí?** Cambié la condición del ciclo a `while (inicio <= fin)`. Esto garantiza que, cuando el rango colapsa a un solo elemento, este sea evaluado (`medio` será igual a `inicio` y `fin`) antes de descartarlo.

## 4. Explicación en lenguaje llano

> Imagina que buscas una palabra en un diccionario. No empiezas en la página 1 y lees una por una (búsqueda lineal). Abres el libro por la mitad. Si la palabra que buscas empieza por "M" y abriste en la "S", sabes que debes buscar en la primera mitad y descartas la segunda por completo. Repites este proceso de dividir a la mitad hasta encontrarla. Pero este truco solo funciona si el diccionario está ordenado alfabéticamente; si las páginas estuvieran revueltas, este método te llevaría a la página equivocada y nunca la encontrarías.

### Ejemplo o analogía
La búsqueda binaria es como jugar "Adivina el número" (estoy pensando en un número del 1 al 100). En lugar de adivinar 1, 2, 3..., siempre dices "50". Si te dicen "es mayor", descartas instantáneamente 50 números y tu siguiente apuesta es 75. La analogía deja de ser exacta porque en la vida real a veces "trampas" y adivinas por intuición, mientras que el algoritmo es estrictamente matemático y siempre divide el rango exacto a la mitad.

## 5. El vacío que encontré

Al intentar explicar el tema, identifico el punto que aún no comprendía del todo al principio:

- **Mi duda concreta era:** ¿Por qué la búsqueda binaria a veces hace 17 comparaciones y a veces 16 para 100,000 datos, si la fórmula teórica dice que es log₂(n)?
- **Lo que ya podía explicar:** Que log₂(100,000) es aproximadamente 16.6, por lo que el número de comparaciones es un entero que varía.
- **Para resolver la duda consulté:** La traza manual del algoritmo y la explicación de la IA sobre el "mejor caso" vs. "peor caso".
- **Ahora lo entiendo así:** La complejidad O(log n) describe el *peor caso* (cuando el elemento está en los extremos o no existe). El *mejor caso* ocurre cuando el elemento está justo en el medio del arreglo desde el primer intento, lo que toma solo **1 comparación**. Por eso el contador varía ligeramente dependiendo de la posición exacta del dato.

## 6. Trazado de la solución (Requisito 39)

A continuación, presento la traza manual que realicé **antes de corregir el ciclo** del algoritmo, la cual me permitió descubrir el error mencionado en la sección 3.

**Escenario:** Arreglo pequeño de 3 elementos ordenados por timestamp: `["0000000010", "0000000020", "0000000030"]`.
**Objetivo:** Buscar `"0000000030"` (el último elemento).
**Ciclo con error:** `while (inicio < fin)`

| Paso | inicio | fin | medio | Valor en medio | Comparación | Acción |
| :--- | :---: | :---: | :---: | :--- | :--- | :--- |
| 1 | 0 | 2 | 1 | `"0000000020"` | `"0000000020"` < `"0000000030"` | `inicio = medio + 1` → `inicio = 2` |
| 2 | 2 | 2 | - | - | **Condición `2 < 2` es FALSA** | El ciclo termina. Retorna `-1`. |

**Conclusión de la traza:** El algoritmo falló porque nunca evaluó la posición 2.
**Corrección aplicada:** Cambiar a `while (inicio <= fin)`. En el paso 2, la condición `2 <= 2` sería verdadera, `medio` sería 2, se compararía `"0000000030" == "0000000030"`, y retornaría el índice 2 exitosamente.

## 7. Decisión de diseño

Relaciono lo aprendido con la Plataforma de Monitoreo Ambiental Urbano.

- **Problema que debíamos resolver:** Necesitábamos consultar lecturas específicas en grandes volúmenes de datos sin que el tiempo de respuesta creciera linealmente, pero debíamos garantizar la precisión de los resultados.
- **Estructura, algoritmo o estrategia elegida:** Búsqueda binaria para consultas por `timestamp` (aprovechando que el generador los crea ordenados) y búsqueda lineal para campos sin orden garantizado (como `pm25`).
- **Alternativa descartada:** Aplicar búsqueda binaria universalmente a todos los campos para "ahorrar tiempo".
- **Por qué elegimos la primera:** Porque las precondiciones son innegociables. El Experimento 4 demostró empíricamente que usar binaria en datos desordenados produce falsos negativos silenciosos. Es preferible una búsqueda lineal lenta pero correcta, que una binaria rápida pero errónea.
- **Qué evidencia respalda la decisión:** La salida del Experimento 4, donde la búsqueda lineal acertó 20/20 veces en datos de PM2.5, mientras que la binaria falló en 18 de 20 intentos por la falta de ordenamiento.

## 8. Aporte al proyecto

- **Archivo(s) o módulo(s) trabajado(s):** `src/BuscadorLecturas.java`, `src/BancoDePruebas.java` y `src/IngestaSensores.java`.
- **Cambio realizado:** Se implementaron los métodos `busquedaLinealPorTimestamp`, `busquedaBinariaPorTimestamp` y `busquedaBinariaPorPm25`. Se creó la clase `BancoDePruebas` con 6 experimentos que miden comparaciones y tiempos reales, y se integraron al `main` de `IngestaSensores`.
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

> Encontramos datos eficientemente usando búsqueda binaria, que descarta mitades del arreglo en cada paso, reduciendo el costo de O(n) a O(log n). Sin embargo, este algoritmo exige estrictamente que los datos estén ordenados (precondición); si se ignora esta regla, el algoritmo falla silenciosamente, por lo que debemos usar búsqueda lineal en campos desordenados hasta que implementemos un método de ordenamiento.

## 11. Reflexión individual

Respondo con honestidad:

1. **Lo que ahora se puede hacer y antes no se podía:** Medir y demostrar empíricamente el costo computacional de un algoritmo, pasando de la teoría abstracta de "O(log n)" a ver con mis propios ojos que 100,000 datos se resuelven en 17 pasos.
2. **El error o supuesto que más enseñó:** Asumir que un algoritmo "más avanzado" (como la binaria) siempre es mejor. La traza manual me enseñó que un algoritmo avanzado aplicado en el contexto equivocado (datos desordenados) es peor que uno simple y correcto.
3. **La pregunta que me llevaría a la próxima clase:** ¿Cuál es el punto de equilibrio exacto? (¿A partir de qué cantidad de datos vale la pena el costo de *ordenar* el arreglo para luego usar búsqueda binaria, en lugar de simplemente usar búsqueda lineal una sola vez?).
4. **Qué parte del trabajo fue realmente individual:** El trazado manual del algoritmo para detectar el bug del ciclo `while`, la redacción de los experimentos en `BancoDePruebas` y la integración final en el `main`.

## 12. Preguntas específicas de la guía (Adaptadas a Semana 3)

1. **Explica la diferencia entre O(n) y O(log n) usando los números de tu Experimento 6.**
   En mi tabla, para 1,000,000 de datos, la búsqueda lineal requirió 1,000,000 de comparaciones (O(n), crece igual que los datos). La búsqueda binaria requirió solo 20 comparaciones (O(log n), crece de forma logarítmica). La binaria es 50,000 veces más eficiente en este caso.

2. **¿Qué es una precondición y por qué el Experimento 4 es crucial para entenderla?**
   Una precondición es un requisito que debe cumplirse *antes* de ejecutar un algoritmo para que este funcione correctamente. El Experimento 4 es crucial porque demuestra que si aplicamos búsqueda binaria a PM2.5 (que no está ordenado), el algoritmo no lanza un error, sino que devuelve resultados falsos silenciosamente, lo cual es mucho más peligroso en un sistema de monitoreo ambiental.

3. **¿Por qué medimos "comparaciones" además de "tiempo en milisegundos"?**
   El tiempo en milisegundos depende de la velocidad de mi computadora, de otros programas abiertos o del calentamiento de la JVM. El número de comparaciones es una métrica absoluta y matemática que refleja la eficiencia real del algoritmo, independiente del hardware.

4. **En el Experimento 3 (dato inexistente), ¿por qué la búsqueda lineal hace 100,000 comparaciones y la binaria solo 17?**
   Porque la lineal debe recorrer cada elemento uno por uno hasta el final para confirmar que el dato no está. La binaria, en cambio, descarta la mitad del arreglo en cada paso; en 17 pasos, ya ha descartado matemáticamente las 100,000 posiciones posibles.

5. **Tu método `buscarPorEstacion()` es lineal. Si la ciudad crece a 8,000 estaciones, ¿qué harías para mejorarlo?**
   Primero, implementaría un algoritmo de ordenamiento (como se planea en la Semana 4) para ordenar el repositorio por `idSensor`. Una vez ordenado, reemplazaría la llamada a búsqueda lineal por una búsqueda binaria adaptada al ID, reduciendo las comparaciones de 8,000 a aproximadamente 13.

## Uso de Inteligencia Artificial

Durante el desarrollo de esta bitácora y del código asociado, utilicé **Qwen (asistente de inteligencia artificial)** como herramienta de apoyo para:

- Generar la estructura de la tabla de casos de prueba mínimos (Experimento 5) y asegurar que cubriera todos los escenarios límite.
- Validar la lógica de la traza manual del algoritmo de búsqueda binaria para identificar correctamente el error de la condición `inicio < fin`.
- Revisar la redacción y el formato de la bitácora para asegurar que cumpliera con todos los requisitos de la guía de la Semana 03.
- Contrastar la explicación de complejidad O(log n) para asegurar que mi explicación en "lenguaje llano" fuera precisa.

Revisé, adapté y comprendí todas las soluciones propuestas. El código final fue probado y ejecutado de forma independiente en IntelliJ IDEA, y los resultados de la consola (especialmente la tabla de mediciones) fueron verificados manualmente.

## Lista de verificación antes de entregar

- [x] Escribí la predicción antes de consultar el resultado.
- [x] Incluí evidencia concreta del laboratorio (Experimentos 1 al 6).
- [x] Expliqué un concepto sin depender de jerga (analogía del diccionario).
- [x] Registré un vacío, una duda o un error real (el bug de `inicio < fin`).
- [x] **Trace al menos un caso paso a paso (Requisito 39: traza de búsqueda binaria antes de corregir).**
- [x] Justifiqué una decisión del proyecto y una alternativa descartada (no usar binaria en PM2.5).
- [x] Registré mis commits y mi aporte individual.
- [x] Dejé claro qué queda pendiente (algoritmos de ordenamiento).
- [x] Renombré el archivo con el formato `s03-meridel-calderon.md`.
- [x] Respondí las 5 preguntas obligatorias adaptadas a la guía de la Semana 03.