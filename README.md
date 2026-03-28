# Multi-Tenant Commerce Ingestion and Fulfillment Tracking API

A REST API for managing orders, fulfillments, and shipment tracking across multiple organizations. Built with Spring Boot and designed to handle multi-tenant isolation properly.

## What It Does

- Handle multiple organizations (tenants) in a single system
- Ingest orders from different eCommerce platforms  
- Track fulfillments and shipment updates
- Manage websites and order data with proper tenant scoping
- Validate all incoming data and return structured error responses

## Tech Stack

- Java 17, Spring Boot 3.2.5
- Spring Data JPA with Hibernate
- MySQL 8 (or H2 for local dev)
- JUnit 5, Mockito for testing
- Maven

## Quick Start

**Run locally (H2 database):**
```bash
./mvnw spring-boot:run
```

API will be at `http://localhost:9080/`

**Run tests:**
```bash
./mvnw test
```

**With MySQL + Docker:**
```bash
docker-compose up -d
./mvnw spring-boot:run --spring.profiles.active=mysql
```

**View test coverage:**
```bash
./mvnw test
open target/site/jacoco/index.html
```

## API Endpoints

35 endpoints across 5 resources:

| Resource | Path | Methods |
|---|---|---|
| Organizations | `/api/v1/organizations` | GET, POST, PUT, PATCH, DELETE + search |
| Websites | `/api/v1/websites` | GET, POST, PUT, PATCH, DELETE + search |
| Orders | `/api/v1/orders` | GET, POST, PUT, PATCH, DELETE + upsert, search by date |
| Fulfillments | `/api/v1/fulfillments` | GET, POST, PUT, PATCH, DELETE + search |
| Tracking | `/api/v1/tracking` | GET, POST, PUT, PATCH, DELETE + search |

All list endpoints support pagination and sorting.

## Architecture

Standard layered approach:
```
REST Controller → Service → Repository → JPA Entity
```

- Each tenant is isolated at the database level (tenant_id on every entity)
- Manual DTO mapping (no MapStruct)
- Global exception handler catches validation errors and database issues
- Validation happens at the DTO layer with Bean Validation annotations

## Testing

8 test classes covering:
- Controller endpoints via MockMvc
- Service logic with mocked repositories  
- Request validation constraints
- Exception handling scenarios
- Multi-tenant data isolation

58 tests total, good coverage for core functionality.

## Tech Stack
