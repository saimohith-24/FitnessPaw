import { useState } from "react";
import { useApp } from "../context/AppContext";

export default function Profile() {
  const {
    user,
    username,
    coins,
    streak,
    selectedPet,
    waterIntake,
    waterGoal,
    dailySteps,
    stepGoal,
    weight,
    height,
    calorieGoal,
    customHabits,
    themeMode,
    logOutUser,
    updateStepGoal,
    updateWaterGoal,
    updateCalorieGoal,
    updateWeight,
    updateHeight,
    updateProfile,
    updateTheme
  } = useApp();

  // Dialog state
  const [showEditDetails, setShowEditDetails] = useState(false);
  const [editUsername, setEditUsername] = useState(username);
  const [editHeight, setEditHeight] = useState(height);
  const [editWeight, setEditWeight] = useState(weight);

  const completedCount = customHabits.filter((h) => h.completed).length;
  const totalCount = customHabits.length;

  const petEmoji = selectedPet === 0 ? "🐱" : selectedPet === 1 ? "🐶" : "🐼";
  const userEmail = user?.email || "user@fitnesspaw.com";
  const userUid = user?.uid || "Unknown UID";
  const isVerified = user?.emailVerified || false;

  const handleSaveDetails = (e) => {
    e.preventDefault();
    updateProfile(editUsername, selectedPet);
    updateHeight(editHeight);
    updateWeight(editWeight);
    setShowEditDetails(false);
  };

  const handleOpenEdit = () => {
    setEditUsername(username);
    setEditHeight(height);
    setEditWeight(weight);
    setShowEditDetails(true);
  };

  return (
    <div className="profile-page-wrapper">
      {/* Profile Header card info */}
      <div className="profile-hero-card glass-card">
        <div className="profile-avatar">
          <span>{petEmoji}</span>
        </div>
        <h1 className="profile-name-title">{username || "Trainer"}</h1>
        <p className="profile-email-desc">{userEmail}</p>
      </div>

      <div className="profile-grid">
        {/* Left Column: Stats and Info */}
        <div className="profile-left-column">
          {/* Stats summary list card */}
          <div className="profile-card glass-card">
            <h3 className="card-title">Activity Summary 📊</h3>
            <div className="profile-stats-list">
              <div className="stats-row">
                <span>🔥 Current Streak</span>
                <span className="stats-value">{streak} Days</span>
              </div>
              <div className="stats-row">
                <span>💧 Water Logged Today</span>
                <span className="stats-value">{waterIntake} / {waterGoal} Cups</span>
              </div>
              <div className="stats-row">
                <span>👣 Steps Walked Today</span>
                <span className="stats-value">{dailySteps.toLocaleString()} steps</span>
              </div>
              <div className="stats-row">
                <span>🏆 Daily Habits Completed</span>
                <span className="stats-value">{completedCount} / {totalCount}</span>
              </div>
            </div>
          </div>

          {/* Height and Weight Details card */}
          <div className="profile-card glass-card">
            <div className="card-header-row">
              <h3 className="card-title">User Details 👤</h3>
              <button className="btn-icon-edit" onClick={handleOpenEdit}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="edit-icon">
                  <path d="M12 20h9" />
                  <path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z" />
                </svg>
              </button>
            </div>
            <div className="profile-stats-list">
              <div className="stats-row">
                <span>Height</span>
                <span className="stats-value">{height ? `${height} cm` : "Not set"}</span>
              </div>
              <div className="stats-row">
                <span>Weight</span>
                <span className="stats-value">{weight ? `${weight} kg` : "Not set"}</span>
              </div>
            </div>
          </div>

          {/* Firebase Connection Details card */}
          <div className="profile-card glass-card">
            <h3 className="card-title">Firebase Console Link</h3>
            <div className="profile-stats-list">
              <div className="stats-row">
                <span>Status:</span>
                <span className="stats-value text-success">Connected ✅</span>
              </div>
              <div className="stats-row">
                <span>User UID:</span>
                <span className="stats-value doc-uid" title={userUid}>{userUid}</span>
              </div>
              <div className="stats-row">
                <span>Email Status:</span>
                <span className="stats-value">
                  {isVerified ? "Verified Email" : "Pending Verification"}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Goal Settings and App Styling */}
        <div className="profile-right-column">
          {/* Target Daily Goals card */}
          <div className="profile-card glass-card">
            <h3 className="card-title">Daily Target Goals 🎯</h3>
            
            <div className="goals-adjusters-list">
              {/* Steps Goal adjust */}
              <div className="goal-adjust-row">
                <div className="goal-adjust-info">
                  <span className="goal-name">Steps Goal</span>
                  <span className="goal-recommendation">Recommended: 5,000 - 15,000</span>
                </div>
                <div className="counter-row">
                  <button className="btn-adjust-sm" onClick={() => updateStepGoal(stepGoal - 1000)}>-</button>
                  <span className="counter-val">{stepGoal.toLocaleString()}</span>
                  <button className="btn-adjust-sm" onClick={() => updateStepGoal(stepGoal + 1000)}>+</button>
                </div>
              </div>

              {/* Water Goal adjust */}
              <div className="goal-adjust-row">
                <div className="goal-adjust-info">
                  <span className="goal-name">Water Goal</span>
                  <span className="goal-recommendation">Recommended: 4 - 16 Cups</span>
                </div>
                <div className="counter-row">
                  <button className="btn-adjust-sm" onClick={() => updateWaterGoal(waterGoal - 1)}>-</button>
                  <span className="counter-val">{waterGoal} Cups</span>
                  <button className="btn-adjust-sm" onClick={() => updateWaterGoal(waterGoal + 1)}>+</button>
                </div>
              </div>

              {/* Calorie Goal adjust */}
              <div className="goal-adjust-row">
                <div className="goal-adjust-info">
                  <span className="goal-name">Calorie Burn Goal</span>
                  <span className="goal-recommendation">Recommended: 200 - 2,000 kcal</span>
                </div>
                <div className="counter-row">
                  <button className="btn-adjust-sm" onClick={() => updateCalorieGoal(calorieGoal - 50)}>-</button>
                  <span className="counter-val">{calorieGoal} kcal</span>
                  <button className="btn-adjust-sm" onClick={() => updateCalorieGoal(calorieGoal + 50)}>+</button>
                </div>
              </div>
            </div>
          </div>

          {/* Theme card switcher */}
          <div className="profile-card glass-card">
            <h3 className="card-title">App Theme Settings</h3>
            <div className="theme-toggle-row">
              <button
                onClick={() => updateTheme(0)}
                className={`theme-card ${themeMode === 0 ? "active" : ""}`}
              >
                <span>Light</span>
              </button>

              <button
                onClick={() => updateTheme(2)}
                className={`theme-card ${themeMode === 2 ? "active" : ""}`}
              >
                <span>System</span>
              </button>

              <button
                onClick={() => updateTheme(1)}
                className={`theme-card ${themeMode === 1 ? "active" : ""}`}
              >
                <span>Dark</span>
              </button>
            </div>
          </div>

          {/* Logout operation */}
          <button className="btn-primary logout-btn" onClick={logOutUser}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="logout-icon">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            <span>Sign Out</span>
          </button>
        </div>
      </div>

      {/* EDIT PROFILE DIALOG OVERLAY */}
      {showEditDetails && (
        <div className="modal-backdrop">
          <div className="modal-content glass-card" style={{ maxWidth: "420px" }}>
            <button className="modal-close-btn" onClick={() => setShowEditDetails(false)}>×</button>
            <h2>Edit User Details 👤</h2>
            
            <form onSubmit={handleSaveDetails} className="edit-details-form">
              <div className="form-group">
                <label className="form-label">Username</label>
                <input
                  type="text"
                  value={editUsername}
                  onChange={(e) => setEditUsername(e.target.value)}
                  className="input-field"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Height (cm)</label>
                <input
                  type="number"
                  placeholder="Height in cm"
                  value={editHeight}
                  onChange={(e) => setEditHeight(e.target.value)}
                  className="input-field"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Weight (kg)</label>
                <input
                  type="number"
                  placeholder="Weight in kg"
                  value={editWeight}
                  onChange={(e) => setEditWeight(e.target.value)}
                  className="input-field"
                />
              </div>

              <div className="edit-dialog-actions" style={{ display: "flex", gap: "12px", justifyContent: "flex-end", marginTop: "10px" }}>
                <button type="button" className="btn-secondary" onClick={() => setShowEditDetails(false)}>Cancel</button>
                <button type="submit" className="btn-primary">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <style>{`
        .profile-page-wrapper {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .profile-hero-card {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 36px 20px;
          text-align: center;
        }

        .profile-avatar {
          width: 90px;
          height: 90px;
          border-radius: 50%;
          background-color: hsl(var(--primary) / 0.12);
          border: 2px solid hsl(var(--primary) / 0.3);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 2.8rem;
          margin-bottom: 16px;
        }

        .profile-name-title {
          font-size: 1.8rem;
          font-weight: 800;
          letter-spacing: -0.03em;
          margin-bottom: 2px;
        }

        .profile-email-desc {
          font-size: 0.95rem;
          color: hsl(var(--text-secondary));
        }

        /* Profile Layout Grid */
        .profile-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 24px;
        }

        @media (max-width: 1024px) {
          .profile-grid {
            grid-template-columns: 1fr;
          }
        }

        .profile-left-column,
        .profile-right-column {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .profile-card {
          padding: 24px;
          display: flex;
          flex-direction: column;
        }

        .card-title {
          font-size: 1.05rem;
          font-weight: 800;
          color: hsl(var(--text-primary));
          margin-bottom: 16px;
        }

        .card-header-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 16px;
        }

        .card-header-row .card-title {
          margin-bottom: 0;
        }

        .btn-icon-edit {
          background: none;
          border: none;
          color: hsl(var(--primary));
          cursor: pointer;
          padding: 6px;
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: var(--transition-smooth);
        }

        .btn-icon-edit:hover {
          background-color: hsl(var(--primary) / 0.15);
        }

        .edit-icon {
          width: 16px;
          height: 16px;
        }

        .profile-stats-list {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .stats-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-bottom: 12px;
          border-bottom: 1px solid hsl(var(--text-secondary) / 0.05);
          font-size: 0.95rem;
          font-weight: 600;
          color: hsl(var(--text-secondary));
        }

        .stats-row:last-child {
          border-bottom: none;
          padding-bottom: 0;
        }

        .stats-value {
          color: hsl(var(--text-primary));
          font-weight: 700;
        }

        .stats-value.text-success {
          color: hsl(var(--success));
        }

        .doc-uid {
          max-width: 180px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        /* Goal Counters */
        .goals-adjusters-list {
          display: flex;
          flex-direction: column;
          gap: 20px;
        }

        .goal-adjust-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .goal-adjust-info {
          display: flex;
          flex-direction: column;
        }

        .goal-name {
          font-weight: 700;
          font-size: 0.95rem;
          color: hsl(var(--text-primary));
        }

        .goal-recommendation {
          font-size: 0.75rem;
          color: hsl(var(--text-secondary));
        }

        .counter-row {
          display: flex;
          align-items: center;
          gap: 10px;
        }

        .btn-adjust-sm {
          width: 30px;
          height: 30px;
          border-radius: 50%;
          background-color: hsl(var(--bg-muted));
          border: 1px solid var(--border-color);
          color: hsl(var(--primary));
          font-size: 1rem;
          font-weight: 700;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: var(--transition-smooth);
        }

        .btn-adjust-sm:hover {
          background-color: hsl(var(--primary));
          color: #020617;
          border-color: hsl(var(--primary));
        }

        .counter-val {
          font-size: 0.9rem;
          font-weight: 700;
          min-width: 50px;
          text-align: center;
          color: hsl(var(--text-primary));
        }

        /* Theme selectors */
        .theme-toggle-row {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 10px;
        }

        .theme-card {
          height: 48px;
          border-radius: var(--radius-md);
          background-color: transparent;
          border: 1.5px solid hsl(var(--text-secondary) / 0.15);
          color: hsl(var(--text-primary));
          font-weight: 700;
          font-size: 0.85rem;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: var(--transition-smooth);
        }

        .theme-card:hover {
          border-color: hsl(var(--text-secondary) / 0.3);
          background-color: hsl(var(--bg-muted) / 0.3);
        }

        .theme-card.active {
          background-color: hsl(var(--primary));
          border-color: hsl(var(--primary));
          color: #020617;
        }

        /* Logout button */
        .logout-btn {
          width: 100%;
          height: 50px;
          gap: 8px;
        }

        .logout-icon {
          width: 18px;
          height: 18px;
        }

        /* Dialog input form overlay */
        .edit-details-form {
          display: flex;
          flex-direction: column;
          gap: 16px;
          margin-top: 20px;
        }
      `}</style>
    </div>
  );
}
