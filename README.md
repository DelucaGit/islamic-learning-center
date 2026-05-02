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
3. In pgAdmin: create your database and login role; keep the password **only** in environment variables (not in this repo).

## Configuration

**No database URL, username, or password is stored in committed files.** Set these on your machine:

| Variable | Purpose |
|----------|---------|
| `SPRING_DATASOURCE_URL` | Full JDBC URL (`jdbc:postgresql://host:port/database`) |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL role name |
| `SPRING_DATASOURCE_PASSWORD` | That role’s password |
| `SPRING_PROFILES_ACTIVE` | Optional; e.g. `local` or `prod` |

See [`env.example`](env.example) for names only. Spring maps `SPRING_DATASOURCE_*` to `spring.datasource.*`; see comments in [`application.yml`](src/main/resources/application.yml).

### Windows: environment variables

**Option A — current PowerShell session:** assign `$env:SPRING_DATASOURCE_URL`, `$env:SPRING_DATASOURCE_USERNAME`, `$env:SPRING_DATASOURCE_PASSWORD` (and optionally `$env:SPRING_PROFILES_ACTIVE`), then run `.\mvnw.cmd spring-boot:run`.

**Option B — persist for your user:** `Environment` → **Environment variables** (GUI), or `[Environment]::SetEnvironmentVariable("NAME", "value", "User")` in PowerShell — use **your** values; do not commit them.

**Option C — Cursor / VS Code (like IntelliJ run config):**  
1. Copy [`env.example`](env.example) to **`.env.local`** in the project root (that file is **gitignored** via `.env.*`).  
2. Fill in your real `SPRING_DATASOURCE_*` values (and optionally `SPRING_PROFILES_ACTIVE=local`).  
3. Open **Run and Debug** (`Ctrl+Shift+D`), choose **“Spring Boot: IslamicLearningCenterApplication”**, press **Start Debugging** (F5).  
   The launch config is [`.vscode/launch.json`](.vscode/launch.json); it loads `.env.local` only on your machine.

Restart the terminal or IDE after changing user-level Windows variables (options A/B).

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
