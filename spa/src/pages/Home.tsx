import { Outlet, Link } from "react-router-dom";
export default function Home() {
  return  <div>
  <nav style={{ marginBottom: "1rem" }}>
    <Link to="/">Home</Link> |{" "}
    <Link to="/login">Login</Link> |{" "}
    <Link to="/register">Register</Link> |{" "}
    <Link to="/dashboard">Dashboard</Link>
  </nav>
  <Outlet />
  </div>;
}
