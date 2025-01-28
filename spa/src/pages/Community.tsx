import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

interface Community {
  id: number;
  name: string;
}

const Community: React.FC = () => {
  const [community, setCommunity] = useState<Community[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { communityName } = useParams();

  useEffect(() => {
    const fetchCommunity = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/paw-2024a-09/api/communities/${communityName}`,
        );
        if (!response.ok) {
          throw new Error("Failed to fetch posts");
        }
        const data: Community[] = await response.json();
        setCommunity(data);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchCommunity();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h1>{community.name}</h1>
    </div>
  );
};

export default Community;
