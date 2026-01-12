package com.leandrocoelho.portfoliopessoal.service;

import com.leandrocoelho.portfoliopessoal.entity.ProjectAnalysisEntity;
import com.leandrocoelho.portfoliopessoal.repository.ProjectAnalysisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ArchitectureService {

    private final ChatClient chatClient;
    private final ProjectAnalysisRepository projectAnalysisRepository;

    public ArchitectureService(ChatClient.Builder builder, ProjectAnalysisRepository projectAnalysisRepository) {
        this.projectAnalysisRepository = projectAnalysisRepository;
        this.chatClient = builder
                .defaultSystem("Você é um Arquiteto de Software Sênior especializado em diagramas Mermaid.js.")
                .build();
    }

    @Transactional
    public String generateDiagramForProject(Long projectId) {
        ProjectAnalysisEntity projectAnalysisEntity = projectAnalysisRepository.findById(String.valueOf(projectId))
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado" + projectId));

        if (projectAnalysisEntity.getArchitectureDiagram() != null && !projectAnalysisEntity.getArchitectureDiagram().isBlank()) {
            log.info("Diagrama encontrado no cache do banco de dados para o projeto: {}", projectId);
            return projectAnalysisEntity.getArchitectureDiagram();
        }

        log.info("Gerando novo diagrama via IA para: {}", projectAnalysisEntity.getTitulo());

        String prompt = """
                Tarefa: Gere um código MERMAID.JS (tipo 'graph TD') para este projeto.
                
                PROJETO: %s
                DESCRIÇÃO: %s
                TECNOLOGIAS: %s
                
                REGRAS:
                1. Retorne APENAS o código puro. Nada de markdown (```).
                2. Use nós coloridos (style) para ficar bonito.
                3. Se for Java, use nós retangulares. Se for DB, use cilindros [("DB")].
                """.formatted(projectAnalysisEntity.getTitulo(), projectAnalysisEntity.getResumo(), projectAnalysisEntity.getTags());

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        assert response != null;
        String cleanCode = response
                .replace("```mermaid", "")
                .replace("```", "")
                .trim();
        projectAnalysisEntity.setArchitectureDiagram(cleanCode);
        projectAnalysisRepository.save(projectAnalysisEntity);
        return cleanCode;
    }
}
