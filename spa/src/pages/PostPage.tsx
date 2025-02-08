import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import Sidebar from "../components/Sidebar.js";
import Navbar from "../components/Navbar.js";
import { format } from "date-fns";
import { fetchPostById, fetchComments, fetchCommunities } from "../api.js";
import { Post } from "../types/Post.js";
import { Community } from "../types/Community.tsx";
import { Comment } from "../types/Comment.tsx";

const PostPage: React.FC = () => {
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
        const postData = await fetchPostById(postId);
        const commentsData = await fetchComments(postId);
        const communityData = await fetchCommunities();
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
          <h2 className="text-5xl font-bold">{post.title}</h2>
          <p>{post.body}</p>
          <p className="text-gray-500">
            {format(new Date(post.date), "MMMM d, yyyy h:mm a")}
          </p>
          <p>{post.grooviness}</p>
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

export default PostPage;
