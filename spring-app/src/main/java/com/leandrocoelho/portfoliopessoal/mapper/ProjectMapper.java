package com.leandrocoelho.portfoliopessoal.mapper;

import com.leandrocoelho.portfoliopessoal.entity.ProjectAnalysisEntity;
import com.leandrocoelho.portfoliopessoal.model.dto.AiAnalysisResultDTO;
import com.leandrocoelho.portfoliopessoal.model.dto.GitHubRepositoryDTO;
import com.leandrocoelho.portfoliopessoal.model.portfolio.ProjectCard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        return new ProjectAnalysisEntity(
                repo.name(),
                analysis.titulo(),
                analysis.resumo(),
                combinedTags,
                "https://github.com/LeCo851/" + repo.name() + "/raw/main/cover.png",
                repo.html_url(),
                repo.updated_at()
        );
    }
}
