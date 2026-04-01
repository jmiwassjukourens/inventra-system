const ACCESS = "inventra_access";
const REFRESH = "inventra_refresh";

export function setTokens(access: string, refresh: string | undefined, expiresIn: number) {
  if (typeof window === "undefined") return;
  localStorage.setItem(ACCESS, access);
  if (refresh) {
    localStorage.setItem(REFRESH, refresh);
  }
  const maxAge = Math.max(expiresIn || 3600, 300);
  document.cookie = `inventra_session=1; path=/; max-age=${maxAge}; SameSite=Lax`;
}

export function clearTokens() {
  if (typeof window === "undefined") return;
  localStorage.removeItem(ACCESS);
  localStorage.removeItem(REFRESH);
  document.cookie = "inventra_session=; path=/; max-age=0";
}

export function getAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(ACCESS);
}

export function getRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(REFRESH);
}
