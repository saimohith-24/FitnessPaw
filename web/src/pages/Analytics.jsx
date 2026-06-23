import { useEffect, useState } from "react";
import { useApp } from "../context/AppContext";

export default function Analytics() {
  const {
    streak,
    dailySteps,
    waterIntake,
    waterGoal,
    coins,
    caloriesBurned,
    customHabits
  } = useApp();

  const [animateChart, setAnimateChart] = useState(false);

  // Trigger chart entrance animations
  useEffect(() => {
    const timer = setTimeout(() => setAnimateChart(true), 150);
    return () => clearTimeout(timer);
  }, []);

  // Calculate today's completion percentage
  const totalHabitsCount = customHabits.length;
  const completedHabitsCount = customHabits.filter((h) => h.completed).length;
  const todayProgress = totalHabitsCount === 0 ? 0.0 : completedHabitsCount / totalHabitsCount;

  // Weekday identifiers (Sunday is 0, Monday is 1, ..., Saturday is 6)
  const todayDayIndex = new Date().getDay();
  const todayWeekPos = todayDayIndex === 0 ? 6 : todayDayIndex - 1;

  const dayNames = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

  // Dynamically map weekly progress based on active streak and current day's progress
  const resolvedDays = dayNames.map((name, i) => {
    let val = 0.0;
    const isToday = i === todayWeekPos;
    if (isToday) {
      val = todayProgress;
    } else if (i < todayWeekPos) {
      const daysAgo = todayWeekPos - i;
      val = (streak > 0 && daysAgo <= streak) ? 1.0 : 0.0;
    } else {
      val = 0.0;
    }
    return { name, val, isToday };
  });

  // Average weekly success calculation
  const avgCompletion = resolvedDays.reduce((acc, curr) => acc + curr.val, 0) / resolvedDays.length;

  return (
    <div className="analytics-page-wrapper">
      {/* Header section */}
      <section className="analytics-header">
        <h1 className="analytics-title-text">Analytics 📊</h1>
      </section>

      {/* Custom chart card */}
      <div className="chart-card glass-card">
        <h2 className="chart-title">Weekly Habit Success</h2>
        <div className="chart-container">
          <div className="bars-container">
            {resolvedDays.map((day, i) => {
              const barHeight = animateChart ? `${day.val * 100}%` : "0%";
              return (
                <div key={i} className={`bar-column ${day.isToday ? "today" : ""}`}>
                  <span className="bar-tooltip">{Math.round(day.val * 100)}%</span>
                  <div className="bar-track">
                    <div
                      className="bar-fill"
                      style={{ height: barHeight }}
                    ></div>
                  </div>
                  <span className="bar-label">{day.name}</span>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Performance metrics grid */}
      <div className="performance-section">
        <h2 className="section-title">Performance Metrics</h2>
        
        <div className="metrics-detail-grid">
          <div className="metric-detail-card glass-card">
            <span className="metric-val">{Math.round(avgCompletion * 100)}%</span>
            <span className="metric-title">Weekly Success</span>
            <span className="metric-desc">Avg habit completion</span>
          </div>

          <div className="metric-detail-card glass-card">
            <span className="metric-val">{streak} Days</span>
            <span className="metric-title">Streak Heat</span>
            <span className="metric-desc">Consecutive active days</span>
          </div>

          <div className="metric-detail-card glass-card">
            <span className="metric-val">{waterIntake} / {waterGoal}</span>
            <span className="metric-title">Hydration Stats</span>
            <span className="metric-desc">Cups logged today</span>
          </div>

          <div className="metric-detail-card glass-card">
            <span className="metric-val">{dailySteps.toLocaleString()}</span>
            <span className="metric-title">Steps Counter</span>
            <span className="metric-desc">Total steps walked today</span>
          </div>

          <div className="metric-detail-card glass-card">
            <span className="metric-val">{caloriesBurned} kcal</span>
            <span className="metric-title">Calories Burned</span>
            <span className="metric-desc">Based on steps/weight</span>
          </div>

          <div className="metric-detail-card glass-card">
            <span className="metric-val">🪙 {coins}</span>
            <span className="metric-title">Earned Tokens</span>
            <span className="metric-desc">Total paw tokens collected</span>
          </div>
        </div>
      </div>

      <style>{`
        .analytics-page-wrapper {
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .analytics-header {
          padding: 8px 0;
        }

        .analytics-title-text {
          font-size: 2.2rem;
          font-weight: 800;
          letter-spacing: -0.04em;
        }

        /* Chart Card */
        .chart-card {
          padding: 32px;
          display: flex;
          flex-direction: column;
          gap: 24px;
        }

        .chart-title {
          font-size: 1.25rem;
          font-weight: 800;
          letter-spacing: -0.02em;
        }

        .chart-container {
          height: 200px;
          display: flex;
          align-items: flex-end;
          padding-top: 24px;
          margin-bottom: 8px;
        }

        .bars-container {
          display: flex;
          justify-content: space-between;
          align-items: flex-end;
          width: 100%;
          height: 100%;
        }

        .bar-column {
          display: flex;
          flex-direction: column;
          align-items: center;
          width: 48px;
          height: 100%;
          position: relative;
        }

        .bar-tooltip {
          position: absolute;
          top: -24px;
          font-size: 0.75rem;
          font-weight: 700;
          color: hsl(var(--text-secondary));
          transition: var(--transition-smooth);
        }

        .bar-column.today .bar-tooltip {
          color: hsl(var(--primary));
          font-weight: 800;
        }

        .bar-track {
          flex-grow: 1;
          width: 16px;
          background-color: hsl(var(--text-secondary) / 0.05);
          border-radius: 8px;
          display: flex;
          flex-direction: column;
          justify-content: flex-end;
          overflow: hidden;
          margin-bottom: 8px;
        }

        .bar-fill {
          width: 100%;
          border-radius: 8px;
          background: linear-gradient(to top, hsl(var(--text-secondary) / 0.15), hsl(var(--text-secondary) / 0.3));
          transition: height 1s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .bar-column.today .bar-fill {
          background: linear-gradient(to top, hsl(var(--primary) / 0.6), hsl(var(--primary)));
          box-shadow: 0 0 10px 0 hsl(var(--primary) / 0.3);
        }

        .bar-label {
          font-size: 0.85rem;
          font-weight: 600;
          color: hsl(var(--text-secondary));
          transition: var(--transition-smooth);
        }

        .bar-column.today .bar-label {
          color: hsl(var(--primary));
          font-weight: 800;
        }

        /* Performance detail grid */
        .performance-section {
          display: flex;
          flex-direction: column;
          gap: 16px;
        }

        .section-title {
          font-size: 1.4rem;
          font-weight: 800;
          letter-spacing: -0.02em;
        }

        .metrics-detail-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 16px;
        }

        @media (max-width: 768px) {
          .metrics-detail-grid {
            grid-template-columns: 1fr;
          }
        }

        @media (min-width: 769px) and (max-width: 1024px) {
          .metrics-detail-grid {
            grid-template-columns: 1fr 1fr;
          }
        }

        .metric-detail-card {
          padding: 24px;
          display: flex;
          flex-direction: column;
          justify-content: center;
          height: 120px;
        }

        .metric-val {
          font-size: 1.6rem;
          font-weight: 800;
          color: hsl(var(--primary));
          line-height: 1.1;
          margin-bottom: 4px;
        }

        .metric-title {
          font-size: 0.95rem;
          font-weight: 700;
          color: hsl(var(--text-primary));
        }

        .metric-desc {
          font-size: 0.75rem;
          color: hsl(var(--text-secondary));
          margin-top: 2px;
        }
      `}</style>
    </div>
  );
}
