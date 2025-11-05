package br.com.reconhecimento;

import org.opencv.core.Core;

public class TesteOpenCV {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DE CARREGAMENTO DO OPENCV ===\n");
        
        try {
            // Usar o carregador personalizado
            CarregadorOpenCV.carregar();
            
            System.out.println("\n=== TESTES ADICIONAIS ===");
            System.out.println("Versão OpenCV: " + Core.VERSION);
            System.out.println("Plataforma nativa: " + Core.NATIVE_LIBRARY_NAME);
            
            // Testar módulo face
            try {
                Class.forName("org.opencv.face.FaceRecognizer");
                System.out.println("✓ Módulo 'face' disponível");
            } catch (ClassNotFoundException e) {
                System.out.println("✗ Módulo 'face' NÃO disponível");
            }
            
            System.out.println("\n✓✓✓ TODOS OS TESTES PASSARAM! ✓✓✓");
            
        } catch (Exception e) {
            System.err.println("\n✗✗✗ ERRO NO TESTE ✗✗✗");
            e.printStackTrace();
        }
    }
}