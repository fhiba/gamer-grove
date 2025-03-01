import React, { useEffect, useState } from "react";
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
import { AxiosResponse } from "axios";
import { Helmet } from "react-helmet-async";

const All: React.FC = () => {
  const [posts, setPosts] = useState<AxiosResponse>();
  const [auxPosts, setAuxPosts] = useState<Post[]>([]);
  const [news, setNews] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [communities, setCommunities] = useState<AxiosResponse>();
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
  const isLogged = userName !== null ? true : false;
  console.log("is logged:", isLogged);
  const defaultSearch = "";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const newsData: Post[] = await fetchNews();
        const postResponse = await fetchPosts();
        const communityData = await fetchCommunities();
        setPosts(postResponse);
        setCommunities(communityData);
        setNews(newsData);
        setAuxPosts(postResponse.data);
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
    <>
      <Helmet>
        <title>All</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <div className="w-screen ">
        <Navbar
          userName={decoded?.sub}
          isLoggedIn={isLogged}
          defaultSearch={defaultSearch}
          onSearch={(searchValue) => {
            window.location.href = `/communities?searchTerms=${searchValue}`;
          }}
        />
        <div className="grid grid-cols-6 gap-36">
          <div className="col-span-2">
            <Sidebar
              isAdmin={isAdmin}
              isLogged={isLogged}
              communities={communities?.data}
              currentPath={location.pathname}
            />
          </div>
          <div className="col-span-2">
            {" "}
            {auxPosts.length > 0 ? (
              <PaginatedPosts postsResponse={posts} />
            ) : (
              <p>No posts found.</p>
            )}
          </div>
          <div className="col-span-2">
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
      </div>
    </>
  );
};

export default All;
