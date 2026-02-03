# Home Solutions - Secure REST API

A production-ready, secure REST API for an on-demand home services platform built with Java 17 and Spring Boot 3.x.

## 🧰 Tech Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** with JWT authentication
- **Spring Data JPA / Hibernate**
- **PostgreSQL** (production)
- **H2** (testing)
- **springdoc-openapi** (Swagger UI)
- **JUnit 5 + MockMvc** (testing)
- **Lombok**
- **Maven**

## 🏗️ Architecture

Follows a clean layered architecture:

```
Controller → Service Interface → Service Implementation → Repository
```

All features include:
- Controller with REST endpoints
- Service Interface
- Service Implementation
- Repository
- DTOs for API boundary
- Entity models
- Mappers for DTO/Entity conversion

## 📂 Project Structure

```
com.homesolutions
 ├── config              # Security, Swagger configuration
 ├── controller          # REST controllers
 ├── dto                 # Data Transfer Objects
 ├── entity              # JPA entities
 ├── exception           # Custom exceptions & global handler
 ├── mapper              # Entity/DTO mappers
 ├── repository          # JPA repositories
 ├── security            # JWT, filters, user details service
 ├── service
 │    ├── interfaces    # Service interfaces
 │    └── impl          # Service implementations
 ├── util               # Utilities, schedulers
 └── HomesolutionsApplication
```

## 🔐 Security Features

- **JWT Authentication** - Stateless token-based authentication with email + password
- **BCrypt Password Hashing** - Secure password storage
- **Role-Based Authorization** - Three roles: `ROLE_CUSTOMER`, `ROLE_EXPERT`, `ROLE_ADMIN`
- **Method-level Security** - `@PreAuthorize` on secured endpoints
- **Proper HTTP Status Codes** - 401 Unauthorized, 403 Forbidden
- **DEBUG Logging** - Comprehensive debug-level logging for troubleshooting
- **Separate Admin Table** - Admins are stored in a dedicated `admins` table, while customers and experts are stored in the `users` table

## 📊 Database Setup

### Database Schema

The application uses two main tables for authentication:

1. **`users` table** - Stores customers and experts
   - `id` (Primary Key)
   - `email` (Unique, NOT NULL)
   - `full_name` (NOT NULL)
   - `password` (NOT NULL)
   - `phone` (Nullable - optional field)
   - `enabled` (Boolean)
   - `created_at`, `updated_at`
   - Associated `user_roles` table for role management

2. **`admins` table** - Stores admin users separately
   - `id` (Primary Key)
   - `email` (Unique, NOT NULL)
   - `full_name` (NOT NULL)
   - `password` (NOT NULL)
   - `enabled` (Boolean)
   - `created_at`, `updated_at`

This separation ensures clean data management and prevents schema conflicts between different user types.

### PostgreSQL Setup

1. Install PostgreSQL
2. Create database:
```sql
CREATE DATABASE homesolutions;
```

3. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/homesolutions
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Default Data

The application seeds initial data on startup:
- 8 service categories
- 32 services across all categories
- Default admin user:
  - Email: `admin@homesolutions.com`
  - Password: `admin123`
  - Role: `ROLE_ADMIN`
  - **Note**: Admin users are stored in the `admins` table

## 🚀 Run Instructions

### 1. Clone the repository
```bash
git clone https://github.com/dhruvchauhan29/Home-Solutions.git
cd Home-Solutions
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

## 🔄 JWT Authentication Flow

Authentication uses **email + password** (phone is optional).

### Authentication Architecture

- **Customers & Experts**: Authenticate using the `users` table via `/auth/register` and `/auth/login`
- **Admins**: Authenticate using the `admins` table via `/auth/admin/register` and `/auth/admin/login`
- **JWT Token**: Both user types receive the same JWT token format containing email and roles
- **UserDetailsService**: Automatically checks both `admins` and `users` tables during authentication

### 1. Register a User (Customer or Expert)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "fullName": "John Doe",
    "password": "password123",
    "phone": "1234567890",
    "role": "CUSTOMER"
  }'
```

**Note**: The `phone` field is optional and can be omitted.

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "customer@example.com",
  "fullName": "John Doe",
  "roles": ["ROLE_CUSTOMER"]
}
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "password": "password123"
  }'
```

### 3. Admin Registration
**Note**: Admin users are stored in a separate `admins` table, not in the `users` table.

```bash
curl -X POST http://localhost:8080/auth/admin/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "fullName": "Admin User",
    "password": "admin123"
  }'
```

### 4. Admin Login
```bash
curl -X POST http://localhost:8080/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

### 5. Use Token for Authenticated Requests
```bash
curl -X GET http://localhost:8080/customer/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 📝 Sample API Requests

### Health Check
```bash
curl http://localhost:8080/health
```

### Browse Services
```bash
# Get all services with pagination
curl "http://localhost:8080/services?page=0&size=10"

# Search services
curl "http://localhost:8080/services?search=cleaning&categoryId=1"

# Get specific service
curl http://localhost:8080/services/1
```

### Get Price Quote
```bash
curl "http://localhost:8080/pricing/quote?serviceId=1&durationMinutes=120"
```

Response:
```json
{
  "basePrice": 500.00,
  "extraCharge": 90.00,
  "discount": 0.00,
  "totalPrice": 590.00,
  "details": "Base price: ₹500.00 + Extra charge for 1 hour(s): ₹90.00"
}
```

### Customer - Create Address
```bash
curl -X POST http://localhost:8080/customer/addresses \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zipCode": "400001",
    "landmark": "Near City Mall",
    "isDefault": true
  }'
```

### Customer - Create Booking
```bash
curl -X POST http://localhost:8080/customer/bookings \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": 1,
    "addressId": 1,
    "scheduledAt": "2026-02-10T10:00:00",
    "durationMinutes": 120,
    "couponCode": "NEW50",
    "notes": "Please bring cleaning supplies"
  }'
```

### Customer - Make Payment
```bash
curl -X POST http://localhost:8080/customer/payments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "method": "CARD"
  }'
```

### Customer - Confirm Payment
```bash
curl -X POST http://localhost:8080/customer/payments/1/confirm \
  -H "Authorization: Bearer <token>"
```

### Customer - Add Rating
```bash
curl -X POST http://localhost:8080/customer/bookings/1/rating \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "rating": 5,
    "comment": "Excellent service!"
  }'
```

### Admin - Create Category
```bash
curl -X POST http://localhost:8080/admin/categories \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gardening",
    "description": "Garden maintenance and landscaping"
  }'
```

### Admin - Approve Expert
```bash
curl -X POST http://localhost:8080/admin/experts/2/approve \
  -H "Authorization: Bearer <admin_token>"
```

### Expert - Accept Job
```bash
curl -X POST http://localhost:8080/expert/jobs/1/accept \
  -H "Authorization: Bearer <expert_token>"
```

### Expert - Start Job
```bash
curl -X POST http://localhost:8080/expert/jobs/1/start \
  -H "Authorization: Bearer <expert_token>"
```

### Expert - Complete Job
```bash
curl -X POST http://localhost:8080/expert/jobs/1/complete \
  -H "Authorization: Bearer <expert_token>"
```

## 🔄 Booking Status Flow

```
PENDING_PAYMENT → CONFIRMED → ASSIGNED → IN_PROGRESS → COMPLETED
                     ↓
                 CANCELLED (by scheduler if unpaid)
```

## ⏰ Scheduled Jobs

**Booking Cleanup Job**: Runs every 10 minutes to cancel unpaid bookings older than 30 minutes.

Configuration in `application.properties`:
```properties
booking.cleanup.cron=0 */10 * * * *
booking.cleanup.unpaid-minutes=30
```

## 💰 Pricing Logic

```
total = basePrice
if duration > 60 minutes:
   extraHours = ceil((duration - 60) / 60)
   total += extraHours * 90
if coupon = "NEW50":
   total -= 50
```

Example:
- Service base price: ₹500
- Duration: 120 minutes (2 hours)
- Extra charge: ceil((120-60)/60) * 90 = 1 * 90 = ₹90
- Total: ₹590
- With NEW50 coupon: ₹590 - ₹50 = ₹540

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run with coverage
```bash
mvn clean test jacoco:report
```

View coverage report: `target/site/jacoco/index.html`

**Test Coverage**: 33%+ overall with comprehensive coverage of business logic

### Test Categories:
- Unit tests for services
- Integration tests for controllers (MockMvc)
- Entity and DTO tests
- Security component tests

## 📚 API Documentation

Interactive API documentation available at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON specification:
```
http://localhost:8080/api-docs
```

## 🌳 Git Branching Strategy

```
main
  └── dev
       ├── dev-m1 (Foundation & Authentication)
       ├── dev-m2 (Service Catalog & Pricing)
       ├── dev-m3 (Customer Profile & Bookings)
       ├── dev-m4 (Payments & Feedback)
       └── dev-m5 (Admin & Expert)
```

## 📦 Key Endpoints Summary

### Public Endpoints
- `GET /health` - Health check
- `POST /auth/register` - Register user (customer or expert)
- `POST /auth/login` - Login user
- `POST /auth/admin/register` - Register admin
- `POST /auth/admin/login` - Login admin
- `GET /services` - Browse services
- `GET /pricing/quote` - Get price quote

### Customer Endpoints (requires `ROLE_CUSTOMER`)
- `GET /customer/profile` - Get profile
- `PATCH /customer/profile` - Update profile
- `POST /customer/addresses` - Add address
- `POST /customer/bookings` - Create booking
- `POST /customer/payments` - Make payment
- `POST /customer/payments/{id}/confirm` - Confirm payment
- `POST /customer/bookings/{id}/rating` - Rate service
- `POST /customer/tickets` - Create support ticket

### Expert Endpoints (requires `ROLE_EXPERT`)
- `POST /expert/onboarding` - Complete onboarding
- `GET /expert/jobs` - View assigned jobs
- `POST /expert/jobs/{id}/accept` - Accept job
- `POST /expert/jobs/{id}/start` - Start job
- `POST /expert/jobs/{id}/complete` - Complete job

### Admin Endpoints (requires `ROLE_ADMIN`)
- `GET /admin/users` - List all users
- `PATCH /admin/users/{id}/roles` - Update user roles
- `POST /admin/categories` - Create category
- `POST /admin/services` - Create service
- `POST /admin/experts/{id}/approve` - Approve expert
- `POST /admin/experts/{id}/reject` - Reject expert

## 🐛 Error Handling

All errors return standard JSON format:
```json
{
  "code": "ERROR_CODE",
  "message": "Human readable message",
  "timestamp": "2026-02-03T10:23:33.128"
}
```

Error Codes:
- `RESOURCE_NOT_FOUND` (404)
- `BUSINESS_ERROR` (400)
- `UNAUTHORIZED` (401)
- `FORBIDDEN` (403)
- `VALIDATION_ERROR` (400)
- `DUPLICATE_EMAIL` (409) - Email already exists in database
- `DUPLICATE_PHONE` (409) - Phone number already exists in database
- `INTERNAL_SERVER_ERROR` (500)

## 📋 Requirements Checklist

### Milestone 1 - Foundation & Authentication ✅
- [x] Spring Boot 3.x project setup with Java 17
- [x] User entity with roles
- [x] JWT authentication
- [x] Register endpoint (Customer/Expert)
- [x] Login endpoint
- [x] Health check endpoint
- [x] Swagger configuration

### Milestone 2 - Service Catalog & Pricing ✅
- [x] Category and Service entities
- [x] Service listing with pagination
- [x] Service search functionality
- [x] Pricing quote calculation
- [x] NEW50 coupon support
- [x] Seed data (data.sql)

### Milestone 3 - Customer Profile & Bookings ✅
- [x] Address entity
- [x] Booking entity with status flow
- [x] Customer profile endpoints
- [x] Address management
- [x] Booking creation and listing

### Milestone 4 - Payments & Feedback ✅
- [x] Payment entity and endpoints
- [x] Payment confirmation (updates booking status)
- [x] Rating entity and endpoints
- [x] Ticket entity and endpoints
- [x] Receipt generation

### Milestone 5 - Admin & Expert ✅
- [x] Admin user management
- [x] Admin category/service creation
- [x] Expert approval/rejection
- [x] Expert onboarding
- [x] Expert job flow (accept, start, complete)

### Additional Requirements ✅
- [x] JWT stateless authentication
- [x] BCrypt password hashing
- [x] Role-based authorization (@PreAuthorize)
- [x] Global exception handler (@ControllerAdvice)
- [x] SLF4J logging
- [x] Pagination support
- [x] Scheduled cleanup job
- [x] Unit and integration tests
- [x] Comprehensive README
- [x] Swagger documentation

## 👥 Authors

Home Solutions Team

## 📄 License

This project is part of a coding assignment.

## 🤝 Support

For issues or questions, create a support ticket through the API or contact the development team.
