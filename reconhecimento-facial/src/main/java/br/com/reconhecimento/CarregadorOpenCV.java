package br.com.reconhecimento;

import org.opencv.core.Core;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

/**
 * Classe responsável por carregar a biblioteca nativa do OpenCV
 */
public class CarregadorOpenCV {
    
    private static boolean carregado = false;
    
    /**
     * Carrega a biblioteca OpenCV usando bytedeco
     */
    public static void carregar() {
        if (carregado) {
            return;
        }
        
        try {
            // Carrega usando bytedeco (funciona com módulos contrib)
            Loader.load(opencv_java.class);
            carregado = true;
            System.out.println("✓ OpenCV carregado via bytedeco");
            System.out.println("✓ Versão: " + Core.VERSION);
            
        } catch (Exception e) {
            System.err.println("✗ Falha ao carregar OpenCV!");
            System.err.println("  Erro: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Não foi possível carregar OpenCV", e);
        }
    }
    
    public static boolean isCarregado() {
        return carregado;
    }
}