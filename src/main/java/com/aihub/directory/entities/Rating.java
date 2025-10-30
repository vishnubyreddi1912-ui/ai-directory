package com.aihub.directory.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many ratings belong to one AI tool
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_id", nullable = false)
    private AiTool aiTool;

    // Many ratings belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating_value", precision = 2, scale = 1, nullable = false)
    private BigDecimal ratingValue;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public AiTool getAiTool() {
        return aiTool;
    }
    public void setAiTool(AiTool aiTool) {
        this.aiTool = aiTool;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getRatingValue() {
        return ratingValue;
    }
    public void setRatingValue(BigDecimal ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
