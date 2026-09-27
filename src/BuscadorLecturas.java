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
}
