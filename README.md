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
3. In pgAdmin: your local database is **`lms_db`** with login role **`lms_user`** (password is only in your environment, not in git).

## Configuration

Never commit real passwords or RDS URLs with embedded credentials.

Defaults in `application.yml` assume **`lms_db`** and **`lms_user`**; override with env vars if yours differ.

### Windows: set database credentials as environment variables

**Option A — current PowerShell session only** (good for trying things):

```powershell
$env:SPRING_PROFILES_ACTIVE = "local"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/lms_db"
$env:SPRING_DATASOURCE_USERNAME = "lms_user"
$env:SPRING_DATASOURCE_PASSWORD = "YOUR_PASSWORD_HERE"
.\mvnw.cmd spring-boot:run
```

**Option B — persist for your Windows user** (new terminals and apps see them after you reopen them):

```powershell
[Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5432/lms_db", "User")
[Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_USERNAME", "lms_user", "User")
[Environment]::SetEnvironmentVariable("SPRING_DATASOURCE_PASSWORD", "YOUR_PASSWORD_HERE", "User")
```

Close and reopen Cursor/terminal so the app picks them up.

**Option C — GUI:** Settings → System → About → **Advanced system settings** → **Environment variables** → under “User variables”, add `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

**Option D — IDE run configuration:** In Cursor/VS Code “Run and Debug”, add the same names under `env` in your launch config (use a **local** file or user settings so secrets are not committed).

See [`env.example`](env.example) for the full list of names. Spring maps `SPRING_DATASOURCE_*` automatically; see comments in [`application.yml`](src/main/resources/application.yml).

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
