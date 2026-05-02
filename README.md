# islamic-learning-center

Backend for an Islamic sciences learning platform—Spring Boot, PostgreSQL, JWT auth (next step), course content on S3, grades, attendance, and payment records. AWS-ready (RDS).

## Stack (step 1)

- Java **17**, Spring Boot **3.4**, **Maven** (`./mvnw`)
- **PostgreSQL** (local install + **pgAdmin** for development; Amazon RDS in production)
- **Flyway** for schema migrations
- **JUnit 5** + **Mockito** for unit tests
- **GitHub Actions** CI runs `./mvnw -B verify`

## Prerequisites

1. **JDK 17** (Temurin or Oracle)
2. **PostgreSQL** locally and **pgAdmin**
3. In pgAdmin: create a database (e.g. `islamic_learning`) and a user with a password you keep out of git

## Configuration

Never commit real passwords or RDS URLs with embedded credentials.

1. Copy [`env.example`](env.example) to a local approach (environment variables in your IDE, shell, or a **gitignored** `application-local.yml`).
2. Set at least:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD` (can be empty only if your local Postgres allows it)

See comments in [`src/main/resources/application.yml`](src/main/resources/application.yml) for which variables map to Spring properties.

## Run locally

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Then:

- Health: [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
- Actuator: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## Build & tests

```bash
./mvnw -B verify
```

## Repository

Remote: [https://github.com/DelucaGit/islamic-learning-center](https://github.com/DelucaGit/islamic-learning-center)

## Next (plan step 2)

Spring Security, JWT access + refresh tokens, registration and login.
