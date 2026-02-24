# Ultimate-Car-Rental-Backend-Spring-Boot

Enterprise-ready **Spring Boot** backend for a **multi-role vehicle rental platform** with secure authentication, payment processing, and analytics.

---

## 🚗 Project Overview

This backend supports a **Vehicle Rental Management System** with multiple roles:

* **Admin** – Manages vehicles, users, driver payouts, and analytics.
* **Local Head** – Manages vehicles and rentals per location.
* **Driver** – Handles trips, earnings, and payout history.
* **Customer** – Registers, books rentals, and makes payments.

The system is built with **clean architecture principles** and is fully scalable.

---

## 🚀 Features

* JWT-based authentication & refresh token support
* Role-based access control (Admin, Local Head, Driver, Customer)
* Vehicle management with status updates and maintenance tracking
* Rental lifecycle: create, assign driver, start, complete, cancel
* Payment processing & refund support
* Driver payout management
* Revenue reports, monthly analytics, and dashboard KPIs
* Pagination, search, and filtering support

---

## 🧱 Tech Stack

* **Java 17+**
* **Spring Boot**
* **Spring Security** (JWT Authentication)
* **Spring Data JPA** (Hibernate)
* **MySQL / PostgreSQL**
* **Maven**
* **OpenAPI / Swagger** for API documentation

---

## 📂 Project Structure

```
controller  → Handles API endpoints
service     → Business logic
repository  → Database interactions
entity      → JPA entity models
dto         → Data transfer objects
security    → JWT & role-based security
exception   → Custom exception handling
config      → Spring Boot configuration
```

Follows **Layered Architecture**: Controller → Service → Repository → Database.

---

## 🔐 Authentication Endpoints

```http
POST /api/customer/auth/register
POST /api/customer/auth/login
POST /api/customer/auth/refresh-token
```

* Supports JWT tokens and refresh mechanism
* Role-based authorization for secure access

---

## 📦 API Modules

```
/api/admin/*
/api/local-head/*
/api/driver/*
/api/customer/*
```

Each module is protected by **role-based access control**.

---

## ⚙️ Installation & Setup

### 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/Ultimate-Car-Rental-Backend-Spring-Boot.git
cd Ultimate-Car-Rental-Backend-Spring-Boot
```

### 2️⃣ Configure Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_rental
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 3️⃣ Run the application

```bash
mvn clean install
mvn spring-boot:run
```

Server will run at:

```
http://localhost:8080
```

---

## 📖 API Documentation

Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## 📊 Key Metrics & Dashboard

* Total Users / Drivers / Customers
* Total Vehicles & Available Vehicles
* Active / Completed Rentals
* Revenue & Platform Commission
* Pending Payouts
* Maintenance Alerts

---

## 🔒 Security

* JWT Authentication
* Role-Based Authorization
* Password Encryption
* Refresh Token Mechanism
* Secure API Endpoints

---
