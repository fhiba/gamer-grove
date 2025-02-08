import React, { useEffect, useState } from "react";
import { Community } from "../types/Community";
import { decodeToken, JwtPayload } from "../utils/jwt";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router";

interface CommunityCardComponents {
  community: Community;
}

const CommunityCard: React.FC<CommunityCardComponents> = ({ community }) => {
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  const navigate = useNavigate();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, []);
  }
  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";

  return (
    <div className="col-3 mt-3">
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
