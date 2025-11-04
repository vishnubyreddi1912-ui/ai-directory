package com.aihub.directory.entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "user_activity_log")
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relationship to User table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;   // e.g. VIEW, SEARCH, LOGIN, COMPARE

    @Column(name = "entity_type", length = 100)
    private String entityType;  // e.g. AI_TOOL, CATEGORY, PAGE

    @Column(name = "entity_id", length = 100)
    private String entityId;    // e.g. tool ID or category ID

    @Column(name = "entity_name", length = 255)
    private String entityName;  // e.g. ChatGPT, Image Generation

    @Type(JsonType.class)
    @Column(name = "metadata_json", columnDefinition = "jsonb")
    private Map<String, Object> metadataJson;
    @Column(name = "accessed_at")
    private LocalDateTime accessedAt = LocalDateTime.now();

    // Constructors
    public UserActivityLog() {}

    public UserActivityLog(User user, String eventType, String entityType, String entityId,
                           String entityName, Map<String, Object> metadataJson) {
        this.user = user;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.metadataJson = metadataJson;
        this.accessedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public Map<String, Object> getMetadataJson() { return metadataJson; }
    public void setMetadataJson(Map<String, Object> metadataJson) { this.metadataJson = metadataJson; }

    public LocalDateTime getAccessedAt() { return accessedAt; }
    public void setAccessedAt(LocalDateTime accessedAt) { this.accessedAt = accessedAt; }
}
