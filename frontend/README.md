# Frontend (tryout UI)

Minimal [Next.js](https://nextjs.org/) app to exercise the Spring API from a browser. Not the final product UI.

## Prerequisites

- Node.js **20+** (LTS recommended) and npm
- Spring Boot API running locally (see repo root `README.md`)

## Configuration

1. Copy `env.local.template` to **`.env.local`** in this folder.
2. Set `BACKEND_ORIGIN` to match your API (default **`http://localhost:8081`**, same as `SERVER_PORT` in the repo root `.env.local`).

Next.js reads `BACKEND_ORIGIN` when starting dev/build so `next.config.ts` can **rewrite** same-origin `/api/v1/*` requests to the Java server (avoids CORS during local dev).

## Run

Terminal 1 — API (from repo root):

```powershell
.\mvnw.cmd spring-boot:run
```

Terminal 2 — UI:

```powershell
cd frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

## Auth note (local only)

Access and refresh tokens are stored in **`localStorage`**. That is fine for local exploration only; use httpOnly cookies or a BFF before any real users.

## What to try

1. **Register** a teacher and a student (separate emails).
2. **Log in** as teacher → **New course** → open the course → **Add student** by the student’s email.
3. **Log out**, **log in** as student → **My enrollments**.
