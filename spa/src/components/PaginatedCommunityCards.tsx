import React, { useEffect, useState } from "react";
import axios, { AxiosResponse } from "axios";
import { User } from "../types/User";
import { Community } from "../types/Community";
import CommunityCard from "./CommunityCard";

interface PaginatedCommunitiesProps {
  communitiesResponse: AxiosResponse | undefined;
  tab: string;
  user: User;
}

const PaginatedCommunityCards: React.FC<PaginatedCommunitiesProps> = ({
  communitiesResponse,
}) => {
  const [communities, setCommunities] = useState<Community[]>(
    communitiesResponse.data,
  );

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

  let parsedLinks = parseLinkHeader(communitiesResponse?.headers.link);
  const loadPreviousPage = () => {
    console.log(parsedLinks["prev"]);
    axios.get(parsedLinks["prev"]).then((response) => {
      setCommunities(response.data);
      parsedLinks = parseLinkHeader(response.headers.link);
    });
  };

  const loadNextPage = () => {
    console.log(parsedLinks["next"]);
    axios.get(parsedLinks["next"]).then((response) => {
      setCommunities(response.data);
      parsedLinks = parseLinkHeader(response.headers.link);
    });
  };

  return (
    <div>
      <div>
        {communities.map((community, i) => (
          <CommunityCard community={community} key={i} />
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

export default PaginatedCommunityCards;
