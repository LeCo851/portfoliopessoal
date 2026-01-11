package com.leandrocoelho.portfoliopessoal.service;

import com.leandrocoelho.portfoliopessoal.entity.ProjectAnalysisEntity;
import com.leandrocoelho.portfoliopessoal.repository.ProjectAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
    private final ProjectAnalysisRepository projectAnalysisRepository;

    @Value("classpath:resume.md")
    private Resource resumeResource;

    public ChatService(ChatClient.Builder builder,ProjectAnalysisRepository projectAnalysisRepository){
        this.projectAnalysisRepository = projectAnalysisRepository;
        this.chatClient = builder
                .defaultSystem(
                        """
                       Você é o assistente virtual do portfólio do Leandro Silva.
                       Persona: Profissional Sênior, técnico e direto.
                       
                       REGRAS:
                       1. Use APENAS o contexto abaixo para responder. Não invente.
                       2. Se não souber, diga: "Não tenho essa informação, mas contate o Leandro no LinkedIn."
                       3. Se perguntarem de tecnologias (Java, Docker), CITE os projetos onde elas aparecem.
                       """
                )
                .build();
    }

    public String generateResponse(String userQuestion){
        String resume = loadResume();
        String projects = loadProjectsContext();

        String userPrompt =
                """
                --- CONTEXTO CURRÍCULO ---
                %s
                
                --- CONTEXTO PROJETOS (GitHub) ---
                %s
                
                --- PERGUNTA ---
                %s
                """
                        .formatted(resume,projects,userQuestion);
        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    private String loadProjectsContext(){
        List<ProjectAnalysisEntity> entities = projectAnalysisRepository.findAll();

        if(entities.isEmpty()) return "Nenhum projeto detalhado disponível";

        return entities.stream()
                .map(proj -> String.format(
                        """
                    - PROJETO: %s
                      Resumo IA: %s
                      Techs: %s
                    """,proj.getTitulo(), proj.getResumo(), proj.getTags()
                )).collect(Collectors.joining("\n\n"));
    }
    private String loadResume(){
        try{
            return new String(resumeResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Erro ao ler currículo",e);
            return "Erro: Currículo indisponível.";
        }
    }

}
