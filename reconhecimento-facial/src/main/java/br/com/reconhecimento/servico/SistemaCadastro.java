package br.com.reconhecimento.servico;

import br.com.reconhecimento.database.BancoDados;
import br.com.reconhecimento.entidade.Usuario;
import br.com.reconhecimento.modelo.NivelAcesso;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

/**
 * Sistema de cadastro de usuários com upload de fotos
 */
public class SistemaCadastro {
    
    private BancoDados bancoDados;
    private Scanner scanner;
    private static final String PASTA_FOTOS = "fotos_cadastradas/";
    
    public SistemaCadastro() {
        this.bancoDados = new BancoDados();
        this.scanner = new Scanner(System.in);
        criarPastaFotos();
    }
    
    private void criarPastaFotos() {
        File pasta = new File(PASTA_FOTOS);
        if (!pasta.exists()) {
            pasta.mkdirs();
            System.out.println("✓ Pasta de fotos criada: " + PASTA_FOTOS);
        }
    }
    
    /**
     * Menu de cadastro interativo
     */
    public void menuCadastro() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              CADASTRO DE NOVO USUÁRIO");
        System.out.println("=".repeat(60));
        
        // Nome
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine().trim();
        
        if (nome.isEmpty()) {
            System.out.println("✗ Nome não pode ser vazio!");
            return;
        }
        
        // Cargo
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine().trim();
        
        // Nível de acesso
        System.out.println("\nNíveis de acesso disponíveis:");
        System.out.println("1 - Público Geral");
        System.out.println("2 - Diretor");
        System.out.println("3 - Ministro");
        System.out.print("Escolha o nível: ");
        
        int nivelEscolhido = 1;
        try {
            nivelEscolhido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("⚠ Valor inválido, usando nível 1 (Público)");
        }
        
        NivelAcesso nivelAcesso = NivelAcesso.PUBLICO;
        switch (nivelEscolhido) {
            case 2:
                nivelAcesso = NivelAcesso.DIRETOR;
                break;
            case 3:
                nivelAcesso = NivelAcesso.MINISTRO;
                break;
            default:
                nivelAcesso = NivelAcesso.PUBLICO;
        }
        
        // Cadastrar no banco
        int usuarioId = bancoDados.cadastrarUsuario(nome, cargo, nivelAcesso);
        
        if (usuarioId == -1) {
            System.out.println("✗ Erro ao cadastrar usuário!");
            return;
        }
        
        // Upload de fotos
        System.out.println("\n>> Agora vamos adicionar fotos do usuário");
        System.out.println("   Recomendado: 3 a 10 fotos com diferentes ângulos e iluminações");
        
        adicionarFotos(usuarioId, nome);
        
        System.out.println("\n✓ Usuário cadastrado com sucesso!");
        System.out.println("ID: " + usuarioId);
        System.out.println("Nome: " + nome);
        System.out.println("Cargo: " + cargo);
        System.out.println("Nível: " + nivelAcesso.getDescricao());
    }
    
    /**
     * Adiciona fotos de um usuário
     */
    private void adicionarFotos(int usuarioId, String nomeUsuario) {
        String pastaUsuario = PASTA_FOTOS + "usuario_" + usuarioId + "/";
        new File(pastaUsuario).mkdirs();
        
        int fotoNum = 1;
        
        while (true) {
            System.out.print("\nCaminho da foto " + fotoNum + " (ou 'fim' para terminar): ");
            String caminhoOrigem = scanner.nextLine().trim();
            
            if (caminhoOrigem.equalsIgnoreCase("fim")) {
                if (fotoNum == 1) {
                    System.out.println("⚠ Você precisa adicionar pelo menos 1 foto!");
                    continue;
                }
                break;
            }
            
            File arquivoOrigem = new File(caminhoOrigem);
            
            if (!arquivoOrigem.exists()) {
                System.out.println("✗ Arquivo não encontrado: " + caminhoOrigem);
                continue;
            }
            
            if (!caminhoOrigem.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
                System.out.println("✗ Formato inválido! Use JPG, JPEG ou PNG");
                continue;
            }
            
            try {
                // Copiar foto para pasta do sistema
                String extensao = caminhoOrigem.substring(caminhoOrigem.lastIndexOf("."));
                String nomeArquivo = "foto" + fotoNum + extensao;
                String caminhoDestino = pastaUsuario + nomeArquivo;
                
                Path origem = Paths.get(caminhoOrigem);
                Path destino = Paths.get(caminhoDestino);
                Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
                
                // Salvar no banco
                bancoDados.adicionarFoto(usuarioId, caminhoDestino);
                
                System.out.println("✓ Foto " + fotoNum + " adicionada!");
                fotoNum++;
                
            } catch (IOException e) {
                System.err.println("✗ Erro ao copiar foto: " + e.getMessage());
            }
        }
        
        System.out.println("\n✓ Total de fotos adicionadas: " + (fotoNum - 1));
    }
    
    /**
     * Lista todos os usuários cadastrados
     */
    public void listarUsuarios() {
        List<Usuario> usuarios = bancoDados.listarTodosUsuarios();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              USUÁRIOS CADASTRADOS");
        System.out.println("=".repeat(60));
        
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        
        for (Usuario usuario : usuarios) {
            System.out.println("\n┌─ ID: " + usuario.getId() + " ─────────────────────────────────");
            System.out.println("│ Nome: " + usuario.getNome());
            System.out.println("│ Cargo: " + usuario.getCargo());
            System.out.println("│ Nível: " + usuario.getNivelAcesso().getDescricao());
            System.out.println("│ Fotos: " + usuario.getTotalFotos());
            System.out.println("└" + "─".repeat(59));
        }
        
        System.out.println("\nTotal: " + usuarios.size() + " usuário(s)");
    }
    
    /**
     * Remove um usuário
     */
    public void removerUsuario() {
        listarUsuarios();
        
        System.out.print("\nID do usuário a remover (ou 0 para cancelar): ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            
            if (id == 0) {
                System.out.println("Operação cancelada.");
                return;
            }
            
            Usuario usuario = bancoDados.buscarUsuarioPorId(id);
            if (usuario == null) {
                System.out.println("✗ Usuário não encontrado!");
                return;
            }
            
            System.out.print("Confirma remoção de '" + usuario.getNome() + "'? (s/n): ");
            String confirmacao = scanner.nextLine();
            
            if (confirmacao.equalsIgnoreCase("s")) {
                bancoDados.removerUsuario(id);
                
                // Remover pasta de fotos
                String pastaUsuario = PASTA_FOTOS + "usuario_" + id + "/";
                File pasta = new File(pastaUsuario);
                if (pasta.exists()) {
                    deleteDirectory(pasta);
                }
            } else {
                System.out.println("Operação cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("✗ ID inválido!");
        }
    }
    
    /**
     * Remove diretório recursivamente
     */
    private void deleteDirectory(File directory) {
        File[] arquivos = directory.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    deleteDirectory(arquivo);
                } else {
                    arquivo.delete();
                }
            }
        }
        directory.delete();
    }
    
    public BancoDados getBancoDados() {
        return bancoDados;
    }
    
    public void fechar() {
        bancoDados.fechar();
    }
}