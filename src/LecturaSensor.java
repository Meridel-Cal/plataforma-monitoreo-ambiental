/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   Clase LecturaSensor - VERSION COMPLETA

   Esta clase SI esta terminada. Es el resultado de lo que
   discutimos en la Semana 1: en vez de arrastrar cinco
   variables sueltas por todos los metodos, una lectura viaja
   completa dentro de un objeto.

   Estudiala antes de usarla. La vas a ver las doce semanas.
   ============================================================ */

public class LecturaSensor {

    // Constantes públicas para evitar "números mágicos" y permitir validación externa.
    public static final double TEMP_MIN = -40.0;
    public static final double TEMP_MAX = 60.0;
    public static final double HUMEDAD_MIN = 0.0;
    public static final double HUMEDAD_MAX = 100.0;
    public static final double PM25_MIN = 0.0;

    // 'private': Encapsulamiento. 'final': Inmutabilidad (una lectura registrada no cambia).
    private final String idSensor;
    private final String timestamp;
    private final double temperatura;
    private final double humedad;
    private final double pm25;

    // Constructor: Asigna los valores una sola vez.
    public LecturaSensor(String idSensor, String timestamp,
                         double temperatura, double humedad, double pm25) {
        this.idSensor = idSensor;
        this.timestamp = timestamp;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.pm25 = pm25;
    }

    // Getters: Único punto de acceso seguro a los atributos privados.
    public String getIdSensor() { return idSensor; }
    public String getTimestamp() { return timestamp; }
    public double getTemperatura() { return temperatura; }
    public double getHumedad() { return humedad; }
    public double getPm25() { return pm25; }

    /*
     * Extrae la hora del string con formato "yyyy-MM-dd HH:mm".
     * substring(11, 13) toma exactamente los 2 caracteres de la hora (índices 11 y 12).
     */
    public int getHora() {
        String parteHora = timestamp.substring(11, 13);
        return Integer.parseInt(parteHora);
    }

    /*
     * Valida que los datos tengan sentido físico real.
     * Usa "Early Return" (retorno temprano) para evitar anidación excesiva de if/else.
     */
    public boolean esValida() {
        if (temperatura < TEMP_MIN || temperatura > TEMP_MAX) return false;
        if (humedad < HUMEDAD_MIN || humedad > HUMEDAD_MAX) return false;
        if (pm25 < PM25_MIN) return false;
        return true;
    }

    /*
     * Sobrescribe toString() para representar el objeto como texto legible.
     * Fundamental para debugging y pruebas en consola.
     */
    @Override
    public String toString() {
        return idSensor + " | " + timestamp
                + " | T=" + temperatura + " | H=" + humedad + " | PM=" + pm25;
    }
}
