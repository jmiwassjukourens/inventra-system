import { apiClient } from "@/lib/api/client";
import type {
  Account,
  AccountMovement,
  Customer,
  CustomerAccountOverview,
  SpringPage,
} from "@/lib/api/types";

export async function fetchCustomers() {
  const { data } = await apiClient.get<Customer[]>("/api/customers");
  return data;
}

export async function createCustomer(body: {
  name: string;
  taxId?: string;
  email?: string;
  phone?: string;
  active?: boolean;
}) {
  const { data } = await apiClient.post<Customer>("/api/customers", body);
  return data;
}

export async function updateCustomer(
  id: number,
  body: {
    name: string;
    taxId?: string;
    email?: string;
    phone?: string;
    active?: boolean;
  }
) {
  const { data } = await apiClient.put<Customer>(`/api/customers/${id}`, body);
  return data;
}

export async function deleteCustomer(id: number) {
  await apiClient.delete(`/api/customers/${id}`);
}

export async function getOrCreateAccount(customerId: number) {
  const { data } = await apiClient.get<Account>(
    `/api/customers/${customerId}/account`
  );
  return data;
}

export async function getAccountOverview(customerId: number) {
  const { data } = await apiClient.get<CustomerAccountOverview>(
    `/api/customers/${customerId}/account/overview`
  );
  return data;
}

export async function getStatement(
  customerId: number,
  params?: { from?: string; to?: string; page?: number; size?: number }
) {
  const { data } = await apiClient.get<SpringPage<AccountMovement>>(
    `/api/customers/${customerId}/account/statement`,
    { params }
  );
  return data;
}
