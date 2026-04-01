export type SpringPage<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
};

export type TokenResponse = {
  access_token: string;
  expires_in: number;
  refresh_expires_in?: number;
  refresh_token?: string;
  token_type?: string;
  scope?: string;
};

export type Product = {
  id: number;
  name: string;
  description?: string;
  sku: string;
  price: number;
  categoryId?: number;
  active: boolean;
};

export type Supplier = {
  id: number;
  name: string;
  email?: string;
  phone?: string;
  address?: string;
  active: boolean;
};

export type ProductSupplierReportRow = {
  productId: number;
  productName?: string;
  productSku?: string;
  supplierId: number;
  supplierName?: string;
  purchasePrice?: number;
  deliveryTimeDays?: number;
};

export type PurchaseSummary = {
  id: number;
  supplierId: number;
  orderDate: string;
  status: string;
  totalAmount: number;
};

export type PurchaseItem = {
  productId: number;
  quantity: number;
  unitPrice: number;
};

export type Purchase = {
  id: number;
  supplierId: number;
  orderDate: string;
  status: string;
  totalAmount: number;
  items: PurchaseItem[];
};

export type Customer = {
  id: number;
  name: string;
  taxId?: string;
  email?: string;
  phone?: string;
  active?: boolean;
  createdAt?: string;
};

export type Account = {
  id: number;
  customerId: number;
  currentBalance: number;
};

export type SaleSummary = {
  id: number;
  customerId: number;
  saleDate: string;
  totalAmount: number;
  status: string;
  paymentStatus: string;
};

export type SaleItem = {
  productId: number;
  quantity: number;
  unitPrice: number;
};

export type Sale = {
  id: number;
  customerId: number;
  saleDate: string;
  totalAmount: number;
  status: string;
  paymentStatus: string;
  items: SaleItem[];
};

export type AccountMovement = {
  id: number;
  type: string;
  amount: number;
  occurredAt: string;
  referenceType?: string;
  referenceId?: number;
};

export type SaleSummaryDto = {
  id: number;
  customerId: number;
  saleDate: string;
  totalAmount: number;
  status?: string;
  paymentStatus?: string;
};

export type CustomerAccountOverview = {
  account: Account;
  recentSales: SaleSummaryDto[];
};
