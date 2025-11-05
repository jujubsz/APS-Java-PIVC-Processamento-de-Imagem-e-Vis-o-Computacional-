package br.com.reconhecimento.entidade;

import br.com.reconhecimento.modelo.NivelAcesso;

/**
 * Representa uma informação com nível de acesso restrito
 */
public class Informacao {
    
    private String titulo;
    private String conteudo;
    private NivelAcesso nivelRequerido;
    private String categoria;
    
    public Informacao(String titulo, String conteudo, NivelAcesso nivelRequerido, String categoria) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.nivelRequerido = nivelRequerido;
        this.categoria = categoria;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public NivelAcesso getNivelRequerido() {
        return nivelRequerido;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    /**
     * Retorna conteúdo censurado se não tiver permissão
     */
    public String getConteudoParaNivel(NivelAcesso nivelUsuario) {
        if (nivelUsuario.podeAcessar(nivelRequerido)) {
            return conteudo;
        } else {
            return "[ACESSO NEGADO - Requer nível " + nivelRequerido.getNivel() + " ou superior]";
        }
    }
    
    @Override
    public String toString() {
        return String.format("[Nível %d] %s (%s)", 
            nivelRequerido.getNivel(), titulo, categoria);
    }
}