import Posts from "@/app/ui/posts";
import { Suspense } from "react";

export const experimental_ppr = true;

async function fetchData() {
  const API_URL = process.env.LOCAL_API_URL;
  const res = await fetch(`${API_URL}/posts`);
  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }
  return res.json();
}

export default async function Post() {
  const allPosts = fetchData();
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <Posts posts={allPosts} />
    </Suspense>
  );
}
