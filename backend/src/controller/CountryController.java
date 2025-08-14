package com.geopulse.controller;

import com.geopulse.model.CountryInfo;
import com.geopulse.model.CountryNotFoundException;
import com.geopulse.model.NewsArticle;
import com.geopulse.service.CountryDataService;
import com.geopulse.service.NewsScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/countries")
@CrossOrigin(origins = "http://localhost:3000")
public class CountryController {
    
    @Autowired
    private CountryDataService countryDataService;
    
    @Autowired
    private NewsScrapingService newsScrapingService;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @GetMapping("/{countryName}")
    @Transactional
    public ResponseEntity<CountryInfo> getCountryInfo(@PathVariable String countryName) {
        try {
            // Validate input
            if (countryName == null || countryName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    createErrorCountryInfo("", "Country name cannot be empty"));
            }
            
            // Clean and validate country name
            String cleanCountryName = countryName.trim();
            if (cleanCountryName.length() < 2) {
                return ResponseEntity.badRequest().body(
                    createErrorCountryInfo(cleanCountryName, "Country name must be at least 2 characters long"));
            }
            
            // Check for obviously invalid input (numbers, special characters)
            if (cleanCountryName.matches(".*[0-9].*") || 
                cleanCountryName.matches(".*[!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/|\\\\].*")) {
                return ResponseEntity.badRequest().body(
                    createErrorCountryInfo(cleanCountryName, "Invalid country name format"));
            }
            
            // Check cache first
            CountryInfo cachedInfo = getCachedCountryInfo(cleanCountryName);
            
            if (cachedInfo != null && cachedInfo.isCacheValid()) {
                // Add live news data
                addLiveNewsData(cachedInfo);
                return ResponseEntity.ok(cachedInfo);
            }
            
            // Fetch fresh data
            CountryInfo countryInfo = fetchCompleteCountryData(cleanCountryName);
            
            // Cache the basic country info (not news as they're real-time)
            cacheCountryInfo(countryInfo);
            
            return ResponseEntity.ok(countryInfo);
            
        } catch (CountryNotFoundException e) {
            System.err.println("Country not found: " + e.getMessage());
            return ResponseEntity.status(404).body(
                createErrorCountryInfo(countryName, e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error processing request for " + countryName + ": " + e.getMessage());
            return ResponseEntity.internalServerError().body(
                createErrorCountryInfo(countryName, "Service temporarily unavailable. Please try again."));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchCountries(@RequestParam String query) {
        // Simple country name suggestions - in production, use a proper search service
        List<String> suggestions = List.of(
            "Germany", "Japan", "Brazil", "Norway", "United States", 
            "United Kingdom", "France", "China", "India", "Australia",
            "Canada", "Mexico", "Argentina", "South Korea", "Italy",
            "Spain", "Netherlands", "Sweden", "Denmark", "Switzerland"
        ).stream()
         .filter(country -> country.toLowerCase().contains(query.toLowerCase()))
         .limit(10)
         .toList();
        
        return ResponseEntity.ok(suggestions);
    }
    
    private CountryInfo fetchCompleteCountryData(String countryName) {
        try {
            // Fetch data concurrently for better performance
            CompletableFuture<CountryInfo> countryFuture = 
                CompletableFuture.supplyAsync(() -> countryDataService.fetchCountryData(countryName));
            
            CompletableFuture<List<NewsArticle>> newsFuture = 
                CompletableFuture.supplyAsync(() -> newsScrapingService.fetchNewsForCountry(countryName));
            
            // Wait for all to complete
            CountryInfo countryInfo = countryFuture.get();
            List<NewsArticle> news = newsFuture.get();
            
            countryInfo.setNews(news);
            
            return countryInfo;
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching concurrent data: " + e.getMessage());
            // Fallback to sequential fetching
            return fetchSequentialCountryData(countryName);
        }
    }
    
    private CountryInfo fetchSequentialCountryData(String countryName) {
        CountryInfo countryInfo = countryDataService.fetchCountryData(countryName);
        
        List<NewsArticle> news = newsScrapingService.fetchNewsForCountry(countryName);
        countryInfo.setNews(news);
        
        return countryInfo;
    }
    
    private void addLiveNewsData(CountryInfo countryInfo) {
        try {
            // Always fetch fresh news data
            CompletableFuture<List<NewsArticle>> newsFuture = 
                CompletableFuture.supplyAsync(() -> 
                    newsScrapingService.fetchNewsForCountry(countryInfo.getCountryName()));
            
            countryInfo.setNews(newsFuture.get());
            
        } catch (Exception e) {
            System.err.println("Error fetching live news data: " + e.getMessage());
            // Continue with cached data only
        }
    }
    
    private CountryInfo getCachedCountryInfo(String countryName) {
        try {
            TypedQuery<CountryInfo> query = entityManager.createQuery(
                "SELECT c FROM CountryInfo c WHERE LOWER(c.countryName) = LOWER(:name)", 
                CountryInfo.class);
            query.setParameter("name", countryName);
            
            List<CountryInfo> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
            
        } catch (Exception e) {
            System.err.println("Error querying cache: " + e.getMessage());
            return null;
        }
    }
    
    private void cacheCountryInfo(CountryInfo countryInfo) {
        try {
            // Remove news before caching (they're real-time)
            CountryInfo cacheInfo = new CountryInfo(countryInfo.getCountryName());
            cacheInfo.setCapital(countryInfo.getCapital());
            cacheInfo.setPopulation(countryInfo.getPopulation());
            cacheInfo.setRegion(countryInfo.getRegion());
            cacheInfo.setSubregion(countryInfo.getSubregion());
            cacheInfo.setArea(countryInfo.getArea());
            cacheInfo.setCurrency(countryInfo.getCurrency());
            cacheInfo.setLanguage(countryInfo.getLanguage());
            cacheInfo.setGdpPerCapita(countryInfo.getGdpPerCapita());
            cacheInfo.setGeopoliticalRiskIndex(countryInfo.getGeopoliticalRiskIndex());
            cacheInfo.setFlagEmoji(countryInfo.getFlagEmoji());
            
            entityManager.persist(cacheInfo);
            
        } catch (Exception e) {
            System.err.println("Error caching country info: " + e.getMessage());
        }
    }
    
    private CountryInfo createErrorCountryInfo(String countryName, String errorMessage) {
        CountryInfo errorInfo = new CountryInfo(countryName);
        errorInfo.setCapital("N/A");
        errorInfo.setPopulation(0L);
        errorInfo.setRegion(errorMessage);
        errorInfo.setSubregion("Please try a different search");
        errorInfo.setArea(0.0);
        errorInfo.setCurrency("N/A");
        errorInfo.setLanguage("N/A");
        errorInfo.setGdpPerCapita(0.0);
        errorInfo.setGeopoliticalRiskIndex(0.0);
        errorInfo.setFlagEmoji("🏳️");
        return errorInfo;
    }
}