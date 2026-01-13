package com.leandrocoelho.portfoliopessoal.controller;

import com.leandrocoelho.portfoliopessoal.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Tag(name = "Currículo", description = "Endpoint para gestão e download do currículo")
public class ResumeController {
    private final PdfService pdfService;

    @GetMapping
    @Operation(summary = "Baixar currículo em PDF", description = "Converte o Markdown interno em um PDF estilizado e retorna o arquivo para download.")
    public ResponseEntity<byte[]> downloadResume(){

        try {
            byte[] pdfBytes = pdfService.generateResumePdf();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"Leandro_Coelho_CV.pdf\"")
                    .body(pdfBytes);


        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
