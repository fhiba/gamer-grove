import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { User } from "../types/User";

const UserInfo = ({ userUrl }: { userUrl: string }) => {
  const [user, setUser] = useState<User | null>(null);
  const { authToken } = useAuth();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await fetch(userUrl, {
          headers: { Authorization: `Bearer ${authToken}` },
        });
        const userData = await response.json();
        setUser(userData);
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };

    fetchUser();
  }, [userUrl, authToken]);

  return (
    <>
      <td>{user ? user.username : "Loading..."}</td>
      <td>{user ? user.email : "Loading..."}</td>
    </>
  );
};

export default UserInfo;
