// src/context/AuthContext.tsx
import React, { createContext, useState, useEffect, ReactNode } from "react";
import axios from "axios";

// Define the shape of the context data
interface AuthContextType {
  authToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  register: (username: string, password: string, repeatPassword: string, email?: string) => Promise<void>;
}

// Create the context with default values (placeholder functions, etc.)
export const AuthContext = createContext<AuthContextType>({
  authToken: null,
  refreshToken: null,
  isAuthenticated: false,
  login: async () => {},
  logout: () => {},
  register: async () => {},
});

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [authToken, setAuthToken] = useState<string | null>(() => {
    return localStorage.getItem("authToken");
  });
  const [refreshToken, setRefreshToken] = useState<string | null>(() => {
    return localStorage.getItem("refreshToken");
  });

  const isAuthenticated = Boolean(authToken);

  // Whenever the tokens change, store them in localStorage
  useEffect(() => {
    if (authToken) {
      localStorage.setItem("authToken", authToken);
    } else {
      localStorage.removeItem("authToken");
    }

    if (refreshToken) {
      localStorage.setItem("refreshToken", refreshToken);
    } else {
      localStorage.removeItem("refreshToken");
    }
  }, [authToken, refreshToken]);

  /**
   * LOGIN function
   * Since your API does login via a custom header on ANY endpoint, you might:
   * 1) Send an HTTP request with custom credentials in the header
   * 2) Receive authToken and refreshToken in response
   * 3) Save them in state
   */
  const login = async (username: string, password: string) => {
    try {
      // Example: sending a GET or POST to any endpoint with "Authorization" headers
      // Adjust this part based on how your Spring Boot server expects the credentials:
      // e.g. Basic Auth, or some custom header. For demonstration, let's assume Basic:
      const response = await axios.get("http://localhost:8080/api", {
        headers: {
          Authorization: `Basic ${btoa(`${username}:${password}`)}`,
        },
      });
      console.log(response)
      // The server is assumed to return an object with these tokens:
      const { 
        'x-gamergrove-authtoken': returnedAuthToken, 'x-gamergrove-refreshtoken': returnedRefreshToken } = response.headers;

      setAuthToken(returnedAuthToken);
      setRefreshToken(returnedRefreshToken);
    } catch (error) {
      console.error("Login error:", error);
      throw error; // re-throw to handle in UI
    }
  };

  /**
   * LOGOUT function
   * Clear tokens from state (and from localStorage by effect).
   */
  const logout = () => {
    setAuthToken(null);
    setRefreshToken(null);
  };

  /**
   * REGISTER function
   * e.g. POST /users with the form data
   */
  const register = async (username: string, password: string, repeatPassword: string, email?: string) => {
    try {
        
        const response = await axios.post(
            "http://localhost:8080/api/users", 
            {
                "username":username,
                "email":email,
                "password":password,
                "repeatPassword": repeatPassword
            }, 
            {
                headers: {
                  'Content-Type': 'application/vnd.users.v1+json', // Set the content type
                }
            });
        console.log("Registered user:", response.data);
        // Possibly auto-login or do something else upon successful registration
    } catch (error) {
      console.error("Registration error:", error);
      throw error; // re-throw to handle in UI
    }
  };

  // Provide everything to children
  return (
    <AuthContext.Provider
      value={{
        authToken,
        refreshToken,
        isAuthenticated,
        login,
        logout,
        register,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
