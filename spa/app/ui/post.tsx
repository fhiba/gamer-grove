"use client";
import { Card, CardHeader } from "@heroui/react";
import { use } from "react";

export default function Posts({
  posts,
}: {
  posts: Promise<{ title: string; body: string }[]>;
}) {
  const post = use(posts);
  return (
    <Card className="py-4">
      <CardHeader className="pb-0 pt-2 px-4 flex-col items-start">
        <p className="text-tiny uppercase font-bold">{post.title}</p>
        <h4 className="font-bold text-large">{post.body}</h4>
      </CardHeader>
    </Card>
  );
}
