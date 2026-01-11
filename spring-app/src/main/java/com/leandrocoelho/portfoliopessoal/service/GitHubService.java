package com.leandrocoelho.portfoliopessoal.service;

import com.leandrocoelho.portfoliopessoal.model.dto.GitHubRepositoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final RestClient restClient;

    @Cacheable("github-repos")
    public List<GitHubRepositoryDTO> getPortolioProjects(){

        return restClient.get()
                .uri("/users/LeCo851/repos?sort=updated&per_page=100")
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubRepositoryDTO>>() {})
                .stream()
                .filter(repo -> !repo.archived() &&
                        repo.topics() != null &&
                        repo.topics().contains("portfolio"))
                .toList();
    }

    public String getReadmeContent(String repoName){

        try{
            var response = restClient.get()
                    .uri("/repos/LeCo851/" + repoName + "/readme")
                    .retrieve()
                    .body(ReadmeResponse.class);
            if(response == null || response.content == null){
                return "Sem detalhes disponíveis.";
            }

            String cleanBase64 = response.content().replaceAll("\\s","");
            byte[] decodeBytes = Base64.getDecoder().decode(cleanBase64);

            return new String(decodeBytes, StandardCharsets.UTF_8);
        }catch (HttpClientErrorException.NotFound e){
            return "Nenhum arquivo readme encontrado.";
        }
    }

    //mapeia resposta do github
    record ReadmeResponse(String content) {}
}
