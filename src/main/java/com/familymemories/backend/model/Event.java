package com.familymemories.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // காலியான கன்ஸ்ட்ரக்டர் (JPA-விற்காக)
    public Event() {}

    // கன்ஸ்ட்ரக்டர் ஓவர்லோடிங்
    public Event(String title, String description, LocalDate eventDate, String createdBy) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}