import axios from "axios";
import { apiClient } from "@/lib/api/client";
import type { TokenResponse } from "@/lib/api/types";
import { getRefreshToken } from "@/lib/auth/token-storage";

const base = typeof window === "undefined" ? "" : "/api/gw";

/** Credential login (no Bearer). */
export async function login(username: string, password: string) {
  const { data } = await axios.post<TokenResponse>(`${base}/api/auth/login`, {
    username,
    password,
  });
  return data;
}

/** Revoke session; uses current access token via apiClient when available. */
export async function logoutRemote() {
  const rt = typeof window !== "undefined" ? getRefreshToken() : null;
  await apiClient.post("/api/auth/logout", { refresh_token: rt ?? undefined });
}
