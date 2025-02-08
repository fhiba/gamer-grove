import React, { useEffect, useState } from "react";
import UserPostsPage from "../components/UserPosts";
import { useParams } from "react-router";
import { fetchPosts, fetchCommunities } from "../api.js";
import { AuthContext } from "../context/AuthContext.js";
import { User } from "../types/User.js";
import { Post } from "../types/Post.js";
import { Community } from "../types/Community.js";

const Profile: React.FC = () => {
  const [user, setUser] = useState<User>();
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [communities, setCommunities] = useState<Community[]>([]);
  const userId = AuthContext;
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [userRes] = await Promise.all([
          fetch(`http://localhost:8080/paw-2024a-09/api/users/${userId}`),
        ]);
        const userData = await userRes.json();
        const postData = await fetchPosts();
        const communityData = await fetchCommunities();
        const filteredPosts = postData.filter((post) => {
          const urlParts = post.author.split("/");
          const authorId = urlParts[urlParts.length - 1];
          return authorId == userId;
        });
        setUser(userData);
        setPosts(filteredPosts);
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

  return <UserPostsPage user={user} posts={posts} communities={communities} />;
};

export default Profile;
