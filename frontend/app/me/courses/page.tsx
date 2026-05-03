"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { apiJson } from "@/lib/api";
import { getRoleFromStorage } from "@/lib/auth";
import type { Course } from "@/lib/types";

export default function MyCoursesPage() {
  const [roleOk, setRoleOk] = useState<boolean | null>(null);
  const [courses, setCourses] = useState<Course[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void Promise.resolve().then(() => {
      setRoleOk(getRoleFromStorage() === "TEACHER");
    });
  }, []);

  useEffect(() => {
    if (roleOk !== true) return;
    let cancelled = false;
    (async () => {
      await Promise.resolve();
      try {
        const data = await apiJson<Course[]>("/me/courses");
        if (!cancelled) setCourses(data);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : "Failed to load");
          setCourses([]);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [roleOk]);

  if (roleOk === null) return <p className="text-zinc-500">Loading…</p>;
  if (!roleOk) {
    return (
      <p className="text-red-700">
        Sign in as a <strong>teacher</strong> to view your courses.
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-xl font-semibold">My courses</h1>
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
        <p className="text-zinc-600">You have not created any courses yet.</p>
      )}
      {courses && courses.length > 0 && (
        <ul className="divide-y divide-zinc-200 rounded-lg border border-zinc-200 bg-white">
          {courses.map((c) => (
            <li key={c.id}>
              <Link href={`/courses/${c.id}`} className="block px-4 py-3 hover:bg-zinc-50">
                <span className="font-medium">{c.title}</span>
                <span className="ml-2 text-sm text-zinc-500">
                  {c.active ? "active" : "inactive"}
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
