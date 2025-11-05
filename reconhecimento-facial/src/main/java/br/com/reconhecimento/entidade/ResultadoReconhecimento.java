package br.com.reconhecimento.entidade;

public class ResultadoReconhecimento {
    private boolean faceDetectada;
    private String nomeUsuario;
    private double confianca;

    public ResultadoReconhecimento() {}

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
