import { useState } from "react";
import { Link } from "react-router-dom";

interface NavbarProps {
  isLogged: boolean;
  username: string | undefined;
  defaultSearch: string;
}

const Navbar: React.FC<NavbarProps> = ({
  username,
  isLogged,
  defaultSearch,
}) => {
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState(defaultSearch);
  return (
    <nav className=" text-white py-4 px-6 shadow-md">
      <div className="container mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <Link
            to="/"
            className="flex items-center !no-underline text-white left-0"
          >
            <img src="/images/favicon.ico" className="w-10 h-10" />
            <span className="text-2xl font-bold ml-2">Gamer Grove</span>
          </Link>
        </div>

        <form
          action="/communities"
          method="get"
          className="flex items-center w-1/2 max-w-lg"
        >
          <input
            type="search"
            name="searchTerms"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search Community..."
            className="form-control w-full bg-gray-800 text-white border border-gray-700 rounded-b-sm px-3"
          />
          <button type="submit" className="btn btn-outline-success px-4 !ml-2">
            Search
          </button>
        </form>

        <div className="flex items-center space-x-4">
          {isLogged ? (
            <div className="relative">
              <span className="text-white">Hi, {username}!</span>
              <button
                className="bg-gray-800 p-2 rounded-md ml-2"
                onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
              >
                <i className="fa fa-user" aria-hidden="true"></i>
              </button>
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
              className="btn btn-outline-primary px-4 py-2 rounded-md !no-underline"
            >
              Login
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
