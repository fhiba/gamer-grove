import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ComplexNavbar } from "../components/Navbar.tsx";
interface Comment {
  id: number;
  user: string;
  body: string;
}

const Comment: React.FC = () => {
  const [comment, setComment] = useState<Comment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const postId = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/paw-2024a-09/api/posts",
        );
        if (!response.ok) {
          throw new Error("Failed to fetch posts");
        }
        const data: Post[] = await response.json();
        setPosts(data);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchPosts();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h1>All Posts</h1>
      {posts.length > 0 ? (
        <ul>
          {posts.map((post, i) => (
            <li
              key={i}
              onClick={() => navigate(`/post/${post.id}`)}
              style={{ cursor: "pointer", marginBottom: "10px" }}
            >
              <h2>{post.title}</h2>
              <p>{post.body}</p>
            </li>
          ))}
        </ul>
      ) : (
        <p>No posts found.</p>
      )}
    </div>
  );
};

export default All;
