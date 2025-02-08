import React, { useState, useEffect } from "react";
// If you're using React Router for navigation:
import { Link } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import { Post } from "../types/Post";
import { Community } from "../types/Community";
import { User } from "../types/User";
import { useAuth } from "../context/AuthContext";
import { decodeToken } from "../utils/jwt";
import { JwtPayload } from "jwt-decode";

interface UserPostsPageProps {
  user: User;
  posts: Post[];
  communities: Community[];
}
const UserPostsPage: React.FC<UserPostsPageProps> = ({
  user,
  posts,
  communities,
}) => {
  const authContext = useAuth();
  const [isAdmin, setIsAdmin] = useState<boolean>();
  const [isLogged, setIsLogged] = useState<boolean>();
  let decoded;
  const [isVerified, setIsVerified] = useState<boolean>();
  if (authContext.authToken !== null) {
    decoded = decodeToken(authContext.authToken);
    setIsAdmin(decoded?.role === "ROLE_ADMIN" ? true : false);
    setIsVerified(decoded?.role !== "ROLE_USER" ? true : false);
    setIsLogged(true);
  }

  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>(
    user?.profileImage ? user.profileImage : "../images/default.jpg",
  );

  const [locale, setLocale] = useState<string>(user.locale);

  const [showUnverifiedModal, setShowUnverifiedModal] = useState<boolean>(
    isVerified === true ? true : false,
  );

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

  const formatPostDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return d.toLocaleString();
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

      <div className="container-fluid min-vh-100">
        <div className="row min-vh-100">
          {/* If you have a Sidebar */}
          <Sidebar
            isAdmin={isAdmin}
            isLogged={isLogged}
            communities={communities}
            currentPath={window.location.pathname}
          />

          <div className="col-1" />

          <div className="col-8 justify-content-center">
            <div className="card border-light border-0">
              <div className="card-body">
                <div className="mb-3 row">
                  <div className="col-3">
                    <img
                      src={previewUrl}
                      className="w-100 h-100 rounded-1 img-com"
                      alt="Profile"
                    />
                  </div>

                  <div className="col-8">
                    <h1 className="card-title">User Profile</h1>
                    <div className="d-flex">
                      <div className="me-5">
                        <label className="form-label fw-semibold">Email</label>
                        <p>{user.email}</p>
                      </div>
                      <div>
                        <label className="form-label fw-semibold">
                          Username
                        </label>
                        <p>{user.username}</p>
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
                        <label className="form-label fw-semibold">
                          Language
                        </label>
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
                    <a className="nav-link" href="/profile/likedPosts">
                      Liked Posts
                    </a>
                  </li>
                  <li className="nav-item">
                    <a className="nav-link" href="/profile/followed">
                      Followed Communities
                    </a>
                  </li>
                </ul>

                {(!posts || posts.length === 0) && (
                  <div className="mt-5 d-flex justify-content-center">
                    <div className="mb-4 d-flex flex-column align-items-center">
                      <h4>No user posts yet</h4>
                      <Link to="/post" className="btn btn-primary w-50">
                        Create a Post
                      </Link>
                    </div>
                  </div>
                )}

                {posts.map((post) => (
                  <a
                    key={post.id}
                    href={`/post/${post.id}`}
                    className="card-link text-decoration-none"
                  >
                    <div className="card mb-3">
                      <div className="card-body">
                        <div className="title-container">
                          <p className="fw-semibold card-subtitle">
                            /{post.community}
                          </p>
                          <span
                            className={`badge rounded-pill mb-1 ${post.category}`}
                          >
                            {post.category}
                          </span>
                        </div>

                        <h4 className="card-title fw-bold">{post.title}</h4>
                        <p className="card-text post-body-home">{post.body}</p>

                        <div className="d-flex row-cols-2 justify-content-between mt-1">
                          <p>
                            <small className="text-body-secondary">
                              {formatPostDate(post.date)}
                            </small>
                          </p>
                          <div className="d-flex justify-content-end align-items-end">
                            <span className="badge text-bg-dark pillUpvoteHome bg-transparent border border-light rounded-2 border-1">
                              {post.grooviness}
                              <i
                                className="fa fa-thumbs-up ms-2"
                                aria-hidden="true"
                              ></i>
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </a>
                ))}

                <div className="d-flex justify-content-center align-items-center">
                  <p>Pagination Footer Placeholder</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserPostsPage;
