package com.leandrocoelho.portfoliopessoal.service;


import com.leandrocoelho.portfoliopessoal.model.dto.AiAnalysisResultDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectDescriptionGeneratorService {
    private final ChatClient chatClient;

    public ProjectDescriptionGeneratorService(ChatClient.Builder builder){
        this.chatClient = builder
                .defaultSystem("Você é um Recrutador Técnico Sênior especialista em Java e Cloud. " +
                        "Sua função é analisar repositórios técnicos e criar resumos atrativos para um portfólio profissional.")
                .build();
    }

    public AiAnalysisResultDTO generate(String name, String language, List<String> topics, String readmeContent){
            String cleanReadme = (readmeContent != null && readmeContent.length() >10000) ? readmeContent.substring(0,10000) :readmeContent;

        String prompt = """
                Analise este projeto GitHub:
                PROJETO: %s (Linguagem: %s)
                TÓPICOS: %s
                
                --- CONTEÚDO DO README ---
                %s
                --------------------------
                
                Gere um objeto JSON com:
                1. 'titulo': Um nome comercial forte e atrativo (máx 5 palavras).
                2. 'resumo': Um resumo técnico denso, focado em valor de negócio e arquitetura (máx 300 caracteres).
                3. 'tecnologias': Uma LISTA (Array) com todas as ferramentas, libs e frameworks citados no README.
                4. 'tags_extras': Sugira 2 categorias gerais do projeto (ex: Backend, DevOps, Data Science).
                """
                .formatted(name, language, topics, cleanReadme);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(AiAnalysisResultDTO.class);
    }
}
