package com.aihub.directory.dto;

import java.util.List;

public class CategoryDto {

    private Long id;
    private String name;
    private int aiCount;
    private List<AiToolDto> aiTools; // ✅ Added — holds the list of AI tools in this category

    // Constructors
    public CategoryDto() {}

    public CategoryDto(Long id, String name, int aiCount) {
        this.id = id;
        this.name = name;
        this.aiCount = aiCount;
    }

    public CategoryDto(Long id, String name, int aiCount, List<AiToolDto> aiTools) {
        this.id = id;
        this.name = name;
        this.aiCount = aiCount;
        this.aiTools = aiTools;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAiCount() {
        return aiCount;
    }
    public void setAiCount(int aiCount) {
        this.aiCount = aiCount;
    }

    public List<AiToolDto> getAiTools() {
        return aiTools;
    }
    public void setAiTools(List<AiToolDto> aiTools) {
        this.aiTools = aiTools;
    }
}
