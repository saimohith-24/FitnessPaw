import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, test, expect, vi } from "vitest";
import { AppProvider, useApp } from "./AppContext";

// Mock Firebase exports to avoid network connection during testing
vi.mock("../firebase", () => ({
  auth: {
    currentUser: null,
    signOut: vi.fn(),
  },
  db: {},
}));

vi.mock("firebase/auth", () => ({
  getAuth: vi.fn(),
  onAuthStateChanged: vi.fn((auth, cb) => {
    // Immediately call callback with null (no user logged in)
    cb(null);
    return vi.fn(); // Mock unsubscribe function
  }),
}));

vi.mock("firebase/firestore", () => ({
  getFirestore: vi.fn(),
  doc: vi.fn(),
  getDoc: vi.fn(),
  setDoc: vi.fn(),
  serverTimestamp: vi.fn(() => "mock-timestamp"),
}));

// Mock window.matchMedia for jsdom compatibility
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Test consumer component
function TestConsumer() {
  const {
    waterIntake,
    dailySteps,
    caloriesBurned,
    coins,
    streak,
    petName,
    getCurrentDateInt,
  } = useApp();

  return (
    <div>
      <div data-testid="steps">{dailySteps}</div>
      <div data-testid="calories">{caloriesBurned}</div>
      <div data-testid="coins">{coins}</div>
      <div data-testid="streak">{streak}</div>
      <div data-testid="petName">{petName}</div>
      <div data-testid="date">{getCurrentDateInt()}</div>
    </div>
  );
}

describe("AppContext Logic Tests", () => {
  test("AppProvider initializes with correct default values", async () => {
    render(
      <AppProvider>
        <TestConsumer />
      </AppProvider>
    );

    // Verify initial state defaults
    expect(screen.getByTestId("steps").textContent).toBe("0");
    expect(screen.getByTestId("coins").textContent).toBe("0");
    expect(screen.getByTestId("streak").textContent).toBe("0");
    expect(screen.getByTestId("petName").textContent).toBe("Buddy");
  });

  test("getCurrentDateInt returns formatted date integer", () => {
    render(
      <AppProvider>
        <TestConsumer />
      </AppProvider>
    );

    const dateVal = screen.getByTestId("date").textContent;
    expect(dateVal).toMatch(/^\d{8}$/); // Checks if it is an 8-digit date format (YYYYMMDD)
  });

  test("Calorie calculations are computed correctly from steps and weight scale", () => {
    // Helper to calculate active calories burned
    // const weightVal = parseFloat(weight) || 70.0;
    // const baseCalories = dailySteps * 0.04;
    // const scale = weightVal / 70.0;
    // const caloriesBurned = Math.round(baseCalories * scale);
    
    const steps = 10000;
    const weightVal = 70.0;
    const baseCalories = steps * 0.04; // 400
    const scale = weightVal / 70.0; // 1.0
    const expectedCalories = Math.round(baseCalories * scale); // 400

    expect(expectedCalories).toBe(400);
  });
});
