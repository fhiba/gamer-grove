import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import PostComponent from "./PostComponent";
import { Post } from "../types/Post";
import axios, { AxiosResponse } from "axios";
import { User } from "../types/User";

interface PaginatedPostsProps {
  postsResponse: AxiosResponse | undefined;
  tab: string;
  user: User;
}

const PaginatedPosts: React.FC<PaginatedPostsProps> = ({
  postsResponse,
  tab,
  user,
}) => {
  const [posts, setPosts] = useState<Post[]>(postsResponse.data);

  function parseLinkHeader(header) {
    const links = {};
    if (!header) {
      return links;
    }

    const parts = header.split(",");
    parts.forEach((part) => {
      const section = part.split(";");
      if (section.length < 2) return;

      const url = section[0].trim().replace(/^<|>$/g, "");

      const relMatch = section[1].trim().match(/rel="(.*)"/);
      if (relMatch && relMatch[1]) {
        const rel = relMatch[1];
        links[rel] = url;
      }
    });

    return links;
  }

  let parsedLinks = parseLinkHeader(postsResponse?.headers.link);
  const loadPreviousPage = () => {
    console.log(parsedLinks["prev"]);
    axios.get(parsedLinks["prev"]).then((response) => {
      setPosts(response.data);
      parsedLinks = parseLinkHeader(response.headers.link);
    });
  };

  const loadNextPage = () => {
    console.log(parsedLinks["next"]);
    axios.get(parsedLinks["next"]).then((response) => {
      setPosts(response.data);
      parsedLinks = parseLinkHeader(response.headers.link);
    });
  };

  return (
    <div>
      <div>
        {posts.map((post, i) => (
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
          onClick={() => loadPreviousPage()}
          style={{
            padding: "5px 10px",
            color: "#000",
            border: "none",
          }}
        >
          Previous
        </button>
        <button
          style={{
            margin: "0 5px",
            border: "none",
            padding: "5px 10px",
            cursor: "pointer",
            background: "black",
          }}
        >
          {1}
        </button>
        <button
          onClick={() => loadNextPage()}
          style={{
            padding: "5px 10px",
            color: "#000",
            border: "none",
          }}
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default PaginatedPosts;
