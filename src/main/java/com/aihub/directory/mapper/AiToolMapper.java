package com.aihub.directory.mapper;

import com.aihub.directory.dto.AiToolDto;
import com.aihub.directory.dto.FeatureDto;
import com.aihub.directory.dto.ProConDto;
import com.aihub.directory.entities.AiTool;

import java.util.List;
import java.util.stream.Collectors;

public class AiToolMapper {


    public static AiToolDto toDto(AiTool tool) {
        if (tool == null) return null;

        // You can replace rating and reviews with actual data if you store them later
        return new AiToolDto(
                tool.getId(),
                tool.getName(),
                tool.getDescription(),
                tool.getWebsiteUrl(), // icon
                "4.8", // mock rating for now
                "500 reviews", // mock reviews for now
                tool.getPricingModel()
        );
    }


    public static List<AiToolDto> toDtoList(List<AiTool> tools) {
        return tools.stream()
                .map(AiToolMapper::toDto)
                .collect(Collectors.toList());
    }

    public static AiToolDto toDtoforAi(AiTool tool) {
        AiToolDto dto = new AiToolDto();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setDescription(tool.getDescription());
        dto.setReleaseDate(tool.getReleaseDate());
        dto.setWebsiteUrl(tool.getWebsiteUrl());
        dto.setPricingModel(tool.getPricingModel());
        dto.setHasFreePlan(tool.getHasFreePlan());
        dto.setHasPremiumPlan(tool.getHasPremiumPlan());
        dto.setFreeFeaturesSummary(tool.getFreeFeaturesSummary());
        dto.setPremiumFeaturesSummary(tool.getPremiumFeaturesSummary());
        dto.setStartingPrice(tool.getStartingPrice());

        if (tool.getCategory() != null) {
            dto.setCategoryId(tool.getCategory().getId());
            dto.setCategoryName(tool.getCategory().getName());
        }

        // Map Features
        if (tool.getFeatures() != null) {
            List<FeatureDto> featureDtos = tool.getFeatures().stream()
                    .map(f -> {
                        FeatureDto fd = new FeatureDto();
                        fd.setId(f.getId());
                        fd.setFeatureName(f.getFeatureName());
                        fd.setPaid(f.getPaid());
                        return fd;
                    }).collect(Collectors.toList());
            dto.setFeatures(featureDtos);
        }

        // Map Pros & Cons
        if (tool.getProsCons() != null) {
            List<ProConDto> proConDtos = tool.getProsCons().stream()
                    .map(p -> {
                        ProConDto pd = new ProConDto();
                        pd.setId(p.getId());
                        pd.setType(p.getType());
                        pd.setContent(p.getContent());
                        return pd;
                    }).collect(Collectors.toList());
            dto.setProsCons(proConDtos);
        }

        return dto;
    }
}
