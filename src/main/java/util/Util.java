package util;

import java.io.*;


public class Util {


    public static void exportaResutado(String fileName, String resposta){

        try {
            FileWriter fstream = new FileWriter(new File(".").getAbsolutePath()+"//resultados da simulacao//"+fileName+".txt",true);
            fstream.write(resposta);
            fstream.close();
            System.out.println("Resultado salvo com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
