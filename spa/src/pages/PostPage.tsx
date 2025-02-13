import React, { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import axios, { AxiosResponse } from "axios";
import {
  deleteComment,
  deletePost,
  fetchAuthor,
  fetchComments,
  fetchCommunities,
  fetchCommunity,
  fetchPostById,
  postComment,
} from "../api";
import { Comment } from "../types/Comment";
import { Community } from "../types/Community";
import { Post } from "../types/Post";
import { decodeToken, JwtPayload } from "../utils/jwt";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import CommunityCard from "../components/CommunityCard";
import { User } from "../types/User";
import { Helmet } from "react-helmet-async";

const PostPage: React.FC = () => {
  const { postId } = useParams();
  const navigate = useNavigate();

  const [post, setPost] = useState<Post>();
  const [author, setAuthor] = useState<User>();
  const [comments, setComments] = useState<Comment[]>([]);
  const [community, setCommunity] = useState<Community>();
  const [communities, setCommunities] = useState<AxiosResponse>();
  const [loading, setLoading] = useState<boolean>();
  const [error, setError] = useState<string | null>(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [canDelete, setCanDelete] = useState(false);
  const [upComments, setUpComments] = useState([]);
  const [downComments, setDownComments] = useState([]);
  const [isGrooved, setIsGrooved] = useState(0);

  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, []);
  }
  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const postData: Post = await fetchPostById(postId);
        const commentsData: Comment[] = await fetchComments(postId);
        const communitiesData = await fetchCommunities();
        setComments(commentsData);
        setCommunities(communitiesData);
        setPost(postData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [postId]);

  useEffect(() => {
    if (post && post.community && post.author) {
      console.log("Fetching community with:", post.community);
      fetchCommunity(post.community)
        .then((communityData: Community) => {
          setCommunity(communityData);
        })
        .catch((error) => {
          console.error("Error fetching community:", error);
        });
      fetchAuthor(post.author)
        .then((authorData) => {
          setAuthor(authorData);
        })
        .catch((error) => {
          console.error("Error fetching post author:", error);
        });
    }
  }, [post]);
  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  const postGroovyUpdate = (updateType) => {
    //TODO:send to api.tsx
    axios
      .post(`/api/posts/${postId}/groovy`, { updateType })
      .then((response) => {
        setPost((prev) => ({ ...prev, grooviness: response.data.grooviness }));
        setIsGrooved(updateType ? 1 : -1);
      })
      .catch((error) =>
        console.error("Error updating post grooviness:", error),
      );
  };

  const commentGroovyUpdate = (updateType, commentId) => {
    //TODO:send to api.tsx
    axios
      .post(`/api/comments/${commentId}/groovy`, { updateType })
      .then((response) => {
        setComments((prevComments) =>
          prevComments.map((c) =>
            c.id === commentId
              ? { ...c, grooviness: response.data.grooviness }
              : c,
          ),
        );
      })
      .catch((error) =>
        console.error("Error updating comment grooviness:", error),
      );
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    console.log("posting comment");
    const formData = new FormData(e.target);
    const body = formData.get("body");
    try {
      const data = await postComment(postId, body);
      setComments((prev) => [...prev, data]);
      e.target.reset();
    } catch (error) {
      console.error("Error posting comment:", error);
    }
  };

  const handlePostDelete = async () => {
    deletePost(postId);
    navigate("/");
  };

  const handleCommentDelete = async (commentId) => {
    const data = await deleteComment(postId, commentId);
    setComments((prev) => prev.filter((c) => c.id !== commentId));
  };

  if (!post) return <div>Loading...</div>;
  console.log(comments);
  return (
    <>
      <Helmet>
        <title>Post</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <div>
        <Navbar
          userName={userName}
          isLoggedIn={isLogged}
          defaultSearch={defaultSearch}
          onSearch={(searchValue) => {
            window.location.href = `/communities?searchTerms=${searchValue}`;
          }}
        />

        <div className="grid grid-cols-3">
          <Sidebar
            isAdmin={isAdmin}
            isLogged={isLogged}
            communities={communities?.data}
            currentPath={location.pathname}
          />
          <div>
            <div>
              <div>
                <div>
                  <p className="fw-semibold card-subtitle mb-1">
                    <Link
                      to={`/community/${post.community}`}
                      className="text-decoration-none text-light text-body-primary text-white"
                    >
                      c/{post.community}
                    </Link>
                    <Link to={`/all?category=${post.category}`}>
                      <span
                        className={`badge rounded-pill pe-auto ${post.category}`}
                      >
                        {post.category}
                      </span>
                    </Link>
                  </p>

                  {post.deleted ? (
                    <>
                      <h4 className="card-title fw-bold mb-0">Post Deleted</h4>
                      <p className="card-subtitle mb-4">u/Anon</p>
                    </>
                  ) : (
                    <>
                      <h4 className="card-title fw-bold mb-0">{post.title}</h4>
                      <Link
                        to={post.author}
                        className="text-decoration-none text-light"
                      >
                        <p className="card-subtitle mb-4">
                          u/{author?.username}
                        </p>
                      </Link>
                      <p className="card-text text-white">{post.body}</p>

                      {post.media && post.images.length > 0 && (
                        <div id="carouselExample" className="carousel slide">
                          <div className="carousel-inner bg-dark">
                            {post.images.map((image, index) => (
                              <div
                                key={index}
                                className={`carousel-item ${index === 0 ? "active" : ""}`}
                              >
                                <img
                                  src={post.images[index]}
                                  className="d-block m-auto carousel-img"
                                />
                              </div>
                            ))}
                          </div>
                          {post.images.length > 1 && (
                            <>
                              <button
                                className="carousel-control-prev"
                                type="button"
                                data-bs-target="#carouselExample"
                                data-bs-slide="prev"
                              >
                                <span
                                  className="carousel-control-prev-icon"
                                  aria-hidden="true"
                                />
                                <span className="visually-hidden">
                                  Previous
                                </span>
                              </button>
                              <button
                                className="carousel-control-next"
                                type="button"
                                data-bs-target="#carouselExample"
                                data-bs-slide="next"
                              >
                                <span
                                  className="carousel-control-next-icon"
                                  aria-hidden="true"
                                />
                                <span className="visually-hidden">Next</span>
                              </button>
                            </>
                          )}
                        </div>
                      )}

                      {canDelete && (
                        <>
                          <button
                            type="button"
                            className="btn btn-danger"
                            data-bs-toggle="modal"
                            data-bs-target="#deletePostModal"
                          >
                            Delete Post
                          </button>
                          <div
                            className="modal fade"
                            id="deletePostModal"
                            tabIndex="-1"
                            aria-hidden="true"
                          >
                            <div className="modal-dialog modal-dialog-centered">
                              <div className="modal-content">
                                <div className="modal-header">
                                  <h1 className="modal-title fs-5">
                                    Delete Confirmation
                                  </h1>
                                  <button
                                    type="button"
                                    className="btn-close"
                                    data-bs-dismiss="modal"
                                    aria-label="Close"
                                  />
                                </div>
                                <div className="modal-footer">
                                  <button
                                    data-bs-dismiss="modal"
                                    className="btn btn-secondary btn-sm"
                                  >
                                    Cancel
                                  </button>
                                  <button
                                    className="btn btn-danger btn-sm"
                                    onClick={handlePostDelete}
                                  >
                                    Delete Post
                                  </button>
                                </div>
                              </div>
                            </div>
                          </div>
                        </>
                      )}

                      <div className="d-flex align-items-center">
                        <p className="card-text mb-0">
                          <small className=" text-gray-500">
                            {new Date(post.date).toLocaleString()}
                          </small>
                        </p>
                        <span
                          className="text-gray-500"
                          style={{ marginLeft: "1rem" }}
                        >
                          {post.grooviness}
                        </span>
                        <div className="d-flex flex-row mb-1">
                          {isGrooved === 1 ? (
                            <>
                              <button
                                onClick={() => postGroovyUpdate(true)}
                                className="btn"
                              >
                                <i className="fas fa-arrow-up text-primary" />
                              </button>
                              <button
                                onClick={() => postGroovyUpdate(false)}
                                className="btn"
                              >
                                <i className="fas fa-arrow-down" />
                              </button>
                            </>
                          ) : isGrooved === -1 ? (
                            <>
                              <button
                                onClick={() => postGroovyUpdate(true)}
                                className="btn"
                              >
                                <i className="fas fa-arrow-up" />
                              </button>
                              <button
                                onClick={() => postGroovyUpdate(false)}
                                className="btn"
                              >
                                <i className="fas fa-arrow-down text-danger" />
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => postGroovyUpdate(true)}
                                className="btn"
                              >
                                <i className="fas fa-arrow-up" />
                              </button>
                              <button
                                onClick={() => postGroovyUpdate(false)}
                                className="btn"
                              >
                                <i className="fas fa-arrow-down" />
                              </button>
                            </>
                          )}
                        </div>
                      </div>
                    </>
                  )}
                </div>
              </div>

              <div className="card ">
                {!post.deleted && (
                  <div className="card-body ">
                    <form onSubmit={handleCommentSubmit}>
                      <div className="form-outline form-white mb-4">
                        <textarea
                          name="body"
                          className="w-100 rounded-3 pt-2 ps-2 text-white"
                          rows="4"
                          placeholder="Enter your comment here..."
                        />
                      </div>
                      <button className="btn btn-primary" type="submit">
                        Comment
                      </button>
                    </form>
                  </div>
                )}
                {comments.length > 0 ? (
                  <ul className="list-group">
                    {comments.map((comment, i) =>
                      !comment.deleted ? (
                        <li
                          key={i}
                          className="list-group-item d-flex justify-content-between align-items-start bg-body-secondary"
                        >
                          <Link to={`/user/${comment.author}/userPosts`}>
                            <img
                              src="/images/default-avatar-icon.jpg"
                              height="50"
                              width="50"
                              className="rounded-5"
                              alt="Profile"
                            />
                          </Link>
                          <div className="ms-2 me-auto">
                            <Link
                              to={`/user/${comment.author}/userPosts`}
                              className="text-decoration-none text-dark"
                            >
                              <div className="fw-bold">{comment.author}</div>
                            </Link>
                            <p className="text-break">{comment.body}</p>
                            <p>
                              <small className="text-body-secondary">
                                {new Date(comment.date).toLocaleString()}
                              </small>
                            </p>
                          </div>
                          <div className="d-flex align-items-center justify-content-center">
                            <div className="d-flex flex-column">
                              {upComments.includes(comment) ? (
                                <>
                                  <button
                                    onClick={() =>
                                      commentGroovyUpdate(true, comment.id)
                                    }
                                    className="btn"
                                  >
                                    <i className="fas fa-arrow-up text-primary" />
                                  </button>
                                  <span className="grooviness-count d-flex justify-content-center align-items-center">
                                    {comment.grooviness}
                                  </span>
                                  <button
                                    onClick={() =>
                                      commentGroovyUpdate(false, comment.id)
                                    }
                                    className="btn"
                                  >
                                    <i className="fas fa-arrow-down" />
                                  </button>
                                </>
                              ) : downComments.includes(comment) ? (
                                <>
                                  <button
                                    onClick={() =>
                                      commentGroovyUpdate(true, comment.id)
                                    }
                                    className="btn"
                                  >
                                    <i className="fas fa-arrow-up" />
                                  </button>
                                  <span className="grooviness-count d-flex justify-content-center align-items-center">
                                    {comment.grooviness}
                                  </span>
                                  <button
                                    onClick={() =>
                                      commentGroovyUpdate(false, comment.id)
                                    }
                                    className="btn"
                                  >
                                    <i className="fas fa-arrow-down text-danger" />
                                  </button>
                                </>
                              ) : (
                                <>
                                  <button
                                    onClick={() =>
                                      commentGroovyUpdate(true, comment.id)
                                    }
                                    className="btn"
                                  >
                                    <i className="fas fa-arrow-up" />
                                  </button>
                                  <span className="grooviness-count d-flex justify-content-center align-items-center">
                                    {comment.grooviness}
                                  </span>
                                  <button
                                    onClick={() =>
                                      commentGroovyUpdate(false, comment.id)
                                    }
                                    className="btn"
                                  >
                                    <i className="fas fa-arrow-down" />
                                  </button>
                                </>
                              )}
                            </div>
                          </div>
                          <div className="d-flex justify-content-center align-items-center">
                            {canDelete && (
                              <>
                                <button
                                  className="btn btn-danger btn-sm align-content-center"
                                  data-bs-toggle="modal"
                                  data-bs-target={`#deleteComment${comment.id}Modal`}
                                >
                                  <i className="fas fa-trash" />
                                </button>
                                <div
                                  className="modal fade"
                                  id={`deleteComment${comment.id}Modal`}
                                  tabIndex="-1"
                                  aria-hidden="true"
                                >
                                  <div className="modal-dialog modal-dialog-centered">
                                    <div className="modal-content">
                                      <div className="modal-header">
                                        <h1 className="modal-title fs-5">
                                          Delete Comment
                                        </h1>
                                        <button
                                          type="button"
                                          className="btn-close"
                                          data-bs-dismiss="modal"
                                          aria-label="Close"
                                        />
                                      </div>
                                      <div className="modal-footer">
                                        <button
                                          data-bs-dismiss="modal"
                                          className="btn btn-secondary btn-sm"
                                        >
                                          Cancel
                                        </button>
                                        <button
                                          className="btn btn-danger btn-sm"
                                          onClick={() =>
                                            handleCommentDelete(comment.id)
                                          }
                                        >
                                          Delete Comment
                                        </button>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </>
                            )}
                          </div>
                        </li>
                      ) : (
                        <li
                          key={comment.id}
                          className="list-group-item d-flex justify-content-between align-items-start bg-body-secondary"
                        >
                          <div className="ms-2 me-auto">
                            <div className="fw-bold">Anon</div>
                            <p>Comment Deleted</p>
                          </div>
                        </li>
                      ),
                    )}
                    <div className="d-flex justify-content-center align-items-center mt-3">
                      {comments.length === 0 && (
                        <span className="badge bg-danger">
                          Invalid page number
                        </span>
                      )}
                      {comments.length > 0}
                    </div>
                  </ul>
                ) : (
                  <p className="align-items-center text-gray-500">
                    No comments found
                  </p>
                )}
              </div>
            </div>
          </div>
          <div>
            <div>
              <CommunityCard community={community} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default PostPage;
