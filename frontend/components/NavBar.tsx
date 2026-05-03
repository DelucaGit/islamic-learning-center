"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import {
  clearTokens,
  decodeJwtPayload,
  getAccessToken,
  getRefreshToken,
  getRoleFromStorage,
} from "@/lib/auth";

export default function NavBar() {
  const router = useRouter();
  const token = typeof window !== "undefined" ? getAccessToken() : null;
  const role = typeof window !== "undefined" ? getRoleFromStorage() : null;
  const email =
    token != null ? decodeJwtPayload(token)?.email ?? "signed in" : null;

  async function logout() {
    const refresh = getRefreshToken();
    if (refresh) {
      await apiFetch("/auth/logout", {
        method: "POST",
        skipAuth: true,
        body: JSON.stringify({ refreshToken: refresh }),
      });
    }
    clearTokens();
    router.push("/");
    router.refresh();
  }

  return (
    <header className="border-b border-zinc-200 bg-white">
      <div className="mx-auto flex max-w-3xl flex-wrap items-center justify-between gap-3 px-4 py-3">
        <nav className="flex flex-wrap items-center gap-4 text-sm font-medium text-zinc-800">
          <Link href="/" className="text-zinc-950">
            Home
          </Link>
          <Link href="/courses">Courses</Link>
          {role === "TEACHER" && (
            <>
              <Link href="/courses/new">New course</Link>
              <Link href="/me/courses">My courses</Link>
            </>
          )}
          {role === "STUDENT" && (
            <Link href="/me/enrollments">My enrollments</Link>
          )}
        </nav>
        <div className="flex items-center gap-3 text-sm">
          {token ? (
            <>
              <span className="hidden text-zinc-500 sm:inline" title={email ?? ""}>
                {email}
                {role ? ` · ${role}` : ""}
              </span>
              <button
                type="button"
                onClick={() => void logout()}
                className="rounded-md border border-zinc-300 px-3 py-1.5 hover:bg-zinc-50"
              >
                Log out
              </button>
            </>
          ) : (
            <>
              <Link
                href="/login"
                className="rounded-md border border-zinc-300 px-3 py-1.5 hover:bg-zinc-50"
              >
                Log in
              </Link>
              <Link
                href="/register"
                className="rounded-md bg-zinc-900 px-3 py-1.5 text-white hover:bg-zinc-800"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
