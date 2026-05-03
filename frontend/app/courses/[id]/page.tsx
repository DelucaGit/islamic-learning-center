"use client";

import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import { apiFetch, apiJson } from "@/lib/api";
import { getRoleFromStorage, getUserIdFromStorage } from "@/lib/auth";
import type { Course, EnrolledStudentResponse } from "@/lib/types";

export default function CourseDetailPage() {
  const params = useParams();
  const router = useRouter();
  const idParam = params.id;
  const courseId = typeof idParam === "string" ? Number(idParam) : NaN;

  const [course, setCourse] = useState<Course | null>(null);
  const [students, setStudents] = useState<EnrolledStudentResponse[] | null>(null);
  const [email, setEmail] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [enrolling, setEnrolling] = useState(false);

  const isOwner =
    course != null &&
    getRoleFromStorage() === "TEACHER" &&
    getUserIdFromStorage() === course.teacherId;

  const load = useCallback(async () => {
    await Promise.resolve();
    if (!Number.isFinite(courseId)) {
      setError("Invalid course id");
      setLoading(false);
      return;
    }
    setError(null);
    try {
      const c = await apiJson<Course>(`/courses/${courseId}`);
      setCourse(c);
      if (
        getRoleFromStorage() === "TEACHER" &&
        getUserIdFromStorage() === c.teacherId
      ) {
        const list = await apiJson<EnrolledStudentResponse[]>(
          `/courses/${courseId}/students`,
        );
        setStudents(list);
      } else {
        setStudents(null);
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load");
      setCourse(null);
      setStudents(null);
    } finally {
      setLoading(false);
    }
  }, [courseId]);

  useEffect(() => {
    void (async () => {
      await load();
    })();
  }, [load]);

  async function addStudent(e: React.FormEvent) {
    e.preventDefault();
    if (!Number.isFinite(courseId)) return;
    setEnrolling(true);
    setError(null);
    try {
      const res = await apiFetch(`/courses/${courseId}/students`, {
        method: "POST",
        body: JSON.stringify({ email: email.trim() }),
      });
      const body = (await res.json()) as { error?: string };
      if (!res.ok) throw new Error(body.error ?? res.statusText);
      setEmail("");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Enroll failed");
    } finally {
      setEnrolling(false);
    }
  }

  async function removeStudent(studentId: number) {
    if (!Number.isFinite(courseId)) return;
    if (!confirm("Remove this student from the course?")) return;
    setError(null);
    try {
      const res = await apiFetch(`/courses/${courseId}/students/${studentId}`, {
        method: "DELETE",
      });
      if (!res.ok) {
        const body = (await res.json()) as { error?: string };
        throw new Error(body.error ?? res.statusText);
      }
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Remove failed");
    }
  }

  async function deleteCourse() {
    if (!Number.isFinite(courseId)) return;
    if (!confirm("Delete this course and all enrollments?")) return;
    const res = await apiFetch(`/courses/${courseId}`, { method: "DELETE" });
    if (!res.ok) {
      const body = (await res.json()) as { error?: string };
      setError(body.error ?? res.statusText);
      return;
    }
    router.push("/courses");
    router.refresh();
  }

  if (loading) return <p className="text-zinc-500">Loading…</p>;
  if (!course) {
    return (
      <div className="space-y-4">
        {error && <p className="text-red-700">{error}</p>}
        <Link href="/courses" className="text-zinc-950 underline">
          Back to courses
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div>
        <Link href="/courses" className="text-sm text-zinc-600 underline">
          ← Courses
        </Link>
        <h1 className="mt-2 text-2xl font-semibold">{course.title}</h1>
        {course.description && (
          <p className="mt-2 whitespace-pre-wrap text-zinc-700">{course.description}</p>
        )}
        <p className="mt-2 text-sm text-zinc-500">
          Teacher id {course.teacherId} · {course.active ? "Active" : "Inactive"}
        </p>
      </div>

      {error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-800">{error}</p>}

      {isOwner && (
        <div className="space-y-4 rounded-lg border border-zinc-200 bg-white p-4">
          <h2 className="font-medium">Enrolled students</h2>
          <form onSubmit={(e) => void addStudent(e)} className="flex flex-wrap gap-2">
            <input
              type="email"
              required
              placeholder="student@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="min-w-[200px] flex-1 rounded-md border border-zinc-300 px-3 py-2 text-sm"
            />
            <button
              type="submit"
              disabled={enrolling}
              className="rounded-md bg-zinc-900 px-3 py-2 text-sm font-medium text-white disabled:opacity-50"
            >
              {enrolling ? "Adding…" : "Add student"}
            </button>
          </form>
          {students && students.length === 0 && (
            <p className="text-sm text-zinc-600">No students yet.</p>
          )}
          {students && students.length > 0 && (
            <ul className="divide-y divide-zinc-100 text-sm">
              {students.map((s) => (
                <li
                  key={s.enrollmentId}
                  className="flex flex-wrap items-center justify-between gap-2 py-2"
                >
                  <span>
                    {s.studentFullName}{" "}
                    <span className="text-zinc-500">({s.studentEmail})</span>
                  </span>
                  <button
                    type="button"
                    onClick={() => void removeStudent(s.studentId)}
                    className="text-red-700 underline"
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
          )}
          <div className="border-t border-zinc-100 pt-4">
            <button
              type="button"
              onClick={() => void deleteCourse()}
              className="text-sm text-red-800 underline"
            >
              Delete course
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
