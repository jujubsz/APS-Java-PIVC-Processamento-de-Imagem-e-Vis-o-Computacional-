package br.com.reconhecimento.servico;

import br.com.reconhecimento.entidade.Usuario;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorUsuarios {
    private final List<Usuario> usuarios;

    public GerenciadorUsuarios() {
        this.usuarios = new ArrayList<>();
    }

    public void adicionarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
