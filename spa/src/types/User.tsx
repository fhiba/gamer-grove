export interface User {
  email: string;
  followedCommunities: string[];
  likedPosts: string[];
  locale: string;
  owner: boolean;
  posts: string[];
  self: string;
  username: string;
  verified: boolean;
  profileImage: string;
}
