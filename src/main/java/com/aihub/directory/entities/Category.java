package com.aihub.directory.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "responsible_person", length = 100)
    private String responsiblePerson;

    // One category can have many AI tools
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AiTool> aiTools;

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

    public String getResponsiblePerson() {
        return responsiblePerson;
    }
    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public List<AiTool> getAiTools() {
        return aiTools;
    }
    public void setAiTools(List<AiTool> aiTools) {
        this.aiTools = aiTools;
    }
}