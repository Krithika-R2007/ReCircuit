# ♻️ ReCircuit AI: Global Recycling Marketplace

**ReCircuit AI** is a high-fidelity sustainability platform designed to bridge the gap between individual recyclers and industrial procurement needs. Using advanced AI-driven material recognition, the platform enables users to scan waste items, identify recyclable materials, and instantly connect with companies looking for specific resources.

---

## 🚀 Key Features
- **AI Material Scanning:** Powered by Google Gemini AI to accurately classify nature-made and industrial materials.
- **Real-Time Marketplace:** Instant synchronization between user uploads and company procurement needs.
- **High-Fidelity UI:** A premium, interactive mobile experience built with Jetpack Compose.
- **Smart Matching:** Automatic "High-Fidelity Match" badges for items that meet active industrial demands.

---

## 🏗️ System Architecture
The project is divided into three core components:
1. **AI Service (`WitchHunt`)**: A Python/FastAPI backend that manages image classification via the Gemini API.
2. **Central Backend (`Node.js`)**: An Express server that handles data management, uploads, and real-time synchronization.
3. **Mobile App (`Android`)**: A Jetpack Compose application that serves as the user interface for both individuals and companies.

---

## 🛠️ Prerequisites
- **Android Studio** (Koala or newer)
- **Node.js & npm**
- **Python 3.10+**
- **Google Gemini API Key** (Get one at [aistudio.google.com](https://aistudio.google.com/))

---

## ⚙️ Installation & Setup

### 1. AI Classification Service (Python)
This service must be running for the "Scan with AI" feature to work.
- Navigate to: `WitchHunt/WitchHunt/ai-service`
- Install dependencies:
  ```bash
  pip install -r requirements.txt
  ```
- Create a `.env` file in this directory and add your API key:
  ```env
  GEMINI_API_KEY=your_actual_api_key_here
  ```
- Run the service:
  ```bash
  python main.py
  ```
  *The service will start on port 8000.*

### 2. Central Backend (Node.js)
This server manages the marketplace listings and real-time sync.
- Navigate to: `ReCircuitAI_Frontend/backend`
- Install dependencies:
  ```bash
  npm install
  ```
- Run the server:
  ```bash
  node index.js
  ```
  *The server will start on port 3000.*

### 3. Mobile Application (Android)
- Open the project in **Android Studio**.
- **Configure Network:** You must point the app to your local computer's IP address.
  - Locate: `app/src/main/java/com/example/recircuitai/data/network/NetworkConfig.kt`
  - Update `ipAddress` to your laptop's Local IP (Find it by running `ipconfig` in your terminal).
- **Run:** Click the **Run** button in Android Studio to install the app on a physical device or emulator.

---

## 📖 How to Use
1. **Individual Mode:** Go to the Upload screen, snap a photo of a recyclable item (like a notebook or wood), and upload it.
2. **Company Mode:** Switch to the Profile section and click the **Sparkle Button**.
3. **Matching:** You will see your uploaded items appear instantly in the "Procurement Needs" hub with a verification badge.
