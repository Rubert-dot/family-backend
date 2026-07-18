package com.familymemories.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class EventController {

    private final JdbcTemplate jdbcTemplate;

    public EventController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Map<String, Object>> getAllEvents() {
        return jdbcTemplate.queryForList("SELECT * FROM events ORDER BY event_date DESC");
    }

    @GetMapping("/upcoming")
    public List<Map<String, Object>> getUpcomingEvents() {
        return jdbcTemplate.queryForList("SELECT * FROM events WHERE event_date >= CURDATE() ORDER BY event_date ASC");
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> payload) {
        try {
            String title = (String) payload.get("title");
            String description = (String) payload.get("description");
            String eventDateStr = (String) payload.get("eventDate");
            String createdBy = (String) payload.get("createdBy");

            if (title == null || title.isBlank()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (eventDateStr == null || eventDateStr.isBlank()) {
                return ResponseEntity.badRequest().body("Event date is required");
            }

            // ஜாவா JPA டேட் எரர் வராமல் தடுக்க நேரடியாக SQL Query மூலம் இன்செர்ட் செய்கிறோம்
            String sql = "INSERT INTO events (title, description, event_date, created_by, created_at) VALUES (?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, title, description, eventDateStr, createdBy);

            return ResponseEntity.status(HttpStatus.CREATED).body("{\"status\":\"success\"}");

        } catch (Exception e) {
            // அசல் எரரை ஃபிரண்ட் எண்டிற்கு அனுப்புகிறது
            return ResponseEntity.internalServerError().body("Database Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            jdbcTemplate.update("DELETE FROM events WHERE id = ?", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Delete Error: " + e.getMessage());
        }
    }
}