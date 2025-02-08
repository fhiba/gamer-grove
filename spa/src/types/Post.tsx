export interface Post {
  author: string;
  body: string;
  category: string;
  community: string;
  date: string;
  deleted: boolean;
  grooviness: number;
  id: number;
  images: [string];
  media: boolean;
  self: string;
  title: string;
}

export interface PaginatedPost {
  data: Post[];
  totalPages: number;
  currentPage: number;
}
