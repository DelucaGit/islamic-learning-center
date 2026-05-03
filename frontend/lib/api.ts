import { clearTokens, getAccessToken, getRefreshToken, setTokens } from "./auth";

const API_PREFIX = "/api/v1";

export type ApiErrorBody = { error?: string };

async function parseBody(res: Response): Promise<unknown> {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

async function tryRefresh(): Promise<boolean> {
  const refresh = getRefreshToken();
  if (!refresh) return false;
  try {
    const res = await fetch(`${API_PREFIX}/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      body: JSON.stringify({ refreshToken: refresh }),
    });
    if (!res.ok) return false;
    const data = (await res.json()) as { accessToken: string; refreshToken: string };
    setTokens(data.accessToken, data.refreshToken);
    return true;
  } catch {
    return false;
  }
}

/**
 * Fetch against same-origin `/api/v1` (proxied to Spring in dev).
 * @param path e.g. `/auth/login` or `auth/login`
 */
export async function apiFetch(
  path: string,
  init: RequestInit & { skipAuth?: boolean } = {},
  retried = false,
): Promise<Response> {
  const normalized = path.startsWith("/") ? path : `/${path}`;
  const url = `${API_PREFIX}${normalized}`;
  const headers = new Headers(init.headers);
  headers.set("Accept", "application/json");
  if (init.body != null && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (!init.skipAuth) {
    const token = getAccessToken();
    if (token) headers.set("Authorization", `Bearer ${token}`);
  }
  const res = await fetch(url, { ...init, headers });

  if (res.status === 401 && !init.skipAuth && !retried) {
    const ok = await tryRefresh();
    if (ok) return apiFetch(path, init, true);
    clearTokens();
    if (typeof window !== "undefined") {
      window.location.href = "/login";
    }
  }
  return res;
}

export async function apiJson<T>(
  path: string,
  init?: RequestInit & { skipAuth?: boolean },
): Promise<T> {
  const res = await apiFetch(path, init);
  const body = await parseBody(res);
  if (!res.ok) {
    const msg =
      body && typeof body === "object" && "error" in body
        ? String((body as ApiErrorBody).error ?? res.statusText)
        : res.statusText;
    throw new Error(msg);
  }
  return body as T;
}
