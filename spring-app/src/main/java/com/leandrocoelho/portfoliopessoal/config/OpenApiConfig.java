package com.leandrocoelho.portfoliopessoal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    // Injeta a URL base do application.yml (localhost ou produção)
    @Value("${spring.base-url}")
    private String baseUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        // 1. Configuração do Servidor (Evita erro de CORS no 'Try it out')
        Server server = new Server();
        server.setUrl(baseUrl);
        server.setDescription("Servidor Principal (Portfolio API)");

        // 2. Contato do Desenvolvedor
        Contact contact = new Contact();
        contact.setEmail("leandrocesar91@gmail.com");
        contact.setName("Leandro Coelho");
        contact.setUrl("https://github.com/LeCo851");

        // 3. Informações Gerais da API
        Info info = new Info()
                .title("Portfolio API - Leandro Coelho")
                .version("1.0.0")
                .contact(contact)
                .description("""
                        ## API RESTful do Portfólio Profissional
                        
                        Esta API alimenta o frontend Angular e fornece funcionalidades avançadas como:
                        
                        * **Assistente IA:** Chatbot integrado com Llama 3 (via Groq) e RAG.
                        * **Gerador de PDF:** Criação dinâmica de currículos a partir de Markdown.
                        * **Arquiteto Virtual:** Geração automática de diagramas Mermaid.js baseados no código dos projetos.
                        * **Monitoramento:** Métricas via Spring Actuator.
                        """)
                .license(new License()
                        .name("Copyright © 2026 Leandro Coelho - Todos os direitos reservados")
                        .url(null));

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}