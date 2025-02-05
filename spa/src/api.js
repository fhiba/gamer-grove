import axios from "axios";

const BASE_URL = "http://localhost:8080/paw-2024a-09/api";

const api = axios.create({
  baseURL: BASE_URL,
});

export const fetchPosts = async () => {
  try {
    const res = await api.get("/posts");
    return res.data;
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
