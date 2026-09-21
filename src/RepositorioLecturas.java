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

    private static final int CAPACIDAD_INICIAL = 10; // Capacidad baja para probar el redimensionamiento

    private LecturaSensor[] lecturas; // Arreglo interno
    private int cantidad;             // Número real de elementos almacenados

    // Métricas para el experimento
    private int redimensionamientos = 0;
    private int copiasRealizadas = 0;

    public RepositorioLecturas() {
        this.lecturas = new LecturaSensor[CAPACIDAD_INICIAL];
        this.cantidad = 0;
    }

    // Agrega al final. Si está lleno, duplica la capacidad automáticamente.
    public boolean agregar(LecturaSensor lectura) {
        if (cantidad == lecturas.length) {
            redimensionar();
        }
        lecturas[cantidad] = lectura;
        cantidad++;
        return true;
    }

    // Retorna el elemento si el índice es válido, de lo contrario null.
    public LecturaSensor obtener(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return null;
        }
        return lecturas[posicion];
    }

    // Retorna la cantidad de elementos reales (no la capacidad del arreglo).
    public int tamano() {
        return cantidad;
    }

    // Elimina el elemento y compacta el arreglo hacia la izquierda para evitar huecos.
    public void eliminar(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return;
        }
        for (int i = posicion; i < cantidad - 1; i++) {
            lecturas[i] = lecturas[i + 1];
        }
        lecturas[cantidad - 1] = null; // Limpia la referencia duplicada
        cantidad--;
    }

    // Búsqueda secuencial. Retorna la primera coincidencia o null.
    public LecturaSensor buscarPorEstacion(String idSensor) {
        for (int i = 0; i < cantidad; i++) {
            //Se usa equal porque se comparan los valores NO la memoria
            if (lecturas[i].getIdSensor().equals(idSensor)) {
                return lecturas[i];
            }
        }
        return null;
    }

    // Reemplaza el elemento si la posición es válida.
    public void actualizar(int posicion, LecturaSensor nueva) {
        if (posicion >= 0 && posicion < cantidad) {
            lecturas[posicion] = nueva;
        }
    }

    // Crea un nuevo arreglo con el doble de capacidad y copia los elementos.
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

    // Calcula el promedio evitando división por cero.
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
        //Operador ternario: si se cumple se ejecuta el despues del ? si no se cumple se ejecuta despues de los :
        return elementosReales == 0 ? 0.0 : suma / elementosReales;
    }

    // Getters para las métricas del experimento
    public int getRedimensionamientos() { return redimensionamientos; }
    public int getCopiasRealizadas() { return copiasRealizadas; }
}