import { apiClient } from "@/lib/api/client";
import type { Product } from "@/lib/api/types";

export async function fetchProducts() {
  const { data } = await apiClient.get<Product[]>("/api/products");
  return data;
}

export async function createProduct(body: Partial<Product>) {
  const { data } = await apiClient.post<Product>("/api/products", body);
  return data;
}

export async function updateProduct(id: number, body: Partial<Product>) {
  const { data } = await apiClient.put<Product>(`/api/products/${id}`, body);
  return data;
}

export async function deleteProduct(id: number) {
  await apiClient.delete(`/api/products/${id}`);
}
