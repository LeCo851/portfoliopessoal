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
                    Atue como um Arquiteto de Software Sênior.
                    Tarefa: Gere um código MERMAID.JS (tipo 'graph TD') EXATAMENTE baseado nas informações fornecidas.
                    
                    INPUT DO PROJETO:
                    - NOME: %s
                    - DESCRIÇÃO: %s
                    - TECNOLOGIAS REAIS: %s
                    
                    🚫 REGRAS ANTI-ALUCINAÇÃO:
                    1. FONTE DA VERDADE: Use APENAS tecnologias listadas no input.
                    2. Se não está na lista, NÃO EXISTE.
                    
                    🛡️ PROTOCOLO DE SEGURANÇA DE SINTAXE (ZERO ERRO):
                    1. ESTRUTURA ESPACIAL: use 'subgraph'. agrupe nós. Faça um fluxo simples.
                    2. IDs ABSTRATOS: Use APENAS 'N1', 'N2', 'N3', etc. para os IDs dos nós.
                       ERRADO: Java[Java] --> Spring[Spring]
                       CERTO:  N1["Java"] --> N2["Spring"]
                    3. RÓTULOS SEGUROS: Sempre use aspas duplas simples nos rótulos. Adicione emojis neles.
                       Ex: N1["⚙️ Java 17"]
                  
                    
                    🎨 ESTILO VISUAL (Aplique no final):
                    1. Defina classes:
                       classDef frontend fill:#0d1117,stroke:#00dfff,stroke-width:2px,color:#fff;
                       classDef backend fill:#0d1117,stroke:#ff0055,stroke-width:2px,color:#fff;
                       classDef data fill:#0d1117,stroke:#ffee00,stroke-width:2px,color:#fff;
                       classDef infra fill:#0d1117,stroke:#bd93f9,stroke-width:2px,color:#fff;
                    2. Aplique as classes aos IDs abstratos. Ex: class N1 backend
                    
                    EXEMPLO DE SAÍDA PERFEITA:
                    graph TD
                    N1["👤 Usuário"] --> N2["💻 Angular"]
                    N2 --> N3["⚙️ Spring Boot"]
                    N3 --> N4["💾 PostgreSQL"]
                    classDef frontend fill:#0d1117,stroke:#00dfff,stroke-width:2px,color:#fff;
                    classDef backend fill:#0d1117,stroke:#ff0055,stroke-width:2px,color:#fff;
                    classDef data fill:#0d1117,stroke:#ffee00,stroke-width:2px,color:#fff;
                    class N2 frontend
                    class N3 backend
                    class N4 data
                    
                    REGRAS DE SINTAXE (CRÍTICO):
                                    1. Retorne APENAS o código.
                                    2. NUNCA use aspas duplas duplicadas (`""texto""`). Use apenas uma (`"texto"`).
                                    3. Certifique-se de fechar cada `subgraph` com a palavra `end`.
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
