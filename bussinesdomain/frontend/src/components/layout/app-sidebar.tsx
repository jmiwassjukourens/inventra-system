"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion } from "framer-motion";
import {
  BarChart3,
  LayoutDashboard,
  Package,
  ShoppingBag,
  ShoppingCart,
  Truck,
  Users,
} from "lucide-react";
import { cn } from "@/lib/utils";

const nav = [
  { href: "/", label: "Overview", icon: LayoutDashboard },
  { href: "/products", label: "Products", icon: Package },
  { href: "/suppliers", label: "Suppliers", icon: Truck },
  { href: "/purchases", label: "Purchases", icon: ShoppingCart },
  { href: "/clients", label: "Clients", icon: Users },
  { href: "/sales", label: "Sales", icon: ShoppingBag },
  { href: "/reports", label: "Reports", icon: BarChart3 },
];

export function AppSidebar() {
  const pathname = usePathname();

  return (
    <aside className="fixed left-0 top-0 z-40 flex h-screen w-64 flex-col border-r border-slate-200/80 bg-white/90 backdrop-blur-md">
      <div className="flex h-16 items-center gap-2 border-b border-slate-100 px-6">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-gradient-to-br from-blue-600 to-blue-800 text-sm font-bold text-white shadow-md">
          I
        </div>
        <div>
          <p className="text-sm font-semibold text-slate-900">Inventra</p>
          <p className="text-xs text-slate-500">Management</p>
        </div>
      </div>
      <nav className="flex-1 space-y-1 p-4">
        {nav.map((item, i) => {
          const active =
            item.href === "/"
              ? pathname === "/"
              : pathname.startsWith(item.href);
          const Icon = item.icon;
          return (
            <motion.div
              key={item.href}
              initial={{ opacity: 0, x: -8 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.04 }}
            >
              <Link
                href={item.href}
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                  active
                    ? "bg-blue-50 text-blue-700 shadow-sm"
                    : "text-slate-600 hover:bg-slate-50"
                )}
              >
                <Icon className="h-4 w-4 shrink-0" />
                {item.label}
              </Link>
            </motion.div>
          );
        })}
      </nav>
      <div className="border-t border-slate-100 p-4 text-xs text-slate-400">
        API via gateway proxy
      </div>
    </aside>
  );
}
