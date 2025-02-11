import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import { Community } from "../types/Community";
import { User } from "../types/User";
import { useAuth } from "../context/AuthContext";
import { decodeToken, JwtPayload } from "../utils/jwt";
import PaginatedPosts from "./PaginatedPosts";
import { AxiosResponse } from "axios";

interface UserPostsPageProps {
  user: User;
  posts: AxiosResponse;
  communities: Community[];
}

const UserTabComponent: React.FC<UserPostsPageProps> = ({
  user,
  posts,
  communities,
}) => {
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const [showUnverifiedModal, setShowUnverifiedModal] = useState<boolean>(true);
  const { authToken } = useAuth();

  useEffect(() => {
    if (authToken !== null) {
      const decodedToken = decodeToken(authToken);
      setDecoded(decodedToken);
      setShowUnverifiedModal(decodedToken?.role === "ROLE_USER");
    }
  }, [authToken]);

  const isAdmin = decoded?.role === "ROLE_ADMIN";
  const isLogged = decoded !== null;

  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>(
    user?.profileImage ? user.profileImage : "../images/default.jpg",
  );

  const [locale, setLocale] = useState<string>(user.locale);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setSelectedFile(e.target.files[0]);
      setPreviewUrl(URL.createObjectURL(e.target.files[0]));
    }
  };

  const handleFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Profile updated (mock)!");
  };

  return (
    <div>
      <Navbar isLogged={isLogged} userName={user.username} />

      {showUnverifiedModal && (
        <div
          className="modal fade show"
          tabIndex={-1}
          style={{ display: "block", backgroundColor: "rgba(0, 0, 0, 0.5)" }}
          aria-labelledby="staticBackdropLabel"
          aria-modal="true"
          role="dialog"
        >
          <div className="modal-dialog modal-dialog-centered modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Resend Verification</h5>
              </div>
              <div className="modal-body">
                Please verify your account.
                <a
                  href="/auth/resend-verification"
                  className="btn btn-link p-0"
                >
                  Resend Email
                </a>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowUnverifiedModal(false)}
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="grid grid-cols-2">
        <div className="w-1/3">
          <Sidebar
            isAdmin={isAdmin}
            isLogged={isLogged}
            communities={communities}
            currentPath={window.location.pathname}
          />
        </div>
        <div className="left-0 justify-content-center">
          <div className="card border-light border-0">
            <div className="card-body">
              <div>
                <div>
                  <img
                    src={user.profileImage}
                    className="w-100 h-100 rounded-1 img-com"
                    alt="Profile"
                  />
                </div>

                <div className="">
                  <h1 className="card-title">User Profile</h1>
                  <div className="d-flex">
                    <div className="me-5">
                      <label className="form-label fw-semibold text-white">
                        Email
                      </label>
                      <p className="text-white">{user.email}</p>
                    </div>
                    <div>
                      <label className="form-label fw-semibold text-white">
                        Username
                      </label>
                      <p className="text-white">{user.username}</p>
                    </div>
                  </div>

                  <form
                    onSubmit={handleFormSubmit}
                    encType="multipart/form-data"
                  >
                    <label className="form-label fw-semibold">
                      Update Profile Picture
                    </label>
                    <input
                      type="file"
                      accept="image/*"
                      className="form-control w-50"
                      onChange={handleFileChange}
                    />

                    <div className="w-25 mt-3">
                      <label className="form-label fw-semibold">Language</label>
                      <select
                        className="form-select"
                        value={locale}
                        onChange={(e) => setLocale(e.target.value)}
                      >
                        <option value="en">en</option>
                        <option value="es">es</option>
                      </select>
                    </div>

                    <button type="submit" className="btn btn-primary mt-3">
                      Update
                    </button>
                  </form>
                </div>
              </div>

              <ul className="nav nav-tabs mb-3">
                <li className="nav-item">
                  <span className="nav-link active" aria-current="page">
                    User Posts
                  </span>
                </li>
                <li className="nav-item">
                  <a className="nav-link" href={`/user/${user.id}/likedPosts`}>
                    Liked Posts
                  </a>
                </li>
                <li className="nav-item">
                  <a className="nav-link" href={`/user/${user.id}/followed`}>
                    Followed Communities
                  </a>
                </li>
              </ul>

              {(!posts || posts.data.length === 0) && (
                <div className="mt-5 d-flex justify-content-center">
                  <div className="mb-4 d-flex flex-column align-items-center">
                    <h4>No user posts yet</h4>
                    <Link to="/post" className="btn btn-primary w-50">
                      Create a Post
                    </Link>
                  </div>
                </div>
              )}

              <PaginatedPosts postsResponse={posts} />
              <div className="d-flex justify-content-center align-items-center">
                <p>Pagination Footer Placeholder</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserTabComponent;
