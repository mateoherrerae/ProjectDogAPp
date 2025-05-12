# Dog Service - Resumen Técnico 🐶

### Descripción General
Microservicio encargado de la gestión de perfiles caninos, con integración segura al servicio de usuarios mediante JWT. Ofrece operaciones CRUD para perros y control de visibilidad de perfiles.

---

### Características Clave
- **Registro de Perros**:
    - Campos: nombre, raza, edad, peso, historial médico, cuidados especiales.
    - Validación de datos con Hibernate Validator (`@NotBlank`, `@Positive`, etc).
- **Autorización por Roles**:
    - `OWNER`: Permite gestión exclusiva de sus perros.
    - `ADMIN`: Acceso total.
- **Comunicación entre Servicios**:
    - Usa Feign Client para obtener username del dueño desde el User Service.
- **Seguridad JWT**:
    - Validación de tokens y extracción de claims (`userId`, `roles`).

---

### Stack Tecnológico
| Categoría          | Tecnologías/Herramientas                              |
|---------------------|-------------------------------------------------------|
| Lenguaje           | Java 17                                               |
| Framework          | Spring Boot 3.2.x                                     |
| Seguridad          | Spring Security, JWT (jjwt)                           |
| Base de Datos      | PostgreSQL, Spring Data JPA                           |
| Comunicación       | OpenFeign                                             |
| Documentación      | Swagger/OpenAPI                                       |
| Herramientas       | Lombok, Hibernate Validator, Maven/Gradle             |

---

### Endpoints Principales
| Método | Ruta                     |                          Descripción | Rol Requerido  |
|--------|--------------------------|-------------------------------------:|----------------|
| POST   | `/api/dogs`              |                    Crear nuevo perro | OWNER          |
| GET    | `/api/dogs/{dogId}`      |     Obtener perfil (público/privado) | Público*       |
| GET    | `/api/dogs/owner/{id}`   |            Listar perros de un dueño | OWNER o ADMIN  |
| PUT    | `/api/dogs/{dogId}`      |                     Actualizar perro | OWNER          |
| DELETE | `/api/dogs/{dogId}`      |                       Eliminar perro | OWNER          |

*Los perfiles privados solo son visibles para dueños y admins.

```mermaid
sequenceDiagram
Cliente->>+Dog Service: POST /api/dogs (con JWT)
Dog Service->>User Service: GET /api/users/{userId}/username (via Feign + JWT)
User Service-->>Dog Service: username
Dog Service->>DB: INSERT dog (owner_id = userId)
Dog Service-->>-Cliente: 201 Created + DogResponse

---

### Estructura de Clases
```bash
src/
├── main/
│   ├── java/com/example/dogservice/
│   │   ├── controller/       # DogController (REST endpoints)
│   │   ├── model/            # Dog (Entity), DogRequest (DTO), DogResponse (DTO)
│   │   ├── repository/       # DogRepository (JPA)
│   │   ├── service/          # DogService (lógica de negocio), JwtService (validación tokens)
│   │   ├── config/           # SecurityConfig, FeignConfig
│   │   └── exception/        # DogNotFoundException, GlobalExceptionHandler




