import axios from "axios";

const BASE_URL = "http://localhost:8080/paw-2024a-09/api";

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const fetchPosts = async () => {
  try {
    const res = await api.get("/posts");
    return res;
  } catch (error) {
    throw error;
  }
};

export const fetchNews = async () => {
  try {
    const posts = await fetchPosts();
    const filteredPosts = posts.data.filter((post) => {
      const category = post.category;
      return category == "News";
    });
    return filteredPosts;
  } catch (error) {
    throw error;
  }
};
export const fetchPostById = async (id) => {
  try {
    const res = await api.get(`/posts/${id}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchComments = async (id) => {
  try {
    const res = await api.get(`/posts/${id}/comments`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchCommunities = async () => {
  try {
    const res = await api.get("/communities");
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchCommunity = async (communityName) => {
  try {
    const res = await api.get(`/communities/${communityName}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchCommunityRating = async (communityName) => {
  try {
    const res = await api.get(`/communities/${communityName}/ratings`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const fetchFollowedCommunitiesPosts = async (token) => {
  try {
    const response = await api.get("/posts", {
      params: { followedCommunitiesPosts: true },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching posts:", error);
    throw error;
  }
};

export const fetchCommunityPosts = async (communityName) => {
  try {
    const res = await api.get("/posts", {
      params: { community: communityName },
    });
    return res.data;
  } catch (error) {
    console.error("Error fetching community posts:", communityName);
    throw error;
  }
};
export const fetchUser = async (id) => {
  try {
    const res = await api.get(`/users/${id}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const postAddFollower = async (communityName, token) => {
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

export const postComment = async (postId, body) => {
  const res = await axios.post(`/posts/${postId}/comments`, { body: body });
  return res.data;
};

export const deletePost = async (postId) => {
  try {
    const res = await axios.delete(`/posts/${postId}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};

export const deleteComment = async (postId, commentId) => {
  try {
    const res = await axios.delete(`/posts/${postId}/comments/${commentId}`);
    return res.data;
  } catch (error) {
    throw error;
  }
};
