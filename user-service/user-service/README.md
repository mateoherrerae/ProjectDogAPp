# User Service Microservice

## Overview
The **User Service** is a Spring Boot-based microservice responsible for user authentication, authorization, and management. It leverages JWT (JSON Web Tokens) for secure authentication and supports multi-role users (e.g., `OWNER`, `WALKER`, `SITTER`, `ADMIN`). This service is designed to integrate seamlessly with other microservices while ensuring security and scalability.

---

## Features
- **User Registration**: Create accounts with encrypted passwords (BCrypt).
- **JWT Authentication**: Secure login and token generation.
- **Role-Based Access Control (RBAC)**: Assign multiple roles to users.
- **API Security**: Protected endpoints with JWT validation.
- **CORS & CSRF**: Pre-configured security policies.

---

## Technologies
- **Backend**: Java 22, Spring Boot
- **Security**: Spring Security, JWT (jjwt), BCrypt
- **Database**: PostgreSQL (relational) + MongoDB (optional for permissions)
- **Tools**: Lombok, OpenAPI (Swagger), Maven/Gradle

---

## Models
| Model             | Description                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `LoginRequest`    | Request body for login (username/password).                                |
| `RegisterRequest` | Request body for registration (username, password, email, phone, roles).   |
| `UserResponse`    | Safe user data exposure (excludes sensitive fields like passwords).        |
| `Users`           | Core user entity stored in the database.                                   |
| `Role`            | Entity for roles (e.g., `OWNER`, `WALKER`) with optional permissions.      |
| `Permission`      | Granular permissions (e.g., `CREATE_BOOKING`).                             |
| `RoleType`        | Enum defining valid roles (`OWNER`, `WALKER`, `ADMIN`, `SITTER`).          |

---

## Repositories
| Repository        | Description                                              |
|-------------------|----------------------------------------------------------|
| `UsersRepo`       | Handles CRUD operations for the `Users` entity.          |
| `RoleRepository`  | Manages roles and permissions in the database.           |

---

## Services
| Service                | Description                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| `JwtService`           | Generates and validates JWT tokens.                                         |
| `MyUserDetailsService` | Loads user details for Spring Security during authentication.               |
| `RoleService`          | Manages role assignments and permissions.                                   |
| `UserService`          | Core logic for registration, password encryption, and user data retrieval.  |

---

## Configuration
| Class             | Description                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `SecurityConfig`  | Configures Spring Security (CORS, CSRF, endpoint access rules).             |
| `JwtFilter`       | Validates JWT tokens in incoming requests.                                  |

---

## API Endpoints
### 1. **Register User**
- **Method**: `POST /api/users/register`
- **Request**:
  ```json
  {
    "username": "laura_petlover",
    "password": "SecurePass123!",
    "email": "laura@example.com",
    "phone": "+5491155551234",
    "roles": ["OWNER", "WALKER"]
  }