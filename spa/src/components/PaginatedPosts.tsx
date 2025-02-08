import React, { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import PostComponent from "./PostComponent";

const PaginatedPosts = ({ posts }) => {
  const navigate = useNavigate();
  const { page } = useParams(); // Get the page number from the URL
  const postsPerPage = 10;

  const currentPage = Math.max(
    1,
    Math.min(Number(page) || 1, Math.ceil(posts.length / postsPerPage)),
  );

  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

  const totalPages = Math.ceil(posts.length / postsPerPage);
  console.log(posts.length);
  console.log("total pages : ", totalPages);

  const paginate = (pageNumber) => {
    if (pageNumber >= 1 && pageNumber <= totalPages) {
      navigate(`/posts/page/${pageNumber}`);
    }
  };

  return (
    <div>
      {/* Render current posts */}
      <div>
        {currentPosts.map((post, i) => (
          <PostComponent post={post} key={i} />
        ))}
      </div>

      <div
        style={{
          marginTop: "20px",
          display: "flex",
          alignItems: "center",
          gap: "10px",
        }}
      >
        <button
          onClick={() => paginate(currentPage - 1)}
          disabled={currentPage === 1}
          style={{
            padding: "5px 10px",
            backgroundColor: currentPage === 1 ? "#ddd" : "#333",
            color: "#fff",
            border: "none",
            cursor: currentPage === 1 ? "not-allowed" : "pointer",
          }}
        >
          Previous
        </button>

        {Array.from({ length: totalPages }, (_, index) => {
          const pageNumber = index + 1;
          return (
            <button
              key={pageNumber}
              onClick={() => paginate(pageNumber)}
              style={{
                margin: "0 5px",
                backgroundColor: currentPage === pageNumber ? "#333" : "#eee",
                color: currentPage === pageNumber ? "#fff" : "#000",
                border: "none",
                padding: "5px 10px",
                cursor: "pointer",
              }}
            >
              {pageNumber}
            </button>
          );
        })}

        <button
          onClick={() => paginate(currentPage + 1)}
          disabled={currentPage === totalPages}
          style={{
            padding: "5px 10px",
            backgroundColor: currentPage === totalPages ? "#ddd" : "#333",
            color: "#fff",
            border: "none",
            cursor: currentPage === totalPages ? "not-allowed" : "pointer",
          }}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default PaginatedPosts;
