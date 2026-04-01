"use client";

import * as React from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { motion } from "framer-motion";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { login as loginRequest } from "@/lib/api/services/auth-api";
import { setTokens } from "@/lib/auth/token-storage";
import { getApiErrorMessage } from "@/lib/api/client";

export function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const from = searchParams.get("from") || "/";

  React.useEffect(() => {
    if (
      typeof document !== "undefined" &&
      document.cookie.split(";").some((c) => c.trim() === "inventra_session=1")
    ) {
      router.replace(from === "/login" ? "/" : from);
    }
  }, [from, router]);

  const [username, setUsername] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [loading, setLoading] = React.useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await loginRequest(username, password);
      setTokens(
        data.access_token,
        data.refresh_token,
        data.expires_in ?? 3600
      );
      toast.success("Signed in");
      router.replace(from === "/login" ? "/" : from);
      router.refresh();
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35 }}
      className="w-full max-w-md"
    >
      <Card className="border-slate-200/80 shadow-xl shadow-blue-900/5">
        <CardHeader>
          <div className="mb-2 flex items-center gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 text-lg font-bold text-white">
              I
            </div>
            <div>
              <CardTitle className="text-2xl">Inventra</CardTitle>
              <p className="text-sm text-slate-500">
                Sign in with your Keycloak account
              </p>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <form onSubmit={onSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="user">Username</Label>
              <Input
                id="user"
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="pass">Password</Label>
              <Input
                id="pass"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Signing in…" : "Sign in"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </motion.div>
  );
}
