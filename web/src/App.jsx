import { BrowserRouter, Routes, Route, Navigate, Outlet } from "react-router-dom";
import { AppProvider, useApp } from "./context/AppContext";

// Pages
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import Pets from "./pages/Pets";
import Analytics from "./pages/Analytics";
import Profile from "./pages/Profile";

// Components
import Navigation from "./components/Navigation";

// Protected Layout component that injects Sidebar / Bottom Nav
function ProtectedLayout() {
  const { user, username, coins, streak, selectedPet } = useApp();

  // Redirect to sign in if not authenticated
  if (!user) {
    return <Navigate to="/" replace />;
  }

  return (
    <Navigation
      username={username}
      coins={coins}
      streak={streak}
      activePetIndex={selectedPet}
    >
      <Outlet />
    </Navigation>
  );
}

// Redirect already logged in users away from auth pages
function PublicRoute() {
  const { user } = useApp();

  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
}

function AppRoutes() {
  const { loading } = useApp();

  if (loading) {
    return (
      <div className="app-loading-screen">
        <div className="loading-spinner"></div>
        <p>Loading FitnessPaw...</p>
        <style>{`
          .app-loading-screen {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            width: 100vw;
            background-color: #020617;
            color: #94a3b8;
            font-family: system-ui, sans-serif;
            gap: 16px;
          }
          .loading-spinner {
            width: 48px;
            height: 48px;
            border: 4px solid #1e293b;
            border-top: 4px solid #8ea2ff;
            border-radius: 50%;
            animation: spin 1s linear infinite;
          }
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        {/* Public Authentication routes */}
        <Route element={<PublicRoute />}>
          <Route path="/" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
        </Route>

        {/* Protected Dashboard/App routes */}
        <Route element={<ProtectedLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/analytics" element={<Analytics />} />
          <Route path="/pets" element={<Pets />} />
          <Route path="/profile" element={<Profile />} />
        </Route>

        {/* Catch-all Redirect */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

function App() {
  return (
    <AppProvider>
      <AppRoutes />
    </AppProvider>
  );
}

export default App;