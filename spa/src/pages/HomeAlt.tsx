import React, { useEffect, useState } from "react";
import { redirect, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { decodeToken, JwtPayload } from "../utils/jwt";
import Sidebar from "../components/Sidebar";
import PostComponent from "../components/PostComponent";
import {
  fetchNews,
  fetchCommunities,
  fetchFollowedCommunitiesPosts,
} from "../api.js";
import { Post } from "../types/Post.tsx";
import Navbar from "../components/Navbar.tsx";
import PaginatedPosts from "../components/PaginatedPosts.tsx";
import { AxiosResponse } from "axios";
import { Helmet } from "react-helmet-async";

export function HomeAlt() {
  const { authToken } = useAuth();

  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [posts, setPosts] = useState<AxiosResponse>();
  const [auxPosts, setAuxPosts] = useState<Post[]>([]);
  const [communities, setCommunities] = useState<AxiosResponse>();
  const [news, setNews] = useState<Post[]>([]);
  const [topPosts, setTopPosts] = useState<Post[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [category, setCategory] = useState("all");
  const [orders, setOrders] = useState<string[]>(["Newest", "Oldest"]);
  const [order, setOrder] = useState("Newest");

  const [showToast, setShowToast] = useState(false);
  const [toastHeader, setToastHeader] = useState("");
  const [toastBody, setToastBody] = useState("");

  const location = useLocation();
  const navigate = useNavigate();
  if (authToken === null) {
    console.log("Not logged in");
    redirect("/login");
  } else {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
      console.log(decoded?.sub);
    }, [authToken]);
  }
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const postsRes = await fetchFollowedCommunitiesPosts(authToken);
        const communityData = await fetchCommunities();
        const newsData: Post[] = await fetchNews();
        setPosts(postsRes);
        setAuxPosts(postsRes.data);
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

  const filterPosts = (cat: string) => {
    setCategory(cat);

    const params = new URLSearchParams(location.search);
    params.set("category", cat);
    navigate({ search: params.toString() });
  };

  const handleOrderChange = (ord: string) => {
    setOrder(ord);
    const params = new URLSearchParams(location.search);
    params.set("order", ord);
    params.set("pageNumber", "1");
    navigate({ search: params.toString() });
  };

  const isLogged = decoded?.sub !== null ? true : false;
  const isVerified = decoded?.role !== "ROLE_USER";
  const isAdmin = decoded?.role === "ROLE_ADMIN";
  return (
    <>
      <Helmet>
        <title>Home</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <Navbar
        username={decoded?.sub}
        isLogged={isLogged}
        onSearch={(searchValue) => {
          window.location.href = `/communities?searchTerms=${searchValue}`;
        }}
      />
      <div className="grid grid-cols-3 gap-36">
        <Sidebar
          isAdmin={isAdmin}
          isLogged={isLogged}
          communities={communities?.data}
          currentPath={location.pathname}
        />
        <div>
          <div>
            <div className="card border-0">
              <div className="card-body">
                <div className="d-flex justify-content-between">
                  <div className="form-floating w-25 mb-3">
                    <select
                      className="form-select"
                      value={category}
                      onChange={(e) => filterPosts(e.target.value)}
                    >
                      <option value="all">All</option>
                      {categories.map((cat) => (
                        <option key={cat} value={cat}>
                          {cat}
                        </option>
                      ))}
                    </select>
                    <label>Category</label>
                  </div>

                  <div className="form-floating w-25 mb-3">
                    <select
                      className="form-select"
                      value={order}
                      onChange={(e) => handleOrderChange(e.target.value)}
                    >
                      {orders.map((ord) => (
                        <option key={ord} value={ord}>
                          {ord}
                        </option>
                      ))}
                    </select>
                    <label>Order</label>
                  </div>

                  <a href="/post" className="btn btn-primary h-25 me-2 mt-1">
                    Create
                  </a>
                </div>

                {posts.data.length === 0 && (
                  <div className="text-center w-100">
                    <h6>No posts found</h6>
                    <a href="/all">
                      <button type="button" className="btn btn-primary mt-2">
                        Go to All
                      </button>
                    </a>
                  </div>
                )}

                {auxPosts.length > 0 ? (
                  <PaginatedPosts postsResponse={posts} />
                ) : (
                  <p>No posts found.</p>
                )}
              </div>
            </div>
          </div>
        </div>
        <div>
          <div>
            <div className="card bg-transparent border-0">
              <div className="card-title news-title">
                <h5>News</h5>
              </div>
              <div className="card-body">
                {news.map((news) => (
                  <PostComponent post={news} />
                ))}
              </div>
            </div>

            <div className="card bg-transparent border-0">
              <div className="card-title news-title">
                <h5>Top</h5>
              </div>
              <div className="card-body">
                {topPosts.map((topPosts) => (
                  <PostComponent post={topPosts} />
                ))}
              </div>
            </div>
          </div>
          {isLogged && !isVerified && (
            <div className="toast show position-fixed bottom-0 end-0 m-3">
              <div className="toast-body text-dark">
                Please verify your account!
                <div className="mt-2 pt-2 border-top">
                  <a href="/auth/resend-verification">
                    <button type="button" className="btn btn-primary btn-sm">
                      Resend
                    </button>
                  </a>
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => {
                      setShowToast(false);
                    }}
                  >
                    Close
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {showToast && (
        <div
          className="position-fixed bottom-0 end-0 p-3"
          style={{ zIndex: 11 }}
        >
          <div
            className="toast show"
            role="alert"
            aria-live="assertive"
            aria-atomic="true"
          >
            <div className="toast-header">
              <strong className="me-auto">{toastHeader}</strong>
              <button
                type="button"
                className="btn-close"
                onClick={() => setShowToast(false)}
                aria-label="Close"
              />
            </div>
            <div className="toast-body text-dark">{toastBody}</div>
          </div>
        </div>
      )}
    </>
  );
}
export default HomeAlt;
