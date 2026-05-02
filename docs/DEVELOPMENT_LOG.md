# Development log

Human-readable notes about what we worked on, what went wrong, and how we fixed it.

---

## 2026-05-02 (Saturday)

### Big picture

We turned an empty GitHub repo into a **runnable Spring Boot backend (step 1)** for an Islamic sciences learning platform: Maven, PostgreSQL + Flyway, core database tables, a health API, basic tests, and a way to run locally without putting secrets in git.

---

### What we built and decided

- **Spring Boot 3.4** with **Maven** (`pom.xml`, Maven Wrapper). Package `com.islamiclearningcenter`.
- **PostgreSQL** as the database engine: **Flyway** migration `V1` creating `users`, `courses`, `enrollments`, `payments`, `refresh_tokens` (ready for JWT and billing later).
- **JPA entities and repositories** for those tables, plus a tiny **`UserService`** and a **Mockito** unit test so `mvn verify` does something useful.
- **Public API:** `GET /api/v1/health` and Actuator health.
- **Configuration:** datasource URL, username, and password come **only** from environment variables—nothing sensitive committed in `application.yml`.
- **`env.example`** lists variable names only (no real passwords).
- **GitHub Actions** CI runs `./mvnw -B verify` on pushes/PRs to `main` (Java 17 in CI, matches the POM).
- **Cursor / VS Code:** `.vscode/launch.json` runs the main class and loads **`.env.local`** so running from the IDE feels closer to IntelliJ “Run configuration” (secrets stay in a gitignored file).
- **Git workflow rule:** the agent should **commit** when appropriate but **`git push` only when you explicitly say so** in that message (we wrote that into `.cursor/rules/git-no-push.mdc` and refined the wording later in the day).

We also aligned the plan (separate document in Cursor) around things like JWT refresh tokens, a `Payment` table tied to users, RDS in AWS, human-readable code, Mockito tests, phased CI/Docker/CD, and local Postgres with **pgAdmin** (not Docker as the default dev database).

---

### Problems we hit and how we solved them

**1. Git was pointing at the wrong remote**  
The project folder didn’t have its own `.git` at first, so Git commands were picking up a parent repo.  
**Fix:** `git init` in the project folder, set `origin` to `https://github.com/DelucaGit/islamic-learning-center.git`, and track `main` from GitHub.

**2. `mvn` wasn’t on PATH**  
**Fix:** Copied the Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`) from an existing local Spring project so builds work without a global Maven install.

**3. Running from the terminal: “URL must start with `jdbc`”**  
Spring couldn’t build the datasource because **`SPRING_DATASOURCE_URL` was empty or invalid**. Two causes showed up in practice:

- **`.\mvnw.cmd spring-boot:run` does not read `.env.local`**—that file is only loaded when you start via the **Run and Debug / F5** configuration that references `envFile`.
- **Spaces after `=`** in `.env`-style files (e.g. `URL= jdbc:...`) make the URL invalid.

**Fix:** Either use **F5** with a corrected `.env.local`, or set **`$env:SPRING_DATASOURCE_*`** in the same PowerShell session before `mvnw spring-boot:run`, with **no space** after `=`.

**4. Database user / password memory**  
You use **`lms_db`** and **`lms_user`**; PostgreSQL doesn’t let you “look up” a forgotten password.  
**Fix:** Reset the role password with `ALTER ROLE ... PASSWORD` while connected as a superuser (e.g. `postgres`), then put the new value only in env / `.env.local`.

**5. Flyway warning about PostgreSQL 18**  
Flyway said PG 18 is newer than the versions it officially lists (up to 17).  
**Outcome:** Migrations still applied successfully; treat it as a heads-up for upgrades later, not a blocker for local dev.

**6. Pushing vs not pushing**  
Early on the agent pushed a large step-1 commit without you asking for push-only behavior. You asked for **commit without push** by default, then later asked to **push** explicitly—we did that. The Cursor rule now encodes: **push only when you clearly say so in that message.**

---

### What is *not* done yet (on purpose)

- **No login / JWT** yet—that’s plan **step 2**.
- **Thin tests:** one Mockito test; CI does not start Postgres (that’s OK for now but doesn’t prove DB wiring on every CI run).

---

### How to run it tomorrow (short reminder)

- Fill **`.env.local`** (no spaces after `=`), or set env vars in the shell.
- **F5:** “Spring Boot: IslamicLearningCenterApplication”, or terminal with `$env:` set then `.\mvnw.cmd spring-boot:run`.
- Check **`http://localhost:8080/api/v1/health`**.

---

*End of entry for 2026-05-02.*
