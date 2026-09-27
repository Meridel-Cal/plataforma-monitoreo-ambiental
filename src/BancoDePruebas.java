/**
 * Contiene los experimentos de la Semana 3.
 *
 * Esta clase NO tiene main().
 * Los experimentos son llamados desde IngestaSensores.
 */
public class BancoDePruebas {

    private static final int[] TAMANOS = {
            1_000,
            100_000,
            1_000_000
    };

    /**
     * Experimento 1:
     * búsqueda lineal en el peor caso.
     */
    public static void experimentoUno() {

        System.out.println(
                "=== EXPERIMENTO 1: BUSQUEDA LINEAL ==="
        );

        System.out.printf(
                "%12s %16s %14s%n",
                "lecturas",
                "comparaciones",
                "tiempo (ms)"
        );

        for (int n : TAMANOS) {

            LecturaSensor[] datos =
                    GeneradorDatos.generar(n);

            String objetivo =
                    GeneradorDatos.timestampEnPosicion(n - 1);

            long inicio = System.nanoTime();

            int posicion =
                    BuscadorLecturas
                            .busquedaLinealPorTimestamp(
                                    datos,
                                    objetivo
                            );

            long fin = System.nanoTime();

            System.out.printf(
                    "%12d %16d %14.3f%n",
                    n,
                    BuscadorLecturas.getComparaciones(),
                    (fin - inicio) / 1_000_000.0
            );

            if (posicion < 0) {
                System.out.println(
                        "ADVERTENCIA: no encontro una lectura existente."
                );
            }
        }

        System.out.println();
    }

    /**
     * Experimento 2:
     * compara búsqueda lineal y búsqueda binaria.
     */
    public static void experimentoDos() {

        System.out.println(
                "=== EXPERIMENTO 2: LINEAL vs BINARIA ==="
        );

        System.out.printf(
                "%12s %14s %14s %12s%n",
                "lecturas",
                "lineal",
                "binaria",
                "relacion"
        );

        for (int n : TAMANOS) {

            LecturaSensor[] datos =
                    GeneradorDatos.generar(n);

            String objetivo =
                    GeneradorDatos.timestampEnPosicion(n - 1);

            BuscadorLecturas.busquedaLinealPorTimestamp(
                    datos,
                    objetivo
            );

            int lineal =
                    BuscadorLecturas.getComparaciones();

            BuscadorLecturas.busquedaBinariaPorTimestamp(
                    datos,
                    objetivo
            );

            int binaria =
                    BuscadorLecturas.getComparaciones();

            System.out.printf(
                    "%12d %14d %14d %12.1f%n",
                    n,
                    lineal,
                    binaria,
                    (double) lineal / binaria
            );
        }

        System.out.println();
    }

    /**
     * Compara búsqueda de un timestamp inexistente.
     */
    public static void experimentoTres() {

        System.out.println(
                "=== EXPERIMENTO 3: DATO INEXISTENTE ==="
        );

        LecturaSensor[] datos =
                GeneradorDatos.generar(100_000);

        String objetivo =
                GeneradorDatos.timestampInexistente();

        BuscadorLecturas.busquedaLinealPorTimestamp(
                datos,
                objetivo
        );

        int lineal =
                BuscadorLecturas.getComparaciones();

        BuscadorLecturas.busquedaBinariaPorTimestamp(
                datos,
                objetivo
        );

        int binaria =
                BuscadorLecturas.getComparaciones();

        System.out.println(
                "Lineal  -> comparaciones: " + lineal
        );

        System.out.println(
                "Binaria -> comparaciones: " + binaria
        );

        System.out.println();
    }
    /**
     * Demuestra qué ocurre cuando la búsqueda binaria
     * se aplica sobre un campo que no está ordenado.
     */
    public static void experimentoCuatro() {

        System.out.println(
                "=== EXPERIMENTO 4: BINARIA POR PM2.5 ==="
        );

        LecturaSensor[] datos =
                GeneradorDatos.generar(10_000);

        int aciertosLineal = 0;
        int aciertosBinaria = 0;

        for (int i = 0; i < 20; i++) {

            double valor =
                    datos[i * 137].getPm25();

            int posLineal = -1;

            for (int j = 0; j < datos.length; j++) {

                if (datos[j].getPm25() == valor) {
                    posLineal = j;
                    break;
                }
            }

            int posBinaria =
                    BuscadorLecturas
                            .busquedaBinariaPorPm25(
                                    datos,
                                    valor
                            );

            if (posLineal >= 0) {
                aciertosLineal++;
            }

            if (posBinaria >= 0) {
                aciertosBinaria++;
            }
        }

        System.out.println(
                "Valores buscados que SI existen: 20"
        );

        System.out.println(
                "Encontrados por búsqueda lineal:  "
                        + aciertosLineal
        );

        System.out.println(
                "Encontrados por búsqueda binaria: "
                        + aciertosBinaria
        );

        System.out.println();
    }
    /**
     * Experimento 5:
     * Casos de prueba mínimos para búsqueda binaria.
     *
     * Verifica:
     * - primer elemento
     * - elemento intermedio
     * - último elemento
     * - elemento existente
     * - elemento inexistente
     * - arreglo pequeño
     * - arreglo grande
     */
    public static void experimentoCinco() {

        System.out.println("=== EXPERIMENTO 5: CASOS DE PRUEBA BINARIA ===");
        System.out.println();

        // ---------- ARREGLO PEQUEÑO (10 elementos) ----------
        System.out.println("--- ARREGLO PEQUEÑO (n=10) ---");
        LecturaSensor[] pequenos = GeneradorDatos.generar(10);

        // Primer elemento (posición 0)
        probarBinaria(pequenos, "Primer elemento",
                GeneradorDatos.timestampEnPosicion(0), 0);

        // Elemento intermedio (posición 5)
        probarBinaria(pequenos, "Elemento intermedio",
                GeneradorDatos.timestampEnPosicion(5), 5);

        // Último elemento (posición 9)
        probarBinaria(pequenos, "Último elemento",
                GeneradorDatos.timestampEnPosicion(9), 9);

        // Elemento existente (posición 3)
        probarBinaria(pequenos, "Elemento existente (pos 3)",
                GeneradorDatos.timestampEnPosicion(3), 3);

        // Elemento inexistente
        probarBinaria(pequenos, "Elemento inexistente",
                GeneradorDatos.timestampInexistente(), -1);

        System.out.println();

        // ---------- ARREGLO GRANDE (100_000 elementos) ----------
        System.out.println("--- ARREGLO GRANDE (n=100,000) ---");
        LecturaSensor[] grandes = GeneradorDatos.generar(100_000);

        probarBinaria(grandes, "Primer elemento",
                GeneradorDatos.timestampEnPosicion(0), 0);

        probarBinaria(grandes, "Elemento intermedio",
                GeneradorDatos.timestampEnPosicion(50_000), 50_000);

        probarBinaria(grandes, "Último elemento",
                GeneradorDatos.timestampEnPosicion(99_999), 99_999);

        probarBinaria(grandes, "Elemento existente (pos 12345)",
                GeneradorDatos.timestampEnPosicion(12_345), 12_345);

        probarBinaria(grandes, "Elemento inexistente",
                GeneradorDatos.timestampInexistente(), -1);

        System.out.println();
    }

    /**
     * Auxiliar: ejecuta la búsqueda binaria y reporta el resultado.
     */
    private static void probarBinaria(
            LecturaSensor[] datos,
            String descripcion,
            String objetivo,
            int esperado) {

        int resultado = BuscadorLecturas.busquedaBinariaPorTimestamp(
                datos,
                objetivo
        );

        int comparaciones = BuscadorLecturas.getComparaciones();

        String estado = (resultado == esperado) ? "OK" : "FALLA";

        System.out.printf("  %-30s esperado=%6d  obtenido=%6d  comparaciones=%4d  [%s]%n",
                descripcion,
                esperado,
                resultado,
                comparaciones,
                estado
        );
    }
}

