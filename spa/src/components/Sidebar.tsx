import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom"; // optional if you want client-side routing
import { Community } from "../types/Community";

interface SidebarProps {
  isAdmin: boolean;
  isLogged: boolean;
  communities: Community[];
  currentPath?: string;
}

const Sidebar: React.FC<SidebarProps> = ({
  isAdmin,
  isLogged,
  communities,
  currentPath = "",
}) => {
  const isHome = currentPath.includes("/home");
  const isAll = currentPath.includes("/all") || currentPath.match(/\/$/);
  const homeAnchorClass = `fs-5 text-light sidebar-nav ${
    isHome ? "fw-bold" : "text-decoration-none"
  }`;

  const allAnchorClass = `fs-5 text-light link sidebar-nav ${
    isAll ? "fw-bold text-decoration-none" : "text-decoration-none"
  }`;

  return (
    <div className="w-3/4 sidebar text-decoration-none h-full">
      <div className=" card sidebar-card m-auto">
        <div className="card-body">
          {isAdmin && (
            <div className="d-flex flex-column justify-content-center align-items-center">
              <a href="/manageMods" className="w-100">
                <button className="btn btn-outline-primary mb-2 w-100">
                  Manage Moderators
                </button>
              </a>
              <a href="/newCommunity" className="w-100">
                <button className="btn btn-outline-success w-100">
                  Add Community
                </button>
              </a>
            </div>
          )}
          <hr />

          <div className="d-flex flex-column">
            <a id="home_anchor" href="/home" className={homeAnchorClass}>
              Home
            </a>
            <hr />
            <a id="all_anchor" href="/all" className={allAnchorClass}>
              All
            </a>
          </div>
          <hr className="mt-3" />
          {!isLogged ? (
            <div className="h5 card-title text-light mb-3">Communities</div>
          ) : (
            <div className="mb-3">
              <a
                href="/profile/followed"
                className="card-title text-light fs-5 sidebar-nav"
              >
                Followed Communities
              </a>
            </div>
          )}

          {communities.map((community) => {
            const hasPortrait = community?.portrait !== null;
            const portraitSrc = hasPortrait
              ? community.portrait
              : "../images/default.jpg";

            return (
              <a
                key={community.name}
                href={`/community/${community.name}`}
                className="text-light text-decoration-none"
              >
                <div className="card-body-community d-flex align-items-center mb-3">
                  <div className="d-flex justify-content-start me-2">
                    <img
                      src={portraitSrc}
                      className="very-small-profile-pic mb-1"
                    />
                  </div>
                  <div>
                    <h5 className="fw-semibold fs-6 card-subtitle text-break truncate-1-lines">
                      /{community.name}
                    </h5>
                  </div>
                </div>
              </a>
            );
          })}

          {communities.length === 0 && (
            <p className="text-light">You are not following any communities.</p>
          )}

          <a href="/communities" className="fs-6 card-title text-light mb-3">
            <p>Browse All Communities</p>
          </a>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
