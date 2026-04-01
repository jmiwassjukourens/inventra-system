"use client";

import * as React from "react";
import { toast } from "sonner";
import { Plus, Trash2 } from "lucide-react";
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
import { fetchSuppliers } from "@/lib/api/services/suppliers";
import {
  createPurchase,
  fetchPurchases,
} from "@/lib/api/services/purchases";
import { getApiErrorMessage } from "@/lib/api/client";
import type { Product, PurchaseItem, PurchaseSummary, Supplier } from "@/lib/api/types";
import { formatMoney } from "@/lib/utils";

type Line = PurchaseItem;

export default function PurchasesPage() {
  const [suppliers, setSuppliers] = React.useState<Supplier[]>([]);
  const [products, setProducts] = React.useState<Product[]>([]);
  const [history, setHistory] = React.useState<PurchaseSummary[]>([]);
  const [loading, setLoading] = React.useState(true);

  const [supplierId, setSupplierId] = React.useState("");
  const [lines, setLines] = React.useState<Line[]>([
    { productId: 0, quantity: 1, unitPrice: 0 },
  ]);

  async function load() {
    try {
      const [s, p, page] = await Promise.all([
        fetchSuppliers(),
        fetchProducts(),
        fetchPurchases({ size: 50, page: 0 }),
      ]);
      setSuppliers(s);
      setProducts(p);
      setHistory(page.content);
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

  function patchLine(i: number, patch: Partial<Line>) {
    setLines(
      lines.map((l, j) => (j === i ? { ...l, ...patch } : l))
    );
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    const sid = Number(supplierId);
    const items = lines
      .filter((l) => l.productId > 0)
      .map((l) => ({
        productId: l.productId,
        quantity: l.quantity,
        unitPrice: l.unitPrice,
      }));
    if (!sid || items.length === 0) {
      toast.error("Select supplier and at least one product");
      return;
    }
    try {
      await createPurchase({ supplierId: sid, items });
      toast.success("Purchase recorded");
      setLines([{ productId: 0, quantity: 1, unitPrice: 0 }]);
      await load();
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    }
  }

  return (
    <DashboardShell title="Purchases">
      <div className="grid gap-8 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>New purchase order</CardTitle>
            <p className="text-sm text-slate-500">
              Add multiple catalog lines for one supplier.
            </p>
          </CardHeader>
          <CardContent>
            <form onSubmit={onSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label>Supplier</Label>
                <Select value={supplierId} onValueChange={setSupplierId} required>
                  <SelectTrigger>
                    <SelectValue placeholder="Choose supplier" />
                  </SelectTrigger>
                  <SelectContent>
                    {suppliers.map((s) => (
                      <SelectItem key={s.id} value={String(s.id)}>
                        {s.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-3">
                {lines.map((line, i) => (
                  <div
                    key={i}
                    className="flex flex-col gap-2 rounded-lg border border-slate-100 bg-slate-50/50 p-3 sm:flex-row sm:items-end"
                  >
                    <div className="flex-1 space-y-2">
                      <Label>Product</Label>
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
                          <SelectValue placeholder="Product" />
                        </SelectTrigger>
                        <SelectContent>
                          {products.map((p) => (
                            <SelectItem key={p.id} value={String(p.id)}>
                              {p.name} ({p.sku})
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="w-24 space-y-2">
                      <Label>Qty</Label>
                      <Input
                        type="number"
                        min={1}
                        value={line.quantity}
                        onChange={(e) =>
                          patchLine(i, { quantity: Number(e.target.value) })
                        }
                      />
                    </div>
                    <div className="w-32 space-y-2">
                      <Label>Unit $</Label>
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
                      onClick={() => removeLine(i)}
                      disabled={lines.length <= 1}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                ))}
                <Button type="button" variant="secondary" onClick={addLine}>
                  <Plus className="h-4 w-4" />
                  Add line
                </Button>
              </div>

              <Button type="submit">Submit purchase</Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Recent orders</CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <Skeleton className="h-48" />
            ) : history.length === 0 ? (
              <p className="text-sm text-slate-500">No purchases yet.</p>
            ) : (
              <ul className="divide-y divide-slate-100">
                {history.map((h) => (
                  <li key={h.id} className="flex justify-between py-3 text-sm">
                    <div>
                      <p className="font-medium">#{h.id}</p>
                      <p className="text-slate-500">
                        Supplier {h.supplierId} ·{" "}
                        {new Date(h.orderDate).toLocaleString()}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold">
                        {formatMoney(h.totalAmount)}
                      </p>
                      <Badge variant="outline">{h.status}</Badge>
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
