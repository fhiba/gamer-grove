// src/pages/RegisterPage.tsx
import React, { useState, useContext } from "react";
import { AuthContext } from "../context/AuthContext";

const Register: React.FC = () => {
  const { register } = useContext(AuthContext);

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await register(username, password, repeatPassword, email);
      setMessage("Registration successful! You can now login.");
      setError(null);
      // Optionally, auto-navigate to login page or auto-login
    } catch (err) {
      setError("Registration failed. Check your input or try again later.");
      setMessage(null);
    }
  };

  return (
    <div>
      <h1>Register</h1>
      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Email (not optional):</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Repeat Password:</label>
          <input
            type="password"
            value={repeatPassword}
            onChange={(e) => setRepeatPassword(e.target.value)}
            required
          />
        </div>
        
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

export default Register;
