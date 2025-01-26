import React, { useContext } from "react";
import { AuthContext } from "../context/AuthContext";


const ManageMods:React.FC = () => {
  const { authToken, logout } = useContext(AuthContext);
  console.log(authToken)
  return (
    <div>
      <h1>Manage Mods</h1>
      <p>Your auth token: {authToken}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
};
export default ManageMods;
