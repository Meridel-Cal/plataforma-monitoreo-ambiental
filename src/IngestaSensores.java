/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   IngestaSensores - VERSION 0.2

   Novedades frente a la Semana 1:
   - La logica esta en metodos, no en un main gigante.
   - Las lecturas ya no se imprimen y se olvidan: se GUARDAN.
   - La validacion vive dentro de LecturaSensor.esValida().

   Este archivo esta terminado. Los que estan incompletos son
   RepositorioLecturas y AnalizadorMatriz.
   ============================================================ */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IngestaSensores {

    // Ruta relativa: garantiza que el código funcione en cualquier máquina sin cambios.
    private static final String ARCHIVO = "data/lecturas_ampliadas.csv";

    // Evita ArrayIndexOutOfBoundsException al validar la estructura del CSV.
    private static final int CAMPOS_ESPERADOS = 5;

    // Contadores estáticos para medir la calidad de los datos de entrada.
    private static int descartadasPorFormato = 0;
    private static int descartadasPorRango = 0;

    public static void main(String[] args) throws IOException {
        // 1. Instanciación de los TADs (Semana 2)
        RepositorioLecturas repositorio = new RepositorioLecturas();
        AnalizadorMatriz analizador = new AnalizadorMatriz();

        // 2. Orquestación: delega la lectura y almacenamiento
        cargarArchivo(repositorio, analizador);

        // 3. Reporte de métricas de ingesta
        System.out.println("\n=== INGESTA ===");
        System.out.println("Lecturas almacenadas:      " + repositorio.tamano());
        System.out.println("Descartadas por formato:   " + descartadasPorFormato);
        System.out.println("Descartadas por rango:     " + descartadasPorRango);
        System.out.println("PM2.5 promedio (repositorio): " + repositorio.promedioPm25() + "\n");

        // 4. Evidencia empírica del experimento de redimensionamiento (Fase 2.1)
        System.out.println("=== MÉTRICAS DE REDIMENSIONAMIENTO ===");
        System.out.println("Redimensionamientos: " + repositorio.getRedimensionamientos());
        System.out.println("Copias realizadas:   " + repositorio.getCopiasRealizadas() + "\n");

        // 5. Análisis de la matriz: perfil horario de la ciudad
        System.out.println("=== PERFIL HORARIO DE LA CIUDAD ===");
        for (int h = 0; h < 24; h++) {
            // %02d: hora con 2 dígitos. %.2f: promedio con 2 decimales.
            System.out.printf("Hora %02d -> PM2.5 promedio: %.2f%n", h, analizador.promedioDeHora(h));
        }

        // 👇 NUEVO: Integración del experimento de la Semana 3
        System.out.println("\n=== EXPERIMENTOS SEMANA 3 ===");
        BancoDePruebas.experimentoUno();
    }

    /*
     * Lee el archivo línea por línea y alimenta las estructuras de datos.
     * Usa BufferedReader por eficiencia de memoria en lectura de archivos.
     */
    private static void cargarArchivo(RepositorioLecturas repositorio,
                                      AnalizadorMatriz analizador) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO));
        lector.readLine(); // Salta la primera línea (encabezados del CSV)

        String linea;
        while ((linea = lector.readLine()) != null) {
            LecturaSensor lectura = construirLectura(linea);

            // Degradación elegante: si falla el formato, ignora la fila y sigue.
            if (lectura == null) {
                continue;
            }
            // Si el formato es correcto pero los valores son físicamente imposibles.
            if (!lectura.esValida()) {
                descartadasPorRango++;
                continue;
            }

            // Solo las lecturas 100% válidas llegan aquí.
            repositorio.agregar(lectura);
            analizador.registrar(lectura);
        }
        lector.close(); // Libera el recurso del sistema operativo
    }

    /*
     * Responsabilidad única: convertir texto crudo a objeto LecturaSensor.
     * No valida rangos (eso lo hace esValida()), solo la estructura y tipos.
     */
    private static LecturaSensor construirLectura(String linea) {
        String[] campos = linea.split(",");

        // Early return: si no tiene 5 columnas, aborta inmediatamente.
        if (campos.length != CAMPOS_ESPERADOS) {
            descartadasPorFormato++;
            return null;
        }

        try {
            // Intenta convertir los campos numéricos.
            double temperatura = Double.parseDouble(campos[2]);
            double humedad = Double.parseDouble(campos[3]);
            double pm25 = Double.parseDouble(campos[4]);

            return new LecturaSensor(campos[0], campos[1], temperatura, humedad, pm25);

        } catch (NumberFormatException e) {
            // Si hay letras o vacíos en campos numéricos, captura el error sin colapsar el programa.
            descartadasPorFormato++;
            return null;
        }
    }
}