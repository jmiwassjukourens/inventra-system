import { AuthProvider } from "@/features/auth/auth-context";
import { AppSidebar } from "@/components/layout/app-sidebar";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthProvider>
      <div className="flex min-h-screen bg-slate-50">
        <AppSidebar />
        <div className="flex flex-1 flex-col pl-64">{children}</div>
      </div>
    </AuthProvider>
  );
}
