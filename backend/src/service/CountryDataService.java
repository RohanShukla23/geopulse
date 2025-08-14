package com.geopulse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopulse.model.CountryInfo;
import com.geopulse.model.CountryNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

@Service
public class CountryDataService {
    
    @Value("${countries.api.url}")
    private String countriesApiUrl;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Random random;
    
    public CountryDataService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }
    
    public CountryInfo fetchCountryData(String countryName) {
        try {
            String url = countriesApiUrl + "/" + countryName.replace(" ", "%20");
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parseCountryData(response.body(), countryName);
            } else if (response.statusCode() == 404) {
                // Country not found
                throw new CountryNotFoundException("Country '" + countryName + "' not found. Please check the spelling and try again.");
            } else {
                throw new RuntimeException("Failed to fetch country data: HTTP " + response.statusCode());
            }
            
        } catch (CountryNotFoundException e) {
            throw e; // Re-throw country not found exceptions
        } catch (Exception e) {
            System.err.println("Error fetching country data for " + countryName + ": " + e.getMessage());
            throw new RuntimeException("Unable to fetch data for '" + countryName + "'. Please try again.", e);
        }
    }
    
    private CountryInfo parseCountryData(String jsonResponse, String countryName) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Check if response is empty or indicates no results
            if (root.isArray() && root.size() == 0) {
                throw new CountryNotFoundException("No data found for country '" + countryName + "'");
            }
            
            JsonNode country = root.isArray() ? root.get(0) : root;
            
            // Validate that we have essential country data
            if (!country.has("name") && !country.has("capital") && !country.has("population")) {
                throw new CountryNotFoundException("Invalid country data received for '" + countryName + "'");
            }
            
            CountryInfo info = new CountryInfo(countryName);
            
            // Basic info
            if (country.has("capital") && country.get("capital").isArray()) {
                info.setCapital(country.get("capital").get(0).asText());
            }
            
            if (country.has("population")) {
                info.setPopulation(country.get("population").asLong());
            }
            
            if (country.has("region")) {
                info.setRegion(country.get("region").asText());
            }
            
            if (country.has("subregion")) {
                info.setSubregion(country.get("subregion").asText());
            }
            
            if (country.has("area")) {
                info.setArea(country.get("area").asDouble());
            }
            
            // Currency
            if (country.has("currencies")) {
                JsonNode currencies = country.get("currencies");
                if (currencies.isObject()) {
                    String firstCurrency = currencies.fieldNames().next();
                    info.setCurrency(currencies.get(firstCurrency).get("name").asText());
                }
            }
            
            // Language
            if (country.has("languages")) {
                JsonNode languages = country.get("languages");
                if (languages.isObject()) {
                    String firstLanguage = languages.fieldNames().next();
                    info.setLanguage(languages.get(firstLanguage).asText());
                }
            }
            
            // Generate mock GDP per capita (in real app, use World Bank API)
            info.setGdpPerCapita(generateMockGdp(countryName));
            
            // Generate geopolitical risk index (0-10 scale)
            info.setGeopoliticalRiskIndex(generateRiskIndex(countryName));
            
            return info;
            
        } catch (CountryNotFoundException e) {
            throw e; // Re-throw our custom exception
        } catch (Exception e) {
            System.err.println("Error parsing country data: " + e.getMessage());
            throw new CountryNotFoundException("Unable to process data for '" + countryName + "'. Please verify the country name.");
        }
    }
    
    private CountryInfo createFallbackCountryData(String countryName) {
        CountryInfo info = new CountryInfo(countryName);
        info.setCapital("Capital City");
        info.setPopulation(10000000L);
        info.setRegion("Unknown Region");
        info.setSubregion("Unknown Subregion");
        info.setArea(100000.0);
        info.setCurrency("Local Currency");
        info.setLanguage("Local Language");
        info.setGdpPerCapita(generateMockGdp(countryName));
        info.setGeopoliticalRiskIndex(generateRiskIndex(countryName));
        return info;
    }
    
    private Double generateMockGdp(String countryName) {
        // Generate realistic GDP per capita based on country name hash
        int hash = Math.abs(countryName.hashCode());
        return 5000.0 + (hash % 50000); // Range: $5,000 - $55,000
    }
    
    private Double generateRiskIndex(String countryName) {
        // Generate risk index (lower is better) based on country characteristics
        int hash = Math.abs(countryName.hashCode());
        double baseRisk = (hash % 100) / 10.0; // 0-10 scale
        
        // Adjust for well-known stable countries
        String lowerName = countryName.toLowerCase();
        if (lowerName.contains("norway") || lowerName.contains("denmark") || 
            lowerName.contains("sweden") || lowerName.contains("switzerland")) {
            baseRisk = Math.min(baseRisk, 2.0);
        }
        
        return Math.round(baseRisk * 10.0) / 10.0;
    }
}