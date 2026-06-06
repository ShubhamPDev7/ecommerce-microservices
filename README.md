<div align="center">

# 🛒 E-Commerce Microservices

**A course-driven microservices project built with Spring Boot & Spring Cloud — covering service discovery, reactive routing, OpenFeign communication, and Resilience4j fault tolerance.**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025.1.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-JPA-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit_Breaker-EF4D23?style=for-the-badge)](https://resilience4j.readme.io/)
[![Status](https://img.shields.io/badge/Status-WIP-yellow?style=for-the-badge)](#)

*Covers modules 12.1 – 12.6: Microservice Architecture · Eureka · API Gateway · OpenFeign · Circuit Breaker, Retry & Rate Limiter*

*Homework: MakeMyTrip Architecture Design · Cancel Order & Restock · ShippingService with Fault Tolerance*

</div>

---

## 📐 Architecture Overview

```
  Client
    │
    ▼
┌──────────────────────────────────────────────────────────────┐
│                   API Gateway  :8080                         │
│            (Spring Cloud Gateway / WebFlux)                  │
│                                                              │
│  /inventory/** ──► StripPrefix + X-Custom-Header             │
│  /orders/**    ──► StripPrefix                               │
│  /shipping/**  ──► StripPrefix                    [NEW]      │
└────────┬─────────────────────┬──────────────────────┬────────┘
         │  lb://              │  lb://               │  lb://
         ▼                     ▼                      ▼
┌────────────────┐   ┌──────────────────────┐  ┌─────────────────┐
│Inventory Svc   │   │    Order Service     │  │ Shipping Svc    │
│  :8081         │   │      :9020           │  │   :8083   [NEW] │
│                │◄──│                      │  │                 │
│  /products/**  │   │  @CircuitBreaker     │──► /shipments/**  │
│  reduceStocks  │   │  @Retry              │  │  initiateShip.. │
│  restockItems  │   │  @RateLimiter        │  │  getStatus      │
│    [NEW]       │   │                      │  └─────────────────┘
└───────┬────────┘   └──────────────────────┘
        │ Feign (both directions)
        └──────────────────────┘

         ┌──────────────────────────┐
         │   Discovery Server       │
         │   (Netflix Eureka) :8761 │
         │                          │
         │  inventory-service ──►   │
         │  order-service     ──►   │
         │  shipping-service  ──►   │  [NEW]
         │  api-gateway       ──►   │
         └──────────────────────────┘
```

---

## 🏗️ Services

| Service | Port | Description |
|---|---|---|
| 🌐 **API Gateway** | `8080` | Reactive entry point; routes `/inventory/**`, `/orders/**`, and `/shipping/**` with load balancing |
| 🔍 **Discovery Server** | `8761` | Netflix Eureka — all services self-register on startup |
| 📦 **Inventory Service** | `8081` | Manages product stock; exposes reduce-stocks and restock-items endpoints |
| 📋 **Order Service** | `9020` | Creates and cancels orders; calls Inventory and Shipping via Feign with Circuit Breaker protection |
| 🚚 **Shipping Service** | `8083` | **[NEW]** Initiates and tracks shipments; called by Order Service after order confirmation |

---

## ✨ Features

### 🔍 Service Discovery & Routing
- **Eureka Server** — services register dynamically; no hardcoded IPs anywhere
- **Reactive API Gateway** — routes with `StripPrefix` filters; injects `X-Custom-Header` on inventory routes
- **Client-Side Load Balancing** — `lb://` URIs resolved via Spring Cloud LoadBalancer

### 🔗 Inter-Service Communication
- **OpenFeign clients** — `InventoryOpenFeignClient` (Order → Inventory), `ShippingOpenFeignClient` (Order → Shipping) **[NEW]**, and `OrdersFeignClient` (Inventory → Order)
- **Stock reduction flow** — on order creation, Order Service calls `PUT /products/reduce-stocks` on Inventory Service via Feign; Inventory validates stock, deducts quantities, and returns the total price
- **Shipping initiation flow** — after order is saved, Order Service calls `POST /shipments/initiate` on Shipping Service via Feign **[NEW]**
- **Cancel & restock flow** — on order cancellation, Order Service calls `PUT /products/restock-items` on Inventory Service via Feign to add quantities back **[NEW]**

### ❌ Order Cancellation & Restocking **[NEW]**
- `DELETE /orders/core/{id}` cancels a confirmed order
- Validates order exists and is not already cancelled
- Manually maps `OrderItem` entities to DTOs (avoids ModelMapper infinite recursion on bidirectional `@ManyToOne` relationship)
- Calls Inventory Service via Feign to restock all items from the cancelled order
- Updates order status to `CANCELLED` and persists

### 🛡️ Fault Tolerance with Resilience4j

All three patterns are configured on the `createOrder` method (switchable via annotations):

| Pattern | Config |
|---|---|
| **Circuit Breaker** | Sliding window: 10 calls · Failure threshold: 50% · Open wait: 20s · Half-open calls: 3 |
| **Retry** | Max attempts: 3 · Wait between retries: 100ms |
| **Rate Limiter** | 100 requests/s · Timeout: 10ms |

- Fallback method `createOrderFallback()` returns an empty `OrderRequestDto` and logs the cause when the circuit opens
- Circuit Breaker now also protects the **Shipping Service call** inside `createOrder` — if Shipping is down, the fallback triggers **[NEW]**

### 🗄️ Data Model

**Inventory Service** — `products` table:

```
Product { id, title, price, stock }
```

**Order Service** — `orders` + `order_items` tables:

```
Orders { id, orderStatus (CONFIRMED/CANCELLED/PENDING), totalPrice, shippingAddress }
  └── OrderItem { id, productId, quantity, order_id }
```

**Shipping Service** — `shipments` table **[NEW]**:

```
Shipment { id, orderId, shippingAddress, status (INITIATED/IN_TRANSIT/DELIVERED/FAILED) }
```

---

## 📡 API Reference

### 📦 Inventory Service — via Gateway at `/inventory`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/products` | List all products with stock |
| `GET` | `/products/{id}` | Get product by ID |
| `PUT` | `/products/reduce-stocks` | Deduct stock for an order (called by Order Service) |
| `PUT` | `/products/restock-items` | Add stock back on cancellation (called by Order Service) **[NEW]** |
| `GET` | `/products/fetchOrders` | Test endpoint — calls Order Service via Feign |

### 📋 Order Service — via Gateway at `/orders`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/core` | List all orders |
| `GET` | `/core/{id}` | Get order by ID |
| `POST` | `/core/create-order` | Create order (triggers stock reduction + shipping initiation + Circuit Breaker) |
| `DELETE` | `/core/{id}` | Cancel order and restock items **[NEW]** |
| `GET` | `/core/helloOrders` | Health check / Feign test endpoint |

### 🚚 Shipping Service — via Gateway at `/shipping` **[NEW]**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/shipments/initiate` | Create a new shipment for a confirmed order |
| `GET` | `/shipments/{id}` | Get current shipment status |

### Request body for `POST /orders/core/create-order`

```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ],
  "shippingAddress": "Pune, Maharashtra, India"
}
```

### Response for `DELETE /orders/core/{id}`

```json
{
  "id": 1,
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ],
  "totalPrice": 1899.97,
  "orderStatus": "CANCELLED"
}
```

---

## 💻 Tech Stack

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Gateway | Spring Cloud Gateway (Reactive / WebFlux) |
| Service Discovery | Spring Cloud Netflix Eureka (`2025.1.1`) |
| Inter-Service Comms | OpenFeign + Spring Cloud LoadBalancer |
| Fault Tolerance | Resilience4j — Circuit Breaker, Retry, Rate Limiter |
| Database | PostgreSQL — Spring Data JPA / Hibernate |
| Object Mapping | ModelMapper 3.2.0 |
| Observability | Spring Boot Actuator (health, circuit breaker indicators) |
| Build | Apache Maven |
| Utilities | Lombok |

---

## 📁 Project Structure

```
eCommerce/
├── api-gateway/                              ← Port 8080 | Spring Cloud Gateway
│   └── src/main/resources/
│       └── application.yml                  ← Route definitions (incl. /shipping/**)
│
├── discovery-service/                        ← Port 8761 | Netflix Eureka Server
│
├── inventory-service/                        ← Port 8081 | Product & stock management
│   └── src/main/java/.../inventory_service/
│       ├── clients/OrdersFeignClient         ← Feign client → Order Service
│       ├── controller/ProductController      ← includes PUT /restock-items [NEW]
│       ├── service/ProductService            ← reduceStocks() + restockItems() [NEW]
│       ├── entity/Product
│       └── dto/
│
├── order-service/                            ← Port 9020 | Order creation & management
│   └── src/main/java/.../order_service/
│       ├── clients/
│       │   ├── InventoryOpenFeignClient      ← reduceStocks() + restockItems() [NEW]
│       │   └── ShippingOpenFeignClient       ← initiateShipping() [NEW]
│       ├── controller/OrdersController       ← includes DELETE /{id} [NEW]
│       ├── service/OrdersService             ← cancelOrder() [NEW] + shipping call in createOrder()
│       ├── entity/{Orders, OrderItem, OrderStatus}
│       └── dto/
│           ├── OrderRequestDto               ← added shippingAddress field [NEW]
│           ├── OrderRequestItemDto
│           └── ShipmentRequestDto            ← [NEW]
│
└── shipping-service/                         ← Port 8083 | Shipment tracking [NEW]
    └── src/main/java/.../shipping_service/
        ├── controller/ShippingController     ← POST /initiate, GET /{id}
        ├── service/ShippingService           ← initiateShipping(), getShipmentStatus()
        ├── entity/{Shipment, ShipmentStatus}
        ├── repository/ShipmentRepository
        └── dto/ShipmentRequestDto
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL

### Database Setup

Create databases in PostgreSQL:

```sql
CREATE DATABASE inventoryDB;
CREATE DATABASE orderDB;
CREATE DATABASE shippingDB;
```

### Configuration

Each service has an `application-example-properties` file. Copy it to `application.properties` and fill in your credentials:

```properties
# inventory-service
spring.datasource.url=jdbc:postgresql://localhost:5432/inventoryDB
spring.datasource.username=your_username
spring.datasource.password=your_password

# order-service
spring.datasource.url=jdbc:postgresql://localhost:5432/orderDB
spring.datasource.username=your_username
spring.datasource.password=your_password

# shipping-service
spring.datasource.url=jdbc:postgresql://localhost:5432/shippingDB
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Boot Order

Always start Eureka first — every other service registers with it on startup.

```bash
# 1. Eureka must be up first
cd discovery-service && ./mvnw spring-boot:run

# 2. API Gateway
cd api-gateway && ./mvnw spring-boot:run

# 3. Application services (any order)
cd inventory-service && ./mvnw spring-boot:run
cd order-service && ./mvnw spring-boot:run
cd shipping-service && ./mvnw spring-boot:run
```

Verify all services registered at: **`http://localhost:8761`**

### Quick Test Flow

```bash
# 1. Place an order (reduces stock + creates shipment)
POST http://localhost:8080/orders/core/create-order

# 2. Check stock was reduced
GET http://localhost:8080/inventory/products/1

# 3. Check shipment was created
GET http://localhost:8080/shipping/shipments/1

# 4. Cancel the order (restocks inventory)
DELETE http://localhost:8080/orders/core/1

# 5. Verify stock is back
GET http://localhost:8080/inventory/products/1
```

### Observability

Circuit breaker health is exposed via Actuator:

```
GET http://localhost:9020/actuator/health
```

---

## 🗺️ Course Modules Covered

| Module | Topic | Status |
|---|---|---|
| 12.1 | Introduction to Microservice Architecture | ✅ |
| 12.2 | Setting up the Inventory Management System | ✅ |
| 12.3 | Service Registration & Discovery with Eureka | ✅ |
| 12.4 | Spring Cloud API Gateway | ✅ |
| 12.5 | OpenFeign Microservice Communication | ✅ |
| 12.6 | Circuit Breaker, Retry & Rate Limiter with Resilience4j | ✅ |
| HW | MakeMyTrip Architecture Breakdown & Flow Design | ✅ |
| HW | Cancel Order + Inventory Restocking | ✅ |
| HW | ShippingService + Circuit Breaker Fallback + Retry | ✅ |

---

<div align="center">

Built by [ShubhamPDev7](https://github.com/ShubhamPDev7) as part of a backend engineering deep-dive into Java, Spring Boot, and Microservices Architecture.

</div>