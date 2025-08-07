package com.geopulse.model;

public class WeatherData {
    
    private String city;
    private Double temperature;
    private Double feelsLike;
    private String description;
    private String mainCondition;
    private Integer humidity;
    private Double windSpeed;
    private Integer pressure;
    private Integer visibility;
    
    // Constructors
    public WeatherData() {}
    
    public WeatherData(String city) {
        this.city = city;
    }
    
    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(Double feelsLike) { this.feelsLike = feelsLike; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getMainCondition() { return mainCondition; }
    public void setMainCondition(String mainCondition) { this.mainCondition = mainCondition; }
    
    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    
    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
    
    public Integer getPressure() { return pressure; }
    public void setPressure(Integer pressure) { this.pressure = pressure; }
    
    public Integer getVisibility() { return visibility; }
    public void setVisibility(Integer visibility) { this.visibility = visibility; }
    
    // Helper method to get temperature in Celsius
    public String getTemperatureDisplay() {
        return temperature != null ? String.format("%.1f°C", temperature) : "N/A";
    }
    
    // Helper method to get wind speed display
    public String getWindSpeedDisplay() {
        return windSpeed != null ? String.format("%.1f m/s", windSpeed) : "N/A";
    }
}