<div align="center">

# 🛒 E-Commerce Microservices

**A production-inspired microservices architecture built with Spring Boot & Spring Cloud**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Status](https://img.shields.io/badge/Status-WIP-yellow?style=for-the-badge)](#)

*A hands-on learning project exploring service decomposition, dynamic discovery, reactive routing, and synchronous inter-service communication.*

</div>

---

## 📐 Architecture Overview

```
                        ┌─────────────────────────────────────┐
                        │         Discovery Service           │
                        │      (Netflix Eureka)  :8761        │
                        │                                     │
                        │  inventory-service ──► registered   │
                        │  order-service     ──► registered   │
                        └────────────────┬────────────────────┘
                                         │ service registry
                     ┌───────────────────┼───────────────────┐
                     │                   │                   │
              ┌──────▼──────┐            │           ┌───────▼──────┐
              │ API Gateway │            │           │ API Gateway  │
              │  :8080      │◄───────────┘           │ (resolves    │
              │ (WebFlux)   │   lookup & route        │  via LB)     │
              └──────┬──────┘                        └──────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
  ┌──────▼──────┐         ┌──────▼──────┐
  │  Inventory  │◄────────│    Order    │
  │  Service    │ Feign   │  Service    │
  │  :8081      │ (stock  │  :8082      │
  └─────────────┘  check) └─────────────┘
         │                       │
    ┌────▼────┐             ┌────▼────┐
    │   DB    │             │   DB    │
    │ (PG)    │             │ (PG)    │
    └─────────┘             └─────────┘
```

---

## 🏗️ Services

| Service | Port | Description |
|---|---|---|
| 🌐 **API Gateway** | `8080` | Single external entry point; handles dynamic routing & load balancing via Eureka |
| 🔍 **Discovery Service** | `8761` | Netflix Eureka Server — central registry for all microservices |
| 📦 **Inventory Service** | `8081` | REST API for managing product stock levels |
| 📋 **Order Service** | `8082` | REST API for order creation and status tracking |

---

## 💻 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.x |
| Gateway | Spring Cloud Gateway (Reactive / WebFlux) |
| Service Discovery | Spring Cloud Netflix Eureka |
| Inter-Service Comms | OpenFeign + Spring Cloud LoadBalancer |
| Database | PostgreSQL + Spring Data JPA |
| Build | Apache Maven |
| Utilities | Lombok, ModelMapper |

---

## ✨ Features

### 🔍 Service Discovery & Routing
- **Eureka Server** — microservices self-register on startup; no hardcoded addresses
- **Reactive API Gateway** — routes requests to internal services with path-prefix stripping
- **Client-Side Load Balancing** — Spring Cloud LoadBalancer resolves instances seamlessly

### 🔗 Inter-Service Communication
- **OpenFeign Clients** — declarative REST clients for clean, readable service-to-service calls
- **Transactional Stock Management** — Order Service verifies and deducts inventory in real-time via Feign during order creation

### 🗄️ Data & Configuration
- **Independent Databases** — each service owns its own PostgreSQL schema (database-per-service pattern)
- **Secure Configuration** — credentials excluded from version control via `.gitignore`; `.example` template files provided for local setup

---

## 📁 Project Structure

```
eCommerce/
├── api-gateway/              ← Port 8080 | Spring Cloud Gateway (WebFlux)
│   └── src/
├── discovery-service/        ← Port 8761 | Netflix Eureka Server
│   └── src/
├── inventory-service/        ← Port 8081 | Product stock management
│   └── src/
└── order-service/            ← Port 8082 | Order lifecycle management
    └── src/
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL (running locally or via Docker)

### Boot Order

Services must start in this order:

```bash
# 1. Start the Eureka Discovery Service first
cd discovery-service && mvn spring-boot:run

# 2. Start backend services (order doesn't matter between these two)
cd inventory-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run

# 3. Start the API Gateway last
cd api-gateway && mvn spring-boot:run
```

### Configuration

Copy the example files and fill in your credentials:

```bash
cp inventory-service/src/main/resources/application.example.yml \
   inventory-service/src/main/resources/application.yml

cp order-service/src/main/resources/application.example.yml \
   order-service/src/main/resources/application.yml
```

### Verify

Once all services are up, visit the Eureka dashboard to confirm registrations:

```
http://localhost:8761
```

---

## 🗺️ Roadmap

The following features are planned as part of this learning journey:

- [ ] **Kafka** — asynchronous messaging and event-driven architecture
- [ ] **Resilience4j** — circuit breakers and fault tolerance
- [ ] **Redis** — distributed caching
- [ ] **Micrometer + Zipkin** — distributed tracing
- [ ] **Docker** — containerization and multi-service `docker-compose`
- [ ] **Kubernetes** — orchestration and deployment
- [ ] **Spring Cloud Config** — centralized configuration management

---

## 🧭 Learning Goals

This project is part of an ongoing backend engineering journey, exploring:

> *Service decomposition · Dynamic service discovery · Reactive API routing · Synchronous inter-service communication · Independent data ownership · Cloud-native patterns*

---

<div align="center">

**🚧 Work in Progress** — built as a backend engineering deep-dive into Java, Spring Boot, and modern Microservices Architecture.

</div>