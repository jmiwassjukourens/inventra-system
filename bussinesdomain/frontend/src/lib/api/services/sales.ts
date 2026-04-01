import { apiClient } from "@/lib/api/client";
import type { Sale, SaleSummary, SpringPage } from "@/lib/api/types";

export type SaleCreate = {
  customerId: number | null;
  items: { productId: number; quantity: number; unitPrice: number }[];
};

export async function fetchSales(params?: {
  from?: string;
  to?: string;
  paymentStatus?: string;
  page?: number;
  size?: number;
}) {
  const { data } = await apiClient.get<SpringPage<SaleSummary>>("/api/sales", {
    params,
  });
  return data;
}

export async function fetchSalesByCustomer(customerId: number) {
  const { data } = await apiClient.get<SaleSummary[]>(
    `/api/sales/by-customer/${customerId}`
  );
  return data;
}

export async function createSale(body: SaleCreate) {
  const { data } = await apiClient.post<Sale>("/api/sales", body);
  return data;
}

export async function fetchSale(id: number) {
  const { data } = await apiClient.get<Sale>(`/api/sales/${id}`);
  return data;
}

export async function markSalePaid(id: number) {
  await apiClient.post(`/api/internal/sales/${id}/mark-paid`);
}
