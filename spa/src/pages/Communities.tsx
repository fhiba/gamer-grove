import React, { useState, useEffect, ChangeEvent, FormEvent } from "react";
import { Link } from "react-router-dom";
import { Modal, Button, Accordion, Toast } from "react-bootstrap";
import Sidebar from "../components/Sidebar";
import { fetchCommunities, fetchCategories, fetchCommunity } from "../api";
import { useAuth } from "../context/AuthContext";
import { decodeToken, JwtPayload } from "../utils/jwt";
import Navbar from "../components/Navbar";
import { AxiosResponse } from "axios";
import PaginatedCommunityCards from "../components/PaginatedCommunityCards";
import PaginatedPosts from "../components/PaginatedPosts";

const CommunitiesPage: React.FC = () => {
  const [communities, setCommunities] = useState<AxiosResponse>();
  const [categories, setCategories] = useState<string[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [showOnboardingModal, setShowOnboardingModal] =
    useState<boolean>(false);
  const [showUnverifiedModal, setShowUnverifiedModal] =
    useState<boolean>(false);
  const [showToast, setShowToast] = useState<boolean>(false);
  const [toastHeader, setToastHeader] = useState<string>("");
  const [toastBody, setToastBody] = useState<string>("");

  const [decoded, setDecoded] = useState<JwtPayload | null>(null);
  const { authToken } = useAuth();
  if (authToken !== null) {
    useEffect(() => {
      const payload = decodeToken(authToken);
      setDecoded(payload);
    }, [authToken]);
  }
  const userName = decoded?.sub;
  const isAdmin = decoded?.role === "ROLE_ADMIN";
  const isVerified = decoded?.role !== "ROLE_USER";
  const isLogged = decoded !== null ? true : false;
  const defaultSearch = "";

  const urlIncludes = (str: string): boolean =>
    window.location.href.includes(str);
  const hasToast = urlIncludes("verifySuccess");
  const hasRegistered = urlIncludes("registerSuccess");
  const resendVerification = urlIncludes("resendVerification");

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const communitiesData = await fetchCommunities();
        const categoriesData = await fetchCategories();
        setCommunities(communitiesData);
        setCategories(categoriesData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred",
        );
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  useEffect(() => {
    //TODO: cambiar de url a query params para la api. <- posbilemente desaparezca este if porque ya traigo todo filtrado??
    if (
      isVerified &&
      !selectedCategories.length &&
      !urlIncludes("categories=")
    ) {
      setShowOnboardingModal(true);
    }
    if (!isVerified) {
      setShowUnverifiedModal(true);
    }
    if (hasToast) {
      setToastHeader("Notification");
      setToastBody("Your account has been verified successfully!");
      setShowToast(true);
    } else if (hasRegistered) {
      setToastHeader("Welcome");
      setToastBody("Registration successful!");
      setShowToast(true);
    } else if (resendVerification) {
      setToastHeader("Notification");
      setToastBody("Verification email sent!");
      setShowToast(true);
    }
  }, [isVerified, selectedCategories]);

  const addCategory = (category: string) => {
    if (!selectedCategories.includes(category)) {
      setSelectedCategories([...selectedCategories, category]);
    }
  };

  const removeCategory = (category: string) => {
    setSelectedCategories(selectedCategories.filter((cat) => cat !== category));
  };

  const handleCategoryFormSubmit = () => {
    const query = selectedCategories.join(",");
    window.location.href = `/communities?searchTerms=&categories=${query}`;
  };

  if (loading) {
    return <div>Loading...</div>;
  }
  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <Navbar
        userName={userName}
        isLoggedIn={isLogged}
        defaultSearch={defaultSearch}
        onSearch={(searchValue) => {
          window.location.href = `/communities?searchTerms=${searchValue}`;
        }}
      />
      <Modal
        show={showOnboardingModal}
        onHide={() => setShowOnboardingModal(false)}
        backdrop="static"
        keyboard={false}
        size="lg"
      >
        <Modal.Body className="text-black">
          {!isVerified ? (
            <div>
              <p>Please verify your account.</p>
              <Link to="/auth/resend-verification">
                <Button variant="primary" size="sm">
                  Resend Verification Email
                </Button>
              </Link>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setShowOnboardingModal(false)}
              className="text-black">
                Close
              </Button>
            </div>
          ) : (
            <>
              <Modal.Header closeButton>
                <Modal.Title>Verification Successful</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                <p>Welcome! Please complete your onboarding.</p>
              </Modal.Body>
              <Modal.Footer>
                <Button
                  variant="primary"
                  onClick={() => setShowOnboardingModal(false)}
                >
                  Done
                </Button>
              </Modal.Footer>
            </>
          )}
        </Modal.Body>
      </Modal>

      <Modal
        show={showUnverifiedModal}
        onHide={() => setShowUnverifiedModal(false)}
        centered
        size="lg"
      >
        <Modal.Header>
          <Modal.Title className="text-black">
            You need to verify your account!
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-black">
          <p>Check your email and verify your account before continuing.</p>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="primary"
            onClick={() => setShowUnverifiedModal(false)}
          >
            Done
          </Button>
        </Modal.Footer>
      </Modal>

      {showToast && (
        <Toast
          onClose={() => setShowToast(false)}
          show={showToast}
          delay={5000}
          autohide
          style={{ position: "fixed", bottom: 20, right: 20 }}
        >
          <Toast.Header>
            <strong className="me-auto">{toastHeader}</strong>
          </Toast.Header>
          <Toast.Body>{toastBody}</Toast.Body>
        </Toast>
      )}

      <div className="grid grid-cols-3">
        <div className="col-span-1">
          <Sidebar
            communities={communities.data}
            isAdmin={isAdmin}
            isLoggedIn={isLogged}
            currentPath={window.location.pathname}
          />
        </div>
        <div className="col-span-1">
          <div className="card border-0 text-decoration-none">
            <div className="card-body">
              {communities.data.length === 0 ? (
                <div className="d-flex flex-column align-items-center">
                  <h4>No communities available</h4>
                  <Link to="/communities" className="btn btn-primary">
                    Search All Communities
                  </Link>
                </div>
              ) : (
                <PaginatedCommunityCards communitiesResponse={communities} />
              )}
            </div>
          </div>
        </div>

        <div>
          <div className="card border-0 text-decoration-none">
            <div className="card-b text-gray-500">
              <h5>Filter by Category</h5>
              <div
                id="categoryPills"
                className="d-flex flex-row flex-wrap mb-3"
              >
                {categories.map((cat) => (
                  <div
                    key={cat}
                    className={`card flex-row align-items-center border m-1 btn p-0 ${selectedCategories.includes(cat) ? "border-primary" : "border-light"}`}
                    onClick={() =>
                      selectedCategories.includes(cat)
                        ? removeCategory(cat)
                        : addCategory(cat)
                    }
                  >
                    <div className="card-body d-flex flex-row p-2 align-items-center justify-content-center">
                      <p className="m-0 me-1">{cat}</p>
                    </div>
                  </div>
                ))}
              </div>
              <Button variant="primary" onClick={handleCategoryFormSubmit}>
                Apply Filters
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CommunitiesPage;
