<p align="center">
  <img src="docs/branding/banner.png" alt="Lindvior Banner" width="100%">
</p>

<h1 align="center">Lindvior</h1>

<p align="center">
  Smart Parking Simulation Platform built with Java & Spring Boot.
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-7.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge)
![Mockito](https://img.shields.io/badge/Mockito-Test-78A641?style=for-the-badge)

</p>

---

# Overview

Lindvior is a backend platform for managing and simulating the operation of a smart parking facility.

The project combines a REST API for parking management with a real-time simulation engine capable of reproducing vehicle
arrivals, parking allocation, occupancy, parking sessions, stays, exits, queues, and operational metrics.

It was developed as a portfolio project with a focus on practical backend engineering, software architecture,
persistence, security, concurrency, caching, messaging, asynchronous processing, reporting, and automated testing.

---

# Architecture

Lindvior is structured as a modular monolith, separating the administrative domain from the simulation domain.

<pre>
                    Client
                      │
                      ▼
                REST Controllers
                      │
                      ▼
                 Application
                      │
          ┌───────────┴───────────┐
          │                       │
          ▼                       ▼
   Administrative Domain    Simulation Domain
          │                       │
          │                 Simulation Engine
          │                       │
          │              ┌────────┼────────┐
          │              ▼        ▼        ▼
          │           Vehicles  Parking  Metrics
          │              │        │        │
          └──────────────┴────────┴────────┘
                         │
                         ▼
                    PostgreSQL
</pre>

RabbitMQ is used for asynchronous daily report processing, while Redis is used for caching.

---

# Domain Model

A parking facility is divided into sectors, and each sector contains parking spots.

<pre>
Parking
   │
   └── ParkingSector
          │
          └── ParkingSpot

Vehicle
   │
   └── ParkingSession
          │
          └── ParkingSpot
</pre>

Parking spots have their own types and statuses.

The simulation supports:

- Regular spots
- PCD spots
- Electric spots
- Motorcycle spots
- Premium sectors
- Free, reserved, and occupied states

Vehicle compatibility is evaluated at the parking spot level.

---

# Features

## Authentication & Security

- JWT authentication
- BCrypt password encoding
- Stateless authentication
- Role-based authorization
- Global exception handling
- Request validation

## Parking Management

- Parking management
- Parking sector management
- Parking spot management
- Vehicle management
- Parking session management
- Dynamic filtering using specifications
- Parking occupancy metrics

## Simulation Engine

The simulation engine operates continuously in real time and reproduces the operational behavior of the parking
facility.

It includes:

- Simulation clock
- Tick processing
- Operating-hours control
- Vehicle generation
- Vehicle selection
- Entry flow
- Entry queues
- Entry gates
- Parking allocation
- Parking sessions
- Stay profiles
- Exit decisions
- Exit flow
- Exit queues
- Exit gates
- Recovery after application restart
- Simulation logging
- Occupancy and operational metrics

## Vehicle Simulation

Generated vehicles can receive:

- License plate
- Vehicle type
- Color
- Stay profile
- PCD priority flag

The simulation supports different stay profiles:

- Short
- Normal
- Long
- Very Long
- Recovery

Vehicle selection can either generate a new vehicle or reuse an existing vehicle that does not currently have an open
parking session.

---

# Parking Allocation

Parking allocation is based on parking spot type and vehicle characteristics.

PCD vehicles prefer PCD spots but can use regular spots when no PCD spot is available.

Electric vehicles prefer electric spots but can use regular spots when necessary.

Regular vehicles cannot use electric spots.

Motorcycles use motorcycle spots and can fall back to regular spots when motorcycle spots are unavailable.

Eligible parking spots are selected randomly.

Larger sectors naturally receive more vehicles because they contain more eligible parking spots.

---

# Daily Reporting

Lindvior includes an asynchronous daily reporting system using RabbitMQ.

<pre>
DailyReportScheduler
        │
        ▼
DailyReportProducer
        │
        ▼
     RabbitMQ
        │
        ▼
DailyReportConsumer
        │
        ▼
   ReportService
        │
        ▼
DailyReportPdfGenerator
        │
        ▼
       PDF
</pre>

The daily report is reconstructed from historical data stored in PostgreSQL.

It includes information such as:

- Total entries
- Completed sessions
- Average stay
- Shortest stay
- Longest stay
- Average occupancy
- Maximum occupancy
- Minimum occupancy
- Peak occupancy time
- Sector performance

---

# Technology Stack

## Backend

- Java 21
- Spring Boot 4.x
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- Jakarta Validation
- Gradle

## Database

- PostgreSQL 17

## Cache

- Redis

## Messaging

- RabbitMQ
- Spring AMQP

## Reporting

- Apache PDFBox

## Testing

- JUnit 5
- Mockito

## Infrastructure

- Docker / Docker Compose — in progress
- Apache Kafka — in progress

---

# Testing

The project uses automated tests to validate business rules and core simulation behavior.

Current tests cover areas such as:

- Parking services
- Parking spot allocation
- Parking session behavior
- Vehicle services
- Vehicle attribute generation
- Vehicle generation
- Vehicle selection
- Generated vehicle factory
- Simulation business rules

Examples of tested rules include:

<pre>
PCD Vehicle
    → PCD Spot Preferred
    → Regular Fallback

Electric Vehicle
    → Electric Spot Preferred
    → Regular Fallback

Regular Vehicle
    → Regular Spot
    → Never Electric Spot
</pre>

Run the complete test suite with:

    ./gradlew clean test

---

# Project Structure

<pre>
src/main/java/com/rick/smartparkingplatform
│
├── config
│   ├── rabbitmq
│   └── ...
│
├── controller
├── dto
│   ├── filter
│   ├── messaging
│   ├── request
│   └── response
│
├── entity
├── enums
├── exception
├── mapper
├── repository
├── report
├── security
├── service
├── simulation
│   ├── engine
│   ├── logger
│   ├── metrics
│   ├── parking
│   ├── queue
│   ├── recovery
│   └── vehicle
│
└── specification
</pre>

---

# Documentation

The project includes technical documentation covering:

- Product Vision
- Functional Requirements
- Business Rules
- Domain Model
- Data Model
- REST API Specification
- Application Architecture
- Technologies and Infrastructure
- Simulation Engine Specification
- Simulation Engine Refactoring Plan

The documentation was created alongside the implementation to keep the requirements, business rules, domain model,
architecture, data model, API, and simulation engine aligned.

---

# Roadmap

The core backend and simulation engine are complete.

The remaining work is focused on infrastructure and the presentation layer.

### Completed

- [x] Product and domain modeling
- [x] PostgreSQL persistence
- [x] REST API
- [x] Authentication and authorization
- [x] Parking management
- [x] Sector management
- [x] Parking spot management
- [x] Vehicle management
- [x] Parking sessions
- [x] Dynamic filtering and specifications
- [x] Business-rule validation
- [x] Simulation engine
- [x] Vehicle generation
- [x] Parking allocation
- [x] Entry and exit flows
- [x] Stay profiles
- [x] Simulation recovery
- [x] Occupancy and operational metrics
- [x] Redis
- [x] RabbitMQ
- [x] Daily PDF reports
- [x] Automated tests

### In Progress

- [ ] Docker
- [ ] Apache Kafka
- [ ] Frontend

---

# Running the Project

## Prerequisites

- Java 21
- PostgreSQL
- Redis
- RabbitMQ

## Clone the Repository

    git clone https://github.com/RickAvles/smart-parking-platform.git
    cd smart-parking-platform

## Run the Application

    ./gradlew bootRun

## Run the Tests

    ./gradlew clean test

---

# Project Goal

Lindvior was developed as a portfolio project focused on practical backend engineering and the integration of modern
application technologies.

The project demonstrates experience with:

- Java and Spring Boot
- REST API development
- Domain modeling
- PostgreSQL and JPA/Hibernate
- Spring Security and JWT
- Automated testing
- Real-time simulation
- Redis caching
- RabbitMQ messaging
- PDF report generation
- Application architecture

The remaining infrastructure work will add Docker and Apache Kafka, followed by the frontend layer.

---

# License

This project is available under the MIT License.
