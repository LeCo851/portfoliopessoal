package com.leandrocoelho.portfoliopessoal.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "project_analysis_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectAnalysisEntity {

    @Id
    private String id;
    private String titulo;

    @Column(length = 5000)
    private String resumo;

    @ElementCollection(fetch = FetchType.EAGER)
    // Como SQL não guarda Listas dentro de uma célula, o JPA cria uma tabela extra
    // chamada "project_analysis_entity_tags" só para guardar as tags e linkar com essa tabela.
    private List<String> tags;

    private String imageUrl;
    private String linkGithub;
    private Instant lastUpdate;

    public boolean isUpToDate(java.time.Instant githubUpdateDate){
        if (this.lastUpdate == null || githubUpdateDate == null){
            return false;
        }

        return this.lastUpdate.getEpochSecond() == githubUpdateDate.getEpochSecond();
    }
    @Column(columnDefinition = "TEXT")
    private String architectureDiagram;
}
