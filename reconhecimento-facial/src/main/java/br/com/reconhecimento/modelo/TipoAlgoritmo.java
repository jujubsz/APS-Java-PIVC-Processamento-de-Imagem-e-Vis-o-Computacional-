// Arquivo: TipoAlgoritmo.java
// ============================================
package br.com.reconhecimento.modelo;

/**
 * Enum com os tipos de algoritmos disponíveis
 */
public enum TipoAlgoritmo {
    FISHER("FisherFaces - Melhor para variações de iluminação"),
    EIGEN("EigenFaces - Mais rápido, usa PCA"),
    LBPH("LBPH - Melhor para variações de pose e expressão");

    private final String descricao;

    TipoAlgoritmo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}