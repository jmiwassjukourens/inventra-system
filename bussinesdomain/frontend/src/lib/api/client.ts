import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios";
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  setTokens,
} from "@/lib/auth/token-storage";

const baseURL =
  typeof window === "undefined"
    ? process.env.API_GATEWAY_URL || "http://localhost:8080"
    : "/api/gw";

export const apiClient = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshing = false;
let queue: Array<(token: string | null) => void> = [];

apiClient.interceptors.response.use(
  (r) => r,
  async (error: AxiosError) => {
    const original = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };
    if (!original || original._retry) {
      if (error.response?.status === 401 && typeof window !== "undefined") {
        clearTokens();
        window.location.href = "/login";
      }
      return Promise.reject(error);
    }

    if (error.response?.status !== 401) {
      return Promise.reject(error);
    }

    const rt = getRefreshToken();
    if (!rt) {
      clearTokens();
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
      return Promise.reject(error);
    }

    original._retry = true;

    if (refreshing) {
      return new Promise((resolve, reject) => {
        queue.push((token) => {
          if (!token) {
            reject(error);
            return;
          }
          original.headers.Authorization = `Bearer ${token}`;
          resolve(apiClient(original));
        });
      });
    }

    refreshing = true;
    try {
      const { data } = await axios.post<{
        access_token: string;
        refresh_token?: string;
        expires_in?: number;
      }>(`${baseURL}/api/auth/refresh`, { refresh_token: rt });

      setTokens(
        data.access_token,
        data.refresh_token ?? rt,
        data.expires_in ?? 3600
      );
      queue.forEach((cb) => cb(data.access_token));
      queue = [];
      original.headers.Authorization = `Bearer ${data.access_token}`;
      return apiClient(original);
    } catch (e) {
      queue.forEach((cb) => cb(null));
      queue = [];
      clearTokens();
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
      return Promise.reject(e);
    } finally {
      refreshing = false;
    }
  }
);

export function getApiErrorMessage(err: unknown): string {
  if (axios.isAxiosError(err)) {
    const d = err.response?.data as { message?: string } | undefined;
    if (d?.message) return String(d.message);
    if (err.message) return err.message;
  }
  if (err instanceof Error) return err.message;
  return "Unexpected error";
}
