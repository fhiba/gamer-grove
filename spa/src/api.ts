import axios from "axios";
import { User } from "./types/User";
import { Post } from "./types/Post";
import { Moderator } from "./types/Moderators";

const BASE_URL = "http://localhost:8080/paw-2024a-09/api";

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

//DEVUELVE AXIOSRESPONSE
export const fetchPosts = async () => {
  try {
    const res = await api.get("/posts");
    return res;
  } catch (error) {
    throw error;
  }
};

//DEVUELVE AXIOSRESPONSE
export const fetchLikedPosts = async (userId: number) => {
  try {
    const response = await api.get("/posts", {
      params: { likedBy: userId },
    });
    return response;
  } catch (error) {
    console.error("Error fetching posts:", error);
    throw error;
  }
};

//DEVUELVE AXIOSRESPONSE
export const fetchPostsByUser = async (userId: number) => {
  try {
    const response = await api.get("/posts", {
      params: { author: userId },
    });
    return response;
  } catch (error) {
    console.error("Error fetching posts:", error);
    throw error;
  }
};
export const fetchNews = async () => {
  try {
    const posts = await fetchPosts();
    const filteredPosts = posts.data.filter((post: Post) => {
      const category = post.category;
      return category == "News";
    });
    return filteredPosts;
  } catch (error) {
    throw error;
  }
};
export const fetchPostById = async (id: number) => {
  try {
    const res = await api.get(`/posts/${id}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchComments = async (id: number) => {
  try {
    const res = await api.get(`/posts/${id}/comments`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

//DEVUELVE AXIOSRESPONSE
export const fetchCommunities = async () => {
  try {
    const res = await api.get("/communities");
    return res;
  } catch (error) {
    throw error;
  }
};

export const fetchCommunity = async (communityName: string) => {
  try {
    const res = await api.get(`/communities/${communityName}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchCommunityRating = async (communityName: string) => {
  try {
    const res = await api.get(`/communities/${communityName}/ratings`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

//DEVUELVE AXIOSRESPONSE
export const fetchFollowedCommunitiesPosts = async (token: string) => {
  try {
    const response = await api.get("/posts", {
      params: { followedCommunitiesPosts: true },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response;
  } catch (error) {
    console.error("Error fetching posts:", error);
    throw error;
  }
};

//DEVUELVE AXIOSRESPONSE
export const fetchFollowedCommunities = async (token: string, id: number) => {
  try {
    const response = await api.get("/communities/", {
      params: { followedBy: id },

      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response;
  } catch (error) {
    console.error("Error fetching posts:", error);
    throw error;
  }
};

export const fetchAuthor = async (authorEndpoint: string): Promise<User> => {
  try {
    const response = await axios.get(authorEndpoint);
    return response.data;
  } catch (error) {
    console.error("Error fetching author:", error);
    throw error;
  }
};

//DEVUELVE AXIOSRESPONSE
export const fetchCommunityPosts = async (communityName: string) => {
  try {
    const res = await api.get("/posts", {
      params: { community: communityName },
    });
    return res;
  } catch (error) {
    console.error("Error fetching community posts:", communityName);
    throw error;
  }
};
export const fetchUser = async (id: number) => {
  try {
    const res = await api.get(`/users/${id}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchModerators = async (
  token: string | null,
  username?: string,
  community?: string,
) => {
  try {
    const params: Record<string, string> = {};
    if (username) params.username = username;
    if (community) params.community = community;

    const res = await axios.get(`/api/mods`, {
      params,
      headers: { Authorization: `Bearer ${token}` },
    });

    console.log("API response:", res.data);

    if (
      !res.data ||
      (typeof res.data === "object" && Object.keys(res.data).length === 0)
    ) {
      console.warn("Empty response from API");
      return [];
    }

    return res.data;
  } catch (error) {
    console.error("Error fetching moderators:", error);
    throw error;
  }
};

export const postAddFollower = async (communityName: string, token: string) => {
  try {
    const res = await api.post(`/communities/${communityName}/followers`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const postComment = async (postId: number, body: string) => {
  const res = await axios.post(`/posts/${postId}/comments`, { body: body });
  return res.data;
};

export const deletePost = async (postId: number) => {
  try {
    const res = await axios.delete(`/posts/${postId}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const deleteComment = async (postId: number, commentId: number) => {
  try {
    const res = await axios.delete(`/posts/${postId}/comments/${commentId}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchCategories = async () => {
  return ["action"];
};

export const postPost = async () => {};

export const postRating = async () => {};

export const deleteRating = async () => {};
