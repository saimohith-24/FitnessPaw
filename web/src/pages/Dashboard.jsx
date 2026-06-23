import { useState } from "react";
import { useApp } from "../context/AppContext";

// Import Pet Assets
import catHappy from "../assets/cat_happy.png";
import catSad from "../assets/cat_sad.png";
import catSleepy from "../assets/cat_sleepy.png";
import dogHappy from "../assets/dog_happy.png";
import dogSad from "../assets/dog_sad.png";
import dogSleepy from "../assets/dog_sleepy.png";
import pandaHappy from "../assets/panda_happy.png";
import pandaSad from "../assets/panda_sad.png";
import pandaSleepy from "../assets/panda_sleepy.png";

export default function Dashboard() {
  const {
    username,
    coins,
    streak,
    selectedPet,
    petName,
    petHappiness,
    waterIntake,
    waterGoal,
    dailySteps,
    stepGoal,
    calorieGoal,
    caloriesBurned,
    customHabits,
    mockAddSteps,
    incrementWater,
    addHabit,
    deleteHabit,
    editHabit,
    toggleCustomHabit,
    updateStepGoal,
    updateWaterGoal,
    updateCalorieGoal
  } = useApp();

  // Local state for adding a habit
  const [newHabitTitle, setNewHabitTitle] = useState("");
  const [newHabitTime, setNewHabitTime] = useState("");

  // Edit Habit state
  const [editingHabit, setEditingHabit] = useState(null);
  const [editTitle, setEditTitle] = useState("");
  const [editTime, setEditTime] = useState("");

  // Modal active states
  const [activeModal, setActiveModal] = useState(null); // 'steps' | 'water' | 'streak' | 'calories' | null

  // Determine pet image based on index and habit completion progress
  const completedCount = customHabits.filter((h) => h.completed).length;
  const totalCount = customHabits.length;
  const progressRatio = totalCount === 0 ? 1 : completedCount / totalCount;

  const getPetImageAndStatus = () => {
    let type = "Cat";
    let image = catHappy;
    let mood = "Happy! ✨";

    if (selectedPet === 0) {
      type = "Cat";
      if (totalCount > 0 && completedCount === 0) {
        image = catSad;
        mood = "Sad 😢";
      } else if (totalCount > 0 && completedCount < totalCount) {
        image = catSleepy;
        mood = "Resting 😴";
      } else {
        image = catHappy;
        mood = "Happy! ✨";
      }
    } else if (selectedPet === 1) {
      type = "Dog";
      if (totalCount > 0 && completedCount === 0) {
        image = dogSad;
        mood = "Sad 😢";
      } else if (totalCount > 0 && completedCount < totalCount) {
        image = dogSleepy;
        mood = "Resting 😴";
      } else {
        image = dogHappy;
        mood = "Happy! ✨";
      }
    } else {
      type = "Panda";
      if (totalCount > 0 && completedCount === 0) {
        image = pandaSad;
        mood = "Sad 😢";
      } else if (totalCount > 0 && completedCount < totalCount) {
        image = pandaSleepy;
        mood = "Resting 😴";
      } else {
        image = pandaHappy;
        mood = "Happy! ✨";
      }
    }

    return { type, image, mood };
  };

  const { image: petImage, mood: petMood } = getPetImageAndStatus();

  // Greeting based on time
  const getGreeting = () => {
    const hours = new Date().getHours();
    if (hours < 12) return "Good Morning 👋";
    if (hours < 17) return "Good Afternoon 👋";
    return "Good Evening 👋";
  };

  // Helper to format timestamp to readable text
  const formatTime = (timeStr) => {
    if (!timeStr) return "";
    const [hrs, mins] = timeStr.split(":");
    const h = parseInt(hrs, 10);
    const ampm = h >= 12 ? "PM" : "AM";
    const formattedHr = h % 12 || 12;
    return `${formattedHr}:${mins} ${ampm}`;
  };

  const handleAddHabitSubmit = (e) => {
    e.preventDefault();
    if (!newHabitTitle.trim()) return;
    addHabit(newHabitTitle, newHabitTime || null);
    setNewHabitTitle("");
    setNewHabitTime("");
  };

  const handleOpenEdit = (habit) => {
    setEditingHabit(habit);
    setEditTitle(habit.title);
    setEditTime(habit.reminderTime || "");
  };

  const handleSaveEdit = () => {
    if (!editTitle.trim()) return;
    editHabit(editingHabit.id, editTitle, editTime || null);
    setEditingHabit(null);
  };

  return (
    <div className="dashboard-wrapper">
      {/* Header section */}
      <section className="dashboard-header">
        <div>
          <span className="greeting-sub">{getGreeting()}</span>
          <h1 className="greeting-main">{username || "Trainer"}</h1>
        </div>
        <div className="quick-badges">
          <div className="header-badge glass-card">
            <span>🪙 {coins}</span>
          </div>
          <div className="header-badge glass-card">
            <span>🔥 {streak} days</span>
          </div>
        </div>
      </section>

      <div className="dashboard-grid">
        {/* Left Column - Pet Card & Metrics */}
        <div className="left-column">
          {/* Pet Hero display card */}
          <div className="pet-hero-card glass-card">
            <div className="pet-ring-container">
              {/* SVG Circular Progress Ring */}
              <svg className="pet-progress-svg" viewBox="0 0 200 200">
                <circle cx="100" cy="100" r="88" className="pet-ring-track" />
                <circle
                  cx="100"
                  cy="100"
                  r="88"
                  className="pet-ring-fill anim-glow"
                  strokeDasharray={2 * Math.PI * 88}
                  strokeDashoffset={2 * Math.PI * 88 * (1 - progressRatio)}
                />
              </svg>
              <div className="pet-image-wrapper">
                <img src={petImage} alt="Virtual Pet" className="pet-hero-image anim-float" />
              </div>
            </div>
            <h2 className="pet-title">{petName} is {petMood}</h2>
            <p className="pet-subtitle">
              {totalCount === 0
                ? "No active habits. Create one below! 🐾"
                : `${completedCount} of ${totalCount} habits completed today`}
            </p>
          </div>

          {/* Stats quick overview cards */}
          <div className="metrics-grid">
            <button className="metric-tile glass-card" onClick={() => setActiveModal("steps")}>
              <span className="tile-icon">👣</span>
              <span className="tile-value">{dailySteps} / {stepGoal}</span>
              <span className="tile-label">Daily Steps</span>
            </button>

            <button className="metric-tile glass-card" onClick={() => setActiveModal("water")}>
              <span className="tile-icon">💧</span>
              <span className="tile-value">{waterIntake} / {waterGoal} Cups</span>
              <span className="tile-label">Water Intake</span>
            </button>

            <button className="metric-tile glass-card" onClick={() => setActiveModal("streak")}>
              <span className="tile-icon">🔥</span>
              <span className="tile-value">{streak} Days</span>
              <span className="tile-label">Active Streak</span>
            </button>

            <button className="metric-tile glass-card" onClick={() => setActiveModal("calories")}>
              <span className="tile-icon">⚡</span>
              <span className="tile-value">{caloriesBurned} / {calorieGoal} kcal</span>
              <span className="tile-label">Calories Burned</span>
            </button>
          </div>
        </div>

        {/* Right Column - Habits List & Adding */}
        <div className="right-column">
          <div className="habits-container glass-card">
            <h2 className="section-title">Habit Tasks 🐾</h2>

            {/* Checklist */}
            <div className="habits-list">
              {customHabits.length === 0 ? (
                <div className="empty-habits">
                  <p>No custom habits logged. Create your daily tasks using the input fields below!</p>
                </div>
              ) : (
                customHabits.map((habit) => (
                  <div key={habit.id} className={`habit-item ${habit.completed ? "completed" : ""}`}>
                    <div className="habit-left" onClick={() => toggleCustomHabit(habit.id)}>
                      <div className="habit-checkbox">
                        {habit.completed && <span className="checkmark">✓</span>}
                      </div>
                      <div className="habit-text-info">
                        <span className="habit-title">{habit.title}</span>
                        {habit.reminderTime && (
                          <span className="habit-reminder">⏰ {formatTime(habit.reminderTime)}</span>
                        )}
                      </div>
                    </div>
                    <div className="habit-actions">
                      <button className="habit-btn-edit" onClick={() => handleOpenEdit(habit)}>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="action-icon">
                          <path d="M12 20h9" />
                          <path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z" />
                        </svg>
                      </button>
                      <button className="habit-btn-delete" onClick={() => deleteHabit(habit.id)}>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="action-icon">
                          <polyline points="3 6 5 6 21 6" />
                          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                        </svg>
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>

            {/* Adding inputs dock */}
            <form onSubmit={handleAddHabitSubmit} className="add-habit-form">
              <input
                type="text"
                placeholder="Log custom daily habit... (e.g. Read 10 Pages)"
                value={newHabitTitle}
                onChange={(e) => setNewHabitTitle(e.target.value)}
                className="input-field"
                required
              />
              <div className="form-sub-row">
                <div className="reminder-picker">
                  <label>⏰ Reminder Time: </label>
                  <input
                    type="time"
                    value={newHabitTime}
                    onChange={(e) => setNewHabitTime(e.target.value)}
                    className="time-input"
                  />
                </div>
                <button type="submit" className="btn-primary add-habit-btn">
                  <span>+ Add Task</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* OVERLAY MODALS FOR METRICS */}
      {activeModal && (
        <div className="modal-backdrop" onClick={() => setActiveModal(null)}>
          <div className="modal-content glass-card anim-float" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={() => setActiveModal(null)}>×</button>

            {/* Steps Modal */}
            {activeModal === "steps" && (
              <div className="modal-inner">
                <h2>Daily Steps Tracker 👣</h2>
                <div className="modal-stat-value">{dailySteps} / {stepGoal} steps</div>
                
                <div className="modal-progress-bar">
                  <div
                    className="modal-progress-fill"
                    style={{ width: `${Math.min(100, (dailySteps / stepGoal) * 100)}%` }}
                  ></div>
                </div>
                <span className="modal-progress-pct">
                  {Math.round((dailySteps / stepGoal) * 100)}% towards step goal
                </span>

                <div className="goal-adjuster">
                  <h3>Adjust Daily Step Goal 🎯</h3>
                  <div className="adjuster-row">
                    <button className="btn-adjust" onClick={() => updateStepGoal(stepGoal - 1000)}>-</button>
                    <span className="adjuster-value">{stepGoal.toLocaleString()} steps</span>
                    <button className="btn-adjust" onClick={() => updateStepGoal(stepGoal + 1000)}>+</button>
                  </div>
                </div>

                <div className="sensor-override-panel">
                  <h4>Sensor Status: Simulation Fallback ⚠️</h4>
                  <p>Mock steps to evaluate tracking and rewards:</p>
                  <div className="mock-buttons">
                    <button className="btn-secondary" onClick={() => mockAddSteps(500)}>+500 Steps</button>
                    <button className="btn-secondary" onClick={() => mockAddSteps(2000)}>+2,000 Steps</button>
                  </div>
                </div>
              </div>
            )}

            {/* Water Modal */}
            {activeModal === "water" && (
              <div className="modal-inner">
                <h2>Water Intake 💧</h2>
                <p className="modal-description">Your pet needs water to stay happy and healthy! Goal is {waterGoal} cups.</p>
                
                <div className="water-cups-grid">
                  {Array.from({ length: waterGoal }).map((_, i) => (
                    <div
                      key={i}
                      className={`water-cup-bubble ${waterIntake > i ? "filled" : ""}`}
                    >
                      💧
                    </div>
                  ))}
                </div>
                
                <div className="modal-stat-value">{waterIntake} / {waterGoal} Cups Logged</div>

                <div className="goal-adjuster">
                  <h3>Adjust Daily Water Goal 🎯</h3>
                  <div className="adjuster-row">
                    <button className="btn-adjust" onClick={() => updateWaterGoal(waterGoal - 1)}>-</button>
                    <span className="adjuster-value">{waterGoal} cups</span>
                    <button className="btn-adjust" onClick={() => updateWaterGoal(waterGoal + 1)}>+</button>
                  </div>
                </div>

                <button className="btn-primary log-water-btn" onClick={incrementWater}>
                  💧 Log 1 Cup
                </button>
              </div>
            )}

            {/* Streak Modal */}
            {activeModal === "streak" && (
              <div className="modal-inner">
                <h2>Activity Streak 🔥</h2>
                <div className="fire-avatar">
                  <span>🔥</span>
                </div>
                <div className="modal-stat-value">{streak} Day Streak</div>
                <p className="modal-description">
                  Check off all daily habits today to keep your streak burning! {petName} is counting on you.
                </p>
              </div>
            )}

            {/* Calories Modal */}
            {activeModal === "calories" && (
              <div className="modal-inner">
                <h2>Calories Burned Tracker ⚡</h2>
                <div className="modal-stat-value">{caloriesBurned} / {calorieGoal} kcal</div>
                
                <div className="modal-progress-bar">
                  <div
                    className="modal-progress-fill"
                    style={{ width: `${Math.min(100, (caloriesBurned / calorieGoal) * 100)}%` }}
                  ></div>
                </div>
                <span className="modal-progress-pct">
                  {Math.round((caloriesBurned / calorieGoal) * 100)}% towards calorie goal
                </span>
                <p className="calories-calc-note">
                  Calories calculated dynamically based on daily steps and weight input values in your Profile screen.
                </p>

                <div className="goal-adjuster">
                  <h3>Adjust Daily Calorie Goal 🎯</h3>
                  <div className="adjuster-row">
                    <button className="btn-adjust" onClick={() => updateCalorieGoal(calorieGoal - 50)}>-</button>
                    <span className="adjuster-value">{calorieGoal} kcal</span>
                    <button className="btn-adjust" onClick={() => updateCalorieGoal(calorieGoal + 50)}>+</button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* EDIT HABIT DIALOG MODAL */}
      {editingHabit && (
        <div className="modal-backdrop">
          <div className="modal-content glass-card" style={{ maxWidth: "400px" }}>
            <h2>Edit Habit Task 🐾</h2>
            <div className="edit-form-fields" style={{ display: "flex", flexDirection: "column", gap: "16px", margin: "20px 0" }}>
              <div className="form-group" style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                <label style={{ fontSize: "0.85rem", fontWeight: "600", color: "hsl(var(--text-secondary))" }}>Habit Name</label>
                <input
                  type="text"
                  value={editTitle}
                  onChange={(e) => setEditTitle(e.target.value)}
                  className="input-field"
                />
              </div>
              <div className="form-group" style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                <label style={{ fontSize: "0.85rem", fontWeight: "600", color: "hsl(var(--text-secondary))" }}>Reminder Time</label>
                <input
                  type="time"
                  value={editTime}
                  onChange={(e) => setEditTime(e.target.value)}
                  className="input-field"
                  style={{ width: "100%" }}
                />
              </div>
            </div>
            <div className="edit-dialog-actions" style={{ display: "flex", gap: "12px", justifyContent: "flex-end" }}>
              <button className="btn-secondary" onClick={() => setEditingHabit(null)}>Cancel</button>
              <button className="btn-primary" onClick={handleSaveEdit}>Save</button>
            </div>
          </div>
        </div>
      )}

      <style>{`
        .dashboard-wrapper {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .dashboard-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 8px 0;
        }

        .greeting-sub {
          font-size: 1rem;
          color: hsl(var(--text-secondary));
          font-weight: 500;
        }

        .greeting-main {
          font-size: 2.2rem;
          font-weight: 800;
          letter-spacing: -0.04em;
        }

        .quick-badges {
          display: flex;
          gap: 12px;
        }

        .header-badge {
          padding: 8px 16px;
          border-radius: var(--radius-md);
          font-weight: 700;
          color: hsl(var(--primary));
          font-size: 0.95rem;
          background-color: hsl(var(--primary) / 0.08);
          border-color: hsl(var(--primary) / 0.15);
        }

        /* Left Column Styles */
        .left-column {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .pet-hero-card {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 40px;
          text-align: center;
        }

        .pet-ring-container {
          position: relative;
          width: 200px;
          height: 200px;
          margin-bottom: 24px;
        }

        .pet-progress-svg {
          width: 100%;
          height: 100%;
          transform: rotate(-90deg);
        }

        .pet-ring-track {
          fill: none;
          stroke: hsl(var(--text-secondary) / 0.08);
          stroke-width: 8px;
        }

        .pet-ring-fill {
          fill: none;
          stroke: hsl(var(--primary));
          stroke-width: 8px;
          stroke-linecap: round;
          transition: stroke-dashoffset 0.8s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .pet-image-wrapper {
          position: absolute;
          top: 15px;
          left: 15px;
          right: 15px;
          bottom: 15px;
          border-radius: 50%;
          overflow: hidden;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .pet-hero-image {
          width: 85%;
          height: 85%;
          object-fit: contain;
        }

        .pet-title {
          font-size: 1.8rem;
          font-weight: 800;
          letter-spacing: -0.02em;
          margin-bottom: 6px;
        }

        .pet-subtitle {
          font-size: 0.95rem;
          color: hsl(var(--text-secondary));
        }

        .metrics-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 16px;
        }

        .metric-tile {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 20px;
          height: 110px;
          cursor: pointer;
          background-color: hsl(var(--bg-surface));
          border-color: hsl(var(--text-secondary) / 0.08);
          transition: var(--transition-smooth);
        }

        .metric-tile:hover {
          transform: translateY(-3px);
          border-color: hsl(var(--primary) / 0.3);
          background-color: hsl(var(--bg-muted) / 0.3);
        }

        .tile-icon {
          font-size: 1.5rem;
          margin-bottom: 6px;
        }

        .tile-value {
          font-size: 0.95rem;
          font-weight: 800;
          color: hsl(var(--text-primary));
        }

        .tile-label {
          font-size: 0.75rem;
          color: hsl(var(--text-secondary));
          margin-top: 2px;
          font-weight: 500;
        }

        /* Right Column Habits Container */
        .right-column {
          display: flex;
          flex-direction: column;
        }

        .habits-container {
          padding: 32px;
          display: flex;
          flex-direction: column;
          min-height: 480px;
          height: 100%;
        }

        .section-title {
          font-size: 1.4rem;
          font-weight: 800;
          margin-bottom: 20px;
        }

        .habits-list {
          display: flex;
          flex-direction: column;
          gap: 12px;
          flex-grow: 1;
          overflow-y: auto;
          margin-bottom: 24px;
          padding-right: 4px;
        }

        .empty-habits {
          display: flex;
          align-items: center;
          justify-content: center;
          text-align: center;
          height: 100%;
          color: hsl(var(--text-secondary));
          font-size: 0.95rem;
          padding: 40px;
        }

        .habit-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 14px 20px;
          border-radius: var(--radius-md);
          background-color: hsl(var(--bg-surface));
          border: 1px solid hsl(var(--text-secondary) / 0.08);
          transition: var(--transition-smooth);
        }

        .habit-item:hover {
          border-color: hsl(var(--primary) / 0.25);
          background-color: hsl(var(--bg-muted) / 0.2);
        }

        .habit-item.completed {
          background-color: hsl(var(--primary) / 0.04);
          border-color: hsl(var(--primary) / 0.2);
        }

        .habit-left {
          display: flex;
          align-items: center;
          gap: 16px;
          flex-grow: 1;
          cursor: pointer;
        }

        .habit-checkbox {
          width: 24px;
          height: 24px;
          border-radius: 50%;
          border: 2.5px solid hsl(var(--text-secondary) / 0.4);
          display: flex;
          align-items: center;
          justify-content: center;
          transition: var(--transition-smooth);
          flex-shrink: 0;
        }

        .habit-item.completed .habit-checkbox {
          background-color: hsl(var(--primary));
          border-color: hsl(var(--primary));
        }

        .checkmark {
          color: #020617;
          font-size: 0.8rem;
          font-weight: 800;
        }

        .habit-text-info {
          display: flex;
          flex-direction: column;
        }

        .habit-title {
          font-weight: 700;
          font-size: 1.05rem;
          color: hsl(var(--text-primary));
          transition: var(--transition-smooth);
        }

        .habit-item.completed .habit-title {
          text-decoration: line-through;
          color: hsl(var(--text-secondary));
        }

        .habit-reminder {
          font-size: 0.8rem;
          color: hsl(var(--primary));
          font-weight: 600;
          margin-top: 2px;
        }

        .habit-actions {
          display: flex;
          gap: 4px;
        }

        .habit-btn-edit,
        .habit-btn-delete {
          background: none;
          border: none;
          padding: 8px;
          border-radius: 8px;
          cursor: pointer;
          color: hsl(var(--text-secondary));
          display: flex;
          align-items: center;
          justify-content: center;
          transition: var(--transition-smooth);
        }

        .habit-btn-edit:hover {
          color: hsl(var(--text-primary));
          background-color: hsl(var(--bg-muted));
        }

        .habit-btn-delete:hover {
          color: hsl(var(--error));
          background-color: hsl(var(--error) / 0.1);
        }

        .action-icon {
          width: 18px;
          height: 18px;
        }

        .add-habit-form {
          display: flex;
          flex-direction: column;
          gap: 12px;
          border-top: 1px solid hsl(var(--text-secondary) / 0.08);
          padding-top: 20px;
        }

        .form-sub-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 16px;
        }

        .reminder-picker {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 0.85rem;
          font-weight: 600;
          color: hsl(var(--text-secondary));
        }

        .time-input {
          padding: 6px 12px;
          border-radius: var(--radius-sm);
          border: 1px solid var(--border-color);
          background-color: hsl(var(--bg-muted));
          color: hsl(var(--text-primary));
          font-family: var(--font-sans);
          outline: none;
        }

        .add-habit-btn {
          padding: 10px 20px;
          font-size: 0.9rem;
        }

        /* Overlay Modals */
        .modal-backdrop {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background-color: rgba(2, 6, 23, 0.7);
          backdrop-filter: blur(8px);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000;
          padding: 20px;
        }

        .modal-content {
          width: 460px;
          max-width: 100%;
          padding: 32px;
          position: relative;
        }

        .modal-close-btn {
          position: absolute;
          right: 20px;
          top: 20px;
          font-size: 1.8rem;
          background: none;
          border: none;
          color: hsl(var(--text-secondary));
          cursor: pointer;
          transition: var(--transition-smooth);
        }

        .modal-close-btn:hover {
          color: hsl(var(--text-primary));
        }

        .modal-inner {
          display: flex;
          flex-direction: column;
          align-items: center;
          text-align: center;
          gap: 16px;
        }

        .modal-inner h2 {
          font-size: 1.4rem;
          font-weight: 800;
          letter-spacing: -0.02em;
        }

        .modal-stat-value {
          font-size: 1.8rem;
          font-weight: 800;
          color: hsl(var(--primary));
          margin: 6px 0;
        }

        .modal-description {
          font-size: 0.9rem;
          color: hsl(var(--text-secondary));
          line-height: 1.4;
        }

        .modal-progress-bar {
          width: 100%;
          height: 12px;
          border-radius: 6px;
          background-color: hsl(var(--text-secondary) / 0.1);
          overflow: hidden;
          margin-top: 8px;
        }

        .modal-progress-fill {
          height: 100%;
          background-color: hsl(var(--primary));
          border-radius: 6px;
          transition: width 0.5s ease;
        }

        .modal-progress-pct {
          font-size: 0.8rem;
          font-weight: 600;
          color: hsl(var(--text-secondary));
        }

        .goal-adjuster {
          width: 100%;
          border-top: 1px solid hsl(var(--text-secondary) / 0.08);
          padding-top: 16px;
          margin-top: 12px;
          display: flex;
          flex-direction: column;
          gap: 10px;
        }

        .goal-adjuster h3 {
          font-size: 0.95rem;
          font-weight: 700;
          color: hsl(var(--text-primary));
        }

        .adjuster-row {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 16px;
        }

        .btn-adjust {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          background-color: hsl(var(--bg-muted));
          border: 1px solid var(--border-color);
          color: hsl(var(--primary));
          font-size: 1.2rem;
          font-weight: 700;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: var(--transition-smooth);
        }

        .btn-adjust:hover {
          background-color: hsl(var(--primary));
          color: #020617;
          border-color: hsl(var(--primary));
        }

        .adjuster-value {
          font-size: 1.1rem;
          font-weight: 700;
          color: hsl(var(--text-primary));
          min-width: 100px;
        }

        .sensor-override-panel {
          width: 100%;
          background-color: hsl(var(--text-secondary) / 0.03);
          border: 1px dashed hsl(var(--text-secondary) / 0.15);
          border-radius: var(--radius-md);
          padding: 16px;
          margin-top: 16px;
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .sensor-override-panel h4 {
          font-size: 0.85rem;
          font-weight: 700;
          color: hsl(var(--primary));
        }

        .sensor-override-panel p {
          font-size: 0.75rem;
          color: hsl(var(--text-secondary));
        }

        .mock-buttons {
          display: flex;
          gap: 12px;
          margin-top: 6px;
        }

        .mock-buttons button {
          flex: 1;
          padding: 8px 12px;
          font-size: 0.85rem;
        }

        /* Water intake modal styles */
        .water-cups-grid {
          display: flex;
          flex-wrap: wrap;
          justify-content: center;
          gap: 10px;
          max-width: 320px;
          margin: 12px auto;
        }

        .water-cup-bubble {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background-color: hsl(var(--text-secondary) / 0.08);
          border: 1.5px solid transparent;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.1rem;
          transition: var(--transition-smooth);
          filter: grayscale(100%) opacity(40%);
        }

        .water-cup-bubble.filled {
          background-color: hsl(var(--primary) / 0.12);
          border-color: hsl(var(--primary) / 0.3);
          filter: none;
        }

        .log-water-btn {
          width: 100%;
          margin-top: 12px;
          height: 46px;
        }

        /* Streak modal styles */
        .fire-avatar {
          width: 100px;
          height: 100px;
          border-radius: 50%;
          background-color: hsl(var(--primary) / 0.08);
          border: 1px solid hsl(var(--primary) / 0.2);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 3rem;
          margin-bottom: 8px;
        }

        .calories-calc-note {
          font-size: 0.75rem;
          color: hsl(var(--text-secondary));
          margin-top: 4px;
        }
      `}</style>
    </div>
  );
}