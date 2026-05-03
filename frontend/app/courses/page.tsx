"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { apiJson } from "@/lib/api";
import { getAccessToken } from "@/lib/auth";
import type { Course } from "@/lib/types";

export default function CoursesPage() {
  const [courses, setCourses] = useState<Course[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    void (async () => {
      await Promise.resolve();
      if (!getAccessToken()) {
        if (!cancelled) {
          setError("Log in to list courses.");
          setCourses([]);
        }
        return;
      }
      try {
        const data = await apiJson<Course[]>("/courses");
        if (!cancelled) setCourses(data);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : "Failed to load courses");
          setCourses([]);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-xl font-semibold">Active courses</h1>
        <Link
          href="/courses/new"
          className="rounded-md border border-zinc-300 px-3 py-1.5 text-sm hover:bg-white"
        >
          New course
        </Link>
      </div>
      {error && <p className="text-sm text-red-700">{error}</p>}
      {courses === null && <p className="text-zinc-500">Loading…</p>}
      {courses && courses.length === 0 && !error && (
        <p className="text-zinc-600">No active courses yet.</p>
      )}
      {courses && courses.length > 0 && (
        <ul className="divide-y divide-zinc-200 rounded-lg border border-zinc-200 bg-white">
          {courses.map((c) => (
            <li key={c.id}>
              <Link
                href={`/courses/${c.id}`}
                className="block px-4 py-3 hover:bg-zinc-50"
              >
                <span className="font-medium">{c.title}</span>
                {c.description && (
                  <p className="mt-1 line-clamp-2 text-sm text-zinc-600">{c.description}</p>
                )}
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
