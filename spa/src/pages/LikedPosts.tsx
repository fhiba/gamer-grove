import React, { useEffect, useState } from "react";
import UserPostsPage from "../components/UserPosts";
import { useParams } from "react-router";
import { fetchPosts, fetchCommunities, fetchUser } from "../api.js";
import { Post } from "../types/Post.js";
import { User } from "../types/User.js";
import { Community } from "../types/Community.js";

const LikedPosts: React.FC = () => {
  const [user, setUser] = useState<User>();
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [communities, setCommunities] = useState<Community[]>([]);
  const { userId } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const userData = await fetchUser(userId);
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

export default LikedPosts;
