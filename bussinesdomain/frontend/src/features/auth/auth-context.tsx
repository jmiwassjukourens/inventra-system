"use client";

import * as React from "react";
import axios from "axios";
import { login as loginRequest, logoutRemote } from "@/lib/api/services/auth-api";
import { clearTokens, getAccessToken, setTokens } from "@/lib/auth/token-storage";

type AuthContextValue = {
  userLabel: string;
  refreshUser: () => Promise<void>;
  signIn: (username: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
};

const AuthContext = React.createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [userLabel, setUserLabel] = React.useState("Signed in");

  const refreshUser = React.useCallback(async () => {
    const token = getAccessToken();
    if (!token) return;
    try {
      const base = "/api/gw";
      const { data } = await axios.get<Record<string, unknown>>(
        `${base}/api/auth/userinfo`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const pre =
        (data.preferred_username as string) ||
        (data.name as string) ||
        (data.email as string);
      if (pre) setUserLabel(pre);
    } catch {
      setUserLabel("User");
    }
  }, []);

  React.useEffect(() => {
    void refreshUser();
  }, [refreshUser]);

  const signIn = React.useCallback(async (username: string, password: string) => {
    const data = await loginRequest(username, password);
    setTokens(
      data.access_token,
      data.refresh_token,
      data.expires_in ?? 3600
    );
    await refreshUser();
  }, [refreshUser]);

  const signOut = React.useCallback(async () => {
    try {
      await logoutRemote();
    } catch {
      /* ignore */
    }
    clearTokens();
    window.location.href = "/login";
  }, []);

  const value = React.useMemo(
    () => ({ userLabel, refreshUser, signIn, signOut }),
    [userLabel, refreshUser, signIn, signOut]
  );

  return (
    <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = React.useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
}
