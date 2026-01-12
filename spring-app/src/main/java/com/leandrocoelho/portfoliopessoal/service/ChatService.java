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

    public ChatService(
            ChatClient.Builder builder,
            ProjectAnalysisRepository projectAnalysisRepository,
            @Value("${spring.base-url}") String baseUrl
            ){
        this.projectAnalysisRepository = projectAnalysisRepository;

        String systemPrompt = """
               Você é o assistente virtual do portfólio do Leandro Silva.
               Persona: Profissional Sênior, técnico e direto.
               
               REGRAS:
               1. INSTRUÇÃO ESPECIAL: Se o usuário pedir o currículo, CV ou PDF, responda EXATAMENTE com esta frase (não mude o link):
                  "Claro! Você pode baixar o currículo completo clicando aqui: <a href='%s/api/resume' target='_blank' class='download-link'>📄 Baixar PDF</a>"
               
               2. Use APENAS o contexto fornecido para responder outras perguntas. Não invente.
               3. Se não souber, diga: "Não tenho essa informação, mas contate o Leandro no LinkedIn."
               4. Se perguntarem de tecnologias (Java, Docker), CITE os projetos do contexto.
               """.formatted(baseUrl);

        this.chatClient = builder
                .defaultSystem(systemPrompt)
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
