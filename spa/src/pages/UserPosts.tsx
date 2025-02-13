import React, { useEffect, useState } from "react";
import { useParams } from "react-router";
import { fetchCommunities, fetchUser, fetchPostsByUser } from "../api.js";
import { User } from "../types/User.js";
import { Community } from "../types/Community.js";
import { decodeToken, JwtPayload } from "../utils/jwt.js";
import { useAuth } from "../context/AuthContext.js";
import { AxiosResponse } from "axios";
import UserTabComponent from "../components/UserTabComponent.js";
import { Helmet } from "react-helmet-async";

const UserPosts: React.FC = () => {
  const [user, setUser] = useState<User>();
  const [posts, setPosts] = useState<AxiosResponse>();
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [communities, setCommunities] = useState<Community[]>([]);
  const { userId } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const userData = await fetchUser(userId);
        const postData = await fetchPostsByUser(userId);
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
  }, [userId]);
  if (loading) {
    return <div>Loading...</div>;
  }
  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <>
      <Helmet>
        <title>User Posts</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <UserTabComponent user={user} posts={posts} communities={communities} />
    </>
  );
};

export default UserPosts;
