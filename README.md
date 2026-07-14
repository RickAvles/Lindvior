<p align="center">
  <img src="docs/branding/banner.png" alt="Lindvior Banner" width="100%">
</p>

<h1 align="center">Lindvior</h1>

<p align="center">
  Intelligent Smart Parking Simulation Platform built with Java & Spring Boot.
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge)
![Mockito](https://img.shields.io/badge/Mockito-Test-78A641?style=for-the-badge)

</p>

---

# Overview

Lindvior is an intelligent Smart Parking Simulation Platform designed to model the behavior of a real-world parking
facility.

Instead of focusing only on CRUD operations, the project simulates vehicle arrivals, parking allocation, occupancy,
session lifecycle, and business rules while exposing a modern REST API for administration and monitoring.

The project is being developed as a production-oriented backend application, emphasizing clean architecture,
maintainability, scalability, automated testing, and modern software engineering practices.

---

# Architecture

```
                   Client
                      │
                      ▼
              REST Controllers
                      │
                      ▼
                Service Layer
                      │
      ┌───────────────┼────────────────┐
      ▼               ▼                ▼
 Business Rules  Specifications   Repositories
                      │
                      ▼
                 PostgreSQL
```

---

# Domain Model

```
Parking
   │
   ├── ParkingSector
   │       │
   │       └── ParkingSpot
   │
Vehicle
   │
   └── ParkingSession
           │
           └── ParkingSpot
```

---

# Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Gradle
- JUnit 5
- Mockito

---

# Features

## Authentication

- JWT Authentication
- BCrypt Password Encryption
- Role-based Authorization

## Parking

- Single Parking Management
- Parking Configuration
- Capacity Management

## Parking Sectors

- Sector Management
- Multiple Floor Support
- Sector Types
- Activation / Deactivation

## Parking Spots

- Parking Spot Management
- Availability Control
- Occupancy Metrics
- Dynamic Filtering

## Vehicles

- Vehicle Registry
- Vehicle Types
- Brazilian License Plate Validation
- Activation / Deactivation

## Parking Sessions

- Vehicle Check-in
- Vehicle Check-out
- Session Lifecycle
- Duplicate Session Prevention
- Parking Spot Allocation
- Dynamic Filtering

---

# Project Structure

```
src
├── config
├── controller
├── dto
│   ├── filter
│   ├── request
│   └── response
├── entity
├── enums
├── exception
├── repository
├── security
├── service
├── specification
└── util

docs
├── Product Vision
├── Functional Requirements
├── Business Rules
├── Domain Model
├── Data Model
├── REST API Specification
├── Application Architecture
└── Technologies & Infrastructure
```

---

# Testing

The project includes comprehensive unit tests covering the business layer.

Covered services:

- ParkingService
- ParkingSectorService
- ParkingSpotService
- ParkingSessionService
- VehicleService

Frameworks:

- JUnit 5
- Mockito

---

# Roadmap

## Completed

- Domain Modeling
- REST API
- Authentication
- Administrative Module
- Dynamic Filtering
- Specifications
- Business Rule Validation
- Unit Tests

## In Progress

- Simulation Engine

## Planned

- Vehicle Generator
- Parking Simulation
- Real-Time Scheduler
- Dashboard
- Redis
- Docker
- RabbitMQ
- Kafka
- Observability
- Prometheus
- Grafana
- CI/CD

---

# Getting Started

Clone the repository

```bash
git clone https://github.com/RickAvles/smart-parking-platform.git
```

Run the application

```bash
./gradlew bootRun
```

Run unit tests

```bash
./gradlew test
```

---

# Documentation

The project is fully documented before implementation.

Available documentation includes:

- Product Vision
- Functional Requirements
- Business Rules
- Domain Model
- Data Model
- REST API Specification
- Application Architecture
- Technologies & Infrastructure

---

# Future Vision

Lindvior is evolving into a fully autonomous parking simulation platform capable of reproducing realistic parking
behavior through configurable business rules, scheduled events, vehicle generation, occupancy metrics, and operational
monitoring.

The long-term goal is to demonstrate modern backend architecture using distributed systems, messaging, caching,
observability, and cloud-native deployment practices.

---

# License

This project is available under the MIT License.