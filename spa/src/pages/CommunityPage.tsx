import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Community } from "../types/Community";
import { fetchCommunity } from "../api";

const CommunityPage: React.FC = () => {
  const [community, setCommunity] = useState<Community[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { communityName } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await fetchCommunity(communityName);
        setCommunity(data);
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
    <div>
      <h1>{community.name}</h1>
    </div>
  );
};

export default CommunityPage;
