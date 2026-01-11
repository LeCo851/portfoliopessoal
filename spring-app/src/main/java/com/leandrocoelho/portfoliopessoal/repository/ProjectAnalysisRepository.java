package com.leandrocoelho.portfoliopessoal.repository;

import com.leandrocoelho.portfoliopessoal.entity.ProjectAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAnalysisRepository extends JpaRepository<ProjectAnalysisEntity,String> {
}
