import React, { useEffect, useState } from "react";
import { Community } from "../types/Community";
import { decodeToken, JwtPayload } from "../utils/jwt";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router";
import { postAddFollower } from "../api";

interface CommunityCardComponents {
  community: Community | undefined;
}
//TODO:traer los campos que faltan como isFollowing y etc, utilizar authcontext
// community included en (followedBy userId) => como consigo el userId si solo tengo el jwt??
const CommunityCard: React.FC<CommunityCardComponents> = ({ community }) => {
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  const navigate = useNavigate();
  const [followedCommunities, setFollowed] = useState<Community[]>([]);
  const [loading, setLoading] = useState<boolean>();
  const [error, setError] = useState<string | null>(null);
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, []);
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        const followedData = await fetchFollowedCommunities();
        setFollowed(followedData);
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

  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";

  const handleFollow = async () => {
    if (!community) return;
    postAddFollower(community.name, authToken);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }
  return (
    <div>
      {community && (
        <div className="card border-black bg-transparent">
          <div className="card-body">
            <div className="card-title d-flex row-cols-2 justify-content-between">
              <h5
                className="card-title text-light fw-bold"
                onClick={() => navigate(`/community/${community.name}`)}
              >
                {community.name}
              </h5>
              <div className="d-none">{/* Hidden follow form if needed */}</div>
              {isFollowing ? (
                <button
                  onClick={handleFollow}
                  className="rounded-pill btn-outline-danger follow-button fw-bold"
                  id="followButton"
                >
                  Following
                </button>
              ) : (
                <button
                  onClick={handleFollow}
                  className="rounded-pill btn-outline-danger follow-button fw-bold"
                  id="followButton"
                >
                  Follow
                </button>
              )}
            </div>
            {community.category &&
              community.category.map((cat, index) => (
                <span key={index} className="cat-badge badge">
                  {cat}
                </span>
              ))}
            <div className="card-subtitle text-body-secondary mt-3">
              {community.description}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CommunityCard;
