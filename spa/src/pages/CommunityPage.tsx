import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Community } from "../types/Community";
import { fetchCommunities, fetchCommunity, fetchCommunityPosts } from "../api";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import { Post } from "../types/Post";
import PostComponent from "../components/PostComponent";
import { useAuth } from "../context/AuthContext";
import { decodeToken, JwtPayload } from "../utils/jwt";

const CommunityPage: React.FC = () => {
  const [community, setCommunity] = useState<Community>();
  const [communities, setCommunities] = useState<Community[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { communityName } = useParams();
  const [posts, setPosts] = useState<Post[]>([]);
  const navigate = useNavigate();
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, []);
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchCommunity(communityName);
        const postData = await fetchCommunityPosts(communityName);
        const communitiesData = await fetchCommunities();
        setCommunity(data);
        setPosts(postData);
        setCommunities(communitiesData);
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
  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";
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

        <div>
          <div>
            <h1>{community.name}</h1>
          </div>
          <div>
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
          </div>
        </div>
      </div>
    </div>
  );
};

export default CommunityPage;
