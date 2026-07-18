package com.familymemories.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String albumName;
    private String fileName;
    private String imagePath;
    private String uploaderName;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // காலியான கன்ஸ்ட்ரக்டர் (JPA-விற்காக)
    public Photo() {}

    // கன்ஸ்ட்ரக்டர் ஓவர்லோடிங்
    public Photo(String title, String albumName, String fileName, String imagePath, String uploaderName) {
        this.title = title;
        this.albumName = albumName;
        this.fileName = fileName;
        this.imagePath = imagePath;
        this.uploaderName = uploaderName;
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAlbumName() { return albumName; }
    public void setAlbumName(String albumName) { this.albumName = albumName; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getUploaderName() { return uploaderName; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}