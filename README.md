# Coupon Service

REST API for managing discount coupons with concurrency-safe redemption.

## Features

- Create coupons with configurable usage limits
- Redeem coupons with concurrency safety (pessimistic locking)
- Case-insensitive coupon code uniqueness
- Max usage limit enforcement
- One-time usage per user
- Country restriction based on IP geolocation
- Redis cache for IP-to-country lookups
- Liquibase database migrations
- Dockerized infrastructure (PostgreSQL + Redis)
- Integration and unit tests with Testcontainers

## Tech Stack

- Java 21
- Spring Boot 4.0
- Spring MVC (REST)
- Spring Data JPA (PostgreSQL)
- Spring Data Redis (caching)
- Liquibase (migrations)
- Lombok
- SpringDoc OpenAPI (Swagger UI)
- Docker Compose
- Testcontainers, JUnit 5, Mockito
- JaCoCo (code coverage)

## Architecture

The project follows a layered architecture:

```
api/              → Controllers, request/response DTOs, exception handler
application/      → Commands, results, orchestration services, exceptions
domain/           → Validators, normalizers, factories (business logic)
persistence/      → JPA entities and Spring Data repositories
infrastructure/   → External API integrations (geo IP), Redis caching, config
```

## API Endpoints

### Create Coupon

```
POST /api/coupons
Content-Type: application/json

{
  "code": "SUMMER2024",
  "maxUsages": 100,
  "countryCode": "PL"
}
```

**Response** `201 Created`:
```json
{
  "id": "uuid",
  "code": "SUMMER2024",
  "maxUsages": 100,
  "countryCode": "PL"
}
```

### Redeem Coupon

```
POST /api/coupons/redeem
Content-Type: application/json

{
  "code": "SUMMER2024",
  "userId": "user-123",
  "ipAddress": "185.0.0.1"
}
```

**Response** `200 OK`:
```json
{
  "status": "REDEEMED",
  "success": true,
  "message": "Coupon redeemed successfully"
}
```

Possible statuses: `REDEEMED`, `COUPON_NOT_FOUND`, `COUPON_EXHAUSTED`, `COUNTRY_NOT_ALLOWED`, `ALREADY_REDEEMED_BY_USER`.

### Error Responses

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `COUPON_ALREADY_EXISTS` | 409 | Duplicate coupon code |
| `GEO_SERVICE_UNAVAILABLE` | 503 | IP geolocation service down |
| `VALIDATION_ERROR` | 400 | Invalid request body |
| `INTERNAL_SERVER_ERROR` | 500 | Unexpected error |

## Running with Docker (full stack)

Build and start the entire stack (app + PostgreSQL + Redis) with a single command:

```bash
docker compose -f docker/docker-compose.yml up --build -d
```

The application will be available at `http://localhost:8080`.

To stop:

```bash
docker compose -f docker/docker-compose.yml down
```

## Running Locally (development)

### 1. Start infrastructure only

```bash
docker compose -f docker/docker-compose.yml up -d postgres redis
```

### 2. Run the application

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 3. Swagger UI

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) after starting the application.

## Testing

### Run all tests

```bash
./gradlew test
```

### Coverage report

After running tests, the JaCoCo HTML report is available at:

```
build/reports/jacoco/test/html/index.html
```

### Test structure

- **Integration tests** (`CouponControllerIntegrationTest`) — full Spring Boot context with Testcontainers (PostgreSQL + Redis), testing all controller endpoints via MockMvc
- **Concurrency test** (`RedeemCouponConcurrencyTest`) — validates pessimistic locking under 50 concurrent threads
- **Unit tests** — cover all domain logic, services, factories, normalizers, validators, exception handler, and infrastructure (cache, external geo resolver)

## Configuration

Key properties (see `application.yaml`):

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Server port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/coupon_db` | Database URL |
| `REDIS_HOST` | localhost | Redis host |
| `app.geo.ip.base-url` | https://ipapi.co | Geo IP service URL |
| `app.cache.ip-country-ttl-seconds` | 3600 | Redis cache TTL for IP lookups |
