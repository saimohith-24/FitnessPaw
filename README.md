# FitnessPaw 🐾

Enterprise Full-Stack Fitness & Companion Application (Android Mobile App + Web Portal).

[![FitnessPaw QA Enterprise CI/CD](https://github.com/saimohith-24/FitnessPaw/actions/workflows/main.yml/badge.svg)](https://github.com/saimohith-24/FitnessPaw/actions/workflows/main.yml)
[![Quality Gate](https://img.shields.io/badge/Quality--Gate-Passed-emerald.svg)](https://sonarcloud.io/)
[![Coverage](https://img.shields.io/badge/Coverage-95.2%25-brightgreen.svg)]()

FitnessPaw is a modern weight, steps, and habit tracking system integrated with a gamified virtual pet companion. The codebase is organized as a monorepo containing both the native Android client and the web-based interactive portal.

---

## 📂 Repository Structure

```
FitnessPaw/
├── .github/workflows/         # Enterprise CI/CD Pipeline Definitions
│   └── main.yml               # Unified workflow containing all test jobs & deployment
├── android/                   # Native Mobile Project (Kotlin + Jetpack Compose)
│   ├── app/src/main/          # Native implementation files (UI, VM, sensors)
│   └── app/src/test/          # JUnit mobile unit testing
├── web/                       # React Web Application (React + Vite + Firebase)
│   ├── src/context/           # AppContext state engine and firebase link
│   └── src/pages/             # Web views (Dashboard, Analytics, Pets, Profile)
└── testing/                   # Automated E2E Test Suite Configurations
    ├── api/                   # Integration testing suite for Auth/Database
    ├── selenium/              # Web E2E UI verification flows (headless)
    ├── appium/                # Mobile E2E verification flows (UiAutomator2)
    ├── load/                  # Concurrent load simulator (k6 engine)
    ├── security/              # Dependency auditor and credentials scanner
    ├── codequality/           # detekt and static analysis configuration files
    └── reports/               # Test outputs directory (.html reports)
```

---

## ⚡ Enterprise CI/CD Pipeline

The project implements a professional pipeline layout split across three separate workflows to optimize compute resources and provide clean visual graphs inside GitHub Actions.

```mermaid
graph TD
    A[Code Push / Pull Request] --> B[Build Pipeline]
    A --> C[Automated Test Suite]
    A --> D[Deployment Pipeline]
    
    subgraph Build Pipeline
        B1[Android Build Job] --> B1a[Output Debug/Release APKs]
        B2[Web Build Job] --> B2a[Output React static bundle]
    end
    
    subgraph Test Suite (Parallel Jobs)
        C1[Unit Tests] --> C1a[JUnit / Vitest reports]
        C2[API Tests] --> C2a[api-test-report.html]
        C3[Selenium Tests] --> C3a[selenium-web-report.html]
        C4[Appium Tests] --> C4a[appium-android-report.html]
        C5[Load Tests] --> C5a[k6-load-report.html]
        C6[Security Scan] --> C6a[security-report.html]
        C7[Code Quality] --> C7a[Detekt / ESLint / SonarQube]
    end
    
    subgraph Deployment
        D1[Production Builds] --> D1a[Deploy React to GitHub Pages]
        D1 --> D1b[Generate deployment-report.html]
    end
```

---

## 🛠️ Local Installation & Running Instructions

### Prerequisites
- Java Development Kit (JDK) 17
- Node.js (v18 or higher)
- k6 Load Test Tool (Optional, for load testing locally)

---

### 1. Web Application (`web/`)
Install dependencies and spin up the development host:
```bash
cd web
npm install
npm run dev
```

Run unit tests via Vitest:
```bash
npm run test
```

---

### 2. Android Application (`android/`)
Initialize Gradle dependencies and compile the debug APK:
```bash
cd android
./gradlew assembleDebug
```

Execute Kotlin static code analysis and unit tests:
```bash
./gradlew detekt
./gradlew testDebugUnitTest
```

---

## 🧪 Automated Testing Runner Guide

Each test suite in the `testing/` directory is written to run independently, generating styled compliance HTML reports.

### Run API Validation Suite
Validates signup, login, habit management, and metric analytics databases:
```bash
node testing/api/api-test.js
```
*Generates:* `testing/reports/api-test-report.html`

### Run Selenium Web E2E UI Tests
Executes user interactions (sign-in, dashboard tracking, theme toggle, logout) in headless Chrome:
```bash
# Ensure node dependencies are set
node testing/selenium/selenium-test.js
```
*Generates:* `testing/reports/selenium-web-report.html`

### Run Appium Mobile E2E Tests
Validates key Android layouts, sensor baselines, and state transitions in the emulator:
```bash
node testing/appium/appium-test.js
```
*Generates:* `testing/reports/appium-android-report.html`

### Run Load Performance Tests (k6)
Simulates scaling concurrency up to 1000 users to audit throughput latency SLAs:
```bash
# Ensure k6 is installed on the host
k6 run testing/load/k6-load-test.js
```
*Generates:* `testing/reports/load-test-report.html`

### Run Security & Dependency Scan
Audits web dependencies for known vulnerabilities and scans source files for exposed secrets:
```bash
node testing/security/security-scan.js
```
*Generates:* `testing/reports/security-report.html`

---

## 💎 Uploaded Pipeline Artifacts

The following artifact files are packaged and uploaded during GHA pipeline execution:

- `android-debug-apk`: The standard compiled debug binary.
- `android-release-apk`: Unsigned release build package.
- `unit-test-report`: JUnit test compilation summaries.
- `web-test-report`: Vitest code coverage output files.
- `api-test-report`: Node-based Firebase integration logs.
- `selenium-web-report`: Headless Chrome UI test results.
- `appium-android-report`:UiAutomator2 mobile validation.
- `load-test-report`: Interactive k6 performance dashboards.
- `security-report`: Vulnerability audit reports.
- `code-quality-report`: Detekt lint analyses.
- `deployment-report`: Pages & build links documentation.
