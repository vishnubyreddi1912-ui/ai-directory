package com.aihub.directory.dto;


import java.util.List;

public class CategoryDto {
    private Long id;
    private String name;
    private String responsiblePerson;
    private List<Long> aiToolIds; // Optional - list of tools under this category

    // Constructors
    public CategoryDto() {}

    public CategoryDto(Long id, String name, String responsiblePerson, List<Long> aiToolIds) {
        this.id = id;
        this.name = name;
        this.responsiblePerson = responsiblePerson;
        this.aiToolIds = aiToolIds;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }

    public List<Long> getAiToolIds() { return aiToolIds; }
    public void setAiToolIds(List<Long> aiToolIds) { this.aiToolIds = aiToolIds; }
}
