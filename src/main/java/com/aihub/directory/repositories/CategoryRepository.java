package com.aihub.directory.repositories;

import com.aihub.directory.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Custom finder methods if needed
    Category findByName(String name);
}
