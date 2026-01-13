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
    public String generateDiagramForProject(String projectId) {
        ProjectAnalysisEntity projectAnalysisEntity = projectAnalysisRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado" + projectId));

        if (projectAnalysisEntity.getArchitectureDiagram() != null && !projectAnalysisEntity.getArchitectureDiagram().isBlank()) {
            log.info("Diagrama encontrado no cache do banco de dados para o projeto: {}", projectId);
            return projectAnalysisEntity.getArchitectureDiagram();
        }

        log.info("Gerando novo diagrama via IA para: {}", projectAnalysisEntity.getTitulo());

        String prompt = """
                Tarefa: Gere um código MERMAID.JS do tipo FLUXOGRAMA ('graph TD') para representar a arquitetura deste projeto SEM INVENTAR, SE NÃO SOUBER NÃO FAÇA.
                
                PROJETO: %s
                DESCRIÇÃO: %s
                TECNOLOGIAS: %s
                
                REGRAS CRÍTICAS DE SINTAXE (SIGA RIGOROSAMENTE):
                1. Comece SEMPRE com: graph TD
                2. Use APENAS a sintaxe de nós: A[Nome] --> B[Nome]
                3. NÃO use 'participant', 'actor' ou 'sequenceDiagram'.
                4. NÃO use 'shape=' dentro do comando 'style'.
                5. Para agrupar, use 'subgraph NomeDoGrupo ... end'.
                6. Para banco de dados, use: id[(NomeDoBanco)]
                7. Para setas com texto, use: A -->|Texto| B
                8. IDs dos nós devem ser simples (apenas letras/numeros). Ex: Node1, ServiceA.
                9. RÓTULOS DOS NÓS: Evite parênteses () ou colchetes [] dentro do texto. Se precisar, use aspas: A["Service Registry (Eureka)"]
                10. Retorne APENAS o código. Sem markdown (```).
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
