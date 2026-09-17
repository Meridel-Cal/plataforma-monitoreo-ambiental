# Bitacora grupal - Semana 02

## 1. Datos de la actividad

- **Estudiantes:** Meridel Calderon - Julian Davila - Alejandro Maldonado
- **Equipo:** Code Trinity
- **Semana:** 02
- **Fecha del laboratorio:** 2026-09-14
- **Fecha del taller:** 2026-09-17
- **Tema principal:** Estructuras de datos, Tipo Abstracto de Dato (TAD), arreglos unidimensionales, redimensionamiento dinámico y matrices para el análisis de datos ambientales.
- **Pregunta de la semana:** Los datos ya llegan limpios. ¿Dónde viven ahora y qué podemos preguntarles?

## 2. Prediccion antes de ejecutar

Antes de abrir o ejecutar el programa, responde:

1. **Que creo que va a ocurrir?**
   Se predice que el programa leerá el archivo `lecturas_ampliadas.csv` (211 filas), descartará las lecturas con problemas de formato o rango, y almacenará aproximadamente 201 lecturas válidas en el repositorio. Además, generará un perfil horario de PM2.5 con 24 promedios (uno por hora del día) que reflejen la contaminación real de la ciudad.

2. **Que parte del programa o del algoritmo puede fallar?**
   El método `agregar()` del `RepositorioLecturas` tiene una capacidad inicial de 10 elementos. Se sospecha que cuando llegue la lectura número 11, el método devolverá `false` y las 191 lecturas restantes se perderán silenciosamente sin generar ninguna excepción. También se prevé que la matriz `double[][]` inicializará todas sus celdas en `0.0`, lo que hará que las horas en las que EST-003 no reportó datos (09:00 a 12:00) se interpreten erróneamente como mediciones de cero, bajando artificialmente los promedios horarios.

3. **Como comprobare mi prediccion?**
   Se ejecutará el método `main` de `IngestaSensores` y se observará la línea "Lecturas almacenadas". Si muestra 10 en lugar de 201, se confirmará el problema del techo. Adicionalmente, se compararán los promedios de las horas 09 a 12 con los de las horas 08 y 13 (donde las 9 estaciones sí reportan) para detectar si los valores están artificialmente bajos por el efecto del cero fantasma.

## 3. Evidencia del laboratorio

### Resultado observado

Al ejecutar el programa utilizando como entrada el archivo `lecturas_ampliadas.csv` (211 filas), la corrida inicial mostró que el repositorio solo almacenaba 10 lecturas (descartando 2 por formato y 8 por rango) y que los promedios de PM2.5 para las horas 09 a 12 eran artificialmente bajos (entre 9.93 y 10.80). Tras implementar el redimensionamiento dinámico y corregir la matriz con `Double[][]`, la ejecución final almacenó exitosamente las 201 lecturas válidas, registró 5 redimensionamientos y 310 copias, y los promedios horarios de las horas 09 a 12 se ajustaron a valores reales y coherentes (11.18, 12.15, 11.34 y 11.83), eliminando el efecto del cero fantasma.

### Diferencia entre la prediccion y el resultado

Las coincidencias entre la predicción y el resultado inicial fueron exactas: el repositorio efectivamente limitó el almacenamiento a 10 lecturas perdiendo el resto de forma silenciosa, y los promedios de las horas 09 a 12 resultaron artificialmente bajos (entre 9 y 10) debido a que la matriz primitiva interpretó la ausencia de datos de la estación EST-003 como ceros. No se encontraron diferencias inesperadas en el diagnóstico, ya que el comportamiento del programa validó ambas hipótesis planteadas. Tras aplicar las correcciones estructurales (redimensionamiento por duplicación y uso de la clase envolvente `Double`), el resultado final coincidió plenamente con los valores teóricos esperados, almacenando las 201 lecturas válidas y corrigiendo los promedios horarios a un rango coherente de 11 a 12, lo que confirma que la causa raíz de los fallos fue correctamente identificada y resuelta.

### Error o comportamiento inesperado

- **Que ocurrio?** Al ejecutar el programa por primera vez, la aplicación falló inmediatamente lanzando una excepción `java.io.FileNotFoundException: lecturas_ampliadas.csv (El sistema no puede encontrar el archivo especificado)`, lo que detuvo la ejecución antes de que se pudiera procesar o almacenar cualquier dato.
- **Por que ocurrio?** El archivo CSV estaba ubicado físicamente dentro de la carpeta `data/` en la estructura del proyecto, pero la constante `ARCHIVO` en el código apuntaba únicamente al nombre del archivo sin especificar la subcarpeta. Java busca los archivos desde el directorio raíz de trabajo del proyecto, por lo que no pudo localizarlo al no encontrar la ruta relativa correcta.
- **Como lo corregimos?** Se modificó la constante en el archivo `IngestaSensores.java` para incluir la ruta relativa exacta: `private static final String ARCHIVO = "data/lecturas_ampliadas.csv";`. Con este ajuste, el programa localizó el archivo correctamente y continuó con el flujo sin interrupciones.

## 4. Explicacion en lenguaje llano

Explica el concepto principal como se lo explicarias a una persona de doce años. Usa entre tres y cinco lineas y evita palabras tecnicas que no expliques.

> Imagina que tienes una caja con 10 espacios para guardar juguetes; cuando se llena, en lugar de botar los nuevos, la cambias por una caja el doble de grande y pasas todos los que ya tenías. Además, si un día no recibes ningún juguete, no debes pensar que te regalaron "cero" juguetes, sino simplemente que no llegó nada, porque no recibir nada es muy diferente a recibir un regalo vacío.

### Ejemplo o analogia

El repositorio es como un álbum de fotos: cuando se llena, compras uno el doble de grande y pegas las fotos viejas en lugar de tirar las nuevas. La matriz es como un boletín de notas: si un alumno falta, la casilla queda en blanco (`null`), pero si saca cero, se escribe "0" (`0.0`). La analogía deja de ser exacta porque mudar el álbum en la vida real toma horas, mientras que en la computadora es instantáneo, aunque igual consume tiempo de procesamiento.

## 5. El vacio que encontre

Al intentar explicar el tema, identifica el punto que aun no comprendes bien.

- **Mi duda concreta es:** ¿Por qué duplicar la capacidad (10 → 20 → 40 → 80) es mucho más eficiente que aumentar de uno en uno (10 → 11 → 12 → 13), si al final ambos arreglos llegan exactamente al mismo tamaño?
- **Lo que ya puedo explicar es:** Que cada vez que se redimensiona hay que copiar todos los elementos existentes al nuevo arreglo, y eso tiene un costo en tiempo de ejecución que se puede medir con un contador.
- **Para resolver la duda consulte:** La guía de la semana (secciones 19 y 20 sobre el experimento de medición), los contadores de mi propio código (`redimensionamientos` y `copiasRealizadas`) y asistente de IA para contrastar el concepto de "costo amortizado".
- **Ahora lo entiendo asi:** Aumentar de uno en uno obliga a copiar casi todo el arreglo en cada nueva inserción, generando miles de copias innecesarias (casi 20.000 para 201 datos). Duplicar, en cambio, hace muy pocos saltos (solo 5 veces en nuestro caso) y el total de copias es mínimo (310). La diferencia de esfuerzo es abismal, aunque el tamaño final sea el mismo.

## 6. Trazado de la solucion

Escoge una ejecucion, recorrido o caso representativo y trazalo paso a paso. Incluye los valores importantes despues de cada paso.

| Paso | Estado de los datos o estructura | Decision o resultado |
|---|---|---|
| 1 | `cantidad = 10`, `lecturas.length = 10` (arreglo lleno) | Se invoca `agregar(lectura11)` desde el ciclo de lectura del CSV. |
| 2 | Se evalúa `cantidad == lecturas.length` (10 == 10) | La condición es `true`. Se invoca el método `redimensionar()`. |
| 3 | Se crea `nuevoArreglo = new LecturaSensor[20]` | La capacidad se duplica a 20. El contador `redimensionamientos` pasa a 1. |
| 4 | Bucle `for` copia los índices 0 a 9 al nuevo arreglo | Se copian 10 elementos. El contador `copiasRealizadas` pasa a 10. |
| 5 | `this.lecturas = nuevoArreglo` | La referencia interna apunta al nuevo arreglo. El arreglo viejo se descarta. |
| 6 | `lecturas[10] = lectura11`, `cantidad = 11` | La lectura 11 se almacena exitosamente en la posición 10. El método retorna `true`. |

## 7. Decision de diseño

Relaciona lo aprendido con la Plataforma de Monitoreo Ambiental Urbano.

- **Problema que debiamos resolver:** El repositorio perdía lecturas válidas al alcanzar su capacidad fija de 10 elementos, y la matriz confundía "ausencia de dato" con "valor cero", distorsionando los promedios de contaminación de la ciudad (especialmente en el caso de EST-003).
- **Estructura, algoritmo o estrategia elegida:** Repositorio con redimensionamiento dinámico por duplicación de capacidad y eliminación por compactación. Matriz de `Double[][]` (objeto) en lugar de `double[][]` (primitivo) para representar la ausencia de datos con `null`.
- **Alternativa descartada:** Arreglo de tamaño fijo grande (no escala), matriz paralela `boolean[][]` (duplica memoria), valor centinela `-1` (puede colisionar con datos reales) o eliminación dejando huecos sin compactar (complica los recorridos).
- **Por que elegimos la primera:** La duplicación ofrece un costo amortizado óptimo O(1) por inserción. La compactación mantiene el arreglo contiguo y simple de recorrer. Usar `Double` es la forma idiomática en Java para representar ausencia sin inventar números mágicos.
- **Que evidencia respalda la decision:** Se realizaron solo 5 redimensionamientos y 310 copias para almacenar 201 lecturas (en lugar de ~20.000 copias si creciéramos de uno en uno). Además, los promedios de las horas 09-12 subieron de ~10 a ~11-12, validando que la exclusión del "cero fantasma" corrigió los cálculos.

## 8. Aporte al proyecto

- **Archivo(s) o modulo(s) trabajado(s):** `src/RepositorioLecturas.java`, `src/AnalizadorMatriz.java` y `src/IngestaSensores.java`.
- **Cambio realizado:** Se completó el TAD `RepositorioLecturas` implementando redimensionamiento dinámico por duplicación, búsqueda, actualización y eliminación por compactación. En `AnalizadorMatriz`, se reemplazó `double[][]` por `Double[][]` para distinguir ausencia de cero y se implementaron los métodos de análisis (`promedioDeEstacion` y `horaMasContaminada`).
- **Como se conecta con la capa anterior:** El módulo de `IngestaSensores` (Semana 1) ahora alimenta las lecturas validadas al repositorio y a la matriz en lugar de solo imprimirlas en consola, otorgando memoria persistente y capacidad de análisis al sistema.
- **Que queda pendiente para la siguiente semana:** Optimizar la búsqueda secuencial de `buscarPorEstacion()` (actualmente O(n)) mediante estructuras más eficientes (como búsqueda binaria o tablas hash) para soportar el escalamiento del sistema a miles de estaciones.

## 9. Commits realizados

Registra los commits que muestran tu aporte individual.

| Commit | Mensaje                                                                               | Que demuestra |
|---|---------------------------------------------------------------------------------------|---|
| `[5fe2809]` | `Agregar contrato formal del TAD RepositorioLecturas (Fase 0)`                        | El documento del contrato TAD con operaciones públicas, privadas, invariantes y complejidad computacional. |
| `[799d165]` | `Actualizar bitácora S02 con hashes reales y preguntas de la guia`                    | La actualización final de la bitácora con los commits correctos del repositorio. |
| `[2a3973b]` | `Agregar decisiones de diseño de la Semana 02 (TAD, redimensionamiento, matriz)`      | La documentación técnica de las decisiones de ingeniería tomadas durante la semana. |
| `[b1792f8]` | `Se agrega bitácora grupal de la Semana 02`                                           | La reflexión individual y grupal sobre el aprendizaje, evidencias y análisis del laboratorio. |
| `[129268b]` | `Agregar métricas de redimensionamiento al main para evidencia del experimento`       | La implementación del experimento de medición de la Fase 2.1 (5 redimensionamientos, 310 copias). |
| `[5dce540]` | `Resolver cero fantasma en AnalizadorMatriz usando Double y corrigiendo promedios`    | La distinción entre ausencia y valor cero en la matriz estación × hora. |
| `[447a23a]` | `Completar TAD RepositorioLecturas con redimensionamiento, compactación y contadores` | La solución al problema del techo con medición empírica de copias. |
| `[76c4307]` | `Initial commit`                                                                      | La estructura inicial del proyecto con la ingesta de la Semana 1. |

## 10. Reexplicacion final

Despues del taller, vuelve a responder la pregunta de la semana en cinco lineas o menos. Esta respuesta debe ser mas precisa que la de la seccion 4 y debe incluir la razon de tu decision tecnica.

> Los datos confiables ahora viven en dos estructuras: un repositorio con arreglo dinámico que crece por duplicación para no perder lecturas, y una matriz `Double[][]` de estación por hora. Gracias a usar `null` en lugar de ceros primitivos, podemos preguntar con precisión por promedios horarios y de estación sin que los huecos de información (como los de EST-003) distorsionen los resultados de contaminación de la ciudad.

## 11. Reflexion individual

Responde con honestidad:

1. **Lo que ahora se puede hacer y antes no se podia:**
   Diseñar estructuras que gestionen su propio crecimiento y representen la ausencia de datos, entendiendo el costo computacional de cada decisión.
2. **El error o supuesto que mas enseñó:**
   Asumir que un `0.0` significa "cero medido". Se aprendió que la ausencia de dato (`null`) es distinta al valor cero y confundirlos altera los promedios reales.
3. **La pregunta que se llevaría a la proxima clase:**
   Con 8.000 estaciones, ¿qué estructura permite buscar un dato específico en menos de O(n) sin recorrer todo el arreglo?
4. **Qué parte del trabajo fue realmente del equipo:**
   El diagnóstico del "techo" de 10 lecturas, la elección de la compactación para `eliminar()` y la justificación del uso de `Double` para resolver el cero fantasma.

## 12. Preguntas específicas de la guía (Sección 54)

1. **Explica qué es un TAD sin utilizar las palabras: abstracto, interfaz, implementación. (Máximo 5 líneas)**
   Un Tipo Abstracto de Dato es un contrato que define qué operaciones puede realizar una estructura de datos y qué resultados debe devolver, sin revelar cómo está construida por dentro. Es como un control remoto: sabes qué botones presionar para cambiar el canal, pero no necesitas saber cómo funciona el televisor internamente para usarlo.

2. **¿Cuántas copias realizó tu repositorio al cargar las 211 filas utilizando crecimiento de uno en uno? ¿Cuántas realizó utilizando duplicación? ¿Qué concluyes?**
   Con crecimiento de uno en uno, el repositorio habría realizado aproximadamente 20.000 copias. Con la estrategia de duplicación, solo realizó 310 copias y 5 redimensionamientos. Concluyo que duplicar la capacidad es drásticamente más eficiente, ya que reduce el costo computacional de reubicar los datos en un 98%, demostrando la ventaja del costo amortizado.

3. **¿Qué estrategia elegiste para eliminar()? Compactar o Marcar. Justifica tu decisión.**
   Elegimos la estrategia de **Compactar**. Justificación: Al mover los elementos hacia la izquierda y reducir el contador `cantidad`, mantenemos el arreglo contiguo y sin huecos. Esto simplifica enormemente los recorridos futuros y la búsqueda secuencial, evitando tener que verificar un arreglo paralelo de booleanos o lidiar con posiciones `null` intermedias que complicarían la lógica de todo el sistema.

4. **¿Cómo resolviste el problema del cero fantasma? Explica por qué descartaste las otras alternativas.**
   Resolvimos el problema cambiando la matriz de `double[][]` a `Double[][]`, permitiendo que las celdas sin datos sean `null` en lugar de `0.0`. Descartamos usar un valor centinela (como `-1`) porque podría confundirse con una medición real en el futuro. También descartamos una matriz paralela de `boolean[][]` porque duplicaría innecesariamente el consumo de memoria del sistema.

5. **Tu método `buscarPorEstacion()` recorre el arreglo completo en el peor caso. Si mañana la ciudad tuviera 8.000 estaciones, ¿seguiría siendo una solución adecuada? Responde con un número, no únicamente con una opinión.**
   No, no sería una solución adecuada. Con 8.000 estaciones, en el peor de los casos (cuando la estación buscada es la última o no existe), el método realizaría exactamente **8.000 comparaciones** (complejidad O(n)). Este número de operaciones por cada búsqueda haría que el sistema sea demasiado lento para consultas en tiempo real, por lo que se requeriría una estructura como una tabla hash o un árbol de búsqueda.

## Uso de Inteligencia Artificial

Durante el desarrollo de esta bitácora y del código asociado, el equipo utilizó **Qwen (asistente de inteligencia artificial)** como herramienta de apoyo para:

- Contrastar y validar la comprensión del concepto de TAD (diferencia entre contrato e implementación).
- Revisar la lógica del redimensionamiento por duplicación y el cálculo amortizado de copias.
- Apoyar la redacción y organización de la bitácora en tercera persona.
- Verificar la corrección de los métodos `promedioDeHora`, `promedioDeEstacion` y `horaMasContaminada`.

El equipo revisó, adaptó y comprendió todas las soluciones propuestas, asegurando que el contenido refleje el aprendizaje real adquirido durante la semana. El código final fue probado y ejecutado de forma independiente en IntelliJ IDEA, y los resultados de la consola fueron verificados manualmente.

## Lista de verificacion antes de entregar

- [x] Escribi la prediccion antes de consultar el resultado.
- [x] Inclui evidencia concreta del laboratorio.
- [x] Explique un concepto sin depender de jerga.
- [x] Registre un vacio, una duda o un error real.
- [x] Trace al menos un caso paso a paso.
- [x] Justifique una decision del proyecto y una alternativa descartada.
- [x] Registre mis commits y mi aporte individual.
- [x] Deje claro que queda pendiente.
- [x] Renombre el archivo con el formato `s02-nombre.md`.
- [x] Respondi las 5 preguntas obligatorias de la Sección 54 de la guía.
