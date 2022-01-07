package ficheros_directorios;

import java.io.File;
import java.io.IOException;

public class Creando {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        File ruta= new File("C:" + File.separator + "Users" + File.separator + "LENOVO" + File.separator + "Desktop" + File.separator + "java_ser" + File.separator + "prueba_texto.txt");

        //ruta.mkdir();  //crea una carpeta

        String archivo_destino= ruta.getAbsolutePath();

        try {
            ruta.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

            Escribiendo accede_es = new Escribiendo();
            accede_es.escribir(archivo_destino);
    }
}
