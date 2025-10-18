package com.aihub.directory.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "pros_cons")
public class ProCon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String type; // Pro or Con

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Many pros/cons belong to one AI tool
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

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public AiTool getAiTool() {
        return aiTool;
    }
    public void setAiTool(AiTool aiTool) {
        this.aiTool = aiTool;
    }
}
