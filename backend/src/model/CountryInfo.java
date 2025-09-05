package com.geopulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "country_cache")
public class CountryInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String countryName;
    
    private String capital;
    private Long population;
    private String region;
    private String subregion;
    private Double area;
    private String currency;
    private String language;
    private Double gdpPerCapita;
    private String flagEmoji;
    
    @Column(name = "risk_index")
    private Double geopoliticalRiskIndex;
    
    @Column(name = "cached_at")
    private LocalDateTime cachedAt;
    
    @Transient
    private List<NewsArticle> news;
    
    // constructors
    public CountryInfo() {
        this.cachedAt = LocalDateTime.now();
    }
    
    public CountryInfo(String countryName) {
        this();
        this.countryName = countryName;
    }
    
    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    
    public String getCapital() { return capital; }
    public void setCapital(String capital) { this.capital = capital; }
    
    public Long getPopulation() { return population; }
    public void setPopulation(Long population) { this.population = population; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getSubregion() { return subregion; }
    public void setSubregion(String subregion) { this.subregion = subregion; }
    
    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public Double getGdpPerCapita() { return gdpPerCapita; }
    public void setGdpPerCapita(Double gdpPerCapita) { this.gdpPerCapita = gdpPerCapita; }
    
    public String getFlagEmoji() { return flagEmoji; }
    public void setFlagEmoji(String flagEmoji) { this.flagEmoji = flagEmoji; }
    
    public Double getGeopoliticalRiskIndex() { return geopoliticalRiskIndex; }
    public void setGeopoliticalRiskIndex(Double geopoliticalRiskIndex) { 
        this.geopoliticalRiskIndex = geopoliticalRiskIndex; 
    }
    
    public LocalDateTime getCachedAt() { return cachedAt; }
    public void setCachedAt(LocalDateTime cachedAt) { this.cachedAt = cachedAt; }
    
    public List<NewsArticle> getNews() { return news; }
    public void setNews(List<NewsArticle> news) { this.news = news; }
    
    // helper method to check if cache is still valid (10 min)
    public boolean isCacheValid() {
        return cachedAt != null && 
               cachedAt.isAfter(LocalDateTime.now().minusMinutes(10));
    }
}