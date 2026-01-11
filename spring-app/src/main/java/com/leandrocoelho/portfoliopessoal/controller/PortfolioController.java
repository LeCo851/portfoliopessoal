package com.leandrocoelho.portfoliopessoal.controller;

import com.leandrocoelho.portfoliopessoal.model.portfolio.ProjectCard;
import com.leandrocoelho.portfoliopessoal.service.GitHubService;
import com.leandrocoelho.portfoliopessoal.service.PortfolioAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PortfolioController {

    private final GitHubService gitHubService;
    private final PortfolioAiService portfolioAiService;

    @GetMapping
    public List<ProjectCard> getPortofolio(){

        var reposBrutos = gitHubService.getPortolioProjects();

        return reposBrutos.stream()
                .parallel()
                .map(portfolioAiService::enrichProject)
                .toList();
    }
}
