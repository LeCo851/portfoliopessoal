package com.leandrocoelho.portfoliopessoal.controller;

import com.leandrocoelho.portfoliopessoal.service.ArchitectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/architecture")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArchitectureController {

    private final ArchitectureService architectureService;

    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, String>> getProjectArchitecture(@PathVariable String projectId){
        String diagram = architectureService.generateDiagramForProject(projectId);
        // Retorna um JSON explícito para facilitar o frontend
        return ResponseEntity.ok(Map.of("mermaidCode", diagram));
    }
}
