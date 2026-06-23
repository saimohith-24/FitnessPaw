# FitnessPaw

Welcome to FitnessPaw! This repository contains both the Android application and the React/Vite web application.

## Repository Structure

- **`android/`**: The Android application project (Kotlin, Jetpack Compose, Firebase Auth, Firestore, WorkManager).
- **`web/`**: The Web application frontend (React, Vite, CSS, React Router, Firebase Auth, Firestore).

## Getting Started

### 1. Android Application (`android/`)
To run and test the Android application:
- Open the `android/` directory in Android Studio.
- Ensure you have JDK 17 configured.
- To run unit tests from the command line:
  ```bash
  cd android
  ./gradlew testDebugUnitTest
  ```

### 2. Web Application (`web/`)
To run and test the Web application:
- Navigate to the `web/` directory.
- Install dependencies:
  ```bash
  cd web
  npm install
  ```
- Run the development server:
  ```bash
  npm run dev
  ```
- Run unit tests:
  ```bash
  npm run test
  ```
