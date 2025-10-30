package com.aihub.directory.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ai_tools")
public class AiTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "pricing_model", length = 20)
    private String pricingModel;

    @Column(name = "has_free_plan")
    private Boolean hasFreePlan = false;

    @Column(name = "has_premium_plan")
    private Boolean hasPremiumPlan = false;

    @Column(name = "free_features_summary", columnDefinition = "TEXT")
    private String freeFeaturesSummary;

    @Column(name = "premium_features_summary", columnDefinition = "TEXT")
    private String premiumFeaturesSummary;

    @Column(name = "starting_price")
    private String startingPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Many AI tools belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Relationships
    @OneToMany(mappedBy = "aiTool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feature> features;

    @OneToMany(mappedBy = "aiTool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProCon> prosCons;

    @OneToMany(mappedBy = "aiTool", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Rating> ratings;

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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getPricingModel() {
        return pricingModel;
    }
    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public Boolean getHasFreePlan() {
        return hasFreePlan;
    }
    public void setHasFreePlan(Boolean hasFreePlan) {
        this.hasFreePlan = hasFreePlan;
    }

    public Boolean getHasPremiumPlan() {
        return hasPremiumPlan;
    }
    public void setHasPremiumPlan(Boolean hasPremiumPlan) {
        this.hasPremiumPlan = hasPremiumPlan;
    }

    public String getFreeFeaturesSummary() {
        return freeFeaturesSummary;
    }
    public void setFreeFeaturesSummary(String freeFeaturesSummary) {
        this.freeFeaturesSummary = freeFeaturesSummary;
    }

    public String getPremiumFeaturesSummary() {
        return premiumFeaturesSummary;
    }
    public void setPremiumFeaturesSummary(String premiumFeaturesSummary) {
        this.premiumFeaturesSummary = premiumFeaturesSummary;
    }

    public String getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(String startingPrice) {
        this.startingPrice = startingPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Feature> getFeatures() {
        return features;
    }
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<ProCon> getProsCons() {
        return prosCons;
    }
    public void setProsCons(List<ProCon> prosCons) {
        this.prosCons = prosCons;
    }


    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
    @Transient
    public double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return 0.0;
        return ratings.stream()
                .mapToDouble(r -> r.getRatingValue().doubleValue())
                .average()
                .orElse(0.0);
    }

    @Transient
    public int getReviewCount() {
        return ratings != null ? ratings.size() : 0;
    }

}
