package br.com.reconhecimento.modelo;

import br.com.reconhecimento.entidade.ResultadoReconhecimento;
import br.com.reconhecimento.entidade.Usuario;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * Reconhecedor facial usando LBPH com suporte a atualização incremental.
 * Métodos principais:
 *  - treinar(List<Usuario> usuarios, boolean atualizar)
 *  - prever(String caminhoImagem)
 *  - reconhecer(String caminhoImagem)
 *  - salvarModelo(String caminho)
 *  - carregarModelo(String caminho)
 */
public class ReconhecedorFacial {
    private TipoAlgoritmo algoritmo;
    private String caminhoCascadeXML;
    private boolean treinado;
    private int totalImagensTreinamento;
    private FaceRecognizer faceRecognizer;
    private final List<String> nomesUsuarios; // índice -> nome

    public ReconhecedorFacial(TipoAlgoritmo algoritmo, String caminhoCascadeXML) {
        this.algoritmo = algoritmo;
        this.caminhoCascadeXML = caminhoCascadeXML;
        this.treinado = false;
        this.totalImagensTreinamento = 0;
        this.faceRecognizer = LBPHFaceRecognizer.create();
        this.nomesUsuarios = new ArrayList<>();
    }

    // Setter manual, caso precise marcar como treinado
    public void setTreinado(boolean treinado) {
        this.treinado = treinado;
    }

    /**
     * Treina ou atualiza o modelo com os usuários informados.
     * @param usuarios lista de usuários com fotos
     * @param atualizar se true, adiciona novos usuários/fotos ao modelo existente
     */
    public void treinar(List<Usuario> usuarios, boolean atualizar) {
        List<Mat> imagens = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        // Só limpa se for treinar do zero
        if (!atualizar) {
            nomesUsuarios.clear();
            totalImagensTreinamento = 0;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getCaminhosFotos() == null || usuario.getCaminhosFotos().isEmpty()) {
                System.out.println("⚠ Ignorando " + usuario.getNome() + " (sem fotos)");
                continue;
            }

            // se o usuário já existe, usa o mesmo índice
            int label = nomesUsuarios.indexOf(usuario.getNome());
            if (label == -1) {
                nomesUsuarios.add(usuario.getNome());
                label = nomesUsuarios.size() - 1;
            }

            for (String caminho : usuario.getCaminhosFotos()) {
                Mat imgColor = Imgcodecs.imread(caminho);
                if (imgColor == null || imgColor.empty()) {
                    System.err.println("✗ Não foi possível carregar: " + caminho);
                    continue;
                }

                Mat imgGray = new Mat();
                Imgproc.cvtColor(imgColor, imgGray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.resize(imgGray, imgGray, new Size(200, 200));

                imagens.add(imgGray);
                labels.add(label);
                totalImagensTreinamento++;
            }
        }

        if (imagens.isEmpty()) {
            System.err.println("✗ Nenhuma imagem válida para treinar/atualizar.");
            return;
        }

        MatOfInt matLabels = new MatOfInt(toIntArray(labels));

        if (atualizar && treinado) {
            System.out.println("↻ Atualizando modelo existente...");
            faceRecognizer.update(imagens, matLabels);
        } else {
            System.out.println("⚙ Treinando modelo do zero...");
            faceRecognizer.train(imagens, matLabels);
            treinado = true;
        }

        System.out.println("✓ Treinamento finalizado. Total de imagens: " + totalImagensTreinamento);
    }

    /**
     * Treina apenas um usuário (atalho).
     */
    public void treinar(Usuario usuario) {
        if (usuario == null || usuario.getCaminhosFotos() == null || usuario.getCaminhosFotos().isEmpty()) {
            System.err.println("Usuário inválido ou sem imagens: " + (usuario != null ? usuario.getNome() : "null"));
            return;
        }

        List<Usuario> lista = new ArrayList<>();
        lista.add(usuario);
        treinar(lista, true); // adiciona incrementalmente
    }

    /**
     * Faz previsão do nome com base em uma imagem.
     */
    public String prever(String caminhoImagem) {
        if (!treinado) {
            System.err.println("Aviso: modelo não treinado.");
            return null;
        }

        Mat imgColor = Imgcodecs.imread(caminhoImagem);
        if (imgColor == null || imgColor.empty()) {
            System.err.println("✗ Não foi possível carregar a imagem: " + caminhoImagem);
            return null;
        }

        Mat imgGray = new Mat();
        Imgproc.cvtColor(imgColor, imgGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(imgGray, imgGray, new Size(200, 200));

        int[] rotulo = new int[1];
        double[] confianca = new double[1];
        faceRecognizer.predict(imgGray, rotulo, confianca);

        int label = rotulo[0];
        String nome = (label >= 0 && label < nomesUsuarios.size()) ? nomesUsuarios.get(label) : "Desconhecido";
        double confPorcentagem = Math.max(0.0, 100.0 - confianca[0]);

        System.out.println("Previsão: " + nome + " (dist=" + confianca[0] + ", conf≈" + String.format("%.2f", confPorcentagem) + "%)");
        return nome;
    }

    /**
     * Retorna um objeto ResultadoReconhecimento com nome e confiança.
     */
    public ResultadoReconhecimento reconhecer(String caminhoImagem) {
        if (caminhoImagem == null || caminhoImagem.isBlank()) {
            return new ResultadoReconhecimento("Desconhecido", 0.0, false);
        }

        Mat imgColor = Imgcodecs.imread(caminhoImagem);
        if (imgColor == null || imgColor.empty()) {
            return new ResultadoReconhecimento("Desconhecido", 0.0, false);
        }

        Mat imgGray = new Mat();
        Imgproc.cvtColor(imgColor, imgGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(imgGray, imgGray, new Size(200, 200));

        int[] rotulo = new int[1];
        double[] confianca = new double[1];
        faceRecognizer.predict(imgGray, rotulo, confianca);

        int label = rotulo[0];
        String nome = (label >= 0 && label < nomesUsuarios.size()) ? nomesUsuarios.get(label) : "Desconhecido";
        double confPorcentagem = Math.max(0.0, 100.0 - confianca[0]);

        boolean faceDetectada = label >= 0;
        return new ResultadoReconhecimento(nome, confPorcentagem, faceDetectada);
    }

    // Persistência do modelo
    public void salvarModelo(String caminhoArquivo) {
        if (treinado) {
            faceRecognizer.save(caminhoArquivo);
            System.out.println("💾 Modelo salvo em: " + caminhoArquivo);
        } else {
            System.out.println("✗ Nenhum modelo treinado para salvar.");
        }
    }

    public void carregarModelo(String caminhoArquivo) {
        faceRecognizer.read(caminhoArquivo);
        treinado = true;
        System.out.println("📂 Modelo carregado de: " + caminhoArquivo);
    }

    // getters auxiliares
    public TipoAlgoritmo getAlgoritmo() { return algoritmo; }
    public boolean isTreinado() { return treinado; }
    public int getTotalImagensTreinamento() { return totalImagensTreinamento; }

    // conversão simples de lista para array
    private int[] toIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }
}
