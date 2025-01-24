import Posts from "@/app/ui/post";
import { Suspense } from "react";

export const experimental_ppr = true;

async function fetchData(id: string) {
  const API_URL = process.env.LOCAL_API_URL;
  const res = await fetch(`${API_URL}/posts/${id}`);
  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }
  return res.json();
}

export default async function Post({ params }) {
  const pars = await params;
  const post = fetchData(pars.id);
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <Posts posts={post} />
    </Suspense>
  );
}
