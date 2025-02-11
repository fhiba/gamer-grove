import { useState } from "react";
import { Link } from "react-router-dom";

interface NavbarProps {
  isLogged: boolean;
  username: string | null;
  defaultSearch: string;
}

const Navbar: React.FC<NavbarProps> = ({
  username,
  isLogged,
  defaultSearch,
}) => {
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  return (
    <nav className="shadow-current text-white py-4 px-6 flex justify-between items-center">
      <Link to="/" className="flex items-center space-x-2">
        <img src="../images/default.jpg" className="w-10 h-10" />
        <span className="text-xl font-bold decoration-black no-underline">
          Gamer Grove
        </span>
      </Link>

      <form className="flex items-center w-1/2 max-w-lg">
        <input
          type="search"
          placeholder="Search Community..."
          className="px-4 py-2 rounded-l-md w-full bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring focus:border-green-400"
        />
        <button
          type="submit"
          className="bg-green-500 hover:bg-green-600 px-4 py-2 rounded-r-md text-white"
        >
          Search
        </button>
      </form>

      {!isLogged ? (
        <div className="relative">
          ${username}
          <button
            className="bg-gray-800 p-2 rounded-md text-white"
            onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
          ></button>
          {isUserMenuOpen && (
            <ul className="absolute right-0 mt-2 w-48 bg-gray-800 text-white rounded-md shadow-lg">
              <li>
                <Link
                  to="/profile"
                  className="block px-4 py-2 hover:bg-gray-700"
                >
                  Profile
                </Link>
              </li>
              <li>
                <Link
                  to="/logout"
                  className="block px-4 py-2 hover:bg-gray-700"
                >
                  Logout
                </Link>
              </li>
            </ul>
          )}
        </div>
      ) : (
        <Link
          to="/login"
          className="bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded-md text-white no-underline"
        >
          Login
        </Link>
      )}
    </nav>
  );
};

export default Navbar;
