# Development log

Notes I keep for myself about what happened on a given day.

---

## Saturday, May 2, 2026

Today was mostly about getting the backend project off the ground instead of it living only in my head or in a loose plan.

I finally pointed my local folder at the real GitHub repo (`islamic-learning-center`). For a while Git was acting weird because the folder didn’t have its own repo and it was picking up something higher up on my machine—once that was sorted and `origin` pointed at the right URL, things felt normal again.

The big chunk of work was the first real version of the Spring app: Maven, Spring Boot, Postgres, Flyway so the tables actually get created when I start the app, and the basic entities for users, courses, enrollments, payments, and refresh tokens (even though login isn’t built yet). There’s a simple health endpoint so I can prove the server is alive, and a tiny test so CI has something to run. Speaking of CI, GitHub Actions now runs the Maven verify step when I push—that’s reassuring.

I spent more time than I expected on configuration. I didn’t want passwords or connection strings sitting in files that get committed, so everything sensitive goes through environment variables. I copied an example file for the variable names, and I use a `.env.local` file that never goes to git. Cursor doesn’t automatically load that file when I run Maven from the terminal—I learned that the hard way when Spring yelled that the JDBC URL had to start with `jdbc`. Turns out the terminal run doesn’t see `.env.local`; the Run/Debug launch config does (similar vibe to how I used to set env vars in IntelliJ). I also had stray spaces after the equals signs in my env file once, which broke things in a confusing way until I noticed.

I had a moment of panic with Postgres where I couldn’t remember which user went with my `lms_db` database and you can’t “look up” a password like you can reset a website password—you just set a new one as the superuser and move on. Annoying but fine.

There was a Flyway warning about Postgres 18 being newer than what they officially test; the migration still ran, so I’m not losing sleep over it.

On the git side, I decided I want commits to happen regularly but pushes only when I actually say to push—so the agent doesn’t surprise me by sending half-baked stuff to GitHub. We wrote that into a Cursor rule and tweaked the wording once I was sure what I wanted.

What’s *not* done: no login, no JWT, no real API beyond health. That’s the next chapter. For today I’m happy that the app boots, talks to my local database, and I can hit the health URL in a browser or Postman.

That’s it for today.

---

## Sunday, May 3, 2026

Shipped **step 2 — auth**: Spring Security with a JWT bearer filter, bcrypt registration, login returning access + refresh tokens, refresh rotation, and logout revoking the refresh token fingerprint. REST lives under `/api/v1/auth/*` with `ApiExceptionHandler` for consistent JSON errors.

Configuration now also requires **`APP_JWT_SECRET`** (long random string, ≥ 32 UTF-8 bytes) alongside the existing datasource env vars; `env.example` and the README table were aligned with that.

Verified end-to-end with Postman (register → login → refresh → logout → health). Hit a port-8080 conflict once when smoke-testing from the terminal; second instance on another port behaved as expected.

Pushed the auth stack to GitHub on `main`. Left a small **TODO** on `EmailAlreadyInUseException` to revisit a friendlier / less enumerable error message for duplicate registration.

**What’s next (step 3):** real APIs for courses, enrollments, and payments on top of the schema we already have — still no S3 content, grades, or attendance yet.
