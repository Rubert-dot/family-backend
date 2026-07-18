package com.familymemories.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userDir = System.getProperty("user.dir");
        Path uploadPath = Paths.get(userDir, "uploads").toAbsolutePath().normalize();
        String uploadFolderUrl = uploadPath.toUri().toString();

        // செக்யூரிட்டி இன்டர்செப்டார் ஃபைல் அப்லோடு ஃபோல்டரைத் தொடாமல் இருக்க 
        // ஸ்பிரிங் பூட்டோட ரிசோர்ஸ் லொகேஷனில் ஸ்ட்ராங்காக ரிஜிஸ்டர் செய்கிறோம்
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadFolderUrl)
                .setCachePeriod(3600);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}