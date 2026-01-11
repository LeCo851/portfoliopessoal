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



    @Transactional(readOnly = true)
    public ProjectCard enrichProject(GitHubRepositoryDTO repoGithub){

        return projectAnalysisRepository.findById(repoGithub.name())
                .filter(entity -> isCacheValid(entity, repoGithub))
                .map(projectMapper::toProjectCard)
                .orElseGet(() -> processNewAnalysis(repoGithub));

    }

    private boolean isCacheValid(ProjectAnalysisEntity entity, GitHubRepositoryDTO gitHubRepositoryDTO){
        boolean isValid = entity.isUpToDate(gitHubRepositoryDTO.updated_at());

        if(!isValid){
            log.info("cache stale: {}", gitHubRepositoryDTO.name());
        }
        log.info("cache hit: {}", gitHubRepositoryDTO.name());

        return  isValid;
    }

    private ProjectCard processNewAnalysis(GitHubRepositoryDTO gitHubRepositoryDTO){
        log.info("Gerando nova análise com IA generativa: {}",gitHubRepositoryDTO.name());

        String readme = gitHubService.getReadmeContent(gitHubRepositoryDTO.name());

        AiAnalysisResultDTO aiAnalysisResultDTO = descriptionGeneratorService.generate(
                gitHubRepositoryDTO.name(),
                gitHubRepositoryDTO.language(),
                gitHubRepositoryDTO.topics(),
                readme
        );
        ProjectAnalysisEntity entity = projectMapper.toEntity(gitHubRepositoryDTO,aiAnalysisResultDTO);
        projectAnalysisRepository.save(entity);

        return projectMapper.toProjectCard(entity);
    }

}
