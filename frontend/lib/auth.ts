const ACCESS = "ilc_accessToken";
const REFRESH = "ilc_refreshToken";

export type JwtPayload = {
  sub?: string;
  email?: string;
  role?: string;
};

export function setTokens(accessToken: string, refreshToken: string): void {
  if (typeof window === "undefined") return;
  localStorage.setItem(ACCESS, accessToken);
  localStorage.setItem(REFRESH, refreshToken);
}

export function clearTokens(): void {
  if (typeof window === "undefined") return;
  localStorage.removeItem(ACCESS);
  localStorage.removeItem(REFRESH);
}

export function getAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(ACCESS);
}

export function getRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(REFRESH);
}

export function decodeJwtPayload(token: string): JwtPayload | null {
  try {
    const parts = token.split(".");
    if (parts.length < 2) return null;
    const base64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const json = atob(base64);
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

export function getRoleFromStorage(): string | null {
  const t = getAccessToken();
  if (!t) return null;
  return decodeJwtPayload(t)?.role ?? null;
}

export function getUserIdFromStorage(): number | null {
  const t = getAccessToken();
  if (!t) return null;
  const sub = decodeJwtPayload(t)?.sub;
  if (sub == null) return null;
  const n = Number(sub);
  return Number.isFinite(n) ? n : null;
}

export function isLoggedIn(): boolean {
  return getAccessToken() != null;
}
