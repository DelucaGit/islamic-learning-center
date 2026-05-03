# islamic-learning-center

Backend for an Islamic sciences learning platform—Spring Boot, PostgreSQL, JWT auth (**done**), course content on S3, grades, attendance, and payment records. AWS-ready (RDS).

## Plan & progress

### Step 1 — Foundation (done)

- [x] Spring Boot 3.4, Java 17, Maven (`./mvnw`)
- [x] PostgreSQL + Flyway (`V1__init_schema.sql`)
- [x] Core JPA entities + repositories: users, courses, enrollments, payments, refresh tokens
- [x] Health API: `GET /api/v1/health`
- [x] Actuator health
- [x] CI: GitHub Actions runs `./mvnw -B verify`
- [x] Secrets via environment only (no committed DB passwords or JWT secrets)

### Step 2 — Auth & security (done)

- [x] Spring Security (stateless API, JWT bearer filter)
- [x] Access JWT (HS256) via `APP_JWT_SECRET` + configurable TTL
- [x] Opaque refresh tokens (fingerprint stored in DB), rotation on refresh
- [x] `POST /api/v1/auth/register` — bcrypt password hashing
- [x] `POST /api/v1/auth/login` — returns access + refresh tokens
- [x] `POST /api/v1/auth/refresh` — new token pair
- [x] `POST /api/v1/auth/logout` — revokes refresh token when present
- [x] Central API error handling (`ApiExceptionHandler`)

### Step 3 — Product APIs (in progress)

- [x] Courses API (CRUD, teacher ownership, listing; `GET /api/v1/me/courses` for owned courses)
- [x] Enrollments API (teacher adds/removes students; `GET /api/v1/me/enrollments` for student)
- [ ] Payments API (record status, tie to enrollment where needed)
- [ ] Later: S3-backed course content, grades, attendance (as designed)

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
| `APP_JWT_SECRET` | JWT signing secret (**≥ 32 UTF-8 bytes**); required at startup |

See [`env.example`](env.example) for names only. Spring maps `SPRING_DATASOURCE_*` to `spring.datasource.*`; see comments in [`application.yml`](src/main/resources/application.yml).

### Windows: environment variables

**Option A — current PowerShell session:** assign `$env:SPRING_DATASOURCE_URL`, `$env:SPRING_DATASOURCE_USERNAME`, `$env:SPRING_DATASOURCE_PASSWORD`, `$env:APP_JWT_SECRET` (and optionally `$env:SPRING_PROFILES_ACTIVE`), then run `.\mvnw.cmd spring-boot:run`.

**Option B — persist for your user:** `Environment` → **Environment variables** (GUI), or `[Environment]::SetEnvironmentVariable("NAME", "value", "User")` in PowerShell — use **your** values; do not commit them.

**Option C — Cursor / VS Code (like IntelliJ run config):**  
1. Copy [`env.example`](env.example) to **`.env.local`** in the project root (that file is **gitignored** via `.env.*`).  
2. Fill in your real `SPRING_DATASOURCE_*`, `APP_JWT_SECRET`, and optionally `SPRING_PROFILES_ACTIVE=local`.  
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
- Auth (JSON `POST`): `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/refresh`, `/api/v1/auth/logout`  
- Courses & enrollments (Bearer): `GET|POST /api/v1/courses`, `GET|PATCH|DELETE /api/v1/courses/{id}`, `GET /api/v1/me/courses` (teacher); `GET|POST /api/v1/courses/{id}/students`, `DELETE /api/v1/courses/{id}/students/{studentId}` (teacher); `GET /api/v1/me/enrollments` (student)  
  Other `/api/v1/**` routes require `Authorization: Bearer <accessToken>` unless explicitly permitted in security config.

## Build & tests

```bash
./mvnw -B verify
```

## Repository

Remote: [https://github.com/DelucaGit/islamic-learning-center](https://github.com/DelucaGit/islamic-learning-center)

## Next (plan step 3)

**Done:** courses + enrollments REST APIs (see **Step 3** above). **Next:** Payments API, then S3 / grades / attendance per roadmap. Living checklist: [`docs/BACKEND_PLAN.md`](docs/BACKEND_PLAN.md).
