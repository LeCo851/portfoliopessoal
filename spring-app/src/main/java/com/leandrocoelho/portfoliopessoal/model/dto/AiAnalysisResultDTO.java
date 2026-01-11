package com.leandrocoelho.portfoliopessoal.model.dto;

import java.util.List;

public record AiAnalysisResultDTO(
        String titulo,
        String resumo,
        List<String> tecnologias,
        List<String> tags_extras
) {}