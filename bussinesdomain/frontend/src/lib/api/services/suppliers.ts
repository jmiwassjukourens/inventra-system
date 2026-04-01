import { apiClient } from "@/lib/api/client";
import type { ProductSupplierReportRow, Supplier } from "@/lib/api/types";

export type SupplierProductLink = {
  supplierId: number;
  productId: number;
  purchasePrice: number;
  deliveryTimeDays?: number;
};

export async function fetchSuppliers() {
  const { data } = await apiClient.get<Supplier[]>("/api/suppliers");
  return data;
}

export async function createSupplier(body: Partial<Supplier>) {
  const { data } = await apiClient.post<Supplier>("/api/suppliers", body);
  return data;
}

export async function updateSupplier(id: number, body: Partial<Supplier>) {
  const { data } = await apiClient.put<Supplier>(`/api/suppliers/${id}`, body);
  return data;
}

export async function deleteSupplier(id: number) {
  await apiClient.delete(`/api/suppliers/${id}`);
}

export async function linkSupplierProduct(body: SupplierProductLink) {
  await apiClient.post("/api/suppliers/products", body);
}

export async function fetchSupplierReport() {
  const { data } = await apiClient.get<ProductSupplierReportRow[]>(
    "/api/suppliers/report/products"
  );
  return data;
}
