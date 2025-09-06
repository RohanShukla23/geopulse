package com.geopulse.model;

import java.time.LocalDateTime;

public class NewsArticle {
    
    private String title;
    private String description;
    private String url;
    private String source;
    private LocalDateTime publishedAt;
    private String category;
    
    // constructors
    public NewsArticle() {}
    
    public NewsArticle(String title, String url, String source) {
        this.title = title;
        this.url = url;
        this.source = source;
        this.publishedAt = LocalDateTime.now();
    }
    
    // getters & setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    // get display-friendly published time
    public String getTimeAgo() {
        if (publishedAt == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(publishedAt, now).toMinutes();
        
        if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (minutes < 1440) { // 24 hrs
            return (minutes / 60) + " hours ago";
        } else {
            return (minutes / 1440) + " days ago";
        }
    }
    
    // truncate long titles
    public String getTruncatedTitle(int maxLength) {
        if (title == null) return "";
        return title.length() > maxLength ? 
            title.substring(0, maxLength - 3) + "..." : title;
    }
}