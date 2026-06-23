import { Link, useLocation } from "react-router-dom";
import { useState } from "react";
import logoImg from "../assets/fitnesspaw_logo.png";

export default function Navigation({ children, username, coins, streak, activePetIndex }) {
  const location = useLocation();
  const currentPath = location.pathname;

  const navItems = [
    {
      path: "/dashboard",
      label: "Dashboard",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="nav-icon">
          <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
          <polyline points="9 22 9 12 15 12 15 22" />
        </svg>
      )
    },
    {
      path: "/analytics",
      label: "Analytics",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="nav-icon">
          <line x1="18" y1="20" x2="18" y2="10" />
          <line x1="12" y1="20" x2="12" y2="4" />
          <line x1="6" y1="20" x2="6" y2="14" />
        </svg>
      )
    },
    {
      path: "/pets",
      label: "Pets",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="nav-icon">
          <path d="M12 5c.67 0 1.35.09 2 .26 1.78-2 3.82-3.26 6.21-3.26.8 0 1.34.54 1.34 1.34 0 2.39-1.24 4.43-3.26 6.21a10 10 0 1 1-12.58 0C3.74 7.78 2.5 5.74 2.5 3.34c0-.8.54-1.34 1.34-1.34 2.39 0 4.43 1.24 6.21 3.26.65-.17 1.33-.26 1.95-.26Z" />
        </svg>
      )
    },
    {
      path: "/profile",
      label: "Profile",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="nav-icon">
          <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
      )
    }
  ];

  const petEmoji = activePetIndex === 0 ? "🐱" : activePetIndex === 1 ? "🐶" : "🐼";

  return (
    <div className="app-container">
      {/* Sidebar - Desktop */}
      <aside className="sidebar glass-card">
        <div className="sidebar-header">
          <img src={logoImg} alt="FitnessPaw Logo" className="sidebar-logo" />
          <span className="sidebar-title">FitnessPaw</span>
        </div>

        <nav className="sidebar-nav">
          {navItems.map((item) => {
            const isActive = currentPath === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`sidebar-link ${isActive ? "active" : ""}`}
              >
                {item.icon}
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>

        <div className="sidebar-profile">
          <div className="sidebar-avatar">
            <span>{petEmoji}</span>
          </div>
          <div className="sidebar-profile-info">
            <span className="profile-name">{username || "User"}</span>
            <span className="profile-coins">🪙 {coins} | 🔥 {streak}d</span>
          </div>
        </div>
      </aside>

      {/* Main Content Pane */}
      <main className="content-pane">
        {/* Top Navbar for Mobile/Tablet */}
        <header className="mobile-header glass-card">
          <div className="mobile-logo-section">
            <img src={logoImg} alt="FitnessPaw Logo" className="mobile-logo" />
            <span className="mobile-title">FitnessPaw</span>
          </div>
          <div className="mobile-coins-badge">
            <span>🪙 {coins}</span>
          </div>
        </header>

        <div className="page-content">
          {children}
        </div>
      </main>

      {/* Bottom Nav - Mobile */}
      <nav className="mobile-bottom-nav glass-card">
        {navItems.map((item) => {
          const isActive = currentPath === item.path;
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`mobile-nav-link ${isActive ? "active" : ""}`}
            >
              {item.icon}
              <span className="mobile-nav-label">{item.label}</span>
            </Link>
          );
        })}
      </nav>

      <style>{`
        .app-container {
          display: flex;
          min-height: 100vh;
          width: 100vw;
          background-color: hsl(var(--bg-base));
        }

        /* Sidebar Desktop styles */
        .sidebar {
          width: 260px;
          height: calc(100vh - 32px);
          position: fixed;
          left: 16px;
          top: 16px;
          display: flex;
          flex-direction: column;
          padding: 24px;
          z-index: 100;
        }

        .sidebar-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 36px;
        }

        .sidebar-logo {
          width: 40px;
          height: 40px;
          object-fit: contain;
        }

        .sidebar-title {
          font-size: 1.3rem;
          font-weight: 800;
          letter-spacing: -0.03em;
          color: hsl(var(--text-primary));
        }

        .sidebar-nav {
          display: flex;
          flex-direction: column;
          gap: 8px;
          flex-grow: 1;
        }

        .sidebar-link {
          display: flex;
          align-items: center;
          gap: 14px;
          padding: 12px 16px;
          border-radius: var(--radius-md);
          color: hsl(var(--text-secondary));
          font-weight: 600;
          transition: var(--transition-smooth);
        }

        .sidebar-link:hover {
          background-color: hsl(var(--bg-muted) / 0.5);
          color: hsl(var(--text-primary));
        }

        .sidebar-link.active {
          background-color: hsl(var(--primary));
          color: #020617;
          box-shadow: 0 4px 15px 0 hsl(var(--primary) / 0.25);
        }

        .sidebar-link.active .nav-icon {
          stroke: #020617;
        }

        .nav-icon {
          width: 20px;
          height: 20px;
          stroke: currentColor;
          transition: var(--transition-smooth);
        }

        .sidebar-profile {
          display: flex;
          align-items: center;
          gap: 12px;
          padding-top: 16px;
          border-top: 1px solid hsl(var(--bg-muted));
        }

        .sidebar-avatar {
          width: 42px;
          height: 42px;
          border-radius: 50%;
          background-color: hsl(var(--primary) / 0.15);
          border: 1px solid hsl(var(--primary) / 0.3);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.4rem;
        }

        .sidebar-profile-info {
          display: flex;
          flex-direction: column;
          overflow: hidden;
        }

        .profile-name {
          font-weight: 700;
          font-size: 0.95rem;
          color: hsl(var(--text-primary));
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }

        .profile-coins {
          font-size: 0.8rem;
          color: hsl(var(--text-secondary));
        }

        /* Content Area layout */
        .content-pane {
          flex-grow: 1;
          margin-left: 292px; /* sidebar width + spacing */
          padding: 24px 32px 32px 16px;
          min-height: 100vh;
          width: calc(100% - 292px);
        }

        .page-content {
          max-width: 1200px;
          margin: 0 auto;
          animation: fadeIn 0.4s ease;
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(6px); }
          to { opacity: 1; transform: translateY(0); }
        }

        /* Mobile layout styling defaults */
        .mobile-header,
        .mobile-bottom-nav {
          display: none;
        }

        /* Responsive breakpoints */
        @media (max-width: 1024px) {
          .sidebar {
            display: none;
          }

          .content-pane {
            margin-left: 0;
            padding: 80px 16px 90px 16px;
            width: 100%;
          }

          .mobile-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            height: 64px;
            padding: 0 16px;
            border-radius: 0;
            border-bottom: 1px solid var(--border-color);
            z-index: 100;
            background-color: hsl(var(--bg-base) / 0.8);
            backdrop-filter: blur(12px);
          }

          .mobile-logo-section {
            display: flex;
            align-items: center;
            gap: 8px;
          }

          .mobile-logo {
            width: 32px;
            height: 32px;
          }

          .mobile-title {
            font-size: 1.15rem;
            font-weight: 800;
          }

          .mobile-coins-badge {
            background-color: hsl(var(--primary) / 0.15);
            border: 1px solid hsl(var(--primary) / 0.3);
            color: hsl(var(--primary));
            font-weight: 700;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
          }

          .mobile-bottom-nav {
            display: flex;
            justify-content: space-around;
            align-items: center;
            position: fixed;
            bottom: 12px;
            left: 12px;
            right: 12px;
            height: 64px;
            border-radius: var(--radius-md);
            z-index: 100;
            background-color: hsl(var(--bg-surface) / 0.95);
            padding: 0 8px;
          }

          .mobile-nav-link {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 4px;
            color: hsl(var(--text-secondary));
            flex: 1;
            height: 100%;
            transition: var(--transition-smooth);
          }

          .mobile-nav-link.active {
            color: hsl(var(--primary));
          }

          .mobile-nav-label {
            font-size: 0.7rem;
            font-weight: 600;
          }
        }
      `}</style>
    </div>
  );
}
