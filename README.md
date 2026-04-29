<h1>ReCircuit AI: Global Recycling Marketplace</h1>
<div align="center">
  <h3>🏆 Built for the Witch Hunt Hackathon 🏆</h3>
  <p><strong>A high-fidelity sustainability platform bridging the gap between individual recyclers and industrial procurement needs.</strong></p>
</div>

**ReCircuit AI** is a high-fidelity sustainability platform designed to bridge the gap between individual recyclers and industrial procurement needs. Using advanced AI-driven material recognition, the platform enables users to scan waste items, identify recyclable materials, and instantly connect with companies looking for specific resources.

Video demo: https://drive.google.com/file/d/1aakRFigMzwPrLNPHf8pDPk9djRJKC6oL/view?usp=sharing

---

## Key Features
- **AI Material Scanning:** Powered by Google Gemini AI to accurately classify nature-made and industrial materials.
- **Real-Time Marketplace:** Instant synchronization between user uploads and company procurement needs.
- **High-Fidelity UI:** A premium, interactive mobile experience built with Jetpack Compose.
- **Smart Matching:** Automatic "High-Fidelity Match" badges for items that meet active industrial demands.

---

## System Architecture
Our ecosystem is structured into three highly decoupled components ensuring scalability and real-time performance:

<details>
<summary><b>1️. AI Service (<code>WitchHunt Engine</code>)</b></summary>
<br>
A robust <b>Python/FastAPI</b> backend managing complex image classification via the Gemini API.
</details>

<details>
<summary><b>2️. Central Backend (<code>Node.js</code>)</b></summary>
<br>
An <b>Express.js</b> server orchestrating data management, media uploads, and real-time synchronization.
</details>

<details>
<summary><b>3️. Mobile Client (<code>Android</code>)</b></summary>
<br>
A highly responsive <b>Jetpack Compose</b> application that dynamically adapts its UI for both individual users and corporate procurement teams.
</details>

---

## Prerequisites
- **Android Studio** (Koala or newer)
- **Node.js & npm**
- **Python 3.10+**
- **Google Gemini API Key** (Get one at [aistudio.google.com](https://aistudio.google.com/))

---

## Installation & Setup

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

## How to Use
Our platform provides a seamless, two-sided marketplace tailored for both individual contributors and large-scale industrial buyers.

### For Individuals (The Supply)
1. **Snap & Scan:** Users open the **Upload Screen** and take a photo of their recyclable/upcyclable waste (e.g., paper, electronics, or scrap wood).
2. **AI Processing:** The image is sent to our `WitchHunt` AI Engine. Google Gemini processes the visual data, automatically categorizing the item, determining its material composition, and assessing its quality.
3. **One-Tap Listing:** The app autofills the listing details, allowing the user to publish their item to the global marketplace with a single tap.

### For Companies (The Demand)
1. **Enter Corporate Mode:** Procurement officers can toggle into Company Mode by navigating to their Profile and tapping the **✨ Sparkle Button**.
2. **Live Procurement Hub:** Companies access a dedicated "Procurement Needs" dashboard that aggregates available materials.
3. **Instant Smart Matches:** As individuals upload items, they appear *instantly* on the company's dashboard via live sync. Items that perfectly align with a company's raw material needs are flagged with a **High-Fidelity Match** badge, making sustainable procurement effortless.
