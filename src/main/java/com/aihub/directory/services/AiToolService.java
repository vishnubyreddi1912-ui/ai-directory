package com.aihub.directory.services;

import com.aihub.directory.dto.AiToolDto;
import com.aihub.directory.entities.AiTool;
import com.aihub.directory.repositories.AiToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AiToolService {

    @Autowired
    private AiToolRepository aiToolRepository;

    // 🧠 Static cache for tools
    private static List<AiToolDto> cachedTools = null;

    /**
     * Get all tools (cached after first fetch)
     */
    public List<AiToolDto> getAllTools() {
        // Return cached data if available
        if (cachedTools != null && !cachedTools.isEmpty()) {
            return cachedTools;
        }

        // Otherwise, fetch from DB and cache it
        synchronized (AiToolService.class) {
            if (cachedTools == null || cachedTools.isEmpty()) {
                cachedTools = aiToolRepository.findAll()
                        .stream()
                        .map(this::convertToDto)
                        .toList();
            }
        }
        return cachedTools;
    }

    /**
     * Clear cache manually (e.g. after updates)
     */
    public static void clearCache() {
        cachedTools = null;
    }

    public Optional<AiToolDto> getToolByName(String name) {
        return aiToolRepository.findByNameIgnoreCase(name)
                .map(this::convertToDto);
    }

    private AiToolDto convertToDto(AiTool entity) {
        AiToolDto dto = new AiToolDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setReleaseDate(entity.getReleaseDate());
        dto.setWebsiteUrl(entity.getWebsiteUrl());
        dto.setPricingModel(entity.getPricingModel());
        dto.setHasFreePlan(entity.getHasFreePlan());
        dto.setHasPremiumPlan(entity.getHasPremiumPlan());
        dto.setFreeFeaturesSummary(entity.getFreeFeaturesSummary());
        dto.setPremiumFeaturesSummary(entity.getPremiumFeaturesSummary());
        dto.setStartingPrice(entity.getStartingPrice());
        dto.setCategoryId(entity.getCategory() != null ? entity.getCategory().getId() : null);
        dto.setCategoryName(entity.getCategory() != null ? entity.getCategory().getName() : null);

        // map features and pros/cons
        if (entity.getFeatures() != null)
            dto.setFeatures(entity.getFeatures().stream().map(f -> {
                var fdto = new com.aihub.directory.dto.FeatureDto();
                fdto.setId(f.getId());
                fdto.setFeatureName(f.getFeatureName());
                fdto.setPaid(f.getPaid());
                return fdto;
            }).toList());

        if (entity.getProsCons() != null)
            dto.setProsCons(entity.getProsCons().stream().map(p -> {
                var pdto = new com.aihub.directory.dto.ProConDto();
                pdto.setId(p.getId());
                pdto.setType(p.getType());
                pdto.setContent(p.getContent());
                return pdto;
            }).toList());

        return dto;
    }
}
