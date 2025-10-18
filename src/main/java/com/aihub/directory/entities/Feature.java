package com.aihub.directory.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "features")
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feature_name", nullable = false, length = 255)
    private String featureName;

    @Column(name = "paid")
    private Boolean paid = false;

    // Many features belong to one AI tool
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_id")
    private AiTool aiTool;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFeatureName() {
        return featureName;
    }
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Boolean getPaid() {
        return paid;
    }
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public AiTool getAiTool() {
        return aiTool;
    }
    public void setAiTool(AiTool aiTool) {
        this.aiTool = aiTool;
    }
}
