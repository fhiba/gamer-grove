import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import "bootstrap/dist/css/bootstrap.min.css";
import { Helmet } from "react-helmet-async";
import Navbar from "../components/Navbar";

const Login: React.FC = () => {
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login(username, password);
      navigate("/home"); // Redirect after successful login
    } catch (err) {
      setError("Invalid credentials or server error.");
    }
  };

  return (
    <>
      <Helmet>
        <title>Login</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <Navbar isLogged={false} username={null} defaultSearch={""} />
      <div className="container-fluid vh-100 d-flex align-items-center justify-content-center ">
        <div className="card shadow p-4 w-25 background-of-card">
          <div className="card-body text-center">
            <h2 className="fw-bold mb-3 !text-gray-500">Login</h2>
            {error && <p className="text-danger">{error}</p>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="username" className="form-label !text-gray-500">
                  Username
                </label>
                <input
                  type="text"
                  id="username"
                  className="form-control"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />
              </div>
              <div className="mb-3">
                <label htmlFor="password" className="form-label !text-gray-500">
                  Password
                </label>
                <input
                  type="password"
                  id="password"
                  className="form-control"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
              <button className="btn btn-primary w-100" type="submit">
                Login
              </button>
            </form>
            <p className="mt-3 !text-gray-500">Or</p>
            <a href="/register" className="d-block">
              Register
            </a>
            <a
              href="/auth/forgotCredentials"
              className="link-underline-opacity-0 mt-2 d-block"
            >
              Forgot Password?
            </a>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
