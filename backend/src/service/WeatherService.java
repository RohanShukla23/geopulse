package com.geoinsight.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoinsight.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

@Service
public class WeatherService {
    
    @Value("${weather.api.url}")
    private String weatherApiUrl;
    
    @Value("${weather.api.key}")
    private String weatherApiKey;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Random random;
    
    private final String[] weatherConditions = {
        "Clear", "Clouds", "Rain", "Snow", "Thunderstorm", "Drizzle", "Mist"
    };
    
    private final String[] weatherDescriptions = {
        "clear sky", "few clouds", "scattered clouds", "broken clouds",
        "overcast clouds", "light rain", "moderate rain", "heavy intensity rain",
        "light snow", "snow", "mist", "thunderstorm"
    };
    
    public WeatherService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }
    
    public WeatherData fetchWeatherData(String city) {
        if (!"demo_key".equals(weatherApiKey)) {
            return fetchRealWeatherData(city);
        } else {
            // Generate mock data when using demo key
            return generateMockWeatherData(city);
        }
    }
    
    private WeatherData fetchRealWeatherData(String city) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric", 
                weatherApiUrl, city.replace(" ", "%20"), weatherApiKey);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parseWeatherData(response.body(), city);
            } else {
                return generateMockWeatherData(city);
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching weather data for " + city + ": " + e.getMessage());
            return generateMockWeatherData(city);
        }
    }
    
    private WeatherData parseWeatherData(String jsonResponse, String city) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            WeatherData weather = new WeatherData(city);
            
            // Main weather data
            if (root.has("main")) {
                JsonNode main = root.get("main");
                weather.setTemperature(main.get("temp").asDouble());
                weather.setFeelsLike(main.get("feels_like").asDouble());
                weather.setHumidity(main.get("humidity").asInt());
                weather.setPressure(main.get("pressure").asInt());
            }
            
            // Weather description
            if (root.has("weather") && root.get("weather").isArray()) {
                JsonNode weatherArray = root.get("weather").get(0);
                weather.setMainCondition(weatherArray.get("main").asText());
                weather.setDescription(weatherArray.get("description").asText());
            }
            
            // Wind data
            if (root.has("wind")) {
                JsonNode wind = root.get("wind");
                weather.setWindSpeed(wind.get("speed").asDouble());
            }
            
            // Visibility
            if (root.has("visibility")) {
                weather.setVisibility(root.get("visibility").asInt());
            }
            
            return weather;
            
        } catch (Exception e) {
            System.err.println("Error parsing weather data: " + e.getMessage());
            return generateMockWeatherData(city);
        }
    }
    
    private WeatherData generateMockWeatherData(String city) {
        WeatherData weather = new WeatherData(city);
        
        // Generate realistic data based on city name hash for consistency
        int hash = Math.abs(city.hashCode());
        
        // Temperature: -10°C to 35°C
        double temp = -10 + (hash % 45);
        weather.setTemperature(temp);
        weather.setFeelsLike(temp + ((hash % 6) - 3)); // ±3 degrees
        
        // Humidity: 20% to 90%
        weather.setHumidity(20 + (hash % 70));
        
        // Pressure: 980 to 1030 hPa
        weather.setPressure(980 + (hash % 50));
        
        // Wind speed: 0 to 15 m/s
        weather.setWindSpeed((hash % 150) / 10.0);
        
        // Visibility: 5000 to 10000 meters
        weather.setVisibility(5000 + (hash % 5000));
        
        // Weather condition
        String condition = weatherConditions[hash % weatherConditions.length];
        weather.setMainCondition(condition);
        weather.setDescription(weatherDescriptions[hash % weatherDescriptions.length]);
        
        return weather;
    }
}