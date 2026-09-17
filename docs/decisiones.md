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

## S2 - Donde viven los datos - 2026-09-16

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
