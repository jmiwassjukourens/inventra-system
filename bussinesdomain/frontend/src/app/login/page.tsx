import { Suspense } from "react";
import { LoginForm } from "./login-form";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-50 via-white to-blue-50 p-4">
      <Suspense fallback={<div className="h-96 w-full max-w-md animate-pulse rounded-xl bg-white/60" />}>
        <LoginForm />
      </Suspense>
    </div>
  );
}
