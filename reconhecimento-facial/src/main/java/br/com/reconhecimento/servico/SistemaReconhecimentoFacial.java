package br.com.reconhecimento.servico;

import br.com.reconhecimento.entidade.ResultadoReconhecimento;
import br.com.reconhecimento.entidade.Usuario;
import br.com.reconhecimento.modelo.NivelAcesso;
import br.com.reconhecimento.modelo.ReconhecedorFacial;
import br.com.reconhecimento.modelo.TipoAlgoritmo;

import java.util.*;
/**
 * Classe responsável por gerenciar o reconhecimento facial e treinamento do modelo.
 * É utilizada pela classe Main e pelo sistema de controle de acesso.
 */
public class SistemaReconhecimentoFacial {

    private ReconhecedorFacial reconhecedor;
    private TipoAlgoritmo tipoAlgoritmo;
    private String caminhoCascadeXML;

    // Armazena os usuários carregados para treinamento
    private GerenciadorUsuarios gerenciadorUsuarios;

    public SistemaReconhecimentoFacial(TipoAlgoritmo tipoAlgoritmo, String caminhoCascadeXML) {
        this.tipoAlgoritmo = tipoAlgoritmo;
        this.caminhoCascadeXML = caminhoCascadeXML;
        this.gerenciadorUsuarios = new GerenciadorUsuarios();
        this.reconhecedor = new ReconhecedorFacial(tipoAlgoritmo, caminhoCascadeXML);
    }

    /**
     * Adiciona um usuário ao sistema para ser usado no treinamento.
     */
    
    
    public void adicionarUsuario(String nome, String cargo, NivelAcesso nivel, List<String> caminhosFotos) {
        // Cria o usuário apenas com os campos que o construtor aceita
        Usuario usuario = new Usuario(0, nome, nivel, cargo); // supondo que só aceite nome e cargo

        // Define o nível manualmente
        usuario.podeAcessar(nivel);

        // Adiciona fotos
        if (caminhosFotos != null) {
            for (String caminho : caminhosFotos) {
                usuario.adicionarFoto(caminho);
            }
        }

        // Adiciona ao gerenciador
        gerenciadorUsuarios.adicionarUsuario(usuario);
    }


    /**
     * Treina o modelo de reconhecimento com todos os usuários adicionados.
     */
    public void treinar() {
        if (gerenciadorUsuarios.getUsuarios().isEmpty()) {
            throw new RuntimeException("Nenhum usuário cadastrado para treinamento!");
        }

        System.out.println(">> Treinando modelo com " + gerenciadorUsuarios.getUsuarios().size() + " usuários...");

        for (Usuario usuario : gerenciadorUsuarios.getUsuarios()) {
            reconhecedor.treinar(usuario);
        }

        reconhecedor.setTreinado(true);
    }

    /**
     * Reconhece o rosto a partir de uma imagem e retorna o resultado.
     */
    public ResultadoReconhecimento reconhecer(String caminhoImagem) {
        if (!reconhecedor.isTreinado()) {
            throw new RuntimeException("O modelo precisa ser treinado antes de fazer predições!");
        }

        // Faz a previsão (simulada)
        String nomePrevisto = reconhecedor.prever(caminhoImagem);

        ResultadoReconhecimento resultado = new ResultadoReconhecimento();
        resultado.setFaceDetectada(nomePrevisto != null && !nomePrevisto.isEmpty());
        resultado.setNomeUsuario(nomePrevisto != null ? nomePrevisto : "Desconhecido");
        resultado.setConfianca(88.0); // Exemplo fixo (você pode ajustar conforme o modelo real)

        return resultado;
    }

    /**
     * Retorna o gerenciador de usuários (usado pelo Main para buscar dados de login).
     */
    public GerenciadorUsuarios getGerenciadorUsuarios() {
        return gerenciadorUsuarios;
    }

    /**
     * Retorna o reconhecedor facial (usado para exibir informações técnicas no Main).
     */
    public ReconhecedorFacial getReconhecedor() {
        return reconhecedor;
    }

    /**
     * Limpa e reinicializa o sistema de reconhecimento.
     */
    public void limparSistema() {
        this.gerenciadorUsuarios = new GerenciadorUsuarios();
        this.reconhecedor = new ReconhecedorFacial(this.tipoAlgoritmo, this.caminhoCascadeXML);
    }
}
