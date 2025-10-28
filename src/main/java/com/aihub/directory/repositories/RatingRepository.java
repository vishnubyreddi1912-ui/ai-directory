package com.aihub.directory.repositories;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Rating;
import com.aihub.directory.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByAiTool(AiTool aiTool);

    Optional<Rating> findByAiToolAndUser(AiTool aiTool, User user);

}
