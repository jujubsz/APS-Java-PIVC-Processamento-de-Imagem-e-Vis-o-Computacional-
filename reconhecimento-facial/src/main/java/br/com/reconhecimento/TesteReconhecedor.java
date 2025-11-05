package br.com.reconhecimento;

import br.com.reconhecimento.modelo.ReconhecedorFacial;
import br.com.reconhecimento.modelo.TipoAlgoritmo;
import org.opencv.core.Core;

public class TesteReconhecedor {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DO RECONHECEDOR ===\n");
        
        try {
            // Carregar OpenCV
            CarregadorOpenCV.carregar();
            
            // Testar cada algoritmo
            System.out.println("\n--- Testando FISHER ---");
            ReconhecedorFacial fisher = new ReconhecedorFacial(TipoAlgoritmo.FISHER);
            
            System.out.println("\n--- Testando EIGEN ---");
            ReconhecedorFacial eigen = new ReconhecedorFacial(TipoAlgoritmo.EIGEN);
            
            System.out.println("\n--- Testando LBPH ---");
            ReconhecedorFacial lbph = new ReconhecedorFacial(TipoAlgoritmo.LBPH);
            
            System.out.println("\n✓✓✓ TODOS OS RECONHECEDORES CRIADOS COM SUCESSO! ✓✓✓");
            
        } catch (Exception e) {
            System.err.println("\n✗✗✗ ERRO NO TESTE ✗✗✗");
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
}