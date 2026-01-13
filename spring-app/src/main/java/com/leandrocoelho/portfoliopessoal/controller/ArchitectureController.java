package com.leandrocoelho.portfoliopessoal.controller;

import com.leandrocoelho.portfoliopessoal.service.ArchitectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/architecture")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Cria diagramas em mermaid.js" ,description = "Diagramas criados com IA generativa")
public class ArchitectureController {

    private final ArchitectureService architectureService;

    @GetMapping("/{projectId}")
    @Operation(summary = "Diagramas criados são baseados apenas no CV interno e informações dos projetos")
    public ResponseEntity<Map<String, String>> getProjectArchitecture(@PathVariable String projectId){
        String diagram = architectureService.generateDiagramForProject(projectId);
        // Retorna um JSON explícito para facilitar o frontend
        return ResponseEntity.ok(Map.of("mermaidCode", diagram));
    }
}
