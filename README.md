# Coupon Service

REST API for managing discount coupons.

## Features

- Create coupon
- Redeem coupon
- Country validation based on IP
- Max usage limit
- One-time usage per user
- Concurrency-safe redemption

## Tech Stack

- Java 21
- Spring Boot 3
- PostgreSQL
- Redis
- Liquibase
- Docker

## Running locally

```bash
docker compose up -d
./gradlew bootRun
```