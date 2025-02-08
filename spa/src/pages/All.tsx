import React, { useEffect, useState } from "react";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import PostComponent from "../components/PostComponent";
import { fetchPosts, fetchCommunities, fetchNews } from "../api.js";
import { Post } from "../types/Post.js";
import { Community } from "../types/Community.js";
import PaginatedPosts from "../components/PaginatedPosts.js";
import { useAuth } from "../context/AuthContext.js";
import { decodeToken, JwtPayload } from "../utils/jwt.js";

const All: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [news, setNews] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [communities, setCommunities] = useState<Community[]>([]);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, []);
  }
  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const newsData: Post[] = await fetchNews();
        const postData: Post[] = await fetchPosts();
        const communityData: Community[] = await fetchCommunities();
        setPosts(postData);
        setCommunities(communityData);
        setNews(newsData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }
  console.log(posts.length);
  return (
    <div className="w-screen h-screen">
      <Navbar
        userName={userName}
        isLoggedIn={isLogged}
        defaultSearch={defaultSearch}
        onSearch={(searchValue) => {
          window.location.href = `/communities?searchTerms=${searchValue}`;
        }}
      />
      <div className="grid grid-cols-3 gap-36">
        <div>
          <Sidebar
            isAdmin={isAdmin}
            isLogged={isLogged}
            communities={communities}
            currentPath={location.pathname}
          />
        </div>
        {posts.length > 0 ? (
          <PaginatedPosts posts={posts} />
        ) : (
          <p>No posts found.</p>
        )}
        {news.length > 0 ? (
          <ul>
            {news.map((newsPost, i) => (
              <li
                key={i}
                onClick={() => navigate(`/post/${newsPost.id}`)}
                style={{ cursor: "pointer", marginBottom: "10px" }}
              >
                <PostComponent post={newsPost} />
              </li>
            ))}
          </ul>
        ) : (
          <p>No news found.</p>
        )}
      </div>
    </div>
  );
};

export default All;
