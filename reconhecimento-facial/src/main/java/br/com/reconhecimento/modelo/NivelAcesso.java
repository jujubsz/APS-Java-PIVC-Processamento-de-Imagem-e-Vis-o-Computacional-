package br.com.reconhecimento.modelo;

/**
 * Enum que define os níveis de acesso do sistema
 */
public enum NivelAcesso {
    PUBLICO(1, "Público Geral", "Acesso básico - Informações públicas"),
    DIRETOR(2, "Diretor", "Acesso intermediário - Informações internas"),
    MINISTRO(3, "Ministro", "Acesso total - Informações confidenciais");
    
    private final int nivel;
    private final String descricao;
    private final String permissoes;
    
    NivelAcesso(int nivel, String descricao, String permissoes) {
        this.nivel = nivel;
        this.descricao = descricao;
        this.permissoes = permissoes;
    }
    
    public int getNivel() {
        return nivel;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getPermissoes() {
        return permissoes;
    }
    
    /**
     * Verifica se este nível tem permissão para acessar outro nível
     */
    public boolean podeAcessar(NivelAcesso nivelRequerido) {
        return this.nivel >= nivelRequerido.nivel;
    }
    
    @Override
    public String toString() {
        return String.format("Nível %d - %s: %s", nivel, descricao, permissoes);
    }
}