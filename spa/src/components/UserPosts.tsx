import React, { useState, useEffect } from "react";
// If you're using React Router for navigation:
import { Link } from "react-router-dom";
// If you have a separate Header / Sidebar:
import Sidebar from "../components/Sidebar";
import Navbar from "./Navbar";

interface User {
  email: string;
  username: string;
  locale: string;
  isVerified: boolean;
  image?: UserImage | null;
}
interface Post {
  id: number;
  communityName: string;
  category: string;
  title: string;
  body: string;
  grooviness: number;
  date: string;
}

const UserPostsPage: React.FC<UserPostsPageProps> = ({
  user,
  posts,
  isAdmin,
  isLogged,
  communities,
}) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>(
    user?.image
      ? `/image/${user.image.imageId}`
      : "/images/default-avatar-icon.jpg",
  );

  const [locale, setLocale] = useState<string>(user.locale);

  const [showUnverifiedModal, setShowUnverifiedModal] = useState<boolean>(
    !user.isVerified,
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
    <div className="w-screen h-screen">
      <Navbar isLoggedIn={isLogged} userName={user.username} />

      <div className="grid grid-cols-5">
        <div className="row min-vh-100">
          <Sidebar
            isAdmin={isAdmin}
            isLogged={isLogged}
            communities={communities}
            currentPath={window.location.pathname}
          />
          <div></div>
          <div>
            <div className="card-body">
              <div className="mb-3 row">
                <div className="col-3">
                  <img
                    src={previewUrl}
                    className="w-100 h-100 rounded-1 img-com"
                    alt="Profile"
                  />
                </div>

                <div>
                  <h1 className="card-title">User Profile</h1>
                  <div className="d-flex">
                    <div className="me-5">
                      <label className="form-label fw-semibold">Email</label>
                      <p>{user.email}</p>
                    </div>
                    <div>
                      <label className="form-label fw-semibold">Username</label>
                      <p>{user.username}</p>
                    </div>
                  </div>
                </div>
              </div>

              <div className="d-flex justify-content-center align-items-center"></div>
            </div>
          </div>
        </div>
      </div>
      {/* end container */}
    </div>
  );
};

export default UserPostsPage;
