import { useState, useEffect } from "react";
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

export default function Pets() {
  const {
    coins,
    selectedPet,
    petName,
    petHappiness,
    feedPet,
    renamePet,
    changeSelectedPet
  } = useApp();

  const [newNameInput, setNewNameInput] = useState("");
  const [showHearts, setShowHearts] = useState(false);
  const [message, setMessage] = useState({ text: "", type: "" }); // { text, type: 'success'|'error' }

  // Clear message after 3 seconds
  useEffect(() => {
    if (message.text) {
      const timer = setTimeout(() => setMessage({ text: "", type: "" }), 3000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  // Determine pet image assets and details
  const getPetDetails = () => {
    let image = catHappy;
    let type = "Cat";
    let mood = "Happy! ✨";

    if (selectedPet === 0) {
      type = "Cat";
      if (petHappiness < 40) {
        image = catSad;
        mood = "Sad 😢";
      } else if (petHappiness < 80) {
        image = catSleepy;
        mood = "Sleepy 😴";
      } else {
        image = catHappy;
        mood = "Happy! ✨";
      }
    } else if (selectedPet === 1) {
      type = "Dog";
      if (petHappiness < 40) {
        image = dogSad;
        mood = "Sad 😢";
      } else if (petHappiness < 80) {
        image = dogSleepy;
        mood = "Sleepy 😴";
      } else {
        image = dogHappy;
        mood = "Happy! ✨";
      }
    } else {
      type = "Panda";
      if (petHappiness < 40) {
        image = pandaSad;
        mood = "Sad 😢";
      } else if (petHappiness < 80) {
        image = pandaSleepy;
        mood = "Sleepy 😴";
      } else {
        image = pandaHappy;
        mood = "Happy! ✨";
      }
    }

    return { image, type, mood };
  };

  const { image: petImage, type: petType, mood: petMood } = getPetDetails();

  const handleFeed = async () => {
    if (coins < 10) {
      setMessage({ text: "Insufficient Coins! Complete habits to earn tokens.", type: "error" });
      return;
    }

    const success = await feedPet();
    if (success) {
      setShowHearts(true);
      setMessage({ text: `${petName} says Thank you! Yum! 🍖 (+15 Happiness)`, type: "success" });
      setTimeout(() => setShowHearts(false), 1500);
    }
  };

  const handleRename = (e) => {
    e.preventDefault();
    if (!newNameInput.trim()) return;
    renamePet(newNameInput);
    setMessage({ text: `Pet renamed to ${newNameInput.trim()}!`, type: "success" });
    setNewNameInput("");
  };

  return (
    <div className="pets-page-wrapper">
      {/* Header section */}
      <section className="pets-header">
        <div>
          <h1 className="pets-title-text">Pet Companion 🐾</h1>
          <p className="pets-subtitle-text">Keep your virtual pet active and fed</p>
        </div>
        <div className="coins-badge-card glass-card">
          <span>🪙 {coins} tokens</span>
        </div>
      </section>

      {/* Action status message banner */}
      {message.text && (
        <div className={`status-banner glass-card ${message.type}`}>
          <span>{message.text}</span>
        </div>
      )}

      <div className="pets-grid">
        {/* Main Pet Display Card */}
        <div className="pet-display-section">
          <div className="pet-card glass-card">
            <div className="happiness-ring-container">
              {/* Circular happiness indicator */}
              <svg className="happiness-ring-svg" viewBox="0 0 220 220">
                <circle cx="110" cy="110" r="96" className="happiness-ring-track" />
                <circle
                  cx="110"
                  cy="110"
                  r="96"
                  className="happiness-ring-fill anim-glow"
                  strokeDasharray={2 * Math.PI * 96}
                  strokeDashoffset={2 * Math.PI * 96 * (1 - petHappiness / 100)}
                />
              </svg>

              <div className="pet-image-container">
                <img src={petImage} alt="Virtual Pet Companion" className="pet-portrait anim-float" />
              </div>

              {/* Heart floating feeding animation */}
              {showHearts && (
                <div className="hearts-overlay anim-heart">
                  ❤️🍗❤️
                </div>
              )}
            </div>

            <div className="pet-identity">
              <h2 className="pet-name-label">{petName}</h2>
              <div className="pet-type-badge">{petType}</div>
            </div>

            <div className="happiness-status">
              <div className="happiness-header">
                <span>Happiness Level:</span>
                <span className="happiness-val">{petHappiness} / 100 ({petMood})</span>
              </div>
              <div className="happiness-bar-track">
                <div
                  className="happiness-bar-fill"
                  style={{ width: `${petHappiness}%` }}
                ></div>
              </div>
            </div>
          </div>
        </div>

        {/* Customization and Feeding Controls */}
        <div className="pet-controls-section">
          {/* Feed Card */}
          <div className="control-card glass-card">
            <h3 className="control-card-title">Feed your Companion 🍖</h3>
            <p className="control-card-desc">
              Spend 10 tokens to feed your pet. Adds +15 happiness instantly!
            </p>
            <button className="btn-primary feed-submit-btn" onClick={handleFeed}>
              🍖 Feed {petName} (10 Coins)
            </button>
          </div>

          {/* Switch Pet and Rename Card */}
          <div className="control-card glass-card">
            <h3 className="control-card-title">Customize Companion</h3>
            
            {/* Rename form */}
            <form onSubmit={handleRename} className="rename-form">
              <label className="control-label">Rename Pet</label>
              <div className="rename-row">
                <input
                  type="text"
                  placeholder={`Current: ${petName}`}
                  value={newNameInput}
                  onChange={(e) => setNewNameInput(e.target.value)}
                  className="input-field rename-input"
                  required
                />
                <button type="submit" className="btn-primary rename-btn">Save</button>
              </div>
            </form>

            {/* Switch Pet type */}
            <div className="switch-pet-section">
              <span className="control-label">Switch active Pet type:</span>
              <div className="pet-type-grid">
                <button
                  onClick={() => changeSelectedPet(0)}
                  className={`pet-type-card ${selectedPet === 0 ? "active" : ""}`}
                >
                  <span className="type-emoji">🐱</span>
                  <span className="type-name">Cat</span>
                </button>

                <button
                  onClick={() => changeSelectedPet(1)}
                  className={`pet-type-card ${selectedPet === 1 ? "active" : ""}`}
                >
                  <span className="type-emoji">🐶</span>
                  <span className="type-name">Dog</span>
                </button>

                <button
                  onClick={() => changeSelectedPet(2)}
                  className={`pet-type-card ${selectedPet === 2 ? "active" : ""}`}
                >
                  <span className="type-emoji">🐼</span>
                  <span className="type-name">Panda</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <style>{`
        .pets-page-wrapper {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .pets-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 8px 0;
        }

        .pets-title-text {
          font-size: 2.2rem;
          font-weight: 800;
          letter-spacing: -0.04em;
        }

        .pets-subtitle-text {
          font-size: 1rem;
          color: hsl(var(--text-secondary));
        }

        .coins-badge-card {
          padding: 8px 16px;
          border-radius: var(--radius-md);
          font-weight: 700;
          color: hsl(var(--primary));
          font-size: 0.95rem;
          background-color: hsl(var(--primary) / 0.08);
          border-color: hsl(var(--primary) / 0.15);
        }

        .status-banner {
          padding: 12px 20px;
          border-radius: var(--radius-md);
          font-weight: 600;
          font-size: 0.95rem;
          text-align: center;
          animation: slideIn 0.3s ease;
        }

        .status-banner.success {
          background-color: hsl(var(--success) / 0.1);
          border-color: hsl(var(--success) / 0.3);
          color: hsl(var(--success));
        }

        .status-banner.error {
          background-color: hsl(var(--error) / 0.1);
          border-color: hsl(var(--error) / 0.3);
          color: hsl(var(--error));
        }

        @keyframes slideIn {
          from { transform: translateY(-10px); opacity: 0; }
          to { transform: translateY(0); opacity: 1; }
        }

        /* Grid Layout */
        .pets-grid {
          display: grid;
          grid-template-columns: 1.2fr 1fr;
          gap: 24px;
        }

        @media (max-width: 1024px) {
          .pets-grid {
            grid-template-columns: 1fr;
          }
        }

        /* Pet Card Display Column */
        .pet-display-section {
          display: flex;
          flex-direction: column;
        }

        .pet-card {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 48px 40px;
          text-align: center;
        }

        .happiness-ring-container {
          position: relative;
          width: 220px;
          height: 220px;
          margin-bottom: 28px;
        }

        .happiness-ring-svg {
          width: 100%;
          height: 100%;
          transform: rotate(-90deg);
        }

        .happiness-ring-track {
          fill: none;
          stroke: hsl(var(--text-secondary) / 0.08);
          stroke-width: 9px;
        }

        .happiness-ring-fill {
          fill: none;
          stroke: hsl(var(--primary));
          stroke-width: 9px;
          stroke-linecap: round;
          transition: stroke-dashoffset 0.8s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .pet-image-container {
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

        .pet-portrait {
          width: 82%;
          height: 82%;
          object-fit: contain;
        }

        .hearts-overlay {
          position: absolute;
          top: 35%;
          font-size: 2.2rem;
          font-weight: bold;
          z-index: 10;
          pointer-events: none;
        }

        .pet-identity {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 6px;
          margin-bottom: 24px;
        }

        .pet-name-label {
          font-size: 2.2rem;
          font-weight: 800;
          letter-spacing: -0.03em;
        }

        .pet-type-badge {
          background-color: hsl(var(--primary) / 0.1);
          border: 1.5px solid hsl(var(--primary) / 0.3);
          color: hsl(var(--primary));
          font-weight: 700;
          font-size: 0.75rem;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          padding: 4px 12px;
          border-radius: 8px;
        }

        .happiness-status {
          width: 100%;
          max-width: 380px;
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .happiness-header {
          display: flex;
          justify-content: space-between;
          font-size: 0.9rem;
          font-weight: 600;
          color: hsl(var(--text-secondary));
        }

        .happiness-val {
          color: hsl(var(--text-primary));
          font-weight: 700;
        }

        .happiness-bar-track {
          width: 100%;
          height: 10px;
          border-radius: 5px;
          background-color: hsl(var(--text-secondary) / 0.08);
          overflow: hidden;
        }

        .happiness-bar-fill {
          height: 100%;
          background-color: hsl(var(--primary));
          border-radius: 5px;
          transition: width 0.5s ease;
        }

        /* Controls Column */
        .pet-controls-section {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .control-card {
          padding: 28px;
          display: flex;
          flex-direction: column;
        }

        .control-card-title {
          font-size: 1.15rem;
          font-weight: 800;
          margin-bottom: 6px;
        }

        .control-card-desc {
          font-size: 0.85rem;
          color: hsl(var(--text-secondary));
          margin-bottom: 20px;
          line-height: 1.4;
        }

        .feed-submit-btn {
          width: 100%;
          height: 50px;
        }

        .rename-form {
          display: flex;
          flex-direction: column;
          gap: 8px;
          margin-bottom: 24px;
        }

        .control-label {
          font-size: 0.85rem;
          font-weight: 700;
          color: hsl(var(--text-secondary));
          padding-left: 2px;
        }

        .rename-row {
          display: flex;
          gap: 12px;
        }

        .rename-input {
          flex-grow: 1;
        }

        .rename-btn {
          padding: 0 20px;
          flex-shrink: 0;
        }

        .switch-pet-section {
          display: flex;
          flex-direction: column;
          gap: 10px;
          border-top: 1px solid hsl(var(--text-secondary) / 0.08);
          padding-top: 20px;
        }

        .pet-type-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 10px;
        }

        .pet-type-card {
          height: 76px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          border-radius: var(--radius-md);
          background-color: transparent;
          border: 1.5px solid hsl(var(--text-secondary) / 0.15);
          cursor: pointer;
          transition: var(--transition-smooth);
        }

        .pet-type-card:hover {
          border-color: hsl(var(--text-secondary) / 0.3);
          background-color: hsl(var(--bg-muted) / 0.3);
        }

        .pet-type-card.active {
          border-color: hsl(var(--primary));
          background-color: hsl(var(--primary) / 0.12);
        }

        .type-emoji {
          font-size: 1.6rem;
          line-height: 1;
        }

        .type-name {
          font-size: 0.75rem;
          font-weight: 700;
          color: hsl(var(--text-primary));
          margin-top: 4px;
        }
      `}</style>
    </div>
  );
}
