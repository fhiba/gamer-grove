import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { BrowserRouter, Routes, Route } from "react-router";
import Home from "./pages/Home.tsx";
import All from "./pages/All.tsx";
import CommunityPage from "./pages/CommunityPage.tsx";
import PostPage from "./pages/PostPage.tsx";
import Communities from "./pages/Communities.tsx";
import ManageMods from "./pages/ManageMods.tsx";
import NewCommunity from "./pages/NewCommunity.tsx";
import Profile from "./pages/Profile.tsx";
import UserPosts from "./pages/UserPosts.tsx";
import LikedPosts from "./pages/LikedPosts.tsx";
import Followed from "./pages/Followed.tsx";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login.tsx";
import Register from "./pages/Register.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";
import HomeAlt from "./pages/HomeAlt.tsx";
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider>
      <BrowserRouter basename={import.meta.env.BASE_URL}>
        <Routes>
          <Route path="/home" element={<HomeAlt />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/all" element={<All />} />
          <Route path="/" element={<All />} />
          <Route path="/community/:communityName" element={<CommunityPage />} />
          <Route path="/post/:postId" element={<PostPage />} />
          <Route path="/communities" element={<Communities />} />
          <Route
            path="/manageMods"
            element={
              <ProtectedRoute>
                <ManageMods />
              </ProtectedRoute>
            }
          />
          <Route path="/newCommunity" element={<NewCommunity />} />
          <Route path="/user/:userId">
            <Route path="userPosts" element={<UserPosts />} />
            <Route path="followed" element={<Followed />} />
            <Route path="likedPosts" element={<LikedPosts />} />
          </Route>
          <Route path="/profile">
            <Route element={<Profile />} />
            <Route path="userPosts" element={<UserPosts />} />
            <Route path="followed" element={<Followed />} />
            <Route path="likedPosts" element={<LikedPosts />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  </StrictMode>,
);
