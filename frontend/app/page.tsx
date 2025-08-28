'use client';
import Section from "@/components/Section";
import { useEffect, useState } from "react";
import { Product } from "@/types/product";

async function fetchProducts(query: string): Promise<Product[]> {
  try {
    const res = await fetch(`/api/products?${query}`, {
      cache: 'no-store',
    });
    if (!res.ok) return [];
    const json = await res.json();
    return json.data || [];
  } catch {
    return [];
  }
}

export default function Home() {
  const [newestItems, setNewestItems] = useState<Product[]>([]);

  useEffect(() => {
    Promise.all([
      fetchProducts('isTimeSale=true'),
      fetchProducts('sort=created_at&order=desc&limit=8'),
      fetchProducts('sort=views&order=desc&limit=8'),
      fetchProducts('featured=true'),
    ]).then(([newest]) => {
      setNewestItems(newest);
    });
  }, []);

  return (
    <div className="w-full max-w-screen-xl">
      <Section title="신상품" products={newestItems} />
    </div >
  );
}

