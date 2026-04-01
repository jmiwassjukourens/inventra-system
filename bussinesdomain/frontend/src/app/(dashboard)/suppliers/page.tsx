"use client";

import * as React from "react";
import { toast } from "sonner";
import { Pencil, Trash2 } from "lucide-react";
import { DashboardShell } from "../shell";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  createSupplier,
  deleteSupplier,
  fetchSuppliers,
  updateSupplier,
} from "@/lib/api/services/suppliers";
import { getApiErrorMessage } from "@/lib/api/client";
import type { Supplier } from "@/lib/api/types";

export default function SuppliersPage() {
  const [rows, setRows] = React.useState<Supplier[]>([]);
  const [loading, setLoading] = React.useState(true);
  const [editing, setEditing] = React.useState<Supplier | null>(null);

  const [name, setName] = React.useState("");
  const [email, setEmail] = React.useState("");
  const [phone, setPhone] = React.useState("");
  const [address, setAddress] = React.useState("");
  const [active, setActive] = React.useState(true);

  async function load() {
    try {
      setRows(await fetchSuppliers());
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
      await createSupplier({ name, email, phone, address, active });
      toast.success("Supplier created");
      setName("");
      setEmail("");
      setPhone("");
      setAddress("");
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  async function saveEdit(e: React.FormEvent) {
    e.preventDefault();
    if (!editing) return;
    try {
      await updateSupplier(editing.id, {
        name: editing.name,
        email: editing.email,
        phone: editing.phone,
        address: editing.address,
        active: editing.active,
      });
      toast.success("Saved");
      setEditing(null);
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  async function onDelete(id: number) {
    if (!confirm("Delete supplier?")) return;
    try {
      await deleteSupplier(id);
      toast.success("Deleted");
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  return (
    <DashboardShell title="Suppliers">
      <div className="grid gap-8 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Add supplier</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={onCreate} className="space-y-3">
              <div className="space-y-2">
                <Label>Name</Label>
                <Input value={name} onChange={(e) => setName(e.target.value)} required />
              </div>
              <div className="space-y-2">
                <Label>Email</Label>
                <Input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label>Phone</Label>
                <Input value={phone} onChange={(e) => setPhone(e.target.value)} />
              </div>
              <div className="space-y-2">
                <Label>Address</Label>
                <Input value={address} onChange={(e) => setAddress(e.target.value)} />
              </div>
              <div className="flex items-center gap-2">
                <input
                  id="sact"
                  type="checkbox"
                  checked={active}
                  onChange={(e) => setActive(e.target.checked)}
                />
                <Label htmlFor="sact">Active</Label>
              </div>
              <Button type="submit">Create</Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Directory</CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <Skeleton className="h-40" />
            ) : rows.length === 0 ? (
              <p className="text-sm text-slate-500">No suppliers.</p>
            ) : (
              <ul className="divide-y divide-slate-100">
                {rows.map((s) => (
                  <li
                    key={s.id}
                    className="flex flex-wrap items-center justify-between gap-2 py-3"
                  >
                    <div>
                      <p className="font-medium">{s.name}</p>
                      <p className="text-xs text-slate-500">{s.email}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant={s.active ? "success" : "outline"}>
                        {s.active ? "active" : "inactive"}
                      </Badge>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setEditing({ ...s })}
                      >
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-red-600"
                        onClick={() => void onDelete(s.id)}
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
            <DialogTitle>Edit supplier</DialogTitle>
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
                <Label>Email</Label>
                <Input
                  value={editing.email || ""}
                  onChange={(e) =>
                    setEditing({ ...editing, email: e.target.value })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label>Phone</Label>
                <Input
                  value={editing.phone || ""}
                  onChange={(e) =>
                    setEditing({ ...editing, phone: e.target.value })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label>Address</Label>
                <Input
                  value={editing.address || ""}
                  onChange={(e) =>
                    setEditing({ ...editing, address: e.target.value })
                  }
                />
              </div>
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={editing.active}
                  onChange={(e) =>
                    setEditing({ ...editing, active: e.target.checked })
                  }
                />
                <Label>Active</Label>
              </div>
              <Button type="submit">Save</Button>
            </form>
          )}
        </DialogContent>
      </Dialog>
    </DashboardShell>
  );
}
