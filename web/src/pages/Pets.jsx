import { useState, useEffect } from "react";
import { useApp } from "../context/AppContext";

// Import Pet Assets (idle only)
import catHappy from "../assets/cat_happy.png";
import dogHappy from "../assets/dog_happy.png";
import pandaHappy from "../assets/panda_happy.png";

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
  // Always show idle (happy) image on the Pets screen
  const getPetDetails = () => {
    if (selectedPet === 0) return { image: catHappy,   type: "Cat",   mood: "Happy! ✨" };
    if (selectedPet === 1) return { image: dogHappy,   type: "Dog",   mood: "Happy! ✨" };
    return                        { image: pandaHappy, type: "Panda", mood: "Happy! ✨" };
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
            <div className="pet-image-container">
              <img src={petImage} alt="Virtual Pet Companion" className="pet-portrait anim-float" />
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

        .pet-image-container {
          position: relative;
          width: 200px;
          height: 200px;
          border-radius: 50%;
          overflow: hidden;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: hsl(var(--primary) / 0.06);
          border: 2px solid hsl(var(--primary) / 0.15);
          margin-bottom: 28px;
        }

        .pet-portrait {
          width: 85%;
          height: 85%;
          object-fit: contain;
        }

        .hearts-overlay {
          position: absolute;
          top: 30%;
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
