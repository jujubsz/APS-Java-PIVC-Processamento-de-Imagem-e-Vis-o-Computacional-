package br.com.reconhecimento.modelo;

import br.com.reconhecimento.entidade.Usuario;

public class ReconhecedorFacial {
    private TipoAlgoritmo algoritmo;
    private String caminhoCascadeXML;
    private boolean treinado;
    private int totalImagensTreinamento;

    // ✅ Construtor adicional para permitir criar o objeto só com o TipoAlgoritmo
    public ReconhecedorFacial(TipoAlgoritmo algoritmo) {
        this(algoritmo, "cascades/haarcascade_frontalface_default.xml");
    }

    public ReconhecedorFacial(TipoAlgoritmo algoritmo, String caminhoCascadeXML) {
        this.algoritmo = algoritmo;
        this.caminhoCascadeXML = caminhoCascadeXML;
        this.treinado = false;
        this.totalImagensTreinamento = 0;
    }

    public void treinar(Usuario usuario) {
        if (usuario.getCaminhosFotos() != null) {
            totalImagensTreinamento += usuario.getCaminhosFotos().size();
            System.out.println("Treinando usuário: " + usuario.getNome() + " (" + usuario.getCaminhosFotos().size() + " imagens)");
        }
    }

    public String prever(String caminhoImagem) {
        System.out.println("Reconhecendo imagem: " + caminhoImagem);
        return "Usuário Exemplo"; // Simulação
    }

    public TipoAlgoritmo getAlgoritmo() {
        return algoritmo;
    }

    public boolean isTreinado() {
        return treinado;
    }

    public void setTreinado(boolean treinado) {
        this.treinado = treinado;
    }

    public int getTotalImagensTreinamento() {
        return totalImagensTreinamento;
    }
}
