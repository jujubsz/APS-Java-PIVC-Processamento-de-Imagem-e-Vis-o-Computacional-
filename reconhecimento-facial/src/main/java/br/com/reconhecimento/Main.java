package br.com.reconhecimento;

import br.com.reconhecimento.database.BancoDados;
import br.com.reconhecimento.entidade.ResultadoReconhecimento;
import br.com.reconhecimento.entidade.Usuario;
import br.com.reconhecimento.modelo.NivelAcesso;
import br.com.reconhecimento.modelo.TipoAlgoritmo;
import br.com.reconhecimento.servico.ControleAcesso;
import br.com.reconhecimento.servico.SistemaCadastro;
import br.com.reconhecimento.servico.SistemaReconhecimentoFacial;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal - Sistema de Reconhecimento Facial com Controle de Acesso
 * Projeto Acadêmico - TCC
 */
public class Main {

	private static SistemaReconhecimentoFacial sistema;
	private static ControleAcesso controleAcesso;
	private static SistemaCadastro sistemaCadastro;
	private static BancoDados bancoDados;
	private static Scanner scanner;

	public static void main(String[] args) {
		System.out.println("╔══════════════════════════════════════════════════════════════╗");
		System.out.println("║   SISTEMA DE RECONHECIMENTO FACIAL COM CONTROLE DE ACESSO    ║");
		System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

		scanner = new Scanner(System.in);
		controleAcesso = new ControleAcesso();
		sistemaCadastro = new SistemaCadastro();
		bancoDados = sistemaCadastro.getBancoDados();

		try {
			inicializarSistema();
			menuPrincipal();

		} catch (Exception e) {
			System.err.println("\n✗✗✗ ERRO CRÍTICO ✗✗✗");
			System.err.println("Mensagem: " + e.getMessage());
			e.printStackTrace();
		} finally {
			sistemaCadastro.fechar();
			scanner.close();
		}
	}

	/**
	 * Inicializa o sistema: carrega OpenCV e treina modelo com usuários do banco
	 */
	private static void inicializarSistema() {
		System.out.println(">> Carregando OpenCV...");
		CarregadorOpenCV.carregar();

		System.out.println(">> Inicializando sistema de reconhecimento...");
		sistema = new SistemaReconhecimentoFacial(TipoAlgoritmo.FISHER, "haarcascade_frontalface_default.xml");

		// Verificar se existem usuários no banco
		int totalUsuarios = bancoDados.contarUsuarios();

		if (totalUsuarios == 0) {
			System.out.println("\n⚠ Nenhum usuário cadastrado no banco de dados!");
			System.out.println("   Use a opção 4 no menu para cadastrar usuários.");
			System.out.println("   OU use a opção 6 para carregar usuários de exemplo.\n");
			return;
		}

		System.out.println("\n" + "=".repeat(60));
		System.out.println("            CARREGANDO USUÁRIOS DO BANCO");
		System.out.println("=".repeat(60));

		// Carregar todos os usuários do banco
		List<Usuario> usuarios = bancoDados.listarTodosUsuarios();

		for (Usuario usuario : usuarios) {
			if (usuario.getCaminhosFotos().isEmpty()) {
				System.out.println("⚠ Usuário '" + usuario.getNome() + "' não tem fotos cadastradas!");
				continue;
			}

			sistema.adicionarUsuario(usuario.getNome(), usuario.getCargo(), usuario.getNivelAcesso(),
					usuario.getCaminhosFotos());

			System.out.println("\n>> Treinando modelo...");
			sistema.treinar();

			System.out.println("\n✓ Sistema inicializado com sucesso!");
			System.out.println("   Usuários carregados: " + totalUsuarios);
		}
	}

	/**
	 * Menu principal interativo
	 */
	private static void menuPrincipal() {
		while (true) {
			System.out.println("\n" + "=".repeat(60));
			System.out.println("                      MENU PRINCIPAL");
			System.out.println("=".repeat(60));
			System.out.println("1 - Fazer Login (Reconhecimento Facial Níveis 2 e 3)");
			System.out.println("2 - Acessar como Público Geral (Nível 1)");
			System.out.println("3 - Ver Informações do Sistema");
			System.out.println("4 - Cadastrar Novo Usuário");
			System.out.println("5 - Listar Usuários Cadastrados");
			System.out.println("6 - Carregar Usuários de Exemplo");
			System.out.println("7 - Remover Usuário");
			System.out.println("8 - Retreinar Sistema");
			System.out.println("0 - Sair");
			System.out.print("\nEscolha uma opção: ");

			String opcao = scanner.nextLine();

			switch (opcao) {
			case "1":
				fazerLogin();
				break;
			case "2":
				acessarComoPublico();
				break;
			case "3":
				exibirInformacoesSistema();
				break;
			case "4":
				sistemaCadastro.menuCadastro();
				perguntarRetreinar();
				break;
			case "5":
				sistemaCadastro.listarUsuarios();
				break;
			case "6":
				carregarUsuariosExemplo();
				break;
			case "7":
				sistemaCadastro.removerUsuario();
				perguntarRetreinar();
				break;
			case "8":
				retreinarSistema();
				break;
			case "0":
				System.out.println("\n✓ Encerrando sistema...");
				System.out.println("Obrigado por usar o Sistema de Reconhecimento Facial!");
				return;
			default:
				System.out.println("✗ Opção inválida! Tente novamente.");
			}
		}
	}

	/**
	 * Realiza login através de reconhecimento facial
	 */
	private static void fazerLogin() {
		if (bancoDados.contarUsuarios() == 0) {
			System.out.println("\n⚠ Não há usuários cadastrados para reconhecer!");
			System.out.println("   Use a opção 4 para cadastrar usuários primeiro.");
			return;
		}

		System.out.println("\n" + "=".repeat(60));
		System.out.println("                  RECONHECIMENTO FACIAL");
		System.out.println("=".repeat(60));
		System.out.print("Digite o caminho da imagem para reconhecimento: ");
		String caminhoImagem = scanner.nextLine();

		try {
			ResultadoReconhecimento resultado = sistema.reconhecer(caminhoImagem);

			if (!resultado.isFaceDetectada()) {
				System.out.println("\n✗ Nenhuma face detectada na imagem!");
				System.out.println("Acessando como público geral...");
				controleAcesso.exibirInformacoes(null);
				return;
			}

			// Buscar usuário baseado no nome reconhecido
			Usuario usuario = null;
			for (Usuario u : sistema.getGerenciadorUsuarios().getUsuarios()) {
				if (u.getNome().equals(resultado.getNomeUsuario())) {
					usuario = u;
					break;
				}
			}

			System.out.println("\n📸 Resultado do Reconhecimento:");
			System.out.println("   " + resultado);

			if (resultado.getConfianca() > 60 && usuario != null) {
				System.out.println("\n✓ LOGIN BEM-SUCEDIDO!");
				System.out.println("   Bem-vindo(a), " + usuario.getNome());
				System.out.println("   Cargo: " + usuario.getCargo());
				System.out.println("   Nível de Acesso: " + usuario.getNivelAcesso().getDescricao());

				controleAcesso.exibirInformacoes(usuario);
			} else {
				System.out.println("\n⚠ CONFIANÇA BAIXA OU USUÁRIO DESCONHECIDO");
				System.out.println("Acessando como público geral...");
				controleAcesso.exibirInformacoes(null);
			}

		} catch (Exception e) {
			System.err.println("\n✗ Erro ao processar imagem: " + e.getMessage());
			System.out.println("Acessando como público geral...");
			controleAcesso.exibirInformacoes(null);
		}
	}

	/**
	 * Acessa o sistema como público geral (sem autenticação)
	 */
	private static void acessarComoPublico() {
		System.out.println("\n✓ Acessando como Público Geral (Nível 1)...");
		controleAcesso.exibirInformacoes(null);
	}

	/**
	 * Exibe informações técnicas do sistema
	 */
	private static void exibirInformacoesSistema() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("                INFORMAÇÕES DO SISTEMA");
		System.out.println("=".repeat(60));
		System.out.println("Algoritmo: " + sistema.getReconhecedor().getAlgoritmo().getDescricao());
		System.out.println("Usuários cadastrados: " + bancoDados.contarUsuarios());

		if (sistema.getReconhecedor().isTreinado()) {
			System.out.println("Imagens treinadas: " + sistema.getReconhecedor().getTotalImagensTreinamento());
			System.out.println("Status: ✓ Modelo treinado e pronto");
		} else {
			System.out.println("Status: ⚠ Modelo precisa ser treinado");
		}

		System.out.println("\n┌─ USUÁRIOS CADASTRADOS NO BANCO ────────────────────────────");
		List<Usuario> usuarios = bancoDados.listarTodosUsuarios();
		if (usuarios.isEmpty()) {
			System.out.println("│ Nenhum usuário cadastrado");
		} else {
			for (Usuario usuario : usuarios) {
				System.out.println("│ • " + usuario);
			}
		}
		System.out.println("└" + "─".repeat(59));

		System.out.println("\n┌─ NÍVEIS DE ACESSO DISPONÍVEIS ─────────────────────────────");
		for (NivelAcesso nivel : NivelAcesso.values()) {
			System.out.println("│ • " + nivel);
		}
		System.out.println("└" + "─".repeat(59));
	}

	/**
	 * Carrega usuários de exemplo para teste
	 */
	private static void carregarUsuariosExemplo() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("           CARREGAR USUÁRIOS DE EXEMPLO");
		System.out.println("=".repeat(60));
		System.out.println("Esta opção vai criar usuários de exemplo usando as pastas:");
		System.out.println("  • dataset/Ministro/");
		System.out.println("  • dataset/Diretor1/");
		System.out.println("  • dataset/Diretor2/");
		System.out.println();
		System.out.print("Confirma? (s/n): ");

		String confirmacao = scanner.nextLine();
		if (!confirmacao.equalsIgnoreCase("s")) {
			System.out.println("Operação cancelada.");
			return;
		}

		verificarEstruturaDataset();

		// Ministro
		int idMinistro = bancoDados.cadastrarUsuario("Ministro do Meio Ambiente", "Ministro", NivelAcesso.MINISTRO);
		adicionarFotosPasta(idMinistro, "dataset/Ministro/");

		// Diretor 1
		int idDiretor1 = bancoDados.cadastrarUsuario("Diretor do Meio Ambiente 1", "Diretor", NivelAcesso.DIRETOR);
		adicionarFotosPasta(idDiretor1, "dataset/Diretor1/");

		// Diretor 2
		int idDiretor2 = bancoDados.cadastrarUsuario("Diretor do Meio Ambiente 2", "Diretor", NivelAcesso.DIRETOR);
		adicionarFotosPasta(idDiretor2, "dataset/Diretor2/");

		System.out.println("\n✓ Usuários de exemplo carregados!");
		System.out.println("   Agora retreine o sistema (opção 8).");
	}

	/**
	 * Adiciona fotos de uma pasta ao banco de dados
	 */
	private static void adicionarFotosPasta(int usuarioId, String caminhoPasta) {
		File pasta = new File(caminhoPasta);
		File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg")
				|| name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".png"));

		if (arquivos != null && arquivos.length > 0) {
			for (File arquivo : arquivos) {
				bancoDados.adicionarFoto(usuarioId, arquivo.getAbsolutePath());
			}
			System.out.println("✓ " + arquivos.length + " foto(s) adicionada(s) de " + caminhoPasta);
		} else {
			System.out.println("⚠ Nenhuma foto encontrada em " + caminhoPasta);
		}
	}

	/**
	 * Pergunta se deseja retreinar o sistema
	 */
	private static void perguntarRetreinar() {
		System.out.print("\nDeseja retreinar o sistema agora? (s/n): ");
		String resposta = scanner.nextLine();

		if (resposta.equalsIgnoreCase("s")) {
			retreinarSistema();
		} else {
			System.out.println("⚠ Lembre-se de retreinar antes de fazer login (opção 8)!");
		}
	}

	/**
	 * Retreina o sistema com os usuários atuais do banco
	 */
	private static void retreinarSistema() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("                RETREINANDO SISTEMA");
		System.out.println("=".repeat(60));

		// Reinicializar sistema
		sistema = new SistemaReconhecimentoFacial(TipoAlgoritmo.FISHER, "haarcascade_frontalface_default.xml");

		List<Usuario> usuarios = bancoDados.listarTodosUsuarios();

		if (usuarios.isEmpty()) {
			System.out.println("✗ Nenhum usuário para treinar!");
			return;
		}

		int totalFotos = 0;
		for (Usuario usuario : usuarios) {
			if (usuario.getCaminhosFotos().isEmpty()) {
				System.out.println("⚠ Ignorando '" + usuario.getNome() + "' (sem fotos)");
				continue;
			}

			sistema.adicionarUsuario(usuario.getNome(), usuario.getCargo(), usuario.getNivelAcesso(),
					usuario.getCaminhosFotos());

			totalFotos += usuario.getCaminhosFotos().size();
		}

		System.out.println("\n>> Treinando modelo...");
		sistema.treinar();

		System.out.println("\n✓ Sistema retreinado com sucesso!");
		System.out.println("   Usuários: " + usuarios.size());
		System.out.println("   Total de fotos: " + totalFotos);
	}

	/**
	 * Verifica se a estrutura de pastas existe (para usuários de exemplo)
	 */
	private static void verificarEstruturaDataset() {
		String[] pastasNecessarias = { "dataset/Ministro", "dataset/Diretor1", "dataset/Diretor2" };

		boolean todasExistem = true;
		for (String pasta : pastasNecessarias) {
			File dir = new File(pasta);
			if (!dir.exists() || !dir.isDirectory()) {
				System.err.println("   ✗ " + pasta + " não encontrada!");
				todasExistem = false;
			}
		}

		if (!todasExistem) {
			System.err.println("\n⚠ ATENÇÃO: Algumas pastas não foram encontradas!");
			System.err.println("Crie a estrutura ou use a opção 4 para cadastrar manualmente.");
			throw new RuntimeException("Estrutura de pastas incompleta!");
		}
	}
}