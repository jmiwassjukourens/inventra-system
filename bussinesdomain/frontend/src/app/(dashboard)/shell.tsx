"use client";

import { motion } from "framer-motion";
import { AppHeader } from "@/components/layout/app-header";

export function DashboardShell({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <>
      <AppHeader title={title} />
      <motion.main
        className="flex-1 p-8"
        initial={{ opacity: 0, y: 6 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.25 }}
      >
        {children}
      </motion.main>
    </>
  );
}
