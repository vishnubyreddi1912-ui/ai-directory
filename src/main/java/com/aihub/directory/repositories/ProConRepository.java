package com.aihub.directory.repositories;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.ProCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProConRepository extends JpaRepository<ProCon, Long> {

    // Get all pros or cons for an AI tool
    List<ProCon> findByAiTool(AiTool aiTool);

    // Get only Pros
    List<ProCon> findByAiToolAndType(AiTool aiTool, String type);
}