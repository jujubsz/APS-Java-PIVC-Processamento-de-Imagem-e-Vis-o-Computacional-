// Arquivo: ResultadoPredição.java
// ============================================
package br.com.reconhecimento.modelo;

/**
 * Classe que encapsula o resultado de uma predição
 */
public class ResultadoPredicao {
    
    private int label;
    private double confianca;
    private double distancia;

    public ResultadoPredicao(int label, double confianca, double distancia) {
        this.label = label;
        this.confianca = confianca;
        this.distancia = distancia;
    }

    public int getLabel() {
        return label;
    }

    public double getConfianca() {
        return confianca;
    }

    public double getDistancia() {
        return distancia;
    }

    @Override
    public String toString() {
        return String.format("Label: %d, Confiança: %.2f%%, Distância: %.2f", 
                           label, confianca, distancia);
    }
}