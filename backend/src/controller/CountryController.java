package com.geopulse.controller;

import com.geopulse.model.CountryInfo;
import com.geopulse.model.NewsArticle;
import com.geopulse.model.WeatherData;
import com.geopulse.service.CountryDataService;
import com.geopulse.service.NewsScrapingService;
import com.geopulse.service.WeatherService;
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
    private WeatherService weatherService;
    
    @Autowired
    private NewsScrapingService newsScrapingService;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @GetMapping("/{countryName}")
    @Transactional
    public ResponseEntity<CountryInfo> getCountryInfo(@PathVariable String countryName) {
        try {
            // Check cache first
            CountryInfo cachedInfo = getCachedCountryInfo(countryName);
            
            if (cachedInfo != null && cachedInfo.isCacheValid()) {
                // Add live data (weather and news)
                addLiveData(cachedInfo);
                return ResponseEntity.ok(cachedInfo);
            }
            
            // Fetch fresh data
            CountryInfo countryInfo = fetchCompleteCountryData(countryName);
            
            // Cache the basic country info (not weather/news as they're real-time)
            cacheCountryInfo(countryInfo);
            
            return ResponseEntity.ok(countryInfo);
            
        } catch (Exception e) {
            System.err.println("Error processing request for " + countryName + ": " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(createErrorCountryInfo(countryName, e.getMessage()));
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
            
            CompletableFuture<WeatherData> weatherFuture = 
                CompletableFuture.supplyAsync(() -> {
                    // Use country name as city for weather (fallback logic)
                    return weatherService.fetchWeatherData(countryName);
                });
            
            CompletableFuture<List<NewsArticle>> newsFuture = 
                CompletableFuture.supplyAsync(() -> newsScrapingService.fetchNewsForCountry(countryName));
            
            // Wait for all to complete
            CountryInfo countryInfo = countryFuture.get();
            WeatherData weather = weatherFuture.get();
            List<NewsArticle> news = newsFuture.get();
            
            // If weather fetch failed with country name, try with capital
            if (weather.getTemperature() == null && countryInfo.getCapital() != null) {
                weather = weatherService.fetchWeatherData(countryInfo.getCapital());
            }
            
            countryInfo.setWeather(weather);
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
        
        WeatherData weather = weatherService.fetchWeatherData(
            countryInfo.getCapital() != null ? countryInfo.getCapital() : countryName);
        countryInfo.setWeather(weather);
        
        List<NewsArticle> news = newsScrapingService.fetchNewsForCountry(countryName);
        countryInfo.setNews(news);
        
        return countryInfo;
    }
    
    private void addLiveData(CountryInfo countryInfo) {
        try {
            // Always fetch fresh weather and news data
            String cityForWeather = countryInfo.getCapital() != null ? 
                countryInfo.getCapital() : countryInfo.getCountryName();
            
            CompletableFuture<WeatherData> weatherFuture = 
                CompletableFuture.supplyAsync(() -> weatherService.fetchWeatherData(cityForWeather));
            
            CompletableFuture<List<NewsArticle>> newsFuture = 
                CompletableFuture.supplyAsync(() -> 
                    newsScrapingService.fetchNewsForCountry(countryInfo.getCountryName()));
            
            countryInfo.setWeather(weatherFuture.get());
            countryInfo.setNews(newsFuture.get());
            
        } catch (Exception e) {
            System.err.println("Error fetching live data: " + e.getMessage());
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
            // Remove weather and news before caching (they're real-time)
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
            
            entityManager.persist(cacheInfo);
            
        } catch (Exception e) {
            System.err.println("Error caching country info: " + e.getMessage());
        }
    }
    
    private CountryInfo createErrorCountryInfo(String countryName, String errorMessage) {
        CountryInfo errorInfo = new CountryInfo(countryName);
        errorInfo.setCapital("Data unavailable");
        errorInfo.setPopulation(0L);
        errorInfo.setRegion("Error: " + errorMessage);
        return errorInfo;
    }
}