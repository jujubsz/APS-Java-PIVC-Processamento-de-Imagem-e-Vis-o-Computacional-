package br.com.reconhecimento.database;

import br.com.reconhecimento.entidade.Usuario;
import br.com.reconhecimento.modelo.NivelAcesso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia o banco de dados SQLite do sistema
 */
public class BancoDados {

    private static final String URL = "jdbc:sqlite:reconhecimento_facial.db";
    private Connection conexao;

    public BancoDados() {
        conectar();
        criarTabelas();
    }

    private void conectar() {
        try {
            conexao = DriverManager.getConnection(URL);
            System.out.println("✓ Conectado ao banco de dados SQLite");
        } catch (SQLException e) {
            System.err.println("✗ Erro ao conectar ao banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void criarTabelas() {
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "cargo TEXT NOT NULL, " +
                "nivel_acesso INTEGER NOT NULL, " +
                "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String sqlFotos = "CREATE TABLE IF NOT EXISTS fotos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER NOT NULL, " +
                "caminho TEXT NOT NULL, " +
                "data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ")";

        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlFotos);
            System.out.println("✓ Tabelas criadas/verificadas");
        } catch (SQLException e) {
            System.err.println("✗ Erro ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cadastra um novo usuário e retorna o ID gerado
     */
    public int cadastrarUsuario(String nome, String cargo, NivelAcesso nivelAcesso) {
        String sql = "INSERT INTO usuarios (nome, cargo, nivel_acesso) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cargo);
            pstmt.setInt(3, nivelAcesso.getNivel());
            pstmt.executeUpdate();

            // Recupera o ID do último registro inserido (SQLite)
            try (Statement stmt = conexao.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("✓ Usuário cadastrado: " + nome + " (ID: " + id + ")");
                    return id;
                }
            }

        } catch (SQLException e) {
            System.err.println("✗ Erro ao cadastrar usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void adicionarFoto(int usuarioId, String caminho) {
        String sql = "INSERT INTO fotos (usuario_id, caminho) VALUES (?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, caminho);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("✗ Erro ao adicionar foto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Usuario buscarUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String nome = rs.getString("nome");
                String cargo = rs.getString("cargo");
                int nivelNum = rs.getInt("nivel_acesso");

                NivelAcesso nivel = NivelAcesso.PUBLICO;
                for (NivelAcesso n : NivelAcesso.values()) {
                    if (n.getNivel() == nivelNum) {
                        nivel = n;
                        break;
                    }
                }

                Usuario usuario = new Usuario(id, nome, nivel, cargo);
                List<String> fotos = buscarFotosUsuario(id);
                for (String foto : fotos) {
                    usuario.adicionarFoto(foto);
                }
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<String> buscarFotosUsuario(int usuarioId) {
        List<String> fotos = new ArrayList<>();
        String sql = "SELECT caminho FROM fotos WHERE usuario_id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                fotos.add(rs.getString("caminho"));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar fotos: " + e.getMessage());
            e.printStackTrace();
        }
        return fotos;
    }

    public List<Usuario> listarTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id FROM usuarios ORDER BY id";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Usuario usuario = buscarUsuarioPorId(id);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    public void removerUsuario(int id) {
        try {
            conexao.setAutoCommit(false);
            String sqlFotos = "DELETE FROM fotos WHERE usuario_id = ?";
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlFotos)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            String sqlUsuario = "DELETE FROM usuarios WHERE id = ?";
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlUsuario)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            conexao.commit();
            System.out.println("✓ Usuário removido (ID: " + id + ")");
        } catch (SQLException e) {
            try { conexao.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("✗ Erro ao remover usuário: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { conexao.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public int contarUsuarios() {
        String sql = "SELECT COUNT(*) as total FROM usuarios";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public void limparBanco() {
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute("DELETE FROM fotos");
            stmt.execute("DELETE FROM usuarios");
            System.out.println("✓ Banco de dados limpo");
        } catch (SQLException e) {
            System.err.println("✗ Erro ao limpar banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isConectado() {
        try { return conexao != null && !conexao.isClosed(); }
        catch (SQLException e) { return false; }
    }

    public void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("✓ Conexão com banco fechada");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao fechar conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
