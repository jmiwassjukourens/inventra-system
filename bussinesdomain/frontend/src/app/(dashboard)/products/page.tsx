"use client";

import * as React from "react";
import { toast } from "sonner";
import { Plus, Pencil, Trash2, Link2 } from "lucide-react";
import { DashboardShell } from "../shell";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  createProduct,
  deleteProduct,
  fetchProducts,
  updateProduct,
} from "@/lib/api/services/catalog";
import {
  createSupplier,
  fetchSuppliers,
  linkSupplierProduct,
} from "@/lib/api/services/suppliers";
import { getApiErrorMessage } from "@/lib/api/client";
import type { Product, Supplier } from "@/lib/api/types";

export default function ProductsPage() {
  const [rows, setRows] = React.useState<Product[]>([]);
  const [suppliers, setSuppliers] = React.useState<Supplier[]>([]);
  const [loading, setLoading] = React.useState(true);

  const [name, setName] = React.useState("");
  const [sku, setSku] = React.useState("");
  const [description, setDescription] = React.useState("");
  const [price, setPrice] = React.useState("");
  const [active, setActive] = React.useState(true);
  const [supplierId, setSupplierId] = React.useState<string>("");
  const [linkPrice, setLinkPrice] = React.useState("");
  const [deliveryDays, setDeliveryDays] = React.useState("7");

  const [editing, setEditing] = React.useState<Product | null>(null);
  const [supplierOpen, setSupplierOpen] = React.useState(false);
  const [nsName, setNsName] = React.useState("");
  const [nsEmail, setNsEmail] = React.useState("");
  const [nsPhone, setNsPhone] = React.useState("");
  const [nsAddress, setNsAddress] = React.useState("");

  async function load() {
    try {
      const [p, s] = await Promise.all([fetchProducts(), fetchSuppliers()]);
      setRows(p);
      setSuppliers(s);
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    } finally {
      setLoading(false);
    }
  }

  React.useEffect(() => {
    void load();
  }, []);

  async function onCreate(e: React.FormEvent) {
    e.preventDefault();
    try {
      const created = await createProduct({
        name,
        sku,
        description,
        price: Number(price),
        active,
      });
      if (supplierId) {
        await linkSupplierProduct({
          supplierId: Number(supplierId),
          productId: created.id,
          purchasePrice: Number(linkPrice || price),
          deliveryTimeDays: Number(deliveryDays || 7),
        });
      }
      toast.success("Product created");
      setName("");
      setSku("");
      setDescription("");
      setPrice("");
      setSupplierId("");
      setLinkPrice("");
      await load();
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    }
  }

  async function saveEdit(e: React.FormEvent) {
    e.preventDefault();
    if (!editing) return;
    try {
      await updateProduct(editing.id, {
        name: editing.name,
        sku: editing.sku,
        description: editing.description,
        price: Number(editing.price),
        active: editing.active,
      });
      toast.success("Product updated");
      setEditing(null);
      await load();
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    }
  }

  async function onDelete(id: number) {
    if (!confirm("Delete this product?")) return;
    try {
      await deleteProduct(id);
      toast.success("Deleted");
      await load();
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    }
  }

  async function createSupplierFromModal(e: React.FormEvent) {
    e.preventDefault();
    try {
      const s = await createSupplier({
        name: nsName,
        email: nsEmail,
        phone: nsPhone,
        address: nsAddress,
        active: true,
      });
      toast.success("Supplier created");
      setSupplierOpen(false);
      setNsName("");
      setNsEmail("");
      setNsPhone("");
      setNsAddress("");
      await load();
      setSupplierId(String(s.id));
    } catch (err) {
      toast.error(getApiErrorMessage(err));
    }
  }

  return (
    <DashboardShell title="Products">
      <div className="grid gap-8 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>New product</CardTitle>
            <p className="text-sm text-slate-500">
              Optionally link a supplier — open the modal to register one
              inline.
            </p>
          </CardHeader>
          <CardContent>
            <form onSubmit={onCreate} className="space-y-4">
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-2">
                  <Label>Name</Label>
                  <Input value={name} onChange={(e) => setName(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label>SKU</Label>
                  <Input value={sku} onChange={(e) => setSku(e.target.value)} required />
                </div>
              </div>
              <div className="space-y-2">
                <Label>Description</Label>
                <Textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </div>
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-2">
                  <Label>Price</Label>
                  <Input
                    type="number"
                    step="0.01"
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                    required
                  />
                </div>
                <div className="flex items-center gap-2 pt-8">
                  <input
                    id="act"
                    type="checkbox"
                    checked={active}
                    onChange={(e) => setActive(e.target.checked)}
                  />
                  <Label htmlFor="act">Active</Label>
                </div>
              </div>

              <div className="rounded-lg border border-dashed border-blue-200 bg-blue-50/50 p-4">
                <div className="mb-3 flex flex-wrap items-center justify-between gap-2">
                  <p className="text-sm font-medium text-blue-900">
                    Supplier association
                  </p>
                  <Button
                    type="button"
                    variant="secondary"
                    size="sm"
                    onClick={() => setSupplierOpen(true)}
                  >
                    <Plus className="h-4 w-4" />
                    New supplier
                  </Button>
                </div>
                <div className="grid gap-3 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label>Supplier</Label>
                    <Select
                      value={supplierId || undefined}
                      onValueChange={setSupplierId}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Optional" />
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
                  <div className="space-y-2">
                    <Label>Purchase price (link)</Label>
                    <Input
                      type="number"
                      step="0.01"
                      value={linkPrice}
                      onChange={(e) => setLinkPrice(e.target.value)}
                      placeholder="Defaults to sale price"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Delivery days</Label>
                    <Input
                      type="number"
                      value={deliveryDays}
                      onChange={(e) => setDeliveryDays(e.target.value)}
                    />
                  </div>
                </div>
              </div>

              <Button type="submit">
                <Link2 className="h-4 w-4" />
                Create & link
              </Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Catalog</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {loading ? (
              <Skeleton className="h-40 w-full" />
            ) : rows.length === 0 ? (
              <p className="text-sm text-slate-500">No products yet.</p>
            ) : (
              <ul className="divide-y divide-slate-100">
                {rows.map((p) => (
                  <li
                    key={p.id}
                    className="flex flex-wrap items-center justify-between gap-2 py-3"
                  >
                    <div>
                      <p className="font-medium">{p.name}</p>
                      <p className="text-xs text-slate-500">
                        {p.sku} · ${Number(p.price).toFixed(2)}
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant={p.active ? "success" : "outline"}>
                        {p.active ? "active" : "inactive"}
                      </Badge>
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => setEditing({ ...p })}
                      >
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="ghost"
                        className="text-red-600"
                        onClick={() => void onDelete(p.id)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>
      </div>

      <Dialog open={!!editing} onOpenChange={() => setEditing(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit product</DialogTitle>
          </DialogHeader>
          {editing && (
            <form onSubmit={saveEdit} className="space-y-3">
              <div className="space-y-2">
                <Label>Name</Label>
                <Input
                  value={editing.name}
                  onChange={(e) =>
                    setEditing({ ...editing, name: e.target.value })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label>SKU</Label>
                <Input
                  value={editing.sku}
                  onChange={(e) =>
                    setEditing({ ...editing, sku: e.target.value })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label>Price</Label>
                <Input
                  type="number"
                  step="0.01"
                  value={editing.price}
                  onChange={(e) =>
                    setEditing({
                      ...editing,
                      price: Number(e.target.value),
                    })
                  }
                />
              </div>
              <Button type="submit">Save</Button>
            </form>
          )}
        </DialogContent>
      </Dialog>

      <Dialog open={supplierOpen} onOpenChange={setSupplierOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>New supplier</DialogTitle>
          </DialogHeader>
          <form onSubmit={createSupplierFromModal} className="space-y-3">
            <div className="space-y-2">
              <Label>Name</Label>
              <Input
                value={nsName}
                onChange={(e) => setNsName(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>Email</Label>
              <Input
                type="email"
                value={nsEmail}
                onChange={(e) => setNsEmail(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label>Phone</Label>
              <Input value={nsPhone} onChange={(e) => setNsPhone(e.target.value)} />
            </div>
            <div className="space-y-2">
              <Label>Address</Label>
              <Input
                value={nsAddress}
                onChange={(e) => setNsAddress(e.target.value)}
              />
            </div>
            <Button type="submit">Create supplier</Button>
          </form>
        </DialogContent>
      </Dialog>
    </DashboardShell>
  );
}
