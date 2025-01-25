import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { BrowserRouter, Routes, Route } from "react-router";
import Home from "./Home.tsx";
import All from "./All.tsx";
import Community from "./Community.tsx";
import Post from "./Post.tsx";
import Communities from "./Communities";
import ManageMods from "./ManageMods";
import NewCommunity from "./NewCommunity.tsx";
import Profile from "./Profile.tsx";
import UserPosts from "./UserPosts.tsx";
import LikedPosts from "./LikedPosts.tsx";
import Followed from "./Followed.tsx";
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter basename={import.meta.env.BASE_URL}>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/home" element={<Home />} />
        <Route path="/all" element={<All />} />
        <Route path="/community/:communityName" element={<Community />} />
        <Route path="/post/:postId" element={<Post />} />
        <Route path="/communities" element={<Communities />} />
        <Route path="/manageMods" element={<ManageMods />} />
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
  </StrictMode>,
);
