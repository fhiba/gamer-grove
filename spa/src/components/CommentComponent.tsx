import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Comment } from "../types/Comment.tsx";

interface CommentComponentProps {
  comment: Comment;
}

const CommentComponent: React.FC<CommentComponentProps> = ({ comment }) => {
  const navigate = useNavigate();

  return (
    <div>
      <div className="ms-2 me-auto">
        <Link className="fw-bold" to={`/user/${comment.author}/userPosts`}>
          {comment.author}
        </Link>
        <p className="text-break">{comment.body}</p>
        <p>
          <small className="text-body-secondary">
            {new Date(comment.date).toLocaleString()}
          </small>
        </p>
      </div>
    </div>
  );
};

export default CommentComponent;
