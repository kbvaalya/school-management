# 🎓 School Management System </br>
### Developed by Adelia Kabylbaeva Sca-24A </br>

A full-stack web application for managing school operations — including class management, student enrollment, and attendance tracking — built with **Spring Boot** on the backend and **React + Vite** on the frontend. </br>
Developed by Adelia Kabylbaeva Sca-24A

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [API Reference](#api-reference)
- [Role-Based Access Control](#role-based-access-control)
- [Authentication Flow](#authentication-flow)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Frontend](#frontend)
- [How It All Works Together](#how-it-all-works-together)

---

## Overview

School Management System (SMS) is a role-based web platform with three types of users:

| Role | Capabilities |
|------|-------------|
| **Admin** | Create users, create classes, assign managers to classes, enroll students |
| **Manager** | View assigned classes, mark student attendance (present / absent / excused) |
| **Student** | Log in with auto-generated credentials, view personal attendance history |

---

## Tech Stack

### Backend

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Primary language |
| Spring Boot | 3.4+ | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | Database ORM layer |
| JWT (jjwt) | 0.11.5 | Stateless token authentication |
| MySQL | 8.x | Production relational database |
| H2 | — | In-memory DB for development |
| Maven | 3.9+ | Build tool and dependency management |

### Frontend

| Technology | Version | Purpose |
|-----------|---------|---------|
| React | 18 | UI component framework |
| Vite | 5 | Fast build tool and dev server |
| Tailwind CSS | v4 | Utility-first styling |
| React Router | 6 | Client-side routing (SPA) |
| Lucide React | latest | Icon library |
| React Day Picker | latest | Attendance calendar visualization |

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  Browser (Client)                │
│                                                  │
│   ┌──────────────────────────────────────────┐   │
│   │          React SPA (Vite build)          │   │
│   │  Login → Dashboard → Classes → Calendar  │   │
│   └────────────────┬─────────────────────────┘   │
│                    │ HTTP + JWT Bearer Token      │
└────────────────────┼────────────────────────────-┘
                     │
┌────────────────────▼────────────────────────────┐
│              Spring Boot (Port 8080)            │
│                                                 │
│  ┌─────────────┐  ┌──────────────────────────┐  │
│  │ JWT Filter  │  │   Security Config (RBAC) │  │
│  └──────┬──────┘  └──────────────────────────┘  │
│         │                                        │
│  ┌──────▼──────────────────────────────────┐    │
│  │            REST Controllers             │    │
│  │  /api/auth  /api/admin  /api/manager    │    │
│  │             /api/student                │    │
│  └──────┬──────────────────────────────────┘    │
│         │                                        │
│  ┌──────▼──────────────────────────────────┐    │
│  │          Service Layer                  │    │
│  │  UserService / ClassService /           │    │
│  │  AttendanceService / AuthService        │    │
│  └──────┬──────────────────────────────────┘    │
│         │                                        │
│  ┌──────▼──────────────────────────────────┐    │
│  │       Spring Data JPA Repositories      │    │
│  └──────┬──────────────────────────────────┘    │
│         │                                        │
└─────────┼───────────────────────────────────────┘
          │
┌─────────▼───────────┐
│    MySQL Database   │
│  users              │
│  school_classes     │
│  class_students     │
│  attendances        │
└─────────────────────┘
```

---

## Database Schema

### `users`
| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Auto-generated ID |
| `username` | VARCHAR (unique) | Auto-generated from full name |
| `password` | VARCHAR | BCrypt-encoded password |
| `full_name` | VARCHAR | Full display name |
| `email` | VARCHAR (unique) | User email address |
| `role` | ENUM | `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER` |

### `school_classes`
| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Auto-generated ID |
| `name` | VARCHAR | Class name (e.g., "10A") |
| `subject` | VARCHAR | Subject name (e.g., "Mathematics") |
| `description` | VARCHAR | Optional description |
| `manager_id` | BIGINT (FK) | References `users.id` |

### `class_students` *(join table)*
| Column | Type | Description |
|--------|------|-------------|
| `class_id` | BIGINT (FK) | References `school_classes.id` |
| `student_id` | BIGINT (FK) | References `users.id` |

### `attendances`
| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Auto-generated ID |
| `student_id` | BIGINT (FK) | References `users.id` |
| `class_id` | BIGINT (FK) | References `school_classes.id` |
| `date` | DATE | Attendance date |
| `status` | ENUM | `PRESENT`, `ABSENT`, `ABSENT_EXCUSED` |
| `comment` | VARCHAR | Required when status is `ABSENT_EXCUSED` |

> **Unique constraint:** `(student_id, class_id, date)` — one record per student per class per day.

### Entity Relationships

```
User (ROLE_MANAGER) ──── manages ──────────────── SchoolClass
User (ROLE_USER)    ──── enrolled in (M:N) ─────── SchoolClass
User (ROLE_USER)    ──── has many ──────────────── Attendance
SchoolClass         ──── has many ──────────────── Attendance
```

---

## API Reference

### 🔓 Authentication — `/api/auth`

#### `POST /api/auth/login`
Authenticate any user and receive a JWT token.

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "fullName": "System Administrator",
    "role": "ROLE_ADMIN"
  }
}
```

---

### 🔐 Admin Endpoints — `/api/admin` *(ROLE_ADMIN only)*

#### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/admin/users` | Create a new student or manager |
| `GET` | `/api/admin/users` | List all users |
| `GET` | `/api/admin/users?role=ROLE_USER` | Filter by role |
| `GET` | `/api/admin/users?search=John` | Search by name or username |
| `GET` | `/api/admin/users/{id}` | Get user by ID |
| `DELETE` | `/api/admin/users/{id}` | Delete a user |

**Create User — Request Body:**
```json
{
  "fullName": "Ivan Ivanov",
  "email": "ivan@school.com",
  "role": "ROLE_USER"
}
```

**Create User — Response** *(credentials shown only once!)*:
```json
{
  "data": {
    "id": 3,
    "username": "ivan.ivanov",
    "generatedPassword": "Xk9#mP2qLw",
    "fullName": "Ivan Ivanov",
    "email": "ivan@school.com",
    "role": "ROLE_USER",
    "message": "User created. Share these credentials with the user."
  }
}
```

> ⚠️ The `generatedPassword` is returned **only once**. Save it and share it with the student.

#### Classes

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/admin/classes` | Create a new class |
| `GET` | `/api/admin/classes` | List all classes |
| `GET` | `/api/admin/classes?search=Math` | Search by name or subject |
| `GET` | `/api/admin/classes?managerId=2` | Filter by manager |
| `GET` | `/api/admin/classes/{id}` | Get class details |
| `PUT` | `/api/admin/classes/{id}` | Update class info |
| `DELETE` | `/api/admin/classes/{id}` | Delete a class |
| `PATCH` | `/api/admin/classes/{classId}/manager/{managerId}` | Assign manager to class |
| `POST` | `/api/admin/classes/{classId}/students/{studentId}` | Enroll student |
| `DELETE` | `/api/admin/classes/{classId}/students/{studentId}` | Remove student |

**Create Class — Request Body:**
```json
{
  "name": "10A",
  "subject": "Mathematics",
  "description": "Senior class",
  "managerId": 2
}
```

#### Attendance (Admin View)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/admin/attendances` | View all attendance records (filterable) |
| `DELETE` | `/api/admin/attendances/{id}` | Delete an attendance record |

**Filter Parameters:**
```
?classId=1&studentId=3&status=ABSENT&startDate=2026-01-01&endDate=2026-04-30
```

---

### 👔 Manager Endpoints — `/api/manager` *(ROLE_MANAGER or ROLE_ADMIN)*

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/manager/classes` | List classes assigned to current manager |
| `GET` | `/api/manager/classes?search=Math` | Search own classes |
| `GET` | `/api/manager/classes/{id}` | Get class with students list |
| `POST` | `/api/manager/attendances` | Mark attendance |
| `PUT` | `/api/manager/attendances/{id}` | Update an attendance record |
| `GET` | `/api/manager/attendances` | View attendance for own classes |

**Mark Attendance — Request Body:**
```json
{
  "studentId": 3,
  "classId": 1,
  "date": "2026-04-23",
  "status": "ABSENT_EXCUSED",
  "comment": "Medical certificate"
}
```

**Attendance Statuses:**
| Status | Meaning |
|--------|---------|
| `PRESENT` | Student was present |
| `ABSENT` | Absent without reason |
| `ABSENT_EXCUSED` | Absent with valid reason (comment required) |

---

### 👨‍🎓 Student Endpoints — `/api/student` *(all authenticated roles)*

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/student/me` | Get own profile |
| `GET` | `/api/student/classes` | List enrolled classes |
| `GET` | `/api/student/classes/{id}` | Get specific class info |
| `GET` | `/api/student/attendances` | View own attendance history |

**Student Attendance Filter Parameters:**
```
?classId=1&status=ABSENT&startDate=2026-01-01&endDate=2026-04-30
```

---

## Role-Based Access Control

```
Endpoint Group          ADMIN    MANAGER    STUDENT
─────────────────────────────────────────────────────
POST /api/auth/login    ✅       ✅         ✅
GET  /api/admin/**      ✅       ❌         ❌
POST /api/admin/**      ✅       ❌         ❌
GET  /api/manager/**    ✅       ✅         ❌
POST /api/manager/**    ✅       ✅         ❌
GET  /api/student/**    ✅       ✅         ✅
```

> A **Manager** can only mark attendance for classes **assigned to them**. Attempting to mark attendance for another manager's class returns `403 Forbidden`.

---

## Authentication Flow

```
1. Client sends POST /api/auth/login { username, password }
         │
         ▼
2. Spring Security authenticates via DaoAuthenticationProvider
         │
         ▼
3. JwtUtil generates a signed HS256 token (24h expiry)
         │
         ▼
4. Token returned to client → stored in localStorage
         │
         ▼
5. Every subsequent request includes:
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         │
         ▼
6. JwtAuthFilter intercepts → validates token → sets SecurityContext
         │
         ▼
7. Controller receives authenticated User object via @AuthenticationPrincipal
```

---

## Getting Started

### Prerequisites

- **Java 17** or **Java 21** — [Download Temurin](https://adoptium.net)
- **MySQL 8.x** — [Download](https://dev.mysql.com/downloads/mysql/)
- **Maven 3.9+** — or use the included `mvnw.cmd`
- **Node.js 18+** — [Download](https://nodejs.org) *(for frontend only)*

### 1. Create MySQL Database

```sql
CREATE DATABASE school_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configure `application.properties`

```properties
# Server
server.port=8080

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/school_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT
jwt.secret=schoolManagementSecretKeyForJwtGenerationShouldBe256BitsMinimumLength
jwt.expiration=86400000
```

### 3. Run the Backend

```bash
# Using Maven Wrapper (no Maven installation needed)
./mvnw.cmd spring-boot:run        # Windows
./mvnw spring-boot:run            # Mac / Linux

# Or with Maven installed
mvn spring-boot:run
```

The server starts at `http://localhost:8080`

### 4. Run the Frontend (Development)

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`

### 5. Build Frontend into Backend (Production)

```bash
cd frontend
npm run build
```

This compiles React into `src/main/resources/static/` — the Spring Boot app will serve the UI at `http://localhost:8080`.

### 6. Default Credentials

On first startup the system automatically creates:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Manager | `manager1` | `manager123` |

---

## Project Structure

```
school-management/
├── pom.xml                                # Maven dependencies
├── mvnw.cmd                               # Maven Wrapper (Windows)
├── mvnw                                   # Maven Wrapper (Linux/Mac)
│
├── src/main/java/com/school/management/
│   ├── SchoolManagementApplication.java   # Entry point + data seeder
│   │
│   ├── config/
│   │   └── SecurityConfig.java            # Spring Security + JWT filter chain
│   │
│   ├── security/
│   │   ├── JwtUtil.java                   # Token generation & validation
│   │   ├── JwtAuthFilter.java             # Per-request JWT filter
│   │   └── UserDetailsServiceImpl.java    # Load user from DB
│   │
│   ├── entity/
│   │   ├── User.java                      # UserDetails implementation
│   │   ├── SchoolClass.java               # Class entity
│   │   ├── Attendance.java                # Attendance record
│   │   ├── Role.java                      # ROLE_ADMIN / ROLE_MANAGER / ROLE_USER
│   │   └── AttendanceStatus.java          # PRESENT / ABSENT / ABSENT_EXCUSED
│   │
│   ├── repository/
│   │   ├── UserRepository.java            # User queries + search
│   │   ├── SchoolClassRepository.java     # Class queries + search
│   │   └── AttendanceRepository.java      # Attendance queries + filters
│   │
│   ├── service/
│   │   ├── AuthService.java               # Login logic
│   │   ├── UserService.java               # User CRUD + password generation
│   │   ├── SchoolClassService.java        # Class management
│   │   └── AttendanceService.java         # Attendance marking + filtering
│   │
│   ├── controller/
│   │   ├── AuthController.java            # POST /api/auth/login
│   │   ├── AdminController.java           # /api/admin/**
│   │   ├── ManagerController.java         # /api/manager/**
│   │   └── StudentController.java         # /api/student/**
│   │
│   ├── dto/
│   │   ├── ApiResponse.java               # Generic response wrapper
│   │   ├── auth/                          # LoginRequest, AuthResponse
│   │   ├── user/                          # CreateUserRequest/Response, UserResponse
│   │   ├── schoolclass/                   # CreateClassRequest, ClassResponse
│   │   └── attendance/                    # Mark/Update/Filter requests, Response
│   │
│   └── exception/
│       ├── GlobalExceptionHandler.java    # @RestControllerAdvice
│       ├── ResourceNotFoundException.java
│       ├── BadRequestException.java
│       └── AccessDeniedException.java
│
└── src/main/resources/
    ├── application.properties             # App configuration
    └── static/                            # Built frontend files (index.html, js, css)
```

---

## Frontend

The frontend is a React SPA built with Vite. It communicates with the backend exclusively through REST API calls with JWT authentication.

### Key Frontend Files

```
frontend/
├── src/
│   ├── App.jsx                # Root component + route guards
│   ├── main.jsx               # React entry point
│   ├── pages/
│   │   ├── Login.jsx          # Login form
│   │   ├── AdminDashboard.jsx # User & class management
│   │   ├── ManagerDashboard.jsx # Attendance marking
│   │   └── StudentDashboard.jsx # Personal attendance calendar
│   └── components/
│       ├── Sidebar.jsx
│       ├── AttendanceCalendar.jsx  # react-day-picker integration
│       └── ...
├── package.json
└── vite.config.js
```

### Route Protection

```jsx
// App.jsx — role-based route guards
if (role === 'ROLE_ADMIN')   → <AdminDashboard />
if (role === 'ROLE_MANAGER') → <ManagerDashboard />
if (role === 'ROLE_USER')    → <StudentDashboard />
if (!token)                  → redirect to /login
```

### Attendance Calendar

The student dashboard uses `react-day-picker` to visually display attendance:

- 🟢 **Green** — `PRESENT`
- 🔴 **Red** — `ABSENT`
- 🟡 **Yellow** — `ABSENT_EXCUSED`

---

## How It All Works Together

### Build Time
```
npm run build (inside /frontend)
       │
       ▼
Vite compiles React → optimized HTML/JS/CSS
       │
       ▼
Output copied to src/main/resources/static/
       │
       ▼
mvn package → single school-management.jar
             (contains both backend + frontend)
```

### Runtime Request Flow

```
Browser opens http://localhost:8080
       │
       ▼
Spring Boot serves index.html (from /static)
       │
       ▼
React initializes → checks localStorage for JWT
       │
  ┌────┴────┐
  No token  Has token
  │         │
  ▼         ▼
/login    Decode role → render correct dashboard
  │
  ▼
POST /api/auth/login
  │
  ▼
Server returns JWT
  │
  ▼
Token saved to localStorage
  │
  ▼
All API calls include:
Authorization: Bearer <token>
```

### Security Layers

```
Request arrives
      │
      ▼
JwtAuthFilter — validates token signature & expiry
      │
      ▼
SecurityConfig — checks role against endpoint pattern
      │
      ▼ (if Manager tries to mark attendance for another class)
AttendanceService — checks manager owns the class
      │
      ▼
403 Forbidden if any check fails
```

---

## Security Notes

- Passwords are hashed with **BCrypt** (strength 10) — never stored in plain text
- JWT tokens expire after **24 hours** (`jwt.expiration=86400000` ms)
- A manager **cannot** access or modify attendance records for classes not assigned to them — enforced at the service layer, not just the UI
- Admin account cannot be deleted via the API
- Generated student passwords are returned **only once** at creation time

---

## Common Errors & Solutions

| Error | Cause | Fix |
|-------|-------|-----|
| `401 Unauthorized` | Missing or expired JWT | Re-login to get a new token |
| `403 Forbidden` | Wrong role for endpoint | Use correct account role |
| `400 Bad Request` | Missing required fields | Check request body |
| `Comment required` | `ABSENT_EXCUSED` without comment | Add `"comment"` field |
| `Student already enrolled` | Duplicate enrollment | Student is already in the class |
| `User is not a MANAGER` | Assigning non-manager as class manager | Create user with `ROLE_MANAGER` first |
| MySQL connection refused | MySQL not running | Start MySQL service |

---

*Built with ❤️ using Spring Boot 3 + React 18*
