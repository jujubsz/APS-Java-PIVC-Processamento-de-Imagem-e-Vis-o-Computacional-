package br.com.reconhecimento.entidade;

/**
 * Representa o resultado de uma predição de reconhecimento facial.
 */
public class ResultadoReconhecimento {
    private boolean faceDetectada;
    private String nomeUsuario;
    private double confianca;

    public ResultadoReconhecimento() { }

    // Construtor compatível com as chamadas do seu código: (nome, confianca, faceDetectada)
    public ResultadoReconhecimento(String nomeUsuario, double confianca, boolean faceDetectada) {
        this.nomeUsuario = nomeUsuario;
        this.confianca = confianca;
        this.faceDetectada = faceDetectada;
    }

    // Opcional: também manter versão alternativa (face, nome, confianca)
    public ResultadoReconhecimento(boolean faceDetectada, String nomeUsuario, double confianca) {
        this.faceDetectada = faceDetectada;
        this.nomeUsuario = nomeUsuario;
        this.confianca = confianca;
    }

    public boolean isFaceDetectada() {
        return faceDetectada;
    }

    public void setFaceDetectada(boolean faceDetectada) {
        this.faceDetectada = faceDetectada;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public double getConfianca() {
        return confianca;
    }

    public void setConfianca(double confianca) {
        this.confianca = confianca;
    }

    @Override
    public String toString() {
        return "ResultadoReconhecimento{" +
                "faceDetectada=" + faceDetectada +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", confianca=" + confianca +
                '}';
    }
}
