"use client";

import { LogOut, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/features/auth/auth-context";

export function AppHeader({ title }: { title: string }) {
  const { userLabel, signOut } = useAuth();

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-slate-200/80 bg-white/80 px-8 backdrop-blur-md">
      <div>
        <h1 className="text-xl font-semibold tracking-tight text-slate-900">
          {title}
        </h1>
      </div>
      <div className="flex items-center gap-4">
        <div className="hidden items-center gap-2 rounded-full border border-slate-200 bg-slate-50 px-3 py-1.5 text-sm text-slate-600 sm:flex">
          <User className="h-4 w-4 text-blue-600" />
          <span className="max-w-[200px] truncate">{userLabel}</span>
        </div>
        <Button variant="outline" size="sm" onClick={() => void signOut()}>
          <LogOut className="h-4 w-4" />
          Log out
        </Button>
      </div>
    </header>
  );
}
