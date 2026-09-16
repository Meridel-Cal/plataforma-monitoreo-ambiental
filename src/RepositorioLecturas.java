/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   TAD RepositorioLecturas - VERSION 0.1 INCOMPLETA

   Este es el Tipo Abstracto de Dato del semestre: quien lo usa
   solo conoce las operaciones publicas de abajo. NO deberia
   saber que por dentro hay un arreglo.

   Ese es el contrato. Respetalo mientras lo completas.

   ADVERTENCIA: esta version corre sin caerse y entrega
   resultados incorrectos. Tu trabajo de hoy es descubrir en
   que miente antes de arreglarla.
   ============================================================ */

public class RepositorioLecturas {

    private static final int CAPACIDAD_INICIAL = 10;

    private LecturaSensor[] lecturas;
    private int cantidad;

    // Contadores para el experimento de la Fase 2.1
    private int redimensionamientos = 0;
    private int copiasRealizadas = 0;

    public RepositorioLecturas() {
        this.lecturas = new LecturaSensor[CAPACIDAD_INICIAL];
        this.cantidad = 0;
    }

    public boolean agregar(LecturaSensor lectura) {
        if (cantidad == lecturas.length) {
            redimensionar();
        }
        lecturas[cantidad] = lectura;
        cantidad++;
        return true;
    }

    public LecturaSensor obtener(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return null;
        }
        return lecturas[posicion];
    }

    public int tamano() {
        return cantidad;
    }

    public void eliminar(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return;
        }
        // Estrategia de compactación
        for (int i = posicion; i < cantidad - 1; i++) {
            lecturas[i] = lecturas[i + 1];
        }
        lecturas[cantidad - 1] = null; // Evitar fuga de memoria
        cantidad--;
    }

    public LecturaSensor buscarPorEstacion(String idSensor) {
        for (int i = 0; i < cantidad; i++) {
            if (lecturas[i].getIdSensor().equals(idSensor)) {
                return lecturas[i];
            }
        }
        return null;
    }

    public void actualizar(int posicion, LecturaSensor nueva) {
        if (posicion >= 0 && posicion < cantidad) {
            lecturas[posicion] = nueva;
        }
    }

    private void redimensionar() {
        int nuevaCapacidad = lecturas.length * 2;
        LecturaSensor[] nuevoArreglo = new LecturaSensor[nuevaCapacidad];

        redimensionamientos++;
        for (int i = 0; i < cantidad; i++) {
            nuevoArreglo[i] = lecturas[i];
            copiasRealizadas++;
        }
        this.lecturas = nuevoArreglo;
    }

    public double promedioPm25() {
        if (cantidad == 0) return 0.0;
        double suma = 0;
        int elementosReales = 0;
        for (int i = 0; i < cantidad; i++) {
            if (lecturas[i] != null) {
                suma += lecturas[i].getPm25();
                elementosReales++;
            }
        }
        return elementosReales == 0 ? 0.0 : suma / elementosReales;
    }

    // Getters para el experimento
    public int getRedimensionamientos() { return redimensionamientos; }
    public int getCopiasRealizadas() { return copiasRealizadas; }
}
