package com.progweb.trabalho.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID; // Para gerar nomes de arquivos únicos

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class ImagemUploadController {

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @PostMapping("/upload-imagem")
    public ResponseEntity<String> uploadImagem(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Por favor, selecione um arquivo para upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Garante que o diretório de upload exista
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gera um nome de arquivo único para evitar conflitos
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Caminho completo onde o arquivo será salvo
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);

            // Salva o arquivo no sistema de arquivos local
            Files.copy(file.getInputStream(), filePath);

            // seuServicoDeProduto.atualizarUrlImagem(idDoProduto, "/uploads/" + uniqueFileName);

            return new ResponseEntity<>("/uploads/" + uniqueFileName, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Falha no upload da imagem: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
