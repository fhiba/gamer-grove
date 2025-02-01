import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
interface Post {
  id: number;
  title: string;
  body: string;
  grooviness: number;
  date: string;
}

interface Comment {
  author: string;
  body: string;
}

interface Community {
  encodedName: string;
  name: string;
  portrait?: {
    imageId: string;
  } | null;
}

const Post: React.FC = () => {
  const [post, setPost] = useState<Post[]>([]);
  const [comments, setComments] = useState<Comment[]>([]);
  const [communities, setCommunities] = useState<Community[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { postId } = useParams();
  const isAdmin = false;
  const isLogged = true;
  const userName = "JaibaHardcode";
  const defaultSearch = "Hola";

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [postRes, commentsRes, communityRes] = await Promise.all([
          fetch(`http://localhost:8080/paw-2024a-09/api/posts/${postId}`),
          fetch(
            `http://localhost:8080/paw-2024a-09/api/posts/${postId}/comments`,
          ),
          fetch(`http://localhost:8080/paw-2024a-09/api/communities`),
        ]);

        if (!postRes.ok) throw new Error("Failed to fetch post");
        if (!commentsRes.ok && commentsRes.status !== 204)
          throw new Error("Failed to fetch comments");
        if (!communityRes.ok && communityRes.status !== 204)
          throw new Error("Failed to fetch communities");
        const postData = await postRes.json();

        const commentsData =
          commentsRes.status === 204 ? [] : await commentsRes.json();

        const communityData =
          communityRes.status === 204 ? [] : await communityRes.json();
        setPost(postData);
        setComments(commentsData);
        setCommunities(communityData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [postId]);
  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="h-screen w-screen">
      <Navbar
        userName={userName}
        isLoggedIn={isLogged}
        defaultSearch={defaultSearch}
        onSearch={(searchValue) => {
          window.location.href = `/communities?searchTerms=${searchValue}`;
        }}
      />
      <div className="grid grid-cols-3 gap-10">
        <div>
          <Sidebar
            isAdmin={isAdmin}
            isLogged={isLogged}
            communities={communities}
            currentPath={location.pathname}
          />
        </div>
        <div className="grid grid-rows-2 gap-4">
          <div>
            <h1>{post.title}</h1>
            <p>{post.body}</p>
            <p>{post.grooviness}</p>
          </div>
          {comments.length > 0 ? (
            <ul>
              {comments.map((comment, i) => (
                <li key={i}>
                  <h2>{comment.body}</h2>
                  <p>{comment.author}</p>
                </li>
              ))}
            </ul>
          ) : (
            <p>No comments found.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Post;
