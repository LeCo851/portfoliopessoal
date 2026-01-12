package com.leandrocoelho.portfoliopessoal.controller;

import com.leandrocoelho.portfoliopessoal.service.ArchitectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/architecture")
@RequiredArgsConstructor
public class ArchitectureController {

    private final ArchitectureService architectureService;

    @GetMapping("/{projetcId}")
    public ResponseEntity getProjectArchitecture(@PathVariable Long projetcId){
        return ResponseEntity.ok(architectureService.generateDiagramForProject(projetcId));
    }
}
