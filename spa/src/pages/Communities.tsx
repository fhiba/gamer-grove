import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

interface Community {
  id: number;
  name: string;
}

const Communities: React.FC = () => {
  const [communities, setCommunities] = useState<Community[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCommunities = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/paw-2024a-09/api/communities`,
        );
        if (!response.ok) {
          throw new Error("Failed to fetch posts");
        }
        const data: Community[] = await response.json();
        setCommunities(data);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchCommunities();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h1>All communities</h1>
      {communities.length > 0 ? (
        <ul>
          {communities.map((community, i) => (
            <li
              key={i}
              onClick={() => navigate(`community/${community.name}`)}
              style={{ cursor: "pointer", marginBottom: "10px" }}
            >
              <h2>{community.name}</h2>
            </li>
          ))}
        </ul>
      ) : (
        <p>No posts found.</p>
      )}
    </div>
  );
};

export default Communities;
