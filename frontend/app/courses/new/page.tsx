"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { getRoleFromStorage } from "@/lib/auth";
import type { Course } from "@/lib/types";

export default function NewCoursePage() {
  const router = useRouter();
  const [roleOk, setRoleOk] = useState<boolean | null>(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [active, setActive] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    void Promise.resolve().then(() => {
      setRoleOk(getRoleFromStorage() === "TEACHER");
    });
  }, []);

  if (roleOk === null) {
    return <p className="text-zinc-500">Loading…</p>;
  }
  if (!roleOk) {
    return (
      <div className="space-y-4">
        <p className="text-red-700">Only teachers can create courses.</p>
        <Link href="/courses" className="text-zinc-950 underline">
          Back to courses
        </Link>
      </div>
    );
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await apiFetch("/courses", {
        method: "POST",
        body: JSON.stringify({
          title,
          description: description.trim() === "" ? null : description,
          active,
        }),
      });
      const body = (await res.json()) as { error?: string } & Partial<Course>;
      if (!res.ok) throw new Error(body.error ?? res.statusText);
      if (body.id == null) throw new Error("Invalid response");
      router.push(`/courses/${body.id}`);
      router.refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create course");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mx-auto max-w-lg space-y-6">
      <h1 className="text-xl font-semibold">New course</h1>
      <form onSubmit={(e) => void onSubmit(e)} className="space-y-4">
        <div>
          <label htmlFor="title" className="mb-1 block text-sm font-medium">
            Title
          </label>
          <input
            id="title"
            required
            maxLength={500}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full rounded-md border border-zinc-300 px-3 py-2"
          />
        </div>
        <div>
          <label htmlFor="description" className="mb-1 block text-sm font-medium">
            Description (optional)
          </label>
          <textarea
            id="description"
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full rounded-md border border-zinc-300 px-3 py-2"
          />
        </div>
        <label className="flex items-center gap-2 text-sm">
          <input
            type="checkbox"
            checked={active}
            onChange={(e) => setActive(e.target.checked)}
          />
          Active (listed in public catalog)
        </label>
        {error && (
          <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-800">{error}</p>
        )}
        <div className="flex gap-3">
          <button
            type="submit"
            disabled={loading}
            className="rounded-md bg-zinc-900 px-4 py-2 font-medium text-white hover:bg-zinc-800 disabled:opacity-50"
          >
            {loading ? "Saving…" : "Create"}
          </button>
          <Link
            href="/courses"
            className="rounded-md border border-zinc-300 px-4 py-2 hover:bg-white"
          >
            Cancel
          </Link>
        </div>
      </form>
    </div>
  );
}
