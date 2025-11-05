package br.com.reconhecimento.detector;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Classe responsável pela detecção de faces em imagens
 */
public class DetectorFaces {
    
    private CascadeClassifier cascadeClassifier;

    public DetectorFaces(String nomeArquivo) {
        try {
            // Tenta carregar do resources (dentro do JAR)
            String caminhoTemp = extrairDoResources(nomeArquivo);
            cascadeClassifier = new CascadeClassifier(caminhoTemp);
            
            if (cascadeClassifier.empty()) {
                throw new RuntimeException("Erro: Não foi possível carregar o detector de faces!");
            }
            
            System.out.println("✓ Haar Cascade carregado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("✗ Erro ao carregar Haar Cascade: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro: Não foi possível carregar o detector de faces!", e);
        }
    }
    
    /**
     * Extrai o arquivo do resources para um arquivo temporário
     */
    private String extrairDoResources(String nomeArquivo) throws Exception {
        // Tenta carregar do classpath
        InputStream is = getClass().getClassLoader().getResourceAsStream(nomeArquivo);
        
        if (is == null) {
            // Se não encontrar, tenta diretamente do sistema de arquivos
            File arquivo = new File(nomeArquivo);
            if (arquivo.exists()) {
                System.out.println("✓ Arquivo encontrado no sistema: " + arquivo.getAbsolutePath());
                return arquivo.getAbsolutePath();
            }
            throw new Exception("Arquivo não encontrado: " + nomeArquivo);
        }
        
        // Cria arquivo temporário
        File tempFile = File.createTempFile("haarcascade", ".xml");
        tempFile.deleteOnExit();
        
        // Copia do resources para o arquivo temporário
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        is.close();
        
        System.out.println("✓ Arquivo extraído para: " + tempFile.getAbsolutePath());
        return tempFile.getAbsolutePath();
    }

    /**
     * Detecta faces em uma imagem
     */
    public Rect[] detectar(Mat imagem) {
        Mat imagemCinza = new Mat();
        Imgproc.cvtColor(imagem, imagemCinza, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(imagemCinza, imagemCinza);
        
        MatOfRect faces = new MatOfRect();
        cascadeClassifier.detectMultiScale(
            imagemCinza,
            faces,
            1.1,      // scaleFactor
            10,       // minNeighbors
            0,        // flags
            new Size(20, 20),  // minSize
            new Size()         // maxSize
        );
        
        return faces.toArray();
    }

    /**
     * Extrai e redimensiona a região da face
     */
    public Mat extrairFace(Mat imagem, Rect face, Size tamanho) {
        Mat faceRecortada = new Mat(imagem, face);
        Mat faceRedimensionada = new Mat();
        Imgproc.resize(faceRecortada, faceRedimensionada, tamanho);
        return faceRedimensionada;
    }
}