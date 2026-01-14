package com.leandrocoelho.portfoliopessoal.controller;

import com.leandrocoelho.portfoliopessoal.model.portfolio.ProjectCard;
import com.leandrocoelho.portfoliopessoal.service.GitHubService;
import com.leandrocoelho.portfoliopessoal.service.PortfolioAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Recupera repositórios do Github ", description = "Endpoint para comunicação com o Github")
public class PortfolioController {

    private final GitHubService gitHubService;
    private final PortfolioAiService portfolioAiService;

    @GetMapping
    @Operation(summary = "Operação de get repositórios do github", description = "Envia o request para o github e recebe a resposta de repositórios marcados com uma tag")
    public List<ProjectCard> getPortofolio(){

        var reposBrutos = gitHubService.getPortolioProjects();

        return reposBrutos.stream()
                .parallel()
                .map(portfolioAiService::enrichProject)
                .toList();
    }
}
