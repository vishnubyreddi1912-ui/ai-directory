package com.aihub.directory.controllers;

import com.aihub.directory.dto.AiToolDto;
import com.aihub.directory.dto.CategoryDto;
import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;
import com.aihub.directory.mapper.AiToolMapper;
import com.aihub.directory.mapper.CategoryMapper;
import com.aihub.directory.repositories.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*") // Allow Angular frontend test
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return CategoryMapper.toDtoList(categoryRepository.findAll());
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setAiCount(category.getAiTools() != null ? category.getAiTools().size() : 0);

        if (category.getAiTools() != null) {
            dto.setAiTools(
                    category.getAiTools()
                            .stream()
                            .map(AiToolMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    @GetMapping("getnames/{id}")
    public ResponseEntity<List<String>> getAiNamesBasedOnCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        List<String> aiNames = category.getAiTools() != null
                ? category.getAiTools()
                .stream()
                .map(AiTool::getName)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return ResponseEntity.ok(aiNames);
    }

}
