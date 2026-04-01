"use client";

import * as React from "react";
import { toast } from "sonner";
import { Plus, Trash2, CheckCircle2 } from "lucide-react";
import { DashboardShell } from "../shell";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { fetchProducts } from "@/lib/api/services/catalog";
import { fetchCustomers } from "@/lib/api/services/customers";
import { fetchSupplierReport } from "@/lib/api/services/suppliers";
import {
  createSale,
  fetchSales,
  markSalePaid,
} from "@/lib/api/services/sales";
import { getApiErrorMessage } from "@/lib/api/client";
import type {
  Customer,
  Product,
  ProductSupplierReportRow,
  SaleItem,
  SaleSummary,
} from "@/lib/api/types";
import { formatMoney } from "@/lib/utils";

export default function SalesPage() {
  const [products, setProducts] = React.useState<Product[]>([]);
  const [customers, setCustomers] = React.useState<Customer[]>([]);
  const [report, setReport] = React.useState<ProductSupplierReportRow[]>([]);
  const [recent, setRecent] = React.useState<SaleSummary[]>([]);
  const [loading, setLoading] = React.useState(true);

  const [customerId, setCustomerId] = React.useState<string>("");
  const [cashSale, setCashSale] = React.useState(false);
  const [suggestFilter, setSuggestFilter] = React.useState("");
  const [lines, setLines] = React.useState<SaleItem[]>([
    { productId: 0, quantity: 1, unitPrice: 0 },
  ]);

  const purchasedIds = React.useMemo(
    () => new Set(report.map((r) => r.productId)),
    [report]
  );

  function sortedSuggestions(query: string): Product[] {
    const q = query.toLowerCase();
    const list = [...products].sort((a, b) => {
      const ap = purchasedIds.has(a.id) ? 0 : 1;
      const bp = purchasedIds.has(b.id) ? 0 : 1;
      if (ap !== bp) return ap - bp;
      return a.name.localeCompare(b.name);
    });
    if (!q) return list;
    return list.filter(
      (p) =>
        p.name.toLowerCase().includes(q) ||
        p.sku.toLowerCase().includes(q)
    );
  }

  async function load() {
    try {
      const [p, c, r, page] = await Promise.all([
        fetchProducts(),
        fetchCustomers(),
        fetchSupplierReport(),
        fetchSales({ size: 30, page: 0 }),
      ]);
      setProducts(p);
      setCustomers(c);
      setReport(r);
      setRecent(page.content);
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }

  React.useEffect(() => {
    void load();
  }, []);

  function addLine() {
    setLines([...lines, { productId: 0, quantity: 1, unitPrice: 0 }]);
  }

  function removeLine(i: number) {
    setLines(lines.filter((_, j) => j !== i));
  }

  function patchLine(i: number, patch: Partial<SaleItem>) {
    setLines(lines.map((l, j) => (j === i ? { ...l, ...patch } : l)));
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    const cid = cashSale ? null : Number(customerId || 0);
    if (!cashSale && !cid) {
      toast.error("Pick a client or mark as cash sale");
      return;
    }
    const items = lines
      .filter((l) => l.productId > 0)
      .map((l) => ({
        productId: l.productId,
        quantity: l.quantity,
        unitPrice: l.unitPrice,
      }));
    if (items.length === 0) {
      toast.error("Add at least one product");
      return;
    }
    try {
      await createSale({ customerId: cid, items });
      toast.success(
        cashSale
          ? "Cash sale completed (paid)"
          : "Sale registered (unpaid on account)"
      );
      setLines([{ productId: 0, quantity: 1, unitPrice: 0 }]);
      await load();
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    }
  }

  async function paySale(id: number) {
    try {
      await markSalePaid(id);
      toast.success("Marked paid");
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  return (
    <DashboardShell title="Sales">
      <div className="grid gap-8 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>New sale</CardTitle>
            <p className="text-sm text-slate-500">
              Products linked to suppliers surface first as purchase-backed
              suggestions; search narrows the list.
            </p>
          </CardHeader>
          <CardContent>
            <form onSubmit={onSubmit} className="space-y-4">
              <div className="flex flex-wrap items-center gap-4">
                <div className="flex items-center gap-2">
                  <input
                    id="cash"
                    type="checkbox"
                    checked={cashSale}
                    onChange={(e) => {
                      setCashSale(e.target.checked);
                      if (e.target.checked) setCustomerId("");
                    }}
                  />
                  <Label htmlFor="cash">Cash sale (no client · paid)</Label>
                </div>
                {!cashSale && (
                  <div className="min-w-[200px] flex-1 space-y-2">
                    <Label>Client</Label>
                    <Select value={customerId} onValueChange={setCustomerId}>
                      <SelectTrigger>
                        <SelectValue placeholder="Client account" />
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
                )}
              </div>

              <div className="space-y-2">
                <Label>Search / suggest products</Label>
                <Input
                  placeholder="Type to filter products…"
                  value={suggestFilter}
                  onChange={(e) => setSuggestFilter(e.target.value)}
                />
              </div>

              <div className="space-y-3">
                {lines.map((line, i) => (
                  <div
                    key={i}
                    className="flex flex-col gap-2 rounded-lg border border-slate-100 bg-slate-50/50 p-3"
                  >
                    <div className="flex flex-wrap gap-2">
                      <div className="min-w-[180px] flex-1 space-y-1">
                        <Label className="text-xs">Product</Label>
                        <Select
                          value={line.productId ? String(line.productId) : ""}
                          onValueChange={(v) => {
                            const pid = Number(v);
                            const pr = products.find((p) => p.id === pid);
                            patchLine(i, {
                              productId: pid,
                              unitPrice: pr ? Number(pr.price) : line.unitPrice,
                            });
                          }}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Choose product" />
                          </SelectTrigger>
                          <SelectContent className="max-h-72">
                            {sortedSuggestions(suggestFilter).map((p) => (
                              <SelectItem key={p.id} value={String(p.id)}>
                                {p.name}
                                {purchasedIds.has(p.id) ? " *" : ""} ·{" "}
                                {formatMoney(p.price)}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                      <div className="w-24 space-y-1">
                        <Label className="text-xs">Qty</Label>
                        <Input
                          type="number"
                          min={1}
                          value={line.quantity}
                          onChange={(e) =>
                            patchLine(i, {
                              quantity: Number(e.target.value),
                            })
                          }
                        />
                      </div>
                      <div className="w-28 space-y-1">
                        <Label className="text-xs">Unit</Label>
                        <Input
                          type="number"
                          step="0.01"
                          value={line.unitPrice}
                          onChange={(e) =>
                            patchLine(i, {
                              unitPrice: Number(e.target.value),
                            })
                          }
                        />
                      </div>
                      <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        className="self-end"
                        onClick={() => removeLine(i)}
                        disabled={lines.length <= 1}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                ))}
                <Button type="button" variant="secondary" onClick={addLine}>
                  <Plus className="h-4 w-4" />
                  Add line
                </Button>
              </div>

              <Button type="submit">Create sale</Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Recent sales</CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <Skeleton className="h-48" />
            ) : recent.length === 0 ? (
              <p className="text-sm text-slate-500">No sales yet.</p>
            ) : (
              <ul className="divide-y divide-slate-100">
                {recent.map((s) => (
                  <li
                    key={s.id}
                    className="flex flex-wrap items-center justify-between gap-2 py-3 text-sm"
                  >
                    <div>
                      <p className="font-medium">
                        #{s.id}{" "}
                        <span className="font-normal text-slate-500">
                          · client {s.customerId ?? "—"}
                        </span>
                      </p>
                      <p className="text-xs text-slate-500">
                        {new Date(s.saleDate).toLocaleString()}
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-semibold">
                        {formatMoney(s.totalAmount)}
                      </span>
                      <Badge
                        variant={
                          s.paymentStatus === "PAID" ? "success" : "warning"
                        }
                      >
                        {s.paymentStatus}
                      </Badge>
                      {s.paymentStatus !== "PAID" && s.customerId != null && (
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => void paySale(s.id)}
                        >
                          <CheckCircle2 className="h-4 w-4" />
                          Mark paid
                        </Button>
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardShell>
  );
}
