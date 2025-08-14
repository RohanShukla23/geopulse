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
import java.util.HashMap;
import java.util.Map;

@Service
public class CountryDataService {
    
    @Value("${countries.api.url}")
    private String countriesApiUrl;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    // Comprehensive geopolitical risk factors
    private static final Map<String, Double> REGIONAL_BASE_RISK = new HashMap<>();
    private static final Map<String, Double> COUNTRY_SPECIFIC_RISK = new HashMap<>();
    
    static {
        // Regional base risk (0-3 scale)
        REGIONAL_BASE_RISK.put("Europe", 1.5);
        REGIONAL_BASE_RISK.put("North America", 1.2);
        REGIONAL_BASE_RISK.put("Oceania", 1.0);
        REGIONAL_BASE_RISK.put("Asia", 2.8);
        REGIONAL_BASE_RISK.put("South America", 2.5);
        REGIONAL_BASE_RISK.put("Africa", 4.2);
        REGIONAL_BASE_RISK.put("Antarctica", 0.0);
        
        // Country-specific adjustments (based on established risk indices)
        // Low risk countries (stable democracies, strong institutions)
        COUNTRY_SPECIFIC_RISK.put("Norway", -2.0);
        COUNTRY_SPECIFIC_RISK.put("Denmark", -2.0);
        COUNTRY_SPECIFIC_RISK.put("Sweden", -1.8);
        COUNTRY_SPECIFIC_RISK.put("Switzerland", -1.9);
        COUNTRY_SPECIFIC_RISK.put("Finland", -1.8);
        COUNTRY_SPECIFIC_RISK.put("Iceland", -2.0);
        COUNTRY_SPECIFIC_RISK.put("New Zealand", -1.7);
        COUNTRY_SPECIFIC_RISK.put("Luxembourg", -1.8);
        COUNTRY_SPECIFIC_RISK.put("Germany", -1.5);
        COUNTRY_SPECIFIC_RISK.put("Netherlands", -1.6);
        COUNTRY_SPECIFIC_RISK.put("Austria", -1.4);
        COUNTRY_SPECIFIC_RISK.put("Canada", -1.3);
        COUNTRY_SPECIFIC_RISK.put("Australia", -1.2);
        COUNTRY_SPECIFIC_RISK.put("Japan", -1.0);
        COUNTRY_SPECIFIC_RISK.put("United Kingdom", -0.8);
        COUNTRY_SPECIFIC_RISK.put("France", -0.5);
        COUNTRY_SPECIFIC_RISK.put("United States", -0.3);
        COUNTRY_SPECIFIC_RISK.put("South Korea", -0.2);
        
        // Medium-low risk
        COUNTRY_SPECIFIC_RISK.put("Italy", 0.0);
        COUNTRY_SPECIFIC_RISK.put("Spain", 0.0);
        COUNTRY_SPECIFIC_RISK.put("Portugal", -0.2);
        COUNTRY_SPECIFIC_RISK.put("Czech Republic", 0.1);
        COUNTRY_SPECIFIC_RISK.put("Slovenia", 0.0);
        COUNTRY_SPECIFIC_RISK.put("Estonia", 0.2);
        COUNTRY_SPECIFIC_RISK.put("Chile", 0.3);
        COUNTRY_SPECIFIC_RISK.put("Uruguay", 0.2);
        COUNTRY_SPECIFIC_RISK.put("Costa Rica", 0.1);
        
        // Medium risk
        COUNTRY_SPECIFIC_RISK.put("Poland", 0.5);
        COUNTRY_SPECIFIC_RISK.put("Slovakia", 0.4);
        COUNTRY_SPECIFIC_RISK.put("Hungary", 0.8);
        COUNTRY_SPECIFIC_RISK.put("Greece", 0.6);
        COUNTRY_SPECIFIC_RISK.put("Brazil", 0.7);
        COUNTRY_SPECIFIC_RISK.put("Argentina", 1.0);
        COUNTRY_SPECIFIC_RISK.put("Mexico", 1.2);
        COUNTRY_SPECIFIC_RISK.put("India", 1.5);
        COUNTRY_SPECIFIC_RISK.put("South Africa", 1.4);
        COUNTRY_SPECIFIC_RISK.put("Indonesia", 1.3);
        COUNTRY_SPECIFIC_RISK.put("Thailand", 1.1);
        COUNTRY_SPECIFIC_RISK.put("Philippines", 1.8);
        
        // Higher risk (political instability, conflict zones, weak institutions)
        COUNTRY_SPECIFIC_RISK.put("China", 2.0);
        COUNTRY_SPECIFIC_RISK.put("Russia", 3.5);
        COUNTRY_SPECIFIC_RISK.put("Iran", 4.0);
        COUNTRY_SPECIFIC_RISK.put("North Korea", 5.0);
        COUNTRY_SPECIFIC_RISK.put("Venezuela", 4.2);
        COUNTRY_SPECIFIC_RISK.put("Belarus", 3.8);
        COUNTRY_SPECIFIC_RISK.put("Myanmar", 4.5);
        COUNTRY_SPECIFIC_RISK.put("Afghanistan", 5.2);
        COUNTRY_SPECIFIC_RISK.put("Iraq", 4.8);
        COUNTRY_SPECIFIC_RISK.put("Syria", 5.5);
        COUNTRY_SPECIFIC_RISK.put("Yemen", 5.3);
        COUNTRY_SPECIFIC_RISK.put("Libya", 4.9);
        COUNTRY_SPECIFIC_RISK.put("Somalia", 5.4);
        COUNTRY_SPECIFIC_RISK.put("South Sudan", 5.1);
        COUNTRY_SPECIFIC_RISK.put("Central African Republic", 5.0);
        COUNTRY_SPECIFIC_RISK.put("Democratic Republic of the Congo", 4.7);
        COUNTRY_SPECIFIC_RISK.put("Chad", 4.6);
        COUNTRY_SPECIFIC_RISK.put("Mali", 4.4);
        COUNTRY_SPECIFIC_RISK.put("Sudan", 4.8);
        COUNTRY_SPECIFIC_RISK.put("Nigeria", 3.2);
        COUNTRY_SPECIFIC_RISK.put("Pakistan", 3.8);
        COUNTRY_SPECIFIC_RISK.put("Turkey", 2.8);
        COUNTRY_SPECIFIC_RISK.put("Egypt", 2.9);
        COUNTRY_SPECIFIC_RISK.put("Ethiopia", 3.5);
        COUNTRY_SPECIFIC_RISK.put("Ukraine", 4.3);
    }
    
    public CountryDataService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
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
                throw new CountryNotFoundException("Country '" + countryName + "' not found. Please check the spelling and try again.");
            } else {
                throw new RuntimeException("Failed to fetch country data: HTTP " + response.statusCode());
            }
            
        } catch (CountryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error fetching country data for " + countryName + ": " + e.getMessage());
            throw new RuntimeException("Unable to fetch data for '" + countryName + "'. Please try again.", e);
        }
    }
    
    private CountryInfo parseCountryData(String jsonResponse, String countryName) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            if (root.isArray() && root.size() == 0) {
                throw new CountryNotFoundException("No data found for country '" + countryName + "'");
            }
            
            JsonNode country = root.isArray() ? root.get(0) : root;
            
            if (!country.has("name")) {
                throw new CountryNotFoundException("Invalid country data received for '" + countryName + "'");
            }
            
            // Extract proper country name
            String properCountryName = countryName;
            if (country.has("name") && country.get("name").has("common")) {
                properCountryName = country.get("name").get("common").asText();
            }
            
            CountryInfo info = new CountryInfo(properCountryName);
            
            // Capital - handle multiple capitals or empty array
            if (country.has("capital") && country.get("capital").isArray() && country.get("capital").size() > 0) {
                info.setCapital(country.get("capital").get(0).asText());
            } else {
                info.setCapital("N/A");
            }
            
            // Population
            if (country.has("population")) {
                info.setPopulation(country.get("population").asLong());
            }
            
            // Region - this should be accurate from the API
            if (country.has("region")) {
                info.setRegion(country.get("region").asText());
            }
            
            // Subregion
            if (country.has("subregion")) {
                info.setSubregion(country.get("subregion").asText());
            }
            
            // Area
            if (country.has("area") && !country.get("area").isNull()) {
                info.setArea(country.get("area").asDouble());
            }
            
            // Currency - get the first currency
            if (country.has("currencies") && country.get("currencies").isObject()) {
                JsonNode currencies = country.get("currencies");
                if (currencies.size() > 0) {
                    String firstCurrencyCode = currencies.fieldNames().next();
                    JsonNode currencyInfo = currencies.get(firstCurrencyCode);
                    if (currencyInfo.has("name")) {
                        info.setCurrency(currencyInfo.get("name").asText());
                    } else {
                        info.setCurrency(firstCurrencyCode);
                    }
                }
            }
            
            // Language - get the first official language
            if (country.has("languages") && country.get("languages").isObject()) {
                JsonNode languages = country.get("languages");
                if (languages.size() > 0) {
                    String firstLanguageCode = languages.fieldNames().next();
                    info.setLanguage(languages.get(firstLanguageCode).asText());
                }
            }
            
            // Flag emoji - this should be in the API response
            if (country.has("flag")) {
                info.setFlagEmoji(country.get("flag").asText());
            } else {
                info.setFlagEmoji(generateFlagEmoji(properCountryName));
            }
            
            // Generate realistic GDP per capita
            info.setGdpPerCapita(generateRealisticGdp(properCountryName, info.getRegion()));
            
            // Calculate proper geopolitical risk index
            info.setGeopoliticalRiskIndex(calculateGeopoliticalRisk(properCountryName, info.getRegion()));
            
            return info;
            
        } catch (CountryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error parsing country data: " + e.getMessage());
            e.printStackTrace();
            throw new CountryNotFoundException("Unable to process data for '" + countryName + "'. Please verify the country name.");
        }
    }
    
    private String generateFlagEmoji(String countryName) {
        // Map of country names to flag emojis
        Map<String, String> flagMap = new HashMap<>();
        flagMap.put("Germany", "🇩🇪");
        flagMap.put("Japan", "🇯🇵");
        flagMap.put("Brazil", "🇧🇷");
        flagMap.put("Norway", "🇳🇴");
        flagMap.put("United States", "🇺🇸");
        flagMap.put("United Kingdom", "🇬🇧");
        flagMap.put("France", "🇫🇷");
        flagMap.put("China", "🇨🇳");
        flagMap.put("India", "🇮🇳");
        flagMap.put("Australia", "🇦🇺");
        flagMap.put("Canada", "🇨🇦");
        flagMap.put("Mexico", "🇲🇽");
        flagMap.put("Argentina", "🇦🇷");
        flagMap.put("South Korea", "🇰🇷");
        flagMap.put("Italy", "🇮🇹");
        flagMap.put("Spain", "🇪🇸");
        flagMap.put("Netherlands", "🇳🇱");
        flagMap.put("Sweden", "🇸🇪");
        flagMap.put("Denmark", "🇩🇰");
        flagMap.put("Switzerland", "🇨🇭");
        flagMap.put("Russia", "🇷🇺");
        flagMap.put("South Africa", "🇿🇦");
        flagMap.put("Egypt", "🇪🇬");
        flagMap.put("Turkey", "🇹🇷");
        flagMap.put("Indonesia", "🇮🇩");
        flagMap.put("Thailand", "🇹🇭");
        flagMap.put("Vietnam", "🇻🇳");
        flagMap.put("Philippines", "🇵🇭");
        flagMap.put("Nigeria", "🇳🇬");
        flagMap.put("Kenya", "🇰🇪");
        flagMap.put("Ghana", "🇬🇭");
        flagMap.put("Morocco", "🇲🇦");
        flagMap.put("Israel", "🇮🇱");
        flagMap.put("Saudi Arabia", "🇸🇦");
        flagMap.put("Iran", "🇮🇷");
        flagMap.put("Iraq", "🇮🇶");
        flagMap.put("Pakistan", "🇵🇰");
        flagMap.put("Bangladesh", "🇧🇩");
        flagMap.put("Afghanistan", "🇦🇫");
        flagMap.put("Ukraine", "🇺🇦");
        flagMap.put("Poland", "🇵🇱");
        flagMap.put("Czech Republic", "🇨🇿");
        flagMap.put("Hungary", "🇭🇺");
        flagMap.put("Romania", "🇷🇴");
        flagMap.put("Greece", "🇬🇷");
        flagMap.put("Portugal", "🇵🇹");
        flagMap.put("Belgium", "🇧🇪");
        flagMap.put("Austria", "🇦🇹");
        flagMap.put("Finland", "🇫🇮");
        flagMap.put("Iceland", "🇮🇸");
        flagMap.put("Ireland", "🇮🇪");
        flagMap.put("Luxembourg", "🇱🇺");
        flagMap.put("Malta", "🇲🇹");
        flagMap.put("Cyprus", "🇨🇾");
        flagMap.put("Estonia", "🇪🇪");
        flagMap.put("Latvia", "🇱🇻");
        flagMap.put("Lithuania", "🇱🇹");
        flagMap.put("Slovenia", "🇸🇮");
        flagMap.put("Slovakia", "🇸🇰");
        flagMap.put("Croatia", "🇭🇷");
        flagMap.put("Bosnia and Herzegovina", "🇧🇦");
        flagMap.put("Serbia", "🇷🇸");
        flagMap.put("Montenegro", "🇲🇪");
        flagMap.put("North Macedonia", "🇲🇰");
        flagMap.put("Albania", "🇦🇱");
        flagMap.put("Bulgaria", "🇧🇬");
        flagMap.put("New Zealand", "🇳🇿");
        flagMap.put("Chile", "🇨🇱");
        flagMap.put("Peru", "🇵🇪");
        flagMap.put("Colombia", "🇨🇴");
        flagMap.put("Venezuela", "🇻🇪");
        flagMap.put("Ecuador", "🇪🇨");
        flagMap.put("Bolivia", "🇧🇴");
        flagMap.put("Uruguay", "🇺🇾");
        flagMap.put("Paraguay", "🇵🇾");
        flagMap.put("Guyana", "🇬🇾");
        flagMap.put("Suriname", "🇸🇷");
        flagMap.put("Ethiopia", "🇪🇹");
        flagMap.put("Sudan", "🇸🇩");
        flagMap.put("South Sudan", "🇸🇸");
        flagMap.put("Somalia", "🇸🇴");
        flagMap.put("Libya", "🇱🇾");
        flagMap.put("Tunisia", "🇹🇳");
        flagMap.put("Algeria", "🇩🇿");
        
        return flagMap.getOrDefault(countryName, "🏴");
    }
    
    private Double generateRealisticGdp(String countryName, String region) {
        // Base GDP by region (realistic averages)
        Map<String, Double> regionGdp = new HashMap<>();
        regionGdp.put("Europe", 35000.0);
        regionGdp.put("North America", 45000.0);
        regionGdp.put("Oceania", 40000.0);
        regionGdp.put("Asia", 15000.0);
        regionGdp.put("South America", 12000.0);
        regionGdp.put("Africa", 8000.0);
        
        double baseGdp = regionGdp.getOrDefault(region, 15000.0);
        
        // Country-specific adjustments based on economic development
        Map<String, Double> countryGdpMultiplier = new HashMap<>();
        // High-income countries
        countryGdpMultiplier.put("Luxembourg", 3.5);
        countryGdpMultiplier.put("Switzerland", 2.5);
        countryGdpMultiplier.put("Norway", 2.3);
        countryGdpMultiplier.put("United States", 1.8);
        countryGdpMultiplier.put("Denmark", 1.8);
        countryGdpMultiplier.put("Iceland", 1.7);
        countryGdpMultiplier.put("Sweden", 1.5);
        countryGdpMultiplier.put("Germany", 1.4);
        countryGdpMultiplier.put("Netherlands", 1.4);
        countryGdpMultiplier.put("Austria", 1.3);
        countryGdpMultiplier.put("Finland", 1.3);
        countryGdpMultiplier.put("Australia", 1.2);
        countryGdpMultiplier.put("Canada", 1.2);
        countryGdpMultiplier.put("France", 1.1);
        countryGdpMultiplier.put("United Kingdom", 1.1);
        countryGdpMultiplier.put("Japan", 1.0);
        countryGdpMultiplier.put("South Korea", 0.9);
        countryGdpMultiplier.put("Italy", 0.9);
        countryGdpMultiplier.put("Spain", 0.8);
        
        // Upper middle income
        countryGdpMultiplier.put("Russia", 0.3);
        countryGdpMultiplier.put("China", 0.4);
        countryGdpMultiplier.put("Brazil", 0.3);
        countryGdpMultiplier.put("Mexico", 0.3);
        countryGdpMultiplier.put("Argentina", 0.25);
        countryGdpMultiplier.put("Turkey", 0.3);
        countryGdpMultiplier.put("Thailand", 0.2);
        
        // Lower middle income
        countryGdpMultiplier.put("India", 0.07);
        countryGdpMultiplier.put("Indonesia", 0.12);
        countryGdpMultiplier.put("Philippines", 0.1);
        countryGdpMultiplier.put("Vietnam", 0.1);
        countryGdpMultiplier.put("Egypt", 0.12);
        countryGdpMultiplier.put("Nigeria", 0.08);
        countryGdpMultiplier.put("Pakistan", 0.05);
        countryGdpMultiplier.put("Bangladesh", 0.08);
        
        // Low income
        countryGdpMultiplier.put("Afghanistan", 0.02);
        countryGdpMultiplier.put("Ethiopia", 0.03);
        countryGdpMultiplier.put("Somalia", 0.015);
        countryGdpMultiplier.put("Chad", 0.02);
        countryGdpMultiplier.put("Central African Republic", 0.015);
        countryGdpMultiplier.put("South Sudan", 0.01);
        
        double multiplier = countryGdpMultiplier.getOrDefault(countryName, 0.5);
        return Math.round(baseGdp * multiplier);
    }
    
    private Double calculateGeopoliticalRisk(String countryName, String region) {
        // Start with regional base risk
        double risk = REGIONAL_BASE_RISK.getOrDefault(region, 3.0);
        
        // Add country-specific adjustment
        risk += COUNTRY_SPECIFIC_RISK.getOrDefault(countryName, 0.0);
        
        // Ensure risk is within bounds (0-10 scale)
        risk = Math.max(0.0, Math.min(10.0, risk));
        
        // Round to 1 decimal place
        return Math.round(risk * 10.0) / 10.0;
    }
}