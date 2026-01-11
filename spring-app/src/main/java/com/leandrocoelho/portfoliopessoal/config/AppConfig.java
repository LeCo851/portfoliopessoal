package com.leandrocoelho.portfoliopessoal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public RestClient githubRestClient(RestClient.Builder builder){
        return builder
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept","application/vnd.github+json")
                .defaultHeader("User-Agent", "Portfolio-App")
                .build();
    }
}
