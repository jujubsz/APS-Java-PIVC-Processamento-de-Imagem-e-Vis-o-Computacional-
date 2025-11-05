package br.com.reconhecimento.servico;

import br.com.reconhecimento.entidade.Informacao;
import br.com.reconhecimento.entidade.Usuario;
import br.com.reconhecimento.modelo.NivelAcesso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia o controle de acesso às informações
 */
public class ControleAcesso {
    
    private List<Informacao> informacoes;
    
    public ControleAcesso() {
        this.informacoes = new ArrayList<>();
        carregarInformacoesExemplo();
    }
    
    /**
     * Carrega informações de exemplo para demonstração
     */
    private void carregarInformacoesExemplo() {
        // Nível 1 - Público
        informacoes.add(new Informacao(
            "Horário de Funcionamento",
            "Segunda a Sexta: 8h às 18h | Sábado: 8h às 12h",
            NivelAcesso.PUBLICO,
            "Administrativo"
        ));
        
        informacoes.add(new Informacao(
            "Projetos Ambientais Públicos",
            "Lista de projetos: Reflorestamento da Serra, Limpeza de Rios, Educação Ambiental nas Escolas",
            NivelAcesso.PUBLICO,
            "Projetos"
        ));
        
        informacoes.add(new Informacao(
            "Contato",
            "Email: contato@meioambiente.gov.br | Tel: (11) 1234-5678",
            NivelAcesso.PUBLICO,
            "Administrativo"
        ));
        
        // Nível 2 - Diretores
        informacoes.add(new Informacao(
            "Orçamento Anual",
            "Orçamento total: R$ 50.000.000,00 | Distribuído em: Fiscalização (40%), Projetos (35%), Administrativo (25%)",
            NivelAcesso.DIRETOR,
            "Financeiro"
        ));
        
        informacoes.add(new Informacao(
            "Relatório de Fiscalizações",
            "Total de fiscalizações este mês: 47 | Multas aplicadas: 12 | Processos em andamento: 8",
            NivelAcesso.DIRETOR,
            "Fiscalização"
        ));
        
        informacoes.add(new Informacao(
            "Recursos Humanos",
            "Funcionários ativos: 234 | Licenças médicas: 5 | Processos disciplinares: 2",
            NivelAcesso.DIRETOR,
            "RH"
        ));
        
        // Nível 3 - Ministro
        informacoes.add(new Informacao(
            "Estratégia Política",
            "Prioridades para próximo trimestre: Acordo internacional de carbono, Reforma da legislação ambiental, Negociação com setor industrial",
            NivelAcesso.MINISTRO,
            "Estratégico"
        ));
        
        informacoes.add(new Informacao(
            "Informações Confidenciais",
            "Investigações em curso: 3 casos de corrupção, 2 casos de vazamento de informações. Status: Em análise pela auditoria interna.",
            NivelAcesso.MINISTRO,
            "Segurança"
        ));
        
        informacoes.add(new Informacao(
            "Plano de Contingência",
            "Cenários de crise mapeados: Desastre ambiental de grande escala, Crise política institucional, Pressão internacional. Protocolos de resposta ativados.",
            NivelAcesso.MINISTRO,
            "Estratégico"
        ));
    }
    
    /**
     * Adiciona uma nova informação ao sistema
     */
    public void adicionarInformacao(Informacao info) {
        informacoes.add(info);
    }
    
    /**
     * Lista informações acessíveis para o usuário
     */
    public List<Informacao> listarInformacoesAcessiveis(Usuario usuario) {
        if (usuario == null) {
            // Usuário não autenticado - acesso público
            return informacoes.stream()
                .filter(info -> info.getNivelRequerido() == NivelAcesso.PUBLICO)
                .collect(Collectors.toList());
        }
        
        return informacoes.stream()
            .filter(info -> usuario.podeAcessar(info.getNivelRequerido()))
            .collect(Collectors.toList());
    }
    
    /**
     * Exibe informações para o usuário com controle de acesso
     */
    public void exibirInformacoes(Usuario usuario) {
        NivelAcesso nivelUsuario = (usuario != null) ? usuario.getNivelAcesso() : NivelAcesso.PUBLICO;
        String nomeUsuario = (usuario != null) ? usuario.getNome() : "Público Geral";
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("           PAINEL DE INFORMAÇÕES - " + nomeUsuario.toUpperCase());
        System.out.println("           Nível de Acesso: " + nivelUsuario.getDescricao() + " (Nível " + nivelUsuario.getNivel() + ")");
        System.out.println("=".repeat(70));
        
        List<Informacao> infosAcessiveis = listarInformacoesAcessiveis(usuario);
        
        if (infosAcessiveis.isEmpty()) {
            System.out.println("Nenhuma informação disponível para seu nível de acesso.");
            return;
        }
        
        // Agrupar por categoria
        String categoriaAtual = "";
        for (Informacao info : infosAcessiveis) {
            if (!info.getCategoria().equals(categoriaAtual)) {
                categoriaAtual = info.getCategoria();
                System.out.println("\n┌─ " + categoriaAtual.toUpperCase() + " " + "─".repeat(60 - categoriaAtual.length()));
            }
            
            System.out.println("│");
            System.out.println("│ ▸ " + info.getTitulo() + " [Nível " + info.getNivelRequerido().getNivel() + "]");
            System.out.println("│   " + info.getConteudoParaNivel(nivelUsuario));
        }
        
        System.out.println("└" + "─".repeat(68));
        
        // Estatísticas
        long totalInfos = informacoes.size();
        long infosAcessiveisCount = infosAcessiveis.size();
        long infosBloqueadas = totalInfos - infosAcessiveisCount;
        
        System.out.println("\n📊 Estatísticas de Acesso:");
        System.out.println("   • Informações acessíveis: " + infosAcessiveisCount);
        System.out.println("   • Informações bloqueadas: " + infosBloqueadas);
        System.out.println("   • Total no sistema: " + totalInfos);
    }
    
    /**
     * Tenta acessar uma informação específica
     */
    public boolean tentarAcessar(Usuario usuario, String tituloInfo) {
        NivelAcesso nivelUsuario = (usuario != null) ? usuario.getNivelAcesso() : NivelAcesso.PUBLICO;
        
        for (Informacao info : informacoes) {
            if (info.getTitulo().equalsIgnoreCase(tituloInfo)) {
                if (usuario != null && usuario.podeAcessar(info.getNivelRequerido())) {
                    System.out.println("✓ ACESSO PERMITIDO");
                    System.out.println(info.getConteudo());
                    return true;
                } else {
                    System.out.println("✗ ACESSO NEGADO");
                    System.out.println("Seu nível: " + nivelUsuario.getNivel());
                    System.out.println("Nível requerido: " + info.getNivelRequerido().getNivel());
                    return false;
                }
            }
        }
        
        System.out.println("Informação não encontrada.");
        return false;
    }
    
    /**
     * Retorna lista de todas as informações (para uso na GUI)
     */
    public List<Informacao> getTodasInformacoes() {
        return informacoes;
    }
    
    
}