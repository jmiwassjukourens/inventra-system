"use client";

import * as React from "react";
import { Package, ShoppingBag, ShoppingCart, Truck, Users } from "lucide-react";
import { DashboardShell } from "./shell";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { fetchProducts } from "@/lib/api/services/catalog";
import { fetchSuppliers } from "@/lib/api/services/suppliers";
import { fetchPurchases } from "@/lib/api/services/purchases";
import { fetchSales } from "@/lib/api/services/sales";
import { fetchCustomers } from "@/lib/api/services/customers";
import { getApiErrorMessage } from "@/lib/api/client";
import { toast } from "sonner";

export default function HomePage() {
  const [loading, setLoading] = React.useState(true);
  const [stats, setStats] = React.useState({
    products: 0,
    suppliers: 0,
    purchases: 0,
    sales: 0,
    clients: 0,
  });

  React.useEffect(() => {
    let cancel = false;
    (async () => {
      try {
        const [products, suppliers, purPage, salPage, customers] =
          await Promise.all([
            fetchProducts(),
            fetchSuppliers(),
            fetchPurchases({ size: 1, page: 0 }),
            fetchSales({ size: 1, page: 0 }),
            fetchCustomers(),
          ]);
        if (cancel) return;
        setStats({
          products: products.length,
          suppliers: suppliers.length,
          purchases: purPage.totalElements,
          sales: salPage.totalElements,
          clients: customers.length,
        });
      } catch (e) {
        toast.error(getApiErrorMessage(e));
      } finally {
        if (!cancel) setLoading(false);
      }
    })();
    return () => {
      cancel = true;
    };
  }, []);

  const tiles = [
    {
      title: "Products",
      value: stats.products,
      icon: Package,
      color: "text-blue-600",
    },
    {
      title: "Suppliers",
      value: stats.suppliers,
      icon: Truck,
      color: "text-indigo-600",
    },
    {
      title: "Purchase orders",
      value: stats.purchases,
      icon: ShoppingCart,
      color: "text-cyan-600",
    },
    {
      title: "Sales",
      value: stats.sales,
      icon: ShoppingBag,
      color: "text-sky-600",
    },
    {
      title: "Clients",
      value: stats.clients,
      icon: Users,
      color: "text-blue-800",
    },
  ];

  return (
    <DashboardShell title="Overview">
      <p className="mb-8 max-w-2xl text-slate-600">
        Welcome to Inventra. Use the sidebar to manage catalog, supply chain,
        and accounts. All operations are executed through the API gateway with
        Keycloak-backed authentication.
      </p>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
        {loading
          ? Array.from({ length: 5 }).map((_, i) => (
              <Skeleton key={i} className="h-28 rounded-xl" />
            ))
          : tiles.map((t) => {
              const Icon = t.icon;
              return (
                <Card
                  key={t.title}
                  className="overflow-hidden border-slate-200/80 shadow-sm transition-shadow hover:shadow-md"
                >
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium text-slate-600">
                      {t.title}
                    </CardTitle>
                    <Icon className={`h-4 w-4 ${t.color}`} />
                  </CardHeader>
                  <CardContent>
                    <p className="text-3xl font-bold tabular-nums text-slate-900">
                      {t.value}
                    </p>
                  </CardContent>
                </Card>
              );
            })}
      </div>
    </DashboardShell>
  );
}
