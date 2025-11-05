package br.com.reconhecimento.entidade;

import br.com.reconhecimento.modelo.NivelAcesso;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um usuário no sistema
 */
public class Usuario {
    
    private int id;
    private String nome;
    private List<String> caminhosFotos;
    private NivelAcesso nivelAcesso;  // NOVO!
    private String cargo;              // NOVO!

    public Usuario(int id, String nome, NivelAcesso nivelAcesso, String cargo) {
        this.id = id;
        this.nome = nome;
        this.caminhosFotos = new ArrayList<>();
        this.nivelAcesso = nivelAcesso;
        this.cargo = cargo;
    }

    public void adicionarFoto(String caminho) {
        caminhosFotos.add(caminho);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public List<String> getCaminhosFotos() {
        return caminhosFotos;
    }

    public int getTotalFotos() {
        return caminhosFotos.size();
    }
    
    public NivelAcesso getNivelAcesso() {
        return nivelAcesso;
    }
    
    public String getCargo() {
        return cargo;
    }
    
    /**
     * Verifica se o usuário pode acessar determinado nível
     */
    public boolean podeAcessar(NivelAcesso nivelRequerido) {
        return nivelAcesso.podeAcessar(nivelRequerido);
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Nome: %s, Cargo: %s, Nível: %d, Fotos: %d", 
            id, nome, cargo, nivelAcesso.getNivel(), caminhosFotos.size());
    }

	}
