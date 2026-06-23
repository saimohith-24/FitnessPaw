import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from "../firebase";
import logoImg from "../assets/fitnesspaw_logo.png";
import { useApp } from "../context/AppContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { user } = useApp();

  // If already logged in, redirect straight to dashboard
  useEffect(() => {
    if (user) {
      navigate("/dashboard");
    }
  }, [user, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please fill in all fields");
      return;
    }

    setError("");
    setSubmitting(true);
    try {
      await signInWithEmailAndPassword(auth, email, password);
      navigate("/dashboard");
    } catch (err) {
      setError(err.message || "Login failed. Please check your credentials.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card glass-card">
        <div className="auth-header">
          <img src={logoImg} alt="FitnessPaw Logo" className="auth-logo anim-float" />
          <h1 className="auth-title">FitnessPaw</h1>
          <p className="auth-subtitle">Track habits with your virtual pet</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              placeholder="e.g. user@fitnesspaw.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-field"
              disabled={submitting}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <div className="password-input-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="input-field password-field"
                disabled={submitting}
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowPassword(!showPassword)}
                tabIndex="-1"
              >
                {showPassword ? (
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="eye-icon">
                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                    <path d="M6.61 6.61A13.52 13.52 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                    <line x1="2" y1="2" x2="22" y2="22" />
                  </svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="eye-icon">
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>
          </div>

          {error && (
            <div className="auth-error-card">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="error-icon">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              <span>{error}</span>
            </div>
          )}

          <button type="submit" className="btn-primary auth-submit-btn" disabled={submitting}>
            {submitting ? "Signing In..." : "Sign In"}
          </button>
        </form>

        <div className="auth-footer">
          <span>Don't have an account? </span>
          <Link to="/signup" className="auth-link">Create Account</Link>
        </div>
      </div>

      <style>{`
        .auth-page {
          min-height: 100vh;
          width: 100vw;
          display: flex;
          align-items: center;
          justify-content: center;
          background: radial-gradient(circle at top right, hsl(var(--primary-glow) / 0.5), transparent 60%),
                      radial-gradient(circle at bottom left, hsl(var(--secondary-glow) / 0.5), transparent 60%),
                      linear-gradient(to bottom, #030712, #081120, #0f172a);
          padding: 20px;
        }

        .auth-card {
          width: 440px;
          max-width: 100%;
          padding: 40px;
        }

        .auth-header {
          display: flex;
          flex-direction: column;
          align-items: center;
          text-align: center;
          margin-bottom: 32px;
        }

        .auth-logo {
          width: 80px;
          height: 80px;
          object-fit: contain;
          margin-bottom: 16px;
        }

        .auth-title {
          font-size: 2.2rem;
          font-weight: 800;
          letter-spacing: -0.04em;
          margin-bottom: 4px;
        }

        .auth-subtitle {
          font-size: 0.95rem;
          color: hsl(var(--text-secondary));
        }

        .auth-form {
          display: flex;
          flex-direction: column;
          gap: 20px;
        }

        .form-group {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .form-label {
          font-size: 0.9rem;
          font-weight: 600;
          color: hsl(var(--text-secondary));
          padding-left: 4px;
        }

        .password-input-wrapper {
          position: relative;
        }

        .password-field {
          padding-right: 48px;
        }

        .password-toggle-btn {
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          background: none;
          border: none;
          color: hsl(var(--text-secondary));
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 6px;
          border-radius: 6px;
          transition: var(--transition-smooth);
        }

        .password-toggle-btn:hover {
          color: hsl(var(--text-primary));
          background-color: hsl(var(--bg-muted) / 0.5);
        }

        .eye-icon {
          width: 18px;
          height: 18px;
        }

        .auth-submit-btn {
          width: 100%;
          margin-top: 10px;
        }

        .auth-error-card {
          display: flex;
          align-items: center;
          gap: 10px;
          background-color: hsl(var(--error) / 0.1);
          border: 1px solid hsl(var(--error) / 0.3);
          border-radius: var(--radius-md);
          padding: 12px 16px;
          color: hsl(var(--error));
          font-size: 0.85rem;
          font-weight: 500;
        }

        .error-icon {
          width: 18px;
          height: 18px;
          flex-shrink: 0;
        }

        .auth-footer {
          margin-top: 24px;
          text-align: center;
          font-size: 0.9rem;
          color: hsl(var(--text-secondary));
        }

        .auth-link {
          font-weight: 700;
          color: hsl(var(--primary));
        }
      `}</style>
    </div>
  );
}