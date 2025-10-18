package com.aihub.directory.dto;

public class FeatureDto {
    private Long id;
    private String featureName;
    private Boolean paid;

    // Constructors
    public FeatureDto() {}

    public FeatureDto(Long id, String featureName, Boolean paid) {
        this.id = id;
        this.featureName = featureName;
        this.paid = paid;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }
}
