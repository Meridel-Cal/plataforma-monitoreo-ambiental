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

    private static final String ARCHIVO = "data/lecturas_ampliadas.csv"; // Asegúrate de que esta ruta sea correcta
    private static final int CAMPOS_ESPERADOS = 5;

    private static int descartadasPorFormato = 0;
    private static int descartadasPorRango = 0;

    public static void main(String[] args) throws IOException {

        RepositorioLecturas repositorio = new RepositorioLecturas();
        AnalizadorMatriz analizador = new AnalizadorMatriz();

        cargarArchivo(repositorio, analizador);

        System.out.println();
        System.out.println("=== INGESTA ===");
        System.out.println("Lecturas almacenadas:      " + repositorio.tamano());
        System.out.println("Descartadas por formato:   " + descartadasPorFormato);
        System.out.println("Descartadas por rango:     " + descartadasPorRango);
        System.out.println();
        System.out.println("PM2.5 promedio (repositorio): " + repositorio.promedioPm25());
        System.out.println();

        // FASE 2.1: Evidencia del experimento de medición
        System.out.println("=== MÉTRICAS DE REDIMENSIONAMIENTO ===");
        System.out.println("Redimensionamientos: " + repositorio.getRedimensionamientos());
        System.out.println("Copias realizadas:   " + repositorio.getCopiasRealizadas());
        System.out.println();

        System.out.println("=== PERFIL HORARIO DE LA CIUDAD ===");
        for (int h = 0; h < 24; h++) {
            System.out.printf("Hora %02d -> PM2.5 promedio: %.2f%n", h, analizador.promedioDeHora(h));
        }
    }

    private static void cargarArchivo(RepositorioLecturas repositorio,
                                      AnalizadorMatriz analizador) throws IOException {
        BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO));
        lector.readLine(); // encabezado

        String linea;
        while ((linea = lector.readLine()) != null) {
            LecturaSensor lectura = construirLectura(linea);
            if (lectura == null) {
                continue;
            }
            if (!lectura.esValida()) {
                descartadasPorRango++;
                continue;
            }
            repositorio.agregar(lectura);
            analizador.registrar(lectura);
        }
        lector.close();
    }

    private static LecturaSensor construirLectura(String linea) {
        String[] campos = linea.split(",");
        if (campos.length != CAMPOS_ESPERADOS) {
            descartadasPorFormato++;
            return null;
        }
        try {
            double temperatura = Double.parseDouble(campos[2]);
            double humedad = Double.parseDouble(campos[3]);
            double pm25 = Double.parseDouble(campos[4]);
            return new LecturaSensor(campos[0], campos[1], temperatura, humedad, pm25);
        } catch (NumberFormatException e) {
            descartadasPorFormato++;
            return null;
        }
    }
}