import java.util.Random;

/**
 * Genera lecturas sintéticas para los experimentos de la Semana 3.
 */
public class GeneradorDatos {

    private static final int NUM_ESTACIONES = 9;
    private static final long SEMILLA = 20262L;

    /**
     * Genera n lecturas en orden cronológico ascendente.
     *
     * El timestamp aumenta con la posición del arreglo.
     */
    public static LecturaSensor[] generar(int n) {

        Random azar = new Random(SEMILLA);
        LecturaSensor[] datos = new LecturaSensor[n];

        for (int i = 0; i < n; i++) {

            String id = String.format(
                    "EST-%03d",
                    (i % NUM_ESTACIONES) + 1
            );

            String timestamp = String.format(
                    "%010d",
                    i
            );

            double temperatura =
                    11 + azar.nextDouble() * 18;

            double humedad =
                    55 + azar.nextDouble() * 35;

            double pm25 =
                    5 + azar.nextDouble() * 55;

            datos[i] = new LecturaSensor(
                    id,
                    timestamp,
                    redondear(temperatura),
                    redondear(humedad),
                    redondear(pm25)
            );
        }

        return datos;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    /**
     * Devuelve un timestamp que existe.
     */
    public static String timestampEnPosicion(int posicion) {
        return String.format("%010d", posicion);
    }

    /**
     * Devuelve un timestamp que no existe
     * en los arreglos generados.
     */
    public static String timestampInexistente() {
        return "9999999999";
    }
}

