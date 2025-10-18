package com.aihub.directory.mapper;

import com.aihub.directory.dto.CategoryDto;
import com.aihub.directory.entities.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        int aiCount = (category.getAiTools() != null) ? category.getAiTools().size() : 0;

        return new CategoryDto(
                category.getId(),
                category.getName(),
                aiCount
        );
    }

    public static List<CategoryDto> toDtoList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }
}