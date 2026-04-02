# Coupon Service

REST API for managing discount coupons with concurrency-safe redemption

## Features

- create coupon
- redeem coupon
- case-insensitive coupon code uniqueness
- max usage limit enforcement
- one-time usage per user
- country restriction based on IP
- Redis cache for IP geolocation
- Liquibase database migrations
- Dockerized infrastructure
- concurrency integration test

## Tech Stack

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Redis
- Liquibase
- Docker
- Testcontainers

## Architecture

Project is split into clear layers:

- `api` – controllers, API requests and responses, exception handling
- `application` – commands, results, orchestration services
- `domain` – validators, normalizers, factories
- `persistence` – JPA entities and repositories
- `infrastructure` – external integrations and technical configuration

## Running locally

### 1. Start infrastructure

```bash
docker compose up -d
./gradlew bootRun --args='--spring.profiles.active=local'