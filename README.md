<p align="center">
  <img src="docs/branding/banner.png" alt="Lindvior Banner" width="100%">
</p>

<h1 align="center">Lindvior</h1>

<p align="center">
  Modern Smart Parking Platform built with Java & Spring Boot.
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge)
![Mockito](https://img.shields.io/badge/Mockito-Test-78A641?style=for-the-badge)

</p>

---

# Overview

Lindvior is a modern Smart Parking Platform focused on scalability, maintainability, and cloud-native backend practices.

The project simulates a real-world parking management system capable of handling parking availability, vehicle sessions,
authentication, dynamic filtering, and business rules while serving as a production-oriented backend study project.

---

# Architecture

```

Client
│
▼
REST API (Spring Boot)
│
├── Authentication (JWT)
├── Business Rules
├── Validation
├── Services
├── Specifications
└── Persistence
│
▼

PostgreSQL

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

- Parking CRUD
- Pagination
- Dynamic Filtering

## Parking Spots

- Parking Spot Management
- Availability Control
- Occupancy Statistics

## Parking Sessions

- Open / Close Sessions
- Duplicate Session Prevention
- Brazilian License Plate Validation
- Entry Time Filtering
- Parking Spot Filtering

---

# Project Structure

```

src
├── controller
├── dto
│ ├── request
│ └── response
├── entity
├── enums
├── exception
├── repository
├── security
├── service
├── specification
└── config

```

---

# Testing

The project includes unit tests covering the service layer.

Covered components:

- ParkingService
- ParkingSpotService
- ParkingSessionService
- UserService
- AuthService
- JwtService

Frameworks:

- JUnit 5
- Mockito

---

# Roadmap

## Completed

- Parking CRUD
- Parking Spot Management
- Authentication
- JWT
- Dynamic Filtering
- Specifications
- Pagination
- Unit Tests

## In Progress

- Redis Integration
- Observability

## Planned

- Docker
- RabbitMQ
- Kafka
- CI/CD
- Kubernetes
- Prometheus
- Grafana

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

Run tests

```bash
./gradlew test
```

---

# Future Vision

Lindvior is designed to evolve beyond a simple CRUD application into a production-oriented Smart Parking Platform
featuring event-driven architecture, distributed systems, observability, caching, messaging, and cloud-native deployment
strategies.

---

# License

This project is available under the MIT License.