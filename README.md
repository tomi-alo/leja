# 📈 LEJA

**LEJA** is a streamlined trading journal app designed to take the manual math out of tracking closed trades. Once you input a trade, Leja automatically calculates your total Profit & Loss (PnL), risk-to-reward ratio, and outcome status (`WIN`, `LOSS`, or `BREAK EVEN`).

---

## 🌐 Live Demo

* **Web Application:** [lejafrontend.vercel.app](https://lejafrontend.vercel.app/)
* **API Base URL:** `https://leja-e0j6.onrender.com`
* **Interactive API Docs:** [Swagger UI (Live Demo)](https://leja-e0j6.onrender.com/swagger-ui/index.html)

> 💡 **A Quick Note on Cold Starts:**  
> The backend runs on Render’s free tier and goes to sleep when idle. The very first request might take up to two minutes to wake up the server, during which the frontend may temporarily appear empty. Give it a quick refresh once the backend responds!

---

## 💡 What You Can Do in the App

* **Dashboard (`/`):** A high-level overview of your trading performance, including total PnL, average PnL, win rate, total trade count, and average risk-to-reward. You’ll also find your recent trades alongside interactive session breakdown charts and performance by symbol.
* **Journal (`/journal`):** A list of all logged trades with filters for symbol, trading session, and outcome. You can add, edit, or delete trades from here, and review your trading notes (`reason`). Clicking a date in the calendar auto-filters the journal.
* **Calendar (`/calendar`):** A month-at-a-glance view showing daily PnL totals and the number of closed trades for each day.
* **Trade Detail (`/journal/:id`):** Focuses on an individual trade with full edit and delete capabilities.

### Public vs. Authenticated Access
Anyone can browse public trades without logging in. Adding, updating, or deleting trades requires HTTP Basic Authentication. Without valid credentials on the live demo, nothing you enter will be saved, and any `POST`, `PATCH`, or `DELETE` request will return a `401 Unauthorized` response.

---

## 🛠️ Tech Stack

| Layer | Technology |
| --- | --- |
| **Frontend** | React 19, TypeScript, Tailwind CSS, React Router, Vite |
| **Backend** | Java 21, Spring Boot 3.3.5, Maven |
| **Database** | PostgreSQL (Neon in production) |
| **Hosting** | Vercel (Frontend), Render (API), Neon (Database) |
| **CI Pipeline** | GitHub Actions (API unit testing, frontend linting/building, and Docker build verification) |

---

## 📡 API & Endpoints

All trade resources live under `/api/v1/trades`.

| Method | Endpoint | Live Access Level | Description |
| --- | --- | --- | --- |
| `GET` | `/api/v1/trades` | **Public** | Retrieves all trades. |
| `GET` | `/api/v1/trades/{id}` | **Public** | Retrieves a single trade (returns `404` if not found). |
| `GET` | `/actuator/health` | **Public** | Returns application health status. |
| `POST` | `/api/v1/trades` | **Protected** (`401` on Live) | Creates a new trade record (`201`). |
| `PATCH` | `/api/v1/trades/{id}` | **Protected** (`401` on Live) | Updates specific fields on an existing trade (`200`). |
| `DELETE` | `/api/v1/trades/{id}` | **Protected** (`401` on Live) | Deletes a trade (`204`, or `404` if not found). |

> `PUT` is intentionally not supported. Use `PATCH` for partial updates.

Try the live API from your terminal (Windows PowerShell users can use `curl.exe` instead of `curl`):

```bash
curl https://leja-e0j6.onrender.com/actuator/health
curl https://leja-e0j6.onrender.com/api/v1/trades
curl https://leja-e0j6.onrender.com/api/v1/trades/1
```

<details>
<summary><b>Example request & response (<code>POST /api/v1/trades</code>)</b></summary>

Send numeric values (prices and sizes) as strings. Sizes accept up to 4 decimal places, while prices support up to 5.

```json
{
  "symbol": "eurusd",
  "direction": "BUY",
  "session": "NY",
  "positionSize": "1",
  "contractSize": "1",
  "entryPrice": "100",
  "exitPrice": "110",
  "stopLoss": "90",
  "takeProfit": "120",
  "reason": "liq sweep"
}
```

Response (`201 Created`):

```json
{
  "id": 1,
  "symbol": "EURUSD",
  "direction": "BUY",
  "session": "NY",
  "positionSize": 1,
  "contractSize": 1,
  "entryPrice": 100,
  "exitPrice": 110,
  "stopLoss": 90,
  "takeProfit": 120,
  "pnl": 10.00,
  "riskToRewardRatio": 2.00,
  "reason": "liq sweep",
  "outcome": "WIN",
  "executedAt": "2026-10-01T15:00:00Z"
}
```

</details>

<details>
<summary><b>Validation rules & how calculations work</b></summary>

**Trade levels:**
* For **`BUY`** orders, `stopLoss` must sit below `entryPrice`, and `takeProfit` must sit above `entryPrice`.
* For **`SELL`** orders, `stopLoss` must sit above `entryPrice`, and `takeProfit` must sit below `entryPrice`.
* Invalid level configurations return a `400 Bad Request` naming the problematic price.

**Field formatting:**
* `direction`: Accepts `BUY` or `SELL` (case-insensitive, normalized to uppercase).
* `session`: Accepts `NY`, `ASIA`, or `LONDON` (case-insensitive, normalized to uppercase).
* `symbol`: Required, max 32 characters. All spaces are stripped and converted to uppercase (e.g., `eur usd` becomes `EURUSD`).
* `reason`: Optional, up to 255 characters. Sending `"reason": ""` in a `PATCH` request clears existing notes.
* `executedAt`: Optional ISO-8601 timestamp; defaults to current server time if omitted.
* `exitPrice`: Required upon creation because Leja explicitly tracks closed trades.

**Calculations:**
* **PnL:** `(exitPrice - entryPrice) × positionSize × contractSize`, with the sign inverted for `SELL` trades and rounded to 2 decimal places.
* **Risk-to-Reward Ratio:** `|takeProfit - entryPrice| / |entryPrice - stopLoss|`, rounded to 2 decimal places.
* **Outcome:** `WIN` when PnL > 0, `LOSS` when PnL < 0, `BREAK EVEN` when PnL = 0.

</details>

---

## ⚙️ Running Locally

Running locally gives you full write access (`POST`, `PATCH`, `DELETE`).

**Prerequisites:** JDK 21, Maven 3.9+ (or the included `./mvnw` wrapper), PostgreSQL, and Node.js 24.

### 1. Create the database

```sql
CREATE DATABASE leja;
```

Hibernate updates the schema automatically via `ddl-auto=update`, but it will not create the database itself.

### 2. Run the backend

The API expects a non-blank `LEJA_PASSWORD` to launch successfully.

**On Linux / macOS:**

```bash
git clone https://github.com/tomi-alo/leja.git
cd leja

export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/leja"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="your-db-password"
export LEJA_PASSWORD="your-api-password"

./mvnw spring-boot:run
```

**On Windows (PowerShell):**

```powershell
git clone https://github.com/tomi-alo/leja.git
cd leja

$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/leja"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "your-db-password"
$env:LEJA_PASSWORD = "your-api-password"

.\mvnw.cmd spring-boot:run
```

Swagger UI will be at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

### 3. Run the frontend

```bash
cd frontend
npm ci
npm run dev
```

Vite starts the dev server at `http://localhost:5173` and automatically proxies `/api` requests to your backend at `http://localhost:8080`.

### 4. Create a trade (optional)

```bash
curl -u leja:your-api-password -X POST http://localhost:8080/api/v1/trades \
  -H "Content-Type: application/json" \
  -d '{"symbol":"eurusd","direction":"BUY","session":"NY","positionSize":"1","contractSize":"1","entryPrice":"100","exitPrice":"110","stopLoss":"90","takeProfit":"120","reason":"liq sweep"}'
```

---

## 🧪 Tests & CI

```bash
# Backend tests (no database needed)
./mvnw test        # Linux/macOS
.\mvnw.cmd test    # Windows

# Frontend checks
cd frontend
npm ci
npm run lint
npm run build
```

On every push or pull request to `main`, GitHub Actions ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)) runs the backend tests on JDK 21, lints and builds the frontend, then builds a test Docker image (`leja:ci`) once both pass.
