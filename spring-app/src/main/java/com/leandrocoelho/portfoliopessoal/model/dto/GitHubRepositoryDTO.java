package com.leandrocoelho.portfoliopessoal.model.dto;


import java.time.Instant;
import java.util.List;

public record GitHubRepositoryDTO
        (
                String name,
                String html_url,
                String description,
                List<String> topics,
                boolean archived,
                String language,
                Instant updated_at
        ) {
}
