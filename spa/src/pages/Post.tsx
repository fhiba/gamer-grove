import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

interface Post {
  id: number;
  title: string;
  body: string;
  grooviness: number;
  date: string;
}

const Post: React.FC = () => {
  const [post, setPost] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { postId } = useParams();

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/paw-2024a-09/api/posts/${postId}`,
        );
        if (!response.ok) {
          throw new Error("Failed to fetch posts");
        }
        const data: Post[] = await response.json();
        setPost(data);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchPost();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <Link to="/all">all</Link>
      <h1>{post.title}</h1>
      <p>{post.body}</p>
      <p>{post.grooviness}</p>
    </div>
  );
};

export default Post;
