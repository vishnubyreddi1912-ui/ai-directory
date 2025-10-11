package com.aihub.directory.mapper;

import com.aihub.directory.dto.AiToolDto;
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
}
