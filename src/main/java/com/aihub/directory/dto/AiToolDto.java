package com.aihub.directory.dto;

import java.time.LocalDate;
import java.util.List;

public class AiToolDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private String websiteUrl;
    private String rating;
    private Boolean hasFreePlan;
    private Boolean hasPremiumPlan;
    private String freeFeaturesSummary;
    private String premiumFeaturesSummary;
    private String startingPrice;
    private Long categoryId;
    private String categoryName;
    private String reviewsCount;
    private List<FeatureDto> features;
    private List<ProConDto> prosCons;
    private String pricingModel;
    // Constructors
    public AiToolDto() {}

    public AiToolDto(Long id, String name, String description, String websiteUrl,
                     String rating, String reviewsCount,
                     String pricingModel,LocalDate releaseDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.websiteUrl = websiteUrl;
        this.rating = rating;
        this.hasFreePlan = hasFreePlan;
        this.hasPremiumPlan = hasPremiumPlan;
        this.reviewsCount = reviewsCount;
        this.premiumFeaturesSummary = premiumFeaturesSummary;
        this.startingPrice = startingPrice;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.features = features;
        this.prosCons = prosCons;
        this.pricingModel= pricingModel;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getPricingModel() { return pricingModel; }
    public void setPricingModel(String pricingModel) { this.pricingModel = pricingModel; }

    public Boolean getHasFreePlan() { return hasFreePlan; }
    public void setHasFreePlan(Boolean hasFreePlan) { this.hasFreePlan = hasFreePlan; }

    public Boolean getHasPremiumPlan() { return hasPremiumPlan; }
    public void setHasPremiumPlan(Boolean hasPremiumPlan) { this.hasPremiumPlan = hasPremiumPlan; }

    public String getFreeFeaturesSummary() { return freeFeaturesSummary; }
    public void setFreeFeaturesSummary(String freeFeaturesSummary) { this.freeFeaturesSummary = freeFeaturesSummary; }

    public String getPremiumFeaturesSummary() { return premiumFeaturesSummary; }
    public void setPremiumFeaturesSummary(String premiumFeaturesSummary) { this.premiumFeaturesSummary = premiumFeaturesSummary; }

    public String getStartingPrice() { return startingPrice; }
    public void setStartingPrice(String startingPrice) { this.startingPrice = startingPrice; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<FeatureDto> getFeatures() { return features; }
    public void setFeatures(List<FeatureDto> features) { this.features = features; }

    public List<ProConDto> getProsCons() { return prosCons; }
    public void setProsCons(List<ProConDto> prosCons) { this.prosCons = prosCons; }

    public String getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(String reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
