package com.familymemories.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(
    origins = {"*"}, 
    allowedHeaders = {"*"}, 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS}
) 
public class PhotoController {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public PhotoController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   
    @GetMapping("/uploads/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            String userDir = System.getProperty("user.dir");
            Path filePath = Paths.get(userDir, "uploads").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
               
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
   
    @GetMapping("/api/photos")
    public List<Map<String, Object>> getPhotos(@RequestParam(required = false) String album) {
        if (album != null && !album.isBlank()) {
            return jdbcTemplate.queryForList("SELECT * FROM photos WHERE album_name = ? ORDER BY uploaded_at DESC", album);
        }
        return jdbcTemplate.queryForList("SELECT * FROM photos ORDER BY uploaded_at DESC");
    }

    @GetMapping("/api/photos/albums")
    public List<String> getAlbumNames() {
        return jdbcTemplate.queryForList("SELECT DISTINCT album_name FROM photos", String.class);
    }

    @PostMapping(value = "/api/photos", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("albumName") String albumName,
            @RequestParam("uploaderName") String uploaderName,
            @RequestParam(value = "caption", required = false) String caption) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String userDir = System.getProperty("user.dir");
            Path uploadPath = Paths.get(userDir, "uploads").toAbsolutePath().normalize();
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename() == null ? "photo.jpg" : file.getOriginalFilename();
            String extension = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf('.'))
                    : ".jpg";
            String storedFileName = UUID.randomUUID() + extension;

            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String imagePath = "/uploads/" + storedFileName;
            String title = (caption != null && !caption.isBlank()) ? caption : originalName;

            String sql = "INSERT INTO photos (album_name, file_name, image_path, title, uploader_name, uploaded_at) VALUES (?, ?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, albumName, storedFileName, imagePath, title, uploaderName);

            return ResponseEntity.status(HttpStatus.CREATED).body("{\"status\":\"success\"}");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Could not save file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Database Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/photos/{id}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT file_name FROM photos WHERE id = ?", id);
            if (!rows.isEmpty()) {
                String fileName = (String) rows.get(0).get("file_name");
                String userDir = System.getProperty("user.dir");
                Path uploadPath = Paths.get(userDir, "uploads").toAbsolutePath().normalize();
                File file = new File(uploadPath.toFile(), fileName);
                if (file.exists()) file.delete();
                
                jdbcTemplate.update("DELETE FROM photos WHERE id = ?", id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Delete Error: " + e.getMessage());
        }
    }
}