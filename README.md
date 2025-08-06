# GeoPulse 🌍

Hey there! I built this full-stack application because I'm fascinated by how interconnected our world is. Ever wondered what's happening in a specific country right now? This app lets you type in any country name and instantly get a comprehensive snapshot: population stats, current weather, latest news headlines, and even a geopolitical risk assessment.

## What Makes This Cool

- **Real-time Data Fusion**: Combines multiple APIs and web scraping to paint a complete picture
- **Live News Scraping**: Pulls fresh headlines from local news sources using RSS feeds
- **Weather Integration**: Shows current conditions and forecasts
- **Demographic Insights**: Population, GDP, and key statistics
- **Risk Assessment**: Geopolitical stability indicators
- **Clean Architecture**: Organized backend with Spring Boot and responsive React frontend

## Prerequisites

Before you dive in, make sure you have:
- **Java 17** or higher
- **Node.js 18** or higher
- **Maven** (for backend dependency management)
- Internet connection (for API calls and scraping)

## Getting Started

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd geopolitics-app
```

### 2. Start the Backend
```bash
cd backend
mvn spring-boot:run
```
The backend will start on `http://localhost:8080`

### 3. Launch the Frontend
```bash
cd frontend
npm install
npm start
```
The frontend will open at `http://localhost:3000`

### 4. Try It Out!

Open your browser to `http://localhost:3000` and try searching for:
- **"Germany"** - Should show Berlin weather, German news, population ~83M
- **"Japan"** - Tokyo conditions, Japanese headlines, demographic data
- **"Brazil"** - Brasília weather, Portuguese news sources, 215M+ population
- **"Norway"** - Oslo conditions, Norwegian headlines, high stability index

## Folder & File Mapping

```
backend/
├── src/
│   ├── controller/
│   │   ├── CountryController.java
│   │   └── HealthController.java
│   ├── service/
│   │   ├── CountryDataService.java
│   │   ├── NewsScrapingService.java
│   │   └── WeatherService.java
│   └── model/
│       ├── CountryInfo.java
│       ├── NewsArticle.java
│       └── WeatherData.java
├── resources/
│   └── application.properties
└── pom.xml

frontend/
├── src/
│   ├── components/
│   │   ├── CountrySearch.js
│   │   ├── CountryDashboard.js
│   │   └── LoadingSpinner.js
│   ├── services/
│   │   └── apiService.js
│   ├── App.js
│   └── index.js
├── public/
│   └── index.html
└── package.json

Root Files:
├── .gitignore
├── LICENSE
└── README.md
```

## How It Works

1. **User Input**: Type a country name in the search box
2. **Backend Processing**: Spring Boot orchestrates multiple service calls
3. **Data Aggregation**: Combines REST API calls with web scraping
4. **Response Delivery**: Returns unified JSON with all data points
5. **Frontend Rendering**: React displays everything in a clean dashboard

The backend uses an in-memory H2 database to cache results for 10 minutes, making repeat searches lightning-fast while keeping data fresh.

## Tech Stack

**Backend**: Java 17, Spring Boot, Jsoup, H2 Database
**Frontend**: React, Axios, CSS3
**APIs Used**: REST Countries, OpenWeatherMap, RSS feeds

Enjoy exploring the world's data! 🚀
