import React, { useEffect } from "react";
import { format } from "date-fns";

interface Post {
  id: number;
  title: string;
  body: string;
  grooviness: number;
  date: string;
}

interface PostProps {
  post: Post;
}
const PostComponent: React.FC<PostProps> = ({ post }) => {
  return (
    <div className="border border-gray-10 rounded-lg p-5">
      <h2 className="text-2xl font-bold">{post.title}</h2>
      <div className="content-between">
        <p className="">{post.body}</p>
        <p className="text-gray-500">
          {format(new Date(post.date), "MMMM d, yyyy h:mm a")}
        </p>
      </div>
    </div>
  );
};

export default PostComponent;
