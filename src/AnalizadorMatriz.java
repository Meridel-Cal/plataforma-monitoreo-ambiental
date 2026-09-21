/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   AnalizadorMatriz - VERSION 0.1 INCOMPLETA

   Una matriz de 9 estaciones x 24 horas para responder
   preguntas como: a que hora del dia se contamina mas la ciudad,
   y cual estacion sostiene los peores niveles.

   filas    = estaciones (0..8  ->  EST-001..EST-009)
   columnas = horas      (0..23)
   ============================================================ */

public class AnalizadorMatriz {

    private static final int NUM_ESTACIONES = 9;
    private static final int NUM_HORAS = 24;

    // Usamos Double (objeto) en lugar de double (primitivo) para permitir null
    private Double[][] pm25PorEstacionHora;

    //Constructor que se llama igual que la clase
    public AnalizadorMatriz() {
        this.pm25PorEstacionHora = new Double[NUM_ESTACIONES][NUM_HORAS];
    }

    //LLega EST - 001 y sale unicamente el numero; -1 porque el arreglo inicia en 0
    private int indiceDeEstacion(String idSensor) {
        String numero = idSensor.substring(4);
        return Integer.parseInt(numero) - 1;
    }

    //Registra en fila el ID del sensor, en columna la hora y lo añade al Double como lectura.getPm25
    public void registrar(LecturaSensor lectura) {
        int fila = indiceDeEstacion(lectura.getIdSensor());
        int columna = lectura.getHora();
        pm25PorEstacionHora[fila][columna] = lectura.getPm25();
    }

    //Calcula el promedio de las horas y retorna dicho promedio
    public double promedioDeHora(int hora) {
        double suma = 0;
        int estacionesQueReportaron = 0;
        for (int fila = 0; fila < NUM_ESTACIONES; fila++) {
            if (pm25PorEstacionHora[fila][hora] != null) {
                suma += pm25PorEstacionHora[fila][hora];
                estacionesQueReportaron++;
            }
        }
        //Operador ternario: si se cumple se ejecuta el despues del ? si no se cumple se ejecuta despues de los :
        return estacionesQueReportaron == 0 ? 0.0 : suma / estacionesQueReportaron;
    }

    //promedio de la estacion que recorre cada uno
    public double promedioDeEstacion(int fila) {
        if (fila < 0 || fila >= NUM_ESTACIONES) return 0.0;
        double suma = 0;
        int horasQueReportaron = 0;
        for (int hora = 0; hora < NUM_HORAS; hora++) {
            if (pm25PorEstacionHora[fila][hora] != null) {
                suma += pm25PorEstacionHora[fila][hora];
                horasQueReportaron++;
            }
        }
        return horasQueReportaron == 0 ? 0.0 : suma / horasQueReportaron;
    }

    public int horaMasContaminada() {
        //Inicializa en 0.0 al dejarlo en -1 garantiza que al operar comienza en 0.0
        int mejorHora = -1;
        double mayorPromedio = -1.0;
        //Formato de hora de 00 a 23
        for (int hora = 0; hora < NUM_HORAS; hora++) {
            double promedio = promedioDeHora(hora);
            if (promedio > mayorPromedio) {
                mayorPromedio = promedio;
                mejorHora = hora;
            }
        }
        return mejorHora;
    }

    public void imprimirMatriz() {
        System.out.print("EST\\HORA");
        // Bucle para imprimir las 24 horas (columnas) en la primera fila
        for (int h = 0; h < NUM_HORAS; h++) {
            // String.format("%02d", h): Convierte el número en texto con 2 dígitos
            System.out.printf("%7s", String.format("%02d", h));
        }
        System.out.println();
        for (int f = 0; f < NUM_ESTACIONES; f++) {
            // Imprime el nombre de la estación al inicio de cada fila.
            System.out.printf("EST-%03d ", f + 1);
            // Bucle interno: recorre las 24 horas (columnas) para la estación actual
            for (int h = 0; h < NUM_HORAS; h++) {
                // Si no hay dato, imprimimos un guión "-".
                if (pm25PorEstacionHora[f][h] == null) {
                    System.out.printf("%7s", "-");
                } else {
                    // Si SÍ hay dato, lo imprimimos.
                    System.out.printf("%7.1f", pm25PorEstacionHora[f][h]);
                }
            }
            //Saltamos a la siguiente linea
            System.out.println();
        }
    }
}