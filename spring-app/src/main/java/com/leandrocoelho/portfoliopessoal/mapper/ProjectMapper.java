package com.leandrocoelho.portfoliopessoal.mapper;

import com.leandrocoelho.portfoliopessoal.entity.ProjectAnalysisEntity;
import com.leandrocoelho.portfoliopessoal.model.dto.AiAnalysisResultDTO;
import com.leandrocoelho.portfoliopessoal.model.dto.GitHubRepositoryDTO;
import com.leandrocoelho.portfoliopessoal.model.portfolio.ProjectCard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ProjectMapper {
    // Converte Entidade do Banco -> Card do Frontend
    public ProjectCard toProjectCard(ProjectAnalysisEntity entity) {
        return new ProjectCard(
                entity.getId(),
                entity.getTitulo(),
                entity.getResumo(),
                entity.getTags(),
                entity.getImageUrl(),
                entity.getLinkGithub()
        );
    }

    // Converte Dados Brutos + IA -> Entidade do Banco
    public ProjectAnalysisEntity toEntity(GitHubRepositoryDTO repo, AiAnalysisResultDTO analysis) {
        List<String> combinedTags = new ArrayList<>();
        if (analysis.tecnologias() != null) combinedTags.addAll(analysis.tecnologias());
        if (analysis.tags_extras() != null) combinedTags.addAll(analysis.tags_extras());

        return ProjectAnalysisEntity.builder()
                .id(repo.name())
                .titulo(analysis.titulo())
                .resumo(analysis.resumo())
                .tags(combinedTags)
                .imageUrl("https://github.com/LeCo851/" + repo.name() + "/raw/main/cover.png")
                .linkGithub(repo.html_url())
                .lastUpdate(repo.updated_at())
                .build();

    }

    public ProjectAnalysisEntity toBasicEntity(GitHubRepositoryDTO repo) {
        return ProjectAnalysisEntity.builder()
                .id(repo.name())
                .resumo(repo.description() != null ? repo.description() : "Análise detalhada indisponível no momento")
                .tags(repo.topics() != null ? repo.topics() : Collections.emptyList())
                .imageUrl("https://github.com/LeCo851/" + repo.name() + "/raw/main/cover.png")
                .linkGithub(repo.html_url())
                .lastUpdate(repo.updated_at())
                .build();
    };
}
