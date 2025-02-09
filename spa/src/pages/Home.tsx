import React, { useEffect, useState } from "react";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import PostComponent from "../components/PostComponent";
import { AuthContext } from "../context/AuthContext";
import {
  fetchFollowedCommunitiesPosts,
  fetchCommunities,
  fetchNews,
} from "../api.js";
import { useAuth } from "../context/AuthContext";
import { Post } from "../types/Post.js";
import { Community } from "../types/Community.js";

const Home: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [news, setNews] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [communities, setCommunities] = useState<Community[]>([]);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { isLogged, isAdmin, userName, authToken } = useAuth();
  const defaultSearch = "Hola";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const postData: Post[] = await fetchFollowedCommunitiesPosts(authToken);
        const communityData: Community[] = await fetchCommunities();
        const newsData: Post[] = await fetchNews();
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
          <ul>
            {posts.map((post, i) => (
              <li
                key={i}
                onClick={() => navigate(`/post/${post.id}`)}
                style={{ cursor: "pointer", marginBottom: "10px" }}
              >
                <PostComponent post={post} />
              </li>
            ))}
          </ul>
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

export default Home;
