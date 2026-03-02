# Weather API Wrapper Service

> A Spring Boot REST API that fetches and caches real-time weather data from Visual Crossing — built to demonstrate 3rd-party API integration, Redis caching, and clean REST design.

## What It Does

Send a city name to a single endpoint and get back current weather conditions plus a daily forecast. Results are cached in Redis for 12 hours, so repeated queries for the same city skip the upstream API call entirely. The service handles invalid cities and upstream failures with structured error responses.

## Why I Built It

I wanted to practice a real-world pattern that shows up constantly in backend work: wrapping an external API with a caching layer to protect against rate limits and latency. It also gave me hands-on experience with Spring Data Redis and environment-variable-driven configuration.

## Tech Stack

| Layer | Choice | Why |
|-------|--------|-----|
| Language | Java | Strong typing and Spring ecosystem; widely used in enterprise backend roles |
| Framework | Spring Boot 4.x | Industry-standard; auto-configuration reduces boilerplate while keeping things explicit |
| Cache | Redis + Spring Data Redis | In-memory key-value store with native TTL support — the standard tool for this pattern |
| HTTP Client | RestTemplate | Synchronous HTTP integration; straightforward for a single external dependency |
| Weather Data | Visual Crossing API | Free tier, stable JSON structure, and easy to integrate |
| Infrastructure | Docker Compose | Reproducible local Redis setup without manual installation |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker (for Redis via Compose)
- A free [Visual Crossing API key](https://www.visualcrossing.com/weather-api)

### Setup

```bash
git clone https://github.com/your-username/weather-api.git
cd weather-api

# Start Redis locally
docker compose up -d

# Copy the example config and fill in your API key
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
# → Open application.yaml and set weather.api.key to your Visual Crossing key

# Run the app
./mvnw spring-boot:run
```

### Try It

```bash
curl "http://localhost:8080/api/v1/weather?city=London"
```

The first call hits Visual Crossing. The second call (within 12 hours) returns from Redis cache.

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/weather?city={city}` | Returns current conditions and daily forecast for the given city |

### Example Response

```json
{
  "address": "London, England, United Kingdom",
  "description": "Partly cloudy throughout the day.",
  "currentConditions": {
    "temp": 14.2,
    "feelslike": 12.8,
    "humidity": 72.0,
    "windspeed": 18.5,
    "uvindex": 3.0,
    "conditions": "Partially cloudy",
    "icon": "partly-cloudy-day",
    "sunrise": "06:12:00",
    "sunset": "18:44:00"
  },
  "forecast": [
    {
      "datetime": "2026-03-02",
      "tempmax": 16.1,
      "tempmin": 9.3,
      "temp": 12.7,
      "humidity": 68.0,
      "precipprob": 20.0,
      "conditions": "Partially cloudy",
      "description": "Partly cloudy throughout the day.",
      "icon": "partly-cloudy-day"
    }
  ]
}
```

## What I Learned

- **How Redis TTL works in practice** — using `opsForValue().set(key, value, duration, TimeUnit)` to auto-expire cache entries eliminates manual eviction logic entirely.
- **The value of a `@RestControllerAdvice` global exception handler** — without one, Spring returns raw error pages and stack traces to clients; a single handler gives you clean, consistent JSON error responses across the whole API.
- **How `@Value` injection works with YAML profiles** — separating secrets into `application.yaml` (gitignored) and committing only `application-example.yaml` is the correct pattern for environment-specific config.
- **Why RestTemplate is being replaced by WebClient** — RestTemplate is synchronous and being deprecated; the same pattern implemented with `WebClient` is non-blocking and production-ready.

## What I'd Do Differently

- **Add a `GlobalExceptionHandler` from day one** — I handled the happy path first and retrofitted error handling later. Starting with it means every endpoint automatically returns a useful error response.
- **Use `WebClient` instead of `RestTemplate`** — RestTemplate is in maintenance mode as of Spring 5. `WebClient` handles the same use case with non-blocking I/O and better timeout control.
- **Write tests before wiring Redis** — I had no tests when I introduced the caching layer, which made it harder to verify the cache-hit vs cache-miss paths were both correct.
## Roadmap (v2 ideas)

- Rate limiting per IP using Bucket4j
- Support for metric/imperial unit toggle via query parameter
- `/api/v1/weather/history?city={city}` endpoint backed by PostgreSQL for query logging
- Swagger / OpenAPI docs via `springdoc-openapi`
- Deploy to Railway with environment variables configured via dashboard
