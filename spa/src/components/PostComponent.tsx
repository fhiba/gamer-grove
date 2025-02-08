import React from "react";
import { format } from "date-fns";
import { Post } from "../types/Post";

interface PostProps {
  post: Post;
}

function truncateText(str: string, maxLength: number) {
  return str.length > maxLength ? str.substring(0, maxLength) + "..." : str;
}

const PostComponent: React.FC<PostProps> = ({ post, key }) => {
  return (
    <a
      key={key}
      href={`/post/${post.id}`}
      className="card-link text-decoration-none"
    >
      <div className="card mb-3 border-none">
        <div className="card-body">
          <div className="title-container">
            <p className="fw-semibold card-subtitle">/{post.community}</p>
            <span className={`badge rounded-pill mb-1 ${post.category}`}>
              {post.category}
            </span>
          </div>
          {!post.deleted && (
            <>
              <h4 className="card-title fw-bold">{post.title}</h4>
              <p className="card-text post-body">
                {truncateText(post.body, 100)}
              </p>
            </>
          )}
          <div className="d-flex row-cols-2 justify-content-between mt-1">
            <p>
              <small className="text-gray-50">
                {new Date(post.date).toLocaleDateString()}
              </small>
            </p>
            <div className="d-flex justify-content-end align-items-end">
              <span className="badge text-bg-dark pillUpvoteHome bg-transparent border border-light rounded-2 border-1">
                {post.grooviness}
                <i className="fa fa-thumbs-up ms-2" />
              </span>
            </div>
          </div>
        </div>
      </div>
    </a>
  );
};

export default PostComponent;
