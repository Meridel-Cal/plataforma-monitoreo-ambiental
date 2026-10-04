# Decisiones de Diseno - Bitacora Tecnica

Formato: cada entrada con fecha, decision, alternativas consideradas, justificacion.

## S1 - De codigo fragil a confiable - 2026-08-26

### Decision 1: Crear clase LecturaSensor
- **Alternativas:** mantener 5 variables sueltas vs crear clase.
- **Elegida:** clase con atributos privados, constantes de rangos.
- **Justificacion:** encapsulamiento (numeral 1.2), reutilizable 12 semanas, firma de metodos pasa de 5 params a 1. Evita error de orden de parametros.

### Decision 2: Estrategia ante fila invalida
- **Alternativas:** A) descartar solo campo malo B) descartar fila completa.
- **Elegida:** B) fila completa.
- **Justificacion:** principio conservador para reporte oficial. Si un canal falla (-999), no confiamos en sincronia de los otros dos. Preferimos perdida de datos a contaminacion silenciosa. Registrado en descartes.csv para auditoria.

### Decision 3: Manejo de excepciones
- **Alternativas:** catch generico Exception vs especificos.
- **Elegida:** catch especifico NumberFormatException y ArrayIndexOutOfBoundsException + validacion fisica.
- **Justificacion:** catch vacio o generico esconde causa. Necesitamos trazabilidad: cuantos, por que motivo.

### Decision 4: Constantes vs numeros magicos
- **Elegida:** TEMP_MIN=-40, TEMP_MAX=60, HUM_MIN=0, HUM_MAX=100, PM_MIN=0, CODIGO_DESCONECTADO=-999
- **Justificacion:** si cambian umbrales de la norma ambiental, se cambia en un solo lugar.

## S2 - Donde viven los datos - 2026-09-14

### Decision 1: Estrategia de crecimiento del repositorio
- **Alternativas:** A) arreglo fijo grande (ej. 500) B) crecimiento de uno en uno C) duplicación de capacidad.
- **Elegida:** C) duplicación.
- **Justificacion:** arreglo fijo no escala (8000 estaciones = reescribir codigo). Crecer de uno en uno genera ~20.000 copias para 201 datos. Duplicación: solo 5 redimensionamientos y 310 copias. Complejidad amortizada O(1) por inserción. Medido empíricamente con contadores.

### Decision 2: Estrategia para eliminar()
- **Alternativas:** A) compactar (mover elementos) B) marcar hueco con null y arreglo paralelo boolean[].
- **Elegida:** A) compactación.
- **Justificacion:** mantiene arreglo contiguo, simplifica recorridos y búsqueda secuencial. Marcado requiere estructura adicional y todos los métodos deben manejar huecos. Costo de mover elementos es aceptable para 201 lecturas. Se anula última posición para evitar referencia duplicada.

### Decision 3: Representación de ausencia en matriz
- **Alternativas:** A) matriz paralela boolean[][] B) valor centinela -1 C) Double[][] con null.
- **Elegida:** C) Double[][] con null.
- **Justificacion:** matriz paralela duplica memoria. Centinela -1 puede colisionar con valores legítimos futuros. Double permite null nativo, diferencia "no reportó" de "midió 0.0". Requiere validar null antes de operaciones aritméticas para evitar NullPointerException.

### Decision 4: Cálculo de promedios horarios
- **Alternativas:** A) dividir siempre entre 9 estaciones B) dividir entre estaciones que reportaron.
- **Elegida:** B) dividir entre estaciones que reportaron.
- **Justificacion:** EST-003 no reportó horas 09-12. Dividir entre 9 incluye ceros fantasma, baja promedios artificialmente (9.93 vs 11.18 correcto). Se cuenta estacionesQueReportaron en bucle. Misma lógica para promedioDeEstacion(): dividir entre horasQueReportaron, no 24.

### Decision 5: Validación de posiciones en operaciones
- **Alternativas:** A) asumir posición válida B) validar rangos antes de acceder.
- **Elegida:** B) validar rangos.
- **Justificacion:** obtener(), actualizar(), eliminar() verifican posicion >= 0 && posicion < cantidad. Previene ArrayIndexOutOfBoundsException y estados inconsistentes. Retorna null o no opera si posición inválida. Encapsulamiento protege estado interno.

## S3 - Algoritmos de busqueda y complejidad - 2026-09-21

### Decision 1: Algoritmo de busqueda por timestamp
- **Alternativas:** A) busqueda lineal O(n) B) busqueda binaria O(log n).
- **Elegida:** B) busqueda binaria.
- **Justificacion:** `GeneradorDatos` produce timestamps en orden cronologico ascendente, cumpliendo la precondicion de la busqueda binaria. Reduce comparaciones de O(n) a O(log n). Para 100,000 lecturas: lineal ~100,000 comparaciones vs binaria ~17 comparaciones.

### Decision 2: Tratamiento de precondiciones en algoritmos
- **Alternativas:** A) aplicar busqueda binaria a cualquier campo B) respetar precondicion de ordenamiento.
- **Elegida:** B) respetar precondicion.
- **Justificacion:** el experimento 4 demuestra que aplicar busqueda binaria sobre PM2.5 (no ordenado) produce resultados incorrectos silenciosamente. La busqueda lineal encuentra el 100% de los valores, la binaria falla. Las precondiciones no son opcionales: son requisitos criticos para la correctitud del algoritmo.

### Decision 3: Medicion empirica de complejidad
- **Alternativas:** A) confiar en teoria asintotica B) medir comparaciones reales con contador estatico.
- **Elegida:** B) medicion empirica.
- **Justificacion:** la variable estatica `comparaciones` en `BuscadorLecturas` permite cuantificar el costo real de cada algoritmo. Los experimentos 1, 2, 3 y 4 validan empiricamente la teoria: lineal crece proporcional a n, binaria crece logaritmico, y el peor caso (dato inexistente) evidencia la diferencia abismal.

### Decision 4: Pregunta pendiente - ordenamiento previo
- **Pregunta:** ¿Conviene ordenar los datos antes de realizar las busquedas?
- **Estado:** pendiente de analisis.
- **Justificacion:** ordenar tiene costo O(n log n). Si se realizan multiples busquedas, el costo de ordenar se amortiza. Si es una sola busqueda, la lineal puede ser mas eficiente. Esta decision se retomara en la Semana 4 al evaluar estructuras de datos ordenadas.

## S4 - Algoritmos de ordenamiento y efecto colateral - 2026-09-28

### Decision 5: Pivote de QuickSort
- **Alternativas:** A) pivote fijo en el primer elemento B) pivote aleatorio C) mediana de tres (primero, medio, último).
- **Elegida:** C) mediana de tres.
- **Justificacion:** el experimento 4 demuestra que QuickSort con pivote fijo en el primer elemento produce `StackOverflowError` con datos ordenados cronologicamente (como llegan de la red de sensores). La mediana de tres garantiza que el pivote no sea ni el minimo ni el maximo del subarreglo, evitando particiones extremadamente desbalanceadas. Es determinista (no depende de aleatoriedad) y tiene costo O(1). Con esta correccion, QuickSort mantiene complejidad O(n log n) incluso con datos ordenados.

### Decision 6: Efecto colateral del ordenamiento sobre la busqueda binaria
- **Alternativas:** A) trabajar sobre una copia del arreglo B) restaurar el orden por timestamp despues del ranking C) mantener indices separados para cada criterio.
- **Elegida:** A) trabajar sobre una copia del arreglo.
- **Justificacion:** el experimento 5 demuestra que ordenar el arreglo por PM2.5 para generar un ranking destruye el orden por timestamp, rompiendo la precondicion de la busqueda binaria. Trabajar sobre una copia es la solucion mas simple y segura: preserva el orden original sin costo adicional de reordenamiento. El costo de memoria es aceptable para el tamaño de datos de la plataforma. La busqueda binaria por timestamp sigue funcionando despues de generar rankings por PM2.5.

### Decision 7: Eleccion de algoritmo segun el estado inicial de los datos
- **Alternativas:** A) usar siempre MergeSort/HeapSort B) usar Insercion para datos casi ordenados C) usar el mismo algoritmo sin importar el estado.
- **Elegida:** B) usar Insercion para datos casi ordenados, MergeSort/HeapSort para datos desordenados grandes.
- **Justificacion:** los experimentos 2 y 3 muestran que Insercion es excelente con datos ordenados o casi ordenados (~9.999 comparaciones para 10.000 elementos), pero su crecimiento es O(n²) con datos desordenados (~2.500 millones de comparaciones para 100.000 elementos). MergeSort y HeapSort mantienen crecimiento O(n log n) (~1.5 millones de comparaciones para 100.000 elementos). La decision debe basarse en como llegan los datos: si llegan cronologicamente (como los sensores), Insercion es eficiente; si estan desordenados, se requiere un algoritmo avanzado.

### Decision 8: Costo de comparaciones vs intercambios
- **Alternativas:** A) minimizar comparaciones B) minimizar intercambios C) balancear ambos.
- **Elegida:** C) balancear ambos segun el costo de cada operacion.
- **Justificacion:** el experimento 1 muestra que Seleccion hace ~50 millones de comparaciones pero solo ~10.000 intercambios, mientras Burbuja hace ~50 millones de comparaciones y ~25 millones de intercambios. Si los registros son grandes (como `LecturaSensor` con timestamp, id, temperatura, humedad, PM2.5), moverlos en memoria puede ser mas costoso que compararlos. En ese escenario, Seleccion puede ser preferible a Burbuja aunque ambos sean O(n²). La eficiencia no se mide solo por comparaciones, sino por el costo real de cada operacion.