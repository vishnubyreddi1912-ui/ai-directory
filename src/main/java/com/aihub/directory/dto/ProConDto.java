package com.aihub.directory.dto;

public class ProConDto {
    private Long id;
    private String type; // "Pro" or "Con"
    private String content;

    // Constructors
    public ProConDto() {}

    public ProConDto(Long id, String type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
