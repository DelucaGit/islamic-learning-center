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

Today was about finishing what yesterday’s log still called “the next chapter”: actual login, not just tables and a health URL.

The database side was already there—users, refresh token rows from Flyway—but nothing on the wire used it yet. I pulled in Spring Security the way you usually do for a JSON API: no sessions, CSRF turned off, and a small filter that looks for `Authorization: Bearer …`, verifies the JWT, and drops the principal into the security context if it parses. Registration runs passwords through bcrypt; login returns a short-lived access token plus an opaque refresh token, and only a hash of that refresh value gets stored. Refresh hands you a new pair and retires the old refresh row; logout tries to revoke what you send. I added a thin `ApiExceptionHandler` so errors come back as predictable JSON instead of whatever mood the servlet was in.

There’s one more env var to remember now: `APP_JWT_SECRET`. If it’s missing or too short the app won’t boot, which annoyed me for a minute until I remembered HS256 wants a real key. I updated `env.example` and the README so I don’t have to spelunk `application.yml` next time I set up a machine.

I ran the whole path in Postman—register, log in, refresh, log out, poke health—and it felt good when it all worked end to end. I did trip over port 8080 once because something else (probably an earlier run I’d forgotten about) was still listening; starting another instance on a different port for a smoke test made the picture clear. Small stuff, but it’s the kind of friction that eats an evening if you’re not paying attention.

The auth work is on `main` now. I left a TODO on `EmailAlreadyInUseException` because the message still echoes the email back, which is handy while you’re debugging but probably not what you want long term if you care about account enumeration.

What’s *still* not done is the part that actually feels like a school: courses, enrollments, payments as real APIs, and somewhere down the line S3 for materials plus grades and attendance. Yesterday I said login was the next chapter; today that chapter’s closed, and the next one is building on top of the schema instead of underneath it.

That’s it for today.

---

## Sunday, May 3, 2026 (continued) — tryout frontend

Same calendar day, different mood: I wanted something in a **browser** that hits the real API without living in Postman forever.

We already had courses + enrollments on the wire from earlier work on the branch; this stretch was about a **throwaway Next.js UI** in a `frontend/` folder (same repo, not pretending it’s the final product). I used `create-next-app` with the App Router and Tailwind. The annoying bit everyone hits first: **Spring doesn’t have CORS enabled**, so a page on `localhost:3000` can’t casually call `localhost:8081` from the browser. Fix for local dev: **Next `rewrites()`** so the browser only talks to same-origin `/api/v1/...` and the dev server forwards to Spring. No backend change, which I liked.

Port story again: my API listens on **8081** (`SERVER_PORT` in the root `.env.local`), not the Spring default 8080. The proxy target is **`BACKEND_ORIGIN`** in `frontend/.env.local` (with a checked-in `env.local.template` so I don’t forget the name). If those two ports disagree, you get very confident 404s from the wrong place.

Auth in the UI is deliberately crude: **access + refresh in `localStorage`**, a tiny `fetch` wrapper, and decode the JWT in the client just to show teacher vs student links. I know that’s not how you ship to strangers on the internet; it’s fine for me clicking around on my machine.

**Problem of the day:** ESLint (the React compiler rules bundled with the Next 16 template) yelled about **`setState` synchronously inside `useEffect`**. I wasn’t doing anything exotic—load data on mount—but the rule is strict. **Fix:** defer the first state writes by one tick (`await Promise.resolve()` at the top of the async loader, and `Promise.resolve().then(...)` for the “am I a teacher?” gate). After that, `npm run lint` and `npm run build` both went green. `npm audit` still reports a couple of moderate issues in the dependency tree; I didn’t chase them for a scratch UI.

While testing flows I once thought enrollments were “broken” until I realized I’d pasted the **teacher’s** access token where the student token belonged—403, not empty data. Classic human bug; the API was doing the right thing.

I updated `docs/BACKEND_PLAN.md` with a checked-off line for this tryout frontend so the checklist matches reality, and the root `README` points at `frontend/README.md` for how to run both processes.

That’s really it for this continuation.
