"use client";

import * as React from "react";
import { toast } from "sonner";
import Link from "next/link";
import { Pencil, Trash2, Wallet } from "lucide-react";
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
  createCustomer,
  deleteCustomer,
  fetchCustomers,
  getOrCreateAccount,
  updateCustomer,
} from "@/lib/api/services/customers";
import { getApiErrorMessage } from "@/lib/api/client";
import type { Customer } from "@/lib/api/types";
import { formatMoney } from "@/lib/utils";

export default function ClientsPage() {
  const [rows, setRows] = React.useState<Customer[]>([]);
  const [balances, setBalances] = React.useState<Record<number, number>>({});
  const [loading, setLoading] = React.useState(true);
  const [editing, setEditing] = React.useState<Customer | null>(null);

  const [name, setName] = React.useState("");
  const [taxId, setTaxId] = React.useState("");
  const [email, setEmail] = React.useState("");
  const [phone, setPhone] = React.useState("");

  async function load() {
    try {
      const list = await fetchCustomers();
      setRows(list);
      const balEntries = await Promise.all(
        list.map(async (c) => {
          try {
            const acc = await getOrCreateAccount(c.id);
            return [c.id, Number(acc.currentBalance)] as const;
          } catch {
            return [c.id, 0] as const;
          }
        })
      );
      setBalances(Object.fromEntries(balEntries));
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
      const c = await createCustomer({
        name,
        taxId: taxId || undefined,
        email: email || undefined,
        phone: phone || undefined,
        active: true,
      });
      await getOrCreateAccount(c.id);
      toast.success("Client created · current account ready");
      setName("");
      setTaxId("");
      setEmail("");
      setPhone("");
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  async function saveEdit(ev: React.FormEvent) {
    ev.preventDefault();
    if (!editing) return;
    try {
      await updateCustomer(editing.id, {
        name: editing.name,
        taxId: editing.taxId,
        email: editing.email,
        phone: editing.phone,
        active: editing.active ?? true,
      });
      toast.success("Updated");
      setEditing(null);
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  async function onDelete(id: number) {
    if (!confirm("Delete client?")) return;
    try {
      await deleteCustomer(id);
      toast.success("Deleted");
      await load();
    } catch (e) {
      toast.error(getApiErrorMessage(e));
    }
  }

  return (
    <DashboardShell title="Clients">
      <div className="grid gap-8 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>New client</CardTitle>
            <p className="text-sm text-slate-500">
              Creating a customer opens a current account with zero balance.
            </p>
          </CardHeader>
          <CardContent>
            <form onSubmit={onCreate} className="space-y-3">
              <div className="space-y-2">
                <Label>Name</Label>
                <Input value={name} onChange={(e) => setName(e.target.value)} required />
              </div>
              <div className="space-y-2">
                <Label>Tax ID</Label>
                <Input value={taxId} onChange={(e) => setTaxId(e.target.value)} />
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
              <Button type="submit">Create client</Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Accounts snapshot</CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <Skeleton className="h-48" />
            ) : rows.length === 0 ? (
              <p className="text-sm text-slate-500">No clients.</p>
            ) : (
              <ul className="divide-y divide-slate-100">
                {rows.map((c) => (
                  <li
                    key={c.id}
                    className="flex flex-wrap items-center justify-between gap-2 py-3"
                  >
                    <div>
                      <p className="font-medium">{c.name}</p>
                      <p className="text-xs text-slate-500">
                        Balance:{" "}
                        <span className="font-semibold text-slate-800">
                          {formatMoney(balances[c.id] ?? 0)}
                        </span>
                      </p>
                      <Link
                        href={`/reports?tab=account&client=${c.id}`}
                        className="mt-1 inline-flex items-center gap-1 text-xs text-blue-600 hover:underline"
                      >
                        <Wallet className="h-3 w-3" />
                        Account details
                      </Link>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant={c.active ? "success" : "outline"}>
                        {c.active ? "active" : "inactive"}
                      </Badge>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setEditing({ ...c })}
                      >
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-red-600"
                        onClick={() => void onDelete(c.id)}
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
            <DialogTitle>Edit client</DialogTitle>
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
                <Label>Tax ID</Label>
                <Input
                  value={editing.taxId || ""}
                  onChange={(e) =>
                    setEditing({ ...editing, taxId: e.target.value })
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
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={!!editing.active}
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
