package br.com.reconhecimento;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class VerificarRecursos {
    
    public static void main(String[] args) {
        System.out.println("=== DIAGNÓSTICO DE RECURSOS ===\n");
        
        String nomeArquivo = "haarcascade_frontalface_default.xml";
        
        // Teste 1: Classpath
        System.out.println("--- Teste 1: Classpath ---");
        InputStream is = VerificarRecursos.class.getClassLoader().getResourceAsStream(nomeArquivo);
        if (is != null) {
            System.out.println("✓ Encontrado no classpath!");
            try { is.close(); } catch (Exception e) {}
        } else {
            System.out.println("✗ NÃO encontrado no classpath");
        }
        
        // Teste 2: URL do resource
        System.out.println("\n--- Teste 2: URL Resource ---");
        URL url = VerificarRecursos.class.getClassLoader().getResource(nomeArquivo);
        if (url != null) {
            System.out.println("✓ URL: " + url);
        } else {
            System.out.println("✗ URL não encontrada");
        }
        
        // Teste 3: Arquivo local
        System.out.println("\n--- Teste 3: Arquivo Local ---");
        File arquivo = new File(nomeArquivo);
        if (arquivo.exists()) {
            System.out.println("✓ Encontrado localmente: " + arquivo.getAbsolutePath());
        } else {
            System.out.println("✗ NÃO encontrado localmente");
            System.out.println("  Procurado em: " + arquivo.getAbsolutePath());
        }
        
        // Teste 4: Diretório atual
        System.out.println("\n--- Teste 4: Diretório Atual ---");
        System.out.println("Diretório de trabalho: " + System.getProperty("user.dir"));
        
        // Teste 5: Listar resources disponíveis
        System.out.println("\n--- Teste 5: Resources Disponíveis ---");
        try {
            URL resourceUrl = VerificarRecursos.class.getClassLoader().getResource("");
            if (resourceUrl != null) {
                System.out.println("Pasta resources: " + resourceUrl);
                File resourceDir = new File(resourceUrl.toURI());
                if (resourceDir.exists()) {
                    System.out.println("Arquivos na pasta resources:");
                    listarArquivos(resourceDir, "");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar: " + e.getMessage());
        }
    }
    
    private static void listarArquivos(File dir, String indent) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(indent + "- " + file.getName());
                if (file.isDirectory()) {
                    listarArquivos(file, indent + "  ");
                }
            }
        }
    }
}