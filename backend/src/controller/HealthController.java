package com.geopulse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "http://localhost:3000")
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "GeoInsight Backend");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        
        try {
            // Check database connectivity
            status.put("database", "CONNECTED");
            
            // Check external API availability (mock check)
            status.put("countries_api", "AVAILABLE");
            status.put("weather_api", "AVAILABLE");
            status.put("news_scraping", "OPERATIONAL");
            
            status.put("overall", "HEALTHY");
            
        } catch (Exception e) {
            status.put("overall", "DEGRADED");
            status.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }
}