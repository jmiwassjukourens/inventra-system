"use client";

import * as React from "react";
import { Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { toast } from "sonner";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { DashboardShell } from "../shell";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { fetchProducts } from "@/lib/api/services/catalog";
import { fetchSuppliers } from "@/lib/api/services/suppliers";
import { fetchPurchases } from "@/lib/api/services/purchases";
import { fetchSales } from "@/lib/api/services/sales";
import { fetchCustomers } from "@/lib/api/services/customers";
import { fetchSupplierReport } from "@/lib/api/services/suppliers";
import {
  getAccountOverview,
  getStatement,
} from "@/lib/api/services/customers";
import { getApiErrorMessage } from "@/lib/api/client";
import type {
  Customer,
  Product,
  PurchaseSummary,
  SaleSummary,
  Supplier,
} from "@/lib/api/types";
import { formatMoney } from "@/lib/utils";

function dateStart(d: string) {
  return d ? `${d}T00:00:00` : undefined;
}
function dateEnd(d: string) {
  return d ? `${d}T23:59:59` : undefined;
}

function dayKey(iso: string) {
  return iso.slice(0, 10);
}

function ReportsInner() {
  const searchParams = useSearchParams();
  const initialClient = searchParams.get("client") || "";

  const [loading, setLoading] = React.useState(true);
  const [suppliers, setSuppliers] = React.useState<Supplier[]>([]);
  const [products, setProducts] = React.useState<Product[]>([]);
  const [purchaseRows, setPurchaseRows] = React.useState<PurchaseSummary[]>(
    []
  );
  const [saleRows, setSaleRows] = React.useState<SaleSummary[]>([]);
  const [spReport, setSpReport] = React.useState<Awaited<
    ReturnType<typeof fetchSupplierReport>
  > >([]);
  const [customers, setCustomers] = React.useState<Customer[]>([]);

  const [purFrom, setPurFrom] = React.useState("");
  const [purTo, setPurTo] = React.useState("");
  const [salFrom, setSalFrom] = React.useState("");
  const [salTo, setSalTo] = React.useState("");
  const [payFilter, setPayFilter] = React.useState<string>("");

  const [accountClient, setAccountClient] = React.useState(initialClient);
  const [overview, setOverview] = React.useState<Awaited<
    ReturnType<typeof getAccountOverview>
  > | null>(null);
  const [stmtLoading, setStmtLoading] = React.useState(false);
  const skipFilterRefetch = React.useRef(true);

  const tab = searchParams.get("tab") || "overview";

  async function loadCore() {
    try {
      const [s, p, c, rep] = await Promise.all([
        fetchSuppliers(),
        fetchProducts(),
        fetchCustomers(),
        fetchSupplierReport(),
      ]);
      setSuppliers(s);
      setProducts(p);
      setCustomers(c);
      setSpReport(rep);
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  async function loadPurchases() {
    try {
      const page = await fetchPurchases({
        from: dateStart(purFrom),
        to: dateEnd(purTo),
        size: 500,
        page: 0,
      });
      setPurchaseRows(page.content);
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  async function loadSales() {
    try {
      const page = await fetchSales({
        from: dateStart(salFrom),
        to: dateEnd(salTo),
        paymentStatus: payFilter || undefined,
        size: 500,
        page: 0,
      });
      setSaleRows(page.content);
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  React.useEffect(() => {
    void (async () => {
      setLoading(true);
      await loadCore();
      await loadPurchases();
      await loadSales();
      setLoading(false);
      skipFilterRefetch.current = false;
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps -- initial bootstrap only
  }, []);

  React.useEffect(() => {
    if (skipFilterRefetch.current) return;
    void loadPurchases();
    // eslint-disable-next-line react-hooks/exhaustive-deps -- filter-driven refetch
  }, [purFrom, purTo]);

  React.useEffect(() => {
    if (skipFilterRefetch.current) return;
    void loadSales();
    // eslint-disable-next-line react-hooks/exhaustive-deps -- filter-driven refetch
  }, [salFrom, salTo, payFilter]);

  React.useEffect(() => {
    if (!accountClient) {
      setOverview(null);
      return;
    }
    void (async () => {
      setStmtLoading(true);
      try {
        setOverview(await getAccountOverview(Number(accountClient)));
      } catch (e) {
        toast.error(getApiErrorMessage(e));
        setOverview(null);
      } finally {
        setStmtLoading(false);
      }
    })();
  }, [accountClient]);

  const purchaseTime = React.useMemo(() => {
    const map = new Map<string, number>();
    for (const r of purchaseRows) {
      const k = dayKey(r.orderDate);
      map.set(k, (map.get(k) ?? 0) + Number(r.totalAmount));
    }
    return Array.from(map.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([date, total]) => ({ date, total: Math.round(total * 100) / 100 }));
  }, [purchaseRows]);

  const salesTime = React.useMemo(() => {
    const map = new Map<string, number>();
    for (const r of saleRows) {
      const k = dayKey(r.saleDate);
      map.set(k, (map.get(k) ?? 0) + Number(r.totalAmount));
    }
    return Array.from(map.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([date, total]) => ({ date, total: Math.round(total * 100) / 100 }));
  }, [saleRows]);

  const purchasesBySupplier = React.useMemo(() => {
    const map = new Map<number, number>();
    for (const r of purchaseRows) {
      map.set(r.supplierId, (map.get(r.supplierId) ?? 0) + Number(r.totalAmount));
    }
    return Array.from(map.entries()).map(([id, total]) => {
      const name = suppliers.find((s) => s.id === id)?.name ?? `Supplier ${id}`;
      return { name, total: Math.round(total * 100) / 100 };
    });
  }, [purchaseRows, suppliers]);

  const salesByClient = React.useMemo(() => {
    const map = new Map<number, number>();
    for (const r of saleRows) {
      if (r.customerId == null) continue;
      map.set(
        r.customerId,
        (map.get(r.customerId) ?? 0) + Number(r.totalAmount)
      );
    }
    return Array.from(map.entries()).map(([id, total]) => {
      const name = customers.find((c) => c.id === id)?.name ?? `Client ${id}`;
      return { name, total: Math.round(total * 100) / 100 };
    });
  }, [saleRows, customers]);

  const supplierStats = React.useMemo(() => {
    const active = suppliers.filter((s) => s.active).length;
    return [
      { name: "Active", count: active },
      { name: "Inactive", count: suppliers.length - active },
    ];
  }, [suppliers]);

  const productStats = React.useMemo(() => {
    const active = products.filter((p) => p.active).length;
    return [
      { name: "Active SKUs", count: active },
      { name: "Inactive", count: products.length - active },
    ];
  }, [products]);

  if (loading) {
    return (
      <DashboardShell title="Reports">
        <Skeleton className="h-[400px] w-full rounded-xl" />
      </DashboardShell>
    );
  }

  return (
    <DashboardShell title="Reports">
      <Tabs defaultValue={tab === "account" ? "account" : "overview"} className="space-y-6">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="purchases">Purchases</TabsTrigger>
          <TabsTrigger value="sales">Sales</TabsTrigger>
          <TabsTrigger value="matrix">Supplier × Product</TabsTrigger>
          <TabsTrigger value="account">Client account</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Suppliers</CardTitle>
              </CardHeader>
              <CardContent className="h-72">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={supplierStats}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 12 }} />
                    <Tooltip />
                    <Bar dataKey="count" fill="#2563eb" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle>Products</CardTitle>
              </CardHeader>
              <CardContent className="h-72">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={productStats}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 12 }} />
                    <Tooltip />
                    <Bar dataKey="count" fill="#0891b2" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="purchases" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Filters</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-4">
              <div className="space-y-1">
                <Label>From</Label>
                <Input
                  type="date"
                  value={purFrom}
                  onChange={(e) => setPurFrom(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <Label>To</Label>
                <Input
                  type="date"
                  value={purTo}
                  onChange={(e) => setPurTo(e.target.value)}
                />
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Purchases over time</CardTitle>
            </CardHeader>
            <CardContent className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={purchaseTime}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="total"
                    stroke="#2563eb"
                    strokeWidth={2}
                    dot={false}
                    name="Amount"
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Purchases by supplier</CardTitle>
            </CardHeader>
            <CardContent className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={purchasesBySupplier}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis dataKey="name" tick={{ fontSize: 10 }} interval={0} angle={-20} height={60} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip />
                  <Bar dataKey="total" fill="#1d4ed8" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="sales" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Filters</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-wrap gap-4">
              <div className="space-y-1">
                <Label>From</Label>
                <Input
                  type="date"
                  value={salFrom}
                  onChange={(e) => setSalFrom(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <Label>To</Label>
                <Input
                  type="date"
                  value={salTo}
                  onChange={(e) => setSalTo(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <Label>Payment</Label>
                <Select
                  value={payFilter || "ALL"}
                  onValueChange={(v) => setPayFilter(v === "ALL" ? "" : v)}
                >
                  <SelectTrigger className="w-[160px]">
                    <SelectValue placeholder="All" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">All</SelectItem>
                    <SelectItem value="PAID">Paid</SelectItem>
                    <SelectItem value="UNPAID">Unpaid</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Sales over time</CardTitle>
            </CardHeader>
            <CardContent className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={salesTime}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip />
                  <Line
                    type="monotone"
                    dataKey="total"
                    stroke="#0ea5e9"
                    strokeWidth={2}
                    dot={false}
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Sales by client</CardTitle>
            </CardHeader>
            <CardContent className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={salesByClient}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis dataKey="name" tick={{ fontSize: 10 }} interval={0} angle={-20} height={60} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip />
                  <Bar dataKey="total" fill="#0369a1" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="matrix">
          <Card>
            <CardHeader>
              <CardTitle>Purchases by supplier / product</CardTitle>
              <p className="text-sm text-slate-500">
                Enriched rows from the supplier service (catalog snapshot).
              </p>
            </CardHeader>
            <CardContent className="max-h-[480px] overflow-auto">
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="border-b text-slate-500">
                    <th className="py-2">Product</th>
                    <th className="py-2">Supplier</th>
                    <th className="py-2 text-right">Purchase price</th>
                    <th className="py-2 text-right">Lead time (d)</th>
                  </tr>
                </thead>
                <tbody>
                  {spReport.map((r, i) => (
                    <tr key={i} className="border-b border-slate-50">
                      <td className="py-2">
                        {r.productName}{" "}
                        <span className="text-slate-400">{r.productSku}</span>
                      </td>
                      <td className="py-2">{r.supplierName}</td>
                      <td className="py-2 text-right">
                        {formatMoney(r.purchasePrice)}
                      </td>
                      <td className="py-2 text-right">
                        {r.deliveryTimeDays ?? "—"}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="account" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Current account details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="max-w-xs space-y-2">
                <Label>Client</Label>
                <Select
                  value={accountClient || undefined}
                  onValueChange={setAccountClient}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Choose client" />
                  </SelectTrigger>
                  <SelectContent>
                    {customers.map((c) => (
                      <SelectItem key={c.id} value={String(c.id)}>
                        {c.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              {stmtLoading ? (
                <Skeleton className="h-24 w-full" />
              ) : overview ? (
                <div className="grid gap-4 md:grid-cols-2">
                  <div className="rounded-xl border border-blue-100 bg-blue-50/60 p-4">
                    <p className="text-sm text-blue-900">Outstanding balance</p>
                    <p className="text-3xl font-bold text-blue-950">
                      {formatMoney(overview.account.currentBalance)}
                    </p>
                    <Badge variant="outline" className="mt-2">
                      Account #{overview.account.id}
                    </Badge>
                  </div>
                  <div>
                    <p className="mb-2 text-sm font-medium text-slate-700">
                      Recent sales
                    </p>
                    <ul className="space-y-2 text-sm">
                      {overview.recentSales?.length ? (
                        overview.recentSales.map((s) => (
                          <li
                            key={s.id}
                            className="flex justify-between rounded-md border border-slate-100 bg-white px-3 py-2"
                          >
                            <span>#{s.id}</span>
                            <span className="text-slate-500">
                              {formatMoney(s.totalAmount)}
                            </span>
                            <Badge
                              variant={
                                s.paymentStatus === "PAID" ? "success" : "warning"
                              }
                            >
                              {s.paymentStatus}
                            </Badge>
                          </li>
                        ))
                      ) : (
                        <li className="text-slate-500">No sales linked.</li>
                      )}
                    </ul>
                  </div>
                </div>
              ) : (
                <p className="text-sm text-slate-500">Select a client.</p>
              )}
            </CardContent>
          </Card>
          {accountClient ? (
            <StatementSection customerId={Number(accountClient)} />
          ) : null}
        </TabsContent>
      </Tabs>
    </DashboardShell>
  );
}

function StatementSection({ customerId }: { customerId: number }) {
  const [rows, setRows] = React.useState<
    Awaited<ReturnType<typeof getStatement>>["content"]
  >([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    void (async () => {
      setLoading(true);
      try {
        const p = await getStatement(customerId, { size: 50, page: 0 });
        setRows(p.content);
      } catch (e) {
        toast.error(getApiErrorMessage(e));
      } finally {
        setLoading(false);
      }
    })();
  }, [customerId]);

  if (loading) return <Skeleton className="h-48" />;

  return (
    <Card>
      <CardHeader>
        <CardTitle>Movement statement</CardTitle>
      </CardHeader>
      <CardContent className="max-h-80 overflow-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b text-slate-500">
              <th className="py-2">When</th>
              <th className="py-2">Type</th>
              <th className="py-2 text-right">Amount</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((m) => (
              <tr key={m.id} className="border-b border-slate-50">
                <td className="py-2">
                  {new Date(m.occurredAt).toLocaleString()}
                </td>
                <td className="py-2">
                  {m.type}{" "}
                  <span className="text-xs text-slate-400">
                    {m.referenceType}
                  </span>
                </td>
                <td className="py-2 text-right">{formatMoney(m.amount)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </CardContent>
    </Card>
  );
}

export default function ReportsPage() {
  return (
    <Suspense
      fallback={
        <DashboardShell title="Reports">
          <Skeleton className="h-96 w-full rounded-xl" />
        </DashboardShell>
      }
    >
      <ReportsInner />
    </Suspense>
  );
}
