import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatMoney(n: number | string | null | undefined) {
  const v = Number(n ?? 0);
  return new Intl.NumberFormat(undefined, {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2,
  }).format(v);
}

export function toIsoDateTime(d: Date) {
  return d.toISOString().replace(/\.\d{3}Z$/, "");
}
