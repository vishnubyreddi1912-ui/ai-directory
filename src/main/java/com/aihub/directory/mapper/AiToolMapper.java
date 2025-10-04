package com.aihub.directory.mapper;
import com.aihub.directory.dto.AiToolDto;
import com.aihub.directory.dto.FeatureDto;
import com.aihub.directory.dto.ProConDto;
import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;

import java.util.List;
import java.util.stream.Collectors;

public class AiToolMapper {

    // ENTITY → DTO
    public static AiToolDto mapToDto(AiTool aiTool) {
        if (aiTool == null) return null;

        List<FeatureDto> featureDtos = aiTool.getFeatures() != null
                ? aiTool.getFeatures().stream()
                .map(f -> new FeatureDto(f.getId(), f.getFeatureName(), f.getPaid()))
                .collect(Collectors.toList())
                : List.of();

        List<ProConDto> proConDtos = aiTool.getProsCons() != null
                ? aiTool.getProsCons().stream()
                .map(pc -> new ProConDto(pc.getId(), pc.getType(), pc.getContent()))
                .collect(Collectors.toList())
                : List.of();

        return new AiToolDto(
                aiTool.getId(),
                aiTool.getName(),
                aiTool.getDescription(),
                aiTool.getReleaseDate(),
                aiTool.getWebsiteUrl(),
                aiTool.getPricingModel(),
                aiTool.getHasFreePlan(),
                aiTool.getHasPremiumPlan(),
                aiTool.getFreeFeaturesSummary(),
                aiTool.getPremiumFeaturesSummary(),
                aiTool.getStartingPrice(),
                aiTool.getCategory() != null ? aiTool.getCategory().getId() : null,
                aiTool.getCategory() != null ? aiTool.getCategory().getName() : null,
                featureDtos,
                proConDtos
        );
    }

    // Optional: DTO → ENTITY (if you support create/update)
    public static AiTool mapToEntity(AiToolDto dto, Category category) {
        AiTool aiTool = new AiTool();
        aiTool.setId(dto.getId());
        aiTool.setName(dto.getName());
        aiTool.setDescription(dto.getDescription());
        aiTool.setReleaseDate(dto.getReleaseDate());
        aiTool.setWebsiteUrl(dto.getWebsiteUrl());
        aiTool.setPricingModel(dto.getPricingModel());
        aiTool.setHasFreePlan(dto.getHasFreePlan());
        aiTool.setHasPremiumPlan(dto.getHasPremiumPlan());
        aiTool.setFreeFeaturesSummary(dto.getFreeFeaturesSummary());
        aiTool.setPremiumFeaturesSummary(dto.getPremiumFeaturesSummary());
        aiTool.setStartingPrice(dto.getStartingPrice());
        aiTool.setCategory(category);
        return aiTool;
    }
}
