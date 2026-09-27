/**
 * PLATAFORMA DE MONITOREO AMBIENTAL URBANO
 *
 * Contiene algoritmos de búsqueda utilizados por el proyecto.
 */
public class BuscadorLecturas {

    /**
     * Cantidad de comparaciones realizadas por la última búsqueda.
     */
    private static int comparaciones = 0;

    public static int getComparaciones() {
        return comparaciones;
    }

    /**
     * Búsqueda lineal por timestamp.
     *
     * No necesita que los datos estén ordenados.
     *
     * @param datos arreglo de lecturas
     * @param timestamp timestamp que se desea encontrar
     * @return posición de la lectura o -1 si no existe
     */
    public static int busquedaLinealPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {
            comparaciones++;

            if (datos[i].getTimestamp().equals(timestamp)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Busca la primera lectura de una estación.
     *
     * @return posición de la primera coincidencia o -1
     */
    public static int buscarPorEstacion(
            LecturaSensor[] datos,
            String idSensor) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {

            comparaciones++;

            if (datos[i].getIdSensor().equals(idSensor)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Busca un timestamp mediante búsqueda binaria.
     *
     * PRECONDICIÓN:
     * las lecturas deben estar ordenadas
     * ascendentemente por timestamp.
     *
     * @return posición de la lectura o -1 si no existe
     */
    public static int busquedaBinariaPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        int inicio = 0;
        int fin = datos.length - 1;

        while (inicio <= fin) {

            int medio = (inicio + fin) / 2;

            comparaciones++;

            int comparacion =
                    datos[medio]
                            .getTimestamp()
                            .compareTo(timestamp);

            if (comparacion == 0) {
                return medio;
            }

            if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return -1;
    }
    /**
     * Búsqueda binaria por PM2.5.
     *
     * PRECONDICIÓN:
     * el arreglo debe estar ordenado ascendentemente
     * por PM2.5.
     *
     * El generador de la Semana 3 no garantiza esta condición.
     */
    public static int busquedaBinariaPorPm25(
            LecturaSensor[] datos,
            double pm25) {

        comparaciones = 0;

        int inicio = 0;
        int fin = datos.length - 1;

        while (inicio <= fin) {

            int medio = (inicio + fin) / 2;

            comparaciones++;

            if (datos[medio].getPm25() == pm25) {
                return medio;
            }

            if (datos[medio].getPm25() < pm25) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return -1;
    }

}
