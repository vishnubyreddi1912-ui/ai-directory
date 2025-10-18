package com.aihub.directory.repositories;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiToolRepository extends JpaRepository<AiTool, Long> {

    // Custom query examples
    List<AiTool> findByPricingModel(String pricingModel);

    List<AiTool> findByCategory(Category category);

    List<AiTool> findByHasFreePlanTrue();

    List<AiTool> findByHasPremiumPlanTrue();

    // Example: Search by name (case-insensitive)
    List<AiTool> findByNameContainingIgnoreCase(String name);

    Optional<AiTool> findByName(String name);

    List<AiTool> findByNameIn(List<String> names);

    @Query("SELECT a FROM AiTool a WHERE LOWER(a.name) = LOWER(:name)")
    Optional<AiTool> findByNameIgnoreCase(@Param("name") String name);
}

