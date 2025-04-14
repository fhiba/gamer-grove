import { useState, useEffect } from "react";
import { Modal, Button, Table, Form } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { fetchCommunities, fetchModerators } from "../api";
import { AxiosResponse } from "axios";
import { User } from "../types/User";
import { decodeToken, JwtPayload } from "../utils/jwt";
import { useAuth } from "../context/AuthContext";
import { Moderator } from "../types/Moderators";
import UserInfo from "../components/UserInfo";
import { Helmet } from "react-helmet-async";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

interface EnrichedModerator extends Moderator {
  user: User;
}

const ModeratorManagement = () => {
  const [moderators, setModerators] = useState<EnrichedModerator[]>([]);
  const [allCommunities, setAllCommunities] = useState<AxiosResponse>();
  const [selectedCommunity, setSelectedCommunity] = useState("");
  const [filterUsername, setFilterUsername] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedMod, setSelectedMod] = useState<Moderator | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, [authToken]);
  }

  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN" ? true : false;
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const modRes = await fetchModerators(authToken);
        const moderatorsArray = Array.isArray(modRes) ? modRes : [];
        const communityData = await fetchCommunities();
        setAllCommunities(communityData);
        const modsWithUsers = await Promise.all(
          moderatorsArray.map(async (mod: Moderator) => {
            try {
              const userResponse = await fetch(mod.user, {
                headers: { Authorization: `Bearer ${authToken}` },
              });
              const userData: User = await userResponse.json();
              return { ...mod, user: userData };
            } catch (error) {
              console.error("Error fetching user:", error);
              return { ...mod, user: null };
            }
          }),
        );

        setModerators(modsWithUsers);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [authToken]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  const handleFilter = async () => {
    try {
      const filteredModerators = await fetchModerators(
        authToken,
        filterUsername,
        selectedCommunity,
      );
      setModerators(filteredModerators);
    } catch (error) {
      console.error("Error fetching filtered moderators:", error);
    }
  };

  return (
    <>
      <Helmet>
        <title>Manage Mods</title>
        <link rel="icon" type="image/x-icon" />
      </Helmet>
      <Navbar
        userName={userName}
        isLoggedIn={isLogged}
        defaultSearch={defaultSearch}
        onSearch={(searchValue) => {
          window.location.href = `/communities?searchTerms=${searchValue}`;
        }}
      />

      <div className="grid grid-cols-3">
        <Sidebar
          isAdmin={isAdmin}
          isLogged={isLogged}
          communities={allCommunities?.data}
          currentPath={location.pathname}
        />
        <div className="container mt-5">
          <h3>Moderators</h3>
          <div className="d-flex mb-3">
            <Form.Control
              type="text"
              placeholder="Filter by username"
              className="me-2"
              value={filterUsername}
              onChange={(e) => setFilterUsername(e.target.value)}
            />
            <Form.Select
              onChange={(e) => setSelectedCommunity(e.target.value)}
              className="me-2"
            >
              <option value="">Filter by Community</option>
              {allCommunities?.data.map((c) => (
                <option key={c.id} value={c.name}>
                  {c.name}
                </option>
              ))}
            </Form.Select>
            <Button variant="secondary" onClick={handleFilter}>
              Filter
            </Button>
          </div>

          <Table striped bordered hover>
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Community</th>
                <th>Granted Date</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {moderators.map((mod) => (
                <tr key={mod.self}>
                  <UserInfo userUrl={mod.user} />
                  <td>{mod.community}</td>
                  <td>{new Date(mod.sinceDate).toLocaleDateString()}</td>
                  <td>
                    <Button
                      variant="danger"
                      onClick={() => {
                        setSelectedMod(mod);
                        setShowModal(true);
                      }}
                    >
                      Remove
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
        <Modal show={showModal} onHide={() => setShowModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Remove Moderator</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            Are you sure you want to remove {selectedMod?.user?.username} from{" "}
            {selectedMod?.community}?
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="danger">Remove</Button>
          </Modal.Footer>
        </Modal>
      </div>
    </>
  );
};

export default ModeratorManagement;
