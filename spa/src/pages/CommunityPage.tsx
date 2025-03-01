import React, { useState, useEffect, ChangeEvent, FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { Modal, Button, Form } from "react-bootstrap"; // Optional: using react-bootstrap for modals & forms
import Header from "../components/Header"; // Your header component
import Sidebar from "../components/Sidebar"; // Your sidebar component
import CommunityCard from "../components/CommunityCard"; // Component that shows community details
import PaginationFooter from "../components/PaginationFooter"; // Your pagination component
import Rating from "react-rating-stars-component"; // A React rating component
import {
  fetchCommunity,
  fetchCommunities,
  fetchCategories,
  fetchPosts, // fetch posts for this community
  postPost, // create a new post in the community
  postRating,
  deleteRating,
  fetchCommunityPosts,
} from "../api"; // Adjust these API functions as needed
import { Community } from "../types/Community";
import { Post } from "../types/Post";
import { AxiosResponse } from "axios";
import { useAuth } from "../context/AuthContext";
import { decodeToken, JwtPayload } from "../utils/jwt";
import Navbar from "../components/Navbar";
import PaginatedPosts from "../components/PaginatedPosts";
import { Helmet } from "react-helmet-async";

interface RatingData {
  rating: number;
}

const CommunityPage: React.FC = () => {
  const [community, setCommunity] = useState<Community | null>(null);
  const [communities, setCommunities] = useState<AxiosResponse>();
  const [posts, setPosts] = useState<AxiosResponse>();
  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { communityName } = useParams();
  //TODO: FIX CAN EDIT
  const [canEdit, setCanEdit] = useState<boolean>(false);

  const [showCreatePostModal, setShowCreatePostModal] =
    useState<boolean>(false);
  const [showRatingModal, setShowRatingModal] = useState<boolean>(false);

  const [newPost, setNewPost] = useState<{
    title: string;
    body: string;
    category: string;
    files: File[];
  }>({
    title: "",
    body: "",
    category: "",
    files: [],
  });
  const [filePreviews, setFilePreviews] = useState<string[]>([]);

  const [currentRating, setCurrentRating] = useState<RatingData | null>(null);
  const [newRating, setNewRating] = useState<number>(1);

  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, [authToken]);
  }
  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded?.sub !== undefined ? true : false;
  const defaultSearch = "";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const communityData: Community = await fetchCommunity(communityName);
        const communitiesData = await fetchCommunities();
        const postsData: AxiosResponse =
          await fetchCommunityPosts(communityName);
        const categoriesData: string[] = await fetchCategories();
        setCommunity(communityData);
        setPosts(postsData);
        setCategories(categoriesData);
        setCommunities(communitiesData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [communityName]);

  const handleNewPostChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;
    setNewPost((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const filesArray = Array.from(e.target.files);
      setNewPost((prev) => ({ ...prev, files: filesArray }));
      const previews = filesArray.map((file) => URL.createObjectURL(file));
      setFilePreviews(previews);
    }
  };

  const handleCreatePostSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      await postPost(community!.name, newPost);
      setShowCreatePostModal(false);
    } catch (error) {
      console.error("Error creating post:", error);
    }
  };

  const handleRatingSubmit = async () => {
    try {
      await postRating(community!.name, newRating);
      setShowRatingModal(false);
    } catch (error) {
      console.error("Error submitting rating:", error);
    }
  };

  const handleDeleteRating = async () => {
    try {
      await deleteRating(community!.name);
      setCurrentRating(null);
    } catch (error) {
      console.error("Error deleting rating:", error);
    }
  };

  if (loading || !community) {
    return <div>Loading...</div>;
  }
  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <>
      <Helmet>
        <title>Community</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <div>
        <Navbar
          userName={userName}
          isLogged={isLogged}
          defaultSearch={defaultSearch}
          onSearch={(searchValue) => {
            window.location.href = `/communities?searchTerms=${searchValue}`;
          }}
        />
        <Modal
          show={showCreatePostModal}
          onHide={() => setShowCreatePostModal(false)}
          size="lg"
        >
          <Modal.Header closeButton>
            <Modal.Title>Create Post</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form
              onSubmit={handleCreatePostSubmit}
              encType="multipart/form-data"
              id="postForm"
            >
              <Form.Group controlId="titleInput" className="mb-3">
                <Form.Label>Title</Form.Label>
                <Form.Control
                  type="text"
                  name="title"
                  value={newPost.title}
                  onChange={handleNewPostChange}
                />
              </Form.Group>
              <Form.Group controlId="bodyInput" className="mb-3">
                <Form.Label>Body</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  name="body"
                  value={newPost.body}
                  onChange={handleNewPostChange}
                />
              </Form.Group>
              <Form.Group controlId="categorySelect" className="mb-3">
                <Form.Label>Category</Form.Label>
                <Form.Select
                  name="category"
                  value={newPost.category}
                  onChange={handleNewPostChange}
                >
                  <option value="" disabled>
                    Choose Category
                  </option>
                  {categories.map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Form.Group controlId="fileInput" className="mb-3">
                <Form.Label>Image</Form.Label>
                <Form.Control
                  type="file"
                  name="files"
                  multiple
                  accept="image/*"
                  onChange={handleFileChange}
                />
                <div
                  id="photo-upload__preview"
                  className="d-flex flex-row mt-2"
                >
                  {filePreviews.map((src, index) => (
                    <img
                      key={index}
                      src={src}
                      alt="Preview"
                      style={{
                        width: "100px",
                        height: "100px",
                        objectFit: "cover",
                        marginRight: "5px",
                      }}
                    />
                  ))}
                </div>
              </Form.Group>
              <Modal.Footer>
                <Button
                  variant="secondary"
                  onClick={() => setShowCreatePostModal(false)}
                >
                  Close
                </Button>
                <Button type="submit" variant="primary">
                  Create Post
                </Button>
              </Modal.Footer>
            </Form>
          </Modal.Body>
        </Modal>

        <Modal
          show={showRatingModal}
          onHide={() => setShowRatingModal(false)}
          centered
        >
          {currentRating === null ? (
            <>
              <Modal.Header closeButton>
                <Modal.Title>Rate Community</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <Form id="ratingForm">
                  <Form.Group className="mb-3">
                    <Form.Label>Rating</Form.Label>
                    <div>
                      <Rating
                        count={5}
                        size={24}
                        activeColor="#ffd700"
                        value={newRating}
                        onChange={(newValue) => setNewRating(newValue)}
                      />
                    </div>
                  </Form.Group>
                </Form>
              </Modal.Body>
              <Modal.Footer>
                <Button
                  variant="secondary"
                  onClick={() => setShowRatingModal(false)}
                >
                  Cancel
                </Button>
                <Button variant="primary" onClick={handleRatingSubmit}>
                  Submit Rating
                </Button>
              </Modal.Footer>
            </>
          ) : (
            <>
              <Modal.Header closeButton>
                <Modal.Title>Your Rating</Modal.Title>
              </Modal.Header>
              <Modal.Body className="d-flex align-items-center justify-content-between">
                <Rating
                  count={5}
                  size={24}
                  value={currentRating.rating}
                  edit={false}
                />
                <Button variant="danger" onClick={handleDeleteRating}>
                  Delete Rating
                </Button>
              </Modal.Body>
              <Modal.Footer>
                <Button
                  variant="secondary"
                  onClick={() => setShowRatingModal(false)}
                >
                  Confirm
                </Button>
              </Modal.Footer>
            </>
          )}
        </Modal>

        <div className="grid grid-cols-4 grid-flow-col h-screen">
          <div className="col-span-1">
            <Sidebar
              communities={communities?.data}
              isAdmin={isAdmin}
              isLogged={isLogged}
              currentPath={window.location.pathname}
            />
          </div>
          <div className="col-span-3 justify-center mr-56">
            <div className="card border-0">
              <div className="card-body">
                <div className="grid grid-cols-2">
                  <div className="w-1/2">
                    {community.portrait ? (
                      <img
                        src={community.portrait}
                        className="w-100 rounded-1 img-thumbnail"
                      />
                    ) : (
                      <img
                        src="/images/default.png"
                        className="w-100 rounded-1 img-thumbnail"
                      />
                    )}
                  </div>
                  <div>
                    <div className="d-flex justify-content-between align-items-center">
                      <h1 className="card-title fw-bold">c/{community.name}</h1>
                      <div>
                        <Button variant="outline-info" onClick={() => {}}>
                          Follow
                        </Button>
                      </div>
                    </div>
                    <div className="mt-2">
                      {community.category.map((cat, index) => (
                        <Link
                          key={index}
                          to={`/communities?searchTerms=&categories=${cat}`}
                          className="text-decoration-none"
                        >
                          <span className="fs-6 pe-auto btn btn-secondary cat-badge p-1 badge">
                            {cat}
                          </span>
                        </Link>
                      ))}
                    </div>
                    <div className="mt-2">
                      <h6 className=" !text-gray-500">
                        Developer:{community.developer}
                      </h6>
                      <h6 className=" !text-gray-500">
                        Publisher: {community.publisher}
                      </h6>
                    </div>
                    <div className="d-flex align-items-center">
                      {community.ratingCount === 0 ? (
                        <h6 className="fw-bold">Rating: No Rating</h6>
                      ) : (
                        <>
                          <h6 className="fw-bold me-2 !text-gray-500">
                            Rating:
                          </h6>
                          <Rating
                            count={5}
                            size={20}
                            value={
                              community.totalRating / community.ratingCount
                            }
                            edit={false}
                          />
                          <p className="ms-2 !text-gray-500">
                            ({community.ratingCount})
                          </p>
                        </>
                      )}
                    </div>
                    <h5 className="card-subtitle  mt-3 mb-1">
                      {community.description}
                    </h5>
                  </div>
                </div>
                <div className="d-flex justify-content-between mb-3">
                  {canEdit && (
                    <Link to={`/community/${community.name}/info`}>
                      <Button variant="primary">Edit Community</Button>
                    </Link>
                  )}
                </div>
                {posts?.data.length === 0 ? (
                  <h3 className="text-center mt-5">No posts yet</h3>
                ) : (
                  <PaginatedPosts postsResponse={posts} />
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default CommunityPage;
