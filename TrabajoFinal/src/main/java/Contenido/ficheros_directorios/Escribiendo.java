package ficheros_directorios;

import java.io.FileWriter;
import java.io.IOException;

public class Escribiendo {

    public void escribir(String ruta_archivo){

        String frase= "Esto es un ejemplo. Espero que funcione bien";

        try{

            FileWriter escritura= new FileWriter(ruta_archivo);

            for(int i=0; i<frase.length();i++){
                escritura.write(frase.charAt(i));
            }

            escritura.close();

        }catch(IOException e){

        }

    }
}
