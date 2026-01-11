package com.leandrocoelho.portfoliopessoal.service;

import com.leandrocoelho.portfoliopessoal.entity.ProjectAnalysisEntity;
import com.leandrocoelho.portfoliopessoal.mapper.ProjectMapper;
import com.leandrocoelho.portfoliopessoal.model.dto.AiAnalysisResultDTO;
import com.leandrocoelho.portfoliopessoal.model.dto.GitHubRepositoryDTO;
import com.leandrocoelho.portfoliopessoal.model.portfolio.ProjectCard;
import com.leandrocoelho.portfoliopessoal.repository.ProjectAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioAiService {

    private final GitHubService gitHubService;
    private final ProjectAnalysisRepository projectAnalysisRepository;
    private final ProjectDescriptionGeneratorService descriptionGeneratorService;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectCard enrichProject(GitHubRepositoryDTO repoGithub) {
        return projectAnalysisRepository.findById(repoGithub.name())
                .map(existingEntity -> handleExistingProject(repoGithub, existingEntity))
                .orElseGet(() -> handleNewProject(repoGithub));
    }

    // --- Estratégia para Projetos JÁ CADASTRADOS ---
    private ProjectCard handleExistingProject(GitHubRepositoryDTO repo, ProjectAnalysisEntity entity) {
        if (entity.isUpToDate(repo.updated_at())) {
            log.info("Cache HIT: {}", repo.name());
            return projectMapper.toProjectCard(entity);
        }

        // Se precisar atualizar, tenta IA. Se falhar, mantém o antigo (Safe Update)
        try {
            return generateAndSaveAnalysis(repo);
        } catch (Exception e) {
            log.warn("Falha ao atualizar {}. Mantendo versão antiga.", repo.name());
            return projectMapper.toProjectCard(entity);
        }
    }

    // --- Estratégia para Projetos NOVOS ---
    private ProjectCard handleNewProject(GitHubRepositoryDTO repo) {
        try {
            return generateAndSaveAnalysis(repo);
        } catch (Exception e) {
            log.error("IA Indisponível para novo projeto {}. Gerando Básico.", repo.name());
            // DELEGAÇÃO: O Mapper sabe criar o básico, o Service não precisa saber.
            ProjectAnalysisEntity basicEntity = projectMapper.toBasicEntity(repo);
            projectAnalysisRepository.save(basicEntity); // Salva o básico para não tentar IA de novo na próxima F5
            return projectMapper.toProjectCard(basicEntity);
        }
    }

    // --- Lógica de Geração (IA) ---
    private ProjectCard generateAndSaveAnalysis(GitHubRepositoryDTO repo) {
        log.info("Gerando nova análise IA: {}", repo.name());

        String readme = gitHubService.getReadmeContent(repo.name());

        AiAnalysisResultDTO aiResult = descriptionGeneratorService.generate(
                repo.name(), repo.language(), repo.topics(), readme
        );

        // O Service apenas orquestra: Pega DTOs -> Manda pro Mapper -> Salva Entidade
        ProjectAnalysisEntity entity = projectMapper.toEntity(repo, aiResult);
        projectAnalysisRepository.save(entity);

        return projectMapper.toProjectCard(entity);
    }
}