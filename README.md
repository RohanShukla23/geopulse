# GeoPulse 🌍

Ever get curious about what's happening somewhere in the world right now? Like, what's the latest news in Thailand, or how's the political situation in Chile? 

I built GeoPulse because I'm a bit of a news junkie and geopolitics nerd. Just type any country name you get the full picture: population stats, breaking news from local sources, and even how stable things are politically.

## what makes this cool?

- **actually fresh data** - pulls live news from local RSS feeds, not just generic international coverage
- **one search, everything** - population, GDP, news, risk levels all in one place  
- **pretty fast** - caches results so you're not waiting around
- **real local sources** - German news from Germany, Japanese headlines from Japan, etc.

## what you'll need

- **Java 17+**
- **Node.js 18+** 
- **Maven**
- Internet connection (obviously)

## getting it running

### clone and enter
```bash
git clone https://github.com/RohanShukla23/geopulse
cd geopulse
```

### fire up the backend
```bash
cd backend
mvn spring-boot:run
```
Backend lives at `http://localhost:8080`

### start the frontend
```bash
cd frontend
npm install
npm start
```
Frontend opens at `http://localhost:3000`

### test drive
try searching for some countries to see if everything's working:
- **Germany** → should show Berlin, German headlines, ~83M people
- **Japan** → Tokyo, Japanese news, demographic stuff
- **Brazil** → Brasília, Portuguese sources, 215M+ population  
- **Singapore** → tiny but mighty, usually very stable

## how the code's organized

```
backend/
├── src/
│   ├── controller/           # API endpoints
│   ├── service/             # the actual logic
│   └── model/               # data structures
├── resources/
└── pom.xml

frontend/
├── src/
│   ├── components/          # React components
│   ├── services/            # API calls
│   └── App.js              # main app
└── package.json

# usual suspects
├── .gitignore
├── LICENSE  
└── README.md (you're here!)
```

## under the hood

1. you type a country → frontend sends request
2. Spring Boot backend hits multiple APIs and scrapes news
3. everything gets mashed together into one clean response
4. React displays it all nicely
5. results get cached for 10 minutes (fast subsequent searches)

**tech stack:** Java 17 + Spring Boot + React + a bunch of APIs and web scraping magic

## troubleshooting

- **backend won't start?** check your Java version
- **no news showing up?** some countries have limited RSS feeds
- **weird data?** the APIs sometimes return funky results for smaller countries
- **slow searches?** first search per country takes longer (no cache yet)

---

built this because the world's fascinating and news shouldn't be hard to find. enjoy exploring! 🚀
