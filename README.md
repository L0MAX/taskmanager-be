# Task Manager Backend

A Spring Boot RESTful backend application for managing tasks with JWT-based authentication and user management.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Setup & Installation](#setup--installation)
- [Environment Variables](#environment-variables)
- [Database](#database)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Logging](#logging)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- User registration and authentication (JWT-based).
- Create, read, update, and delete tasks.
- Role-based access (all users currently default to `ROLE_USER`).
- Validation for task requests (`title` required, status validation).
- Exception handling with structured API error responses.
- Logging of important operations and warnings.
- Fully tested service layer and security components.

---

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.x
- **Database:** H2 (default) / MySQL/PostgreSQL supported
- **Security:** Spring Security, JWT (JSON Web Token)
- **Testing:** JUnit 5, Mockito
- **Build Tool:** Maven

---

## Setup & Installation

1. **Clone the repository:**

```bash
git clone https://github.com/L0MAX/taskmanager-be.git
cd taskmanager-be
```

2. **Set environment variables (optional)**:

- `JWT_SECRET` — Secret key for signing JWTs (min 32 bytes recommended).
- `JWT_EXPIRATION_MS` - JWT token expiration time
- `SPRING_DATASOURCE_URL` — Database connection URL (for production).
- `SPRING_DATASOURCE_USERNAME` — Database username.
- `SPRING_DATASOURCE_PASSWORD` — Database password.

3. **Build the project with Maven:**

```bash
mvn clean install
```

---

## Database

By default, the application uses an **H2 in-memory database**.

- H2 console available at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(empty)*

You can configure MySQL or PostgreSQL in `application.properties` or via environment variables.

---

## Running the Application

Run using Maven:

```bash
mvn spring-boot:run
```

Or using the packaged JAR:

```bash
java -jar target/taskmanager-0.0.1-SNAPSHOT.jar
```

The server runs on **port 8080** by default.

---

## API Endpoints

### Auth

| Method | Endpoint         | Description            |
| ------ | ---------------- | ---------------------- |
| POST   | `/auth/register` | Register a new user    |
| POST   | `/auth/login`    | Authenticate & get JWT |

### Tasks (Requires JWT)

| Method | Endpoint      | Description                          |
| ------ | ------------- | ------------------------------------ |
| GET    | `/tasks`      | Get all tasks for authenticated user |
| GET    | `/tasks/{id}` | Get a single task by ID              |
| POST   | `/tasks`      | Create a new task                    |
| PUT    | `/tasks/{id}` | Update a task                        |
| DELETE | `/tasks/{id}` | Delete a task                        |

**Note:** Include `Authorization: Bearer <JWT>` header for all `/tasks/**` endpoints.

---
**Swagger:** Swagger API UI URL http://localhost:8080/swagger-ui/index.html#/tasks/create

## Testing

Run all unit tests with Maven:

```bash
mvn test
```

- Tests cover `TaskService`, `CustomUserDetailsService`, `JwtUtil`, and `JwtAuthenticationFilter`.
- High coverage on CRUD operations and security behavior.

---

## Logging

- **SLF4J + Logback** used for logging.
- Logs include info for task creation, updates, deletion, and invalid JWT attempts.

---

## Contributing

1. Fork the repository.
2. Create a new branch: `git checkout -b feature/YourFeature`.
3. Commit changes: `git commit -m "Add your message"`.
4. Push to branch: `git push origin feature/YourFeature`.
5. Open a Pull Request.

---

## License

This project is **MIT Licensed**. See `LICENSE` file for details.

