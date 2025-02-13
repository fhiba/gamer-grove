import React, { useEffect, useState } from "react";
import UserPostsPage from "../components/UserTabComponent";
import { fetchCommunities, fetchUser, fetchPostsByUser } from "../api.js";
import { useAuth } from "../context/AuthContext.js";
import { User } from "../types/User.js";
import { Community } from "../types/Community.js";
import { decodeToken, JwtPayload } from "../utils/jwt.js";
import { AxiosResponse } from "axios";
import { Helmet } from "react-helmet-async";

const Profile: React.FC = () => {
  const [user, setUser] = useState<User>();
  const [posts, setPosts] = useState<AxiosResponse>();
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [communities, setCommunities] = useState<AxiosResponse>();
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  useEffect(() => {
    if (authToken !== null) {
      const decodedToken = decodeToken(authToken);
      setDecoded(decodedToken);
    }
  }, [authToken]);

  useEffect(() => {
    if (!decoded?.id) return;
    const fetchData = async () => {
      try {
        setLoading(true);
        const userData = await fetchUser(decoded.id);
        const postData = await fetchPostsByUser(decoded.id);
        const communityData = await fetchCommunities();
        setUser(userData);
        setPosts(postData);
        setCommunities(communityData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unkown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [decoded]);
  if (loading) {
    return <div>Loading...</div>;
  }
  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <>
      <Helmet>
        <title>Profile</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <UserPostsPage user={user} posts={posts} communities={communities} />;
    </>
  );
};

export default Profile;
