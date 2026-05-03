"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { apiJson } from "@/lib/api";
import { getRoleFromStorage } from "@/lib/auth";
import type { StudentEnrollmentResponse } from "@/lib/types";

export default function MyEnrollmentsPage() {
  const [roleOk, setRoleOk] = useState<boolean | null>(null);
  const [rows, setRows] = useState<StudentEnrollmentResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void Promise.resolve().then(() => {
      setRoleOk(getRoleFromStorage() === "STUDENT");
    });
  }, []);

  useEffect(() => {
    if (roleOk !== true) return;
    let cancelled = false;
    (async () => {
      await Promise.resolve();
      try {
        const data = await apiJson<StudentEnrollmentResponse[]>("/me/enrollments");
        if (!cancelled) setRows(data);
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : "Failed to load");
          setRows([]);
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
        Sign in as a <strong>student</strong> to view your enrollments.
      </p>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-semibold">My enrollments</h1>
      {error && <p className="text-sm text-red-700">{error}</p>}
      {rows === null && <p className="text-zinc-500">Loading…</p>}
      {rows && rows.length === 0 && !error && (
        <p className="text-zinc-600">You are not enrolled in any course yet.</p>
      )}
      {rows && rows.length > 0 && (
        <ul className="divide-y divide-zinc-200 rounded-lg border border-zinc-200 bg-white">
          {rows.map((r) => (
            <li key={r.enrollmentId} className="px-4 py-3">
              <Link
                href={`/courses/${r.courseId}`}
                className="font-medium text-zinc-950 underline"
              >
                {r.courseTitle}
              </Link>
              <p className="mt-1 text-sm text-zinc-500">
                Teacher id {r.teacherId} · enrolled {new Date(r.createdAt).toLocaleString()}
              </p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
