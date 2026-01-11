package com.leandrocoelho.portfoliopessoal.model.portfolio;

import java.util.List;

public record ProjectCard(
        String id,
        String titulo,
        String resumo,
        List<String> tags,
        String imageUrl,
        String linkGithub

) {}
