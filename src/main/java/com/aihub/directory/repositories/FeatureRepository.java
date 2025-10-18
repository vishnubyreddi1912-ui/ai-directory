package com.aihub.directory.repositories;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    // Get all features for an AI tool
    List<Feature> findByAiTool(AiTool aiTool);

    // Optional: Find all paid features
    List<Feature> findByPaidTrue();
}