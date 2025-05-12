# Walker Service

The Walker Service is a Spring Boot application that provides APIs for managing walker profiles and related functionalities. This service interacts with the User Service for authentication and role management.

## Table of Contents

- [Walker Service](#walker-service)
    - [Table of Contents](#table-of-contents)
    - [APIs](#apis)
        - [Register Walker](#register-walker)
        - [Get Walker by ID](#get-walker-by-id)
        - [Get Nearby Walkers](#get-nearby-walkers)
    - [Dependencies](#dependencies)
    - [Configuration](#configuration)
    - [Running the Application](#running-the-application)
    - [Contributing](#contributing)

## APIs

### Register Walker

**Endpoint:** `POST /api/walkers/register`

**Description:** Creates a walker profile. This endpoint verifies the role in the User Service via JWT.

**Request Header:**
- `Authorization: Bearer <JWT_TOKEN>`

**Request Body:**
```json
{
  "name": "John Doe",
  "experience": "5 years",
  "location": {
    "lat": 40.7128,
    "lng": -74.0060
  }
}