import React, { useEffect, useState } from "react";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
interface Post {
  id: number;
  title: string;
  body: string;
}

interface Community {
  encodedName: string;
  name: string;
  portrait?: {
    imageId: string;
  } | null;
}
const All: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [communities, setCommunities] = useState<Community[]>([]);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const isAdmin = false;
  const isLogged = true;
  const userName = "JaibaHardcode";
  const defaultSearch = "Hola";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [postRes, communityRes] = await Promise.all([
          fetch("http://localhost:8080/paw-2024a-09/api/posts"),
          fetch("http://localhost:8080/paw-2024a-09/api/communities"),
        ]);
        if (!postRes.ok) {
          throw new Error("Failed to fetch posts");
        }
        if (!communityRes.ok && communityRes.status !== 204)
          throw new Error("Failed to fetch communities");
        const postData: Post[] = await postRes.json();
        const communityData: Community[] = await communityRes.json();
        setPosts(postData);
        setCommunities(communityData);
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
      <div className="grid grid-cols-3 gap-10">
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
                <div className="border border-gray-200 rounded-lg p-5">
                  {" "}
                  <h2 className="text-2xl font-bold">{post.title}</h2>
                  <div className="content-between">
                    <p className="text-gray-500">{post.body}</p>
                    <p className="text-gray-500">
                      {" "}
                      {format(new Date(post.date), "MMMM d, yyyy h:mm a")}
                    </p>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p>No posts found.</p>
        )}
      </div>
    </div>
  );
};

export default All;
