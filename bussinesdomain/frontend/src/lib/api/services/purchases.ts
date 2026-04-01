import { apiClient } from "@/lib/api/client";
import type { Purchase, PurchaseSummary, SpringPage } from "@/lib/api/types";

export type PurchaseCreate = {
  supplierId: number;
  items: { productId: number; quantity: number; unitPrice: number }[];
};

export async function fetchPurchases(params?: {
  from?: string;
  to?: string;
  page?: number;
  size?: number;
}) {
  const { data } = await apiClient.get<SpringPage<PurchaseSummary>>(
    "/api/purchases",
    { params }
  );
  return data;
}

export async function createPurchase(body: PurchaseCreate) {
  const { data } = await apiClient.post<Purchase>("/api/purchases", body);
  return data;
}

export async function fetchPurchase(id: number) {
  const { data } = await apiClient.get<Purchase>(`/api/purchases/${id}`);
  return data;
}
