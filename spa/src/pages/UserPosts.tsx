import React, { useEffect, useState } from "react";
import UserPostsPage from "../components/UserPosts";
import { useParams } from "react-router";
interface User {
  email: string;
  author: string;
  username: string;
  locale: string; // "en" or "es"
  isVerified: boolean;
}

// Example interface for a single Post
interface Post {
  id: number;
  communityName: string;
  category: string;
  title: string;
  body: string;
  grooviness: number;
  date: string; // or Date if you parse it
}
interface Community {
  encodedName: string;
  name: string;
  portrait?: {
    imageId: string;
  } | null;
}
const UserPosts: React.FC = () => {
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
        const [postRes, userRes, communityRes] = await Promise.all([
          fetch(`http://localhost:8080/paw-2024a-09/api/posts`),
          fetch(`http://localhost:8080/paw-2024a-09/api/users/${userId}`),
          fetch(`http://localhost:8080/paw-2024a-09/api/communities`),
        ]);
        if (!userRes.ok) throw new Error("Failed to fetch user");
        if (!postRes.ok) throw new Error("Failed to fetch posts");
        const userData = await userRes.json();
        const postData = await postRes.json();
        const communityData = await communityRes.json();
        if (!communityRes.ok && communityRes.status !== 204)
          throw new Error("Failed to fetch communities");
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

export default UserPosts;
