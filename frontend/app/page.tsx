import Link from "next/link";

export default function Home() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold tracking-tight">
        Islamic Learning Center — API tryout
      </h1>
      <p className="text-zinc-600">
        This UI is for exercising the Spring backend (courses, enrollments, auth).
        Start the API on your machine (default proxy target{" "}
        <code className="rounded bg-zinc-200 px-1">http://localhost:8081</code>
        ), then use{" "}
        <Link href="/register" className="font-medium text-zinc-950 underline">
          Register
        </Link>{" "}
        and{" "}
        <Link href="/login" className="font-medium text-zinc-950 underline">
          Log in
        </Link>
        .
      </p>
      <ul className="list-inside list-disc space-y-2 text-zinc-700">
        <li>
          <Link href="/courses" className="text-zinc-950 underline">
            Browse active courses
          </Link>
        </li>
        <li>Teachers: create a course, add students by email.</li>
        <li>Students: view enrollments after a teacher adds you.</li>
      </ul>
    </div>
  );
}
