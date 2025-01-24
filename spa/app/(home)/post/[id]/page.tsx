export const experimental_ppr = true;

const Post = async ({ params }: { params: Promise<{ id: string }> }) => {
  const id = (await params).id;
  const API_URL = process.env.LOCAL_API_URL;
  const data = await fetch(`${API_URL}/posts/${id}`);
  const posts = await data.json();
  return (
    <article>
      <h1>{posts.title}</h1>
    </article>
  );
};

export default Post;
