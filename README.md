# Leja

**Leja** is a REST API for a trading journal. When a trade position is closed, Leja automatically calculates the Profit & Loss (PnL), risk-to-reward ratio, and outcome (`WIN`, `LOSS`, or `BREAK EVEN`).

---

## API & Endpoints

Explore and interact with the endpoints using either the live demo server or your own local instance:

- **Live Base URL:** `https://leja-e0j6.onrender.com`
- **Local Base URL:** `http://localhost:8080`
- **Interactive Documentation:** [Swagger UI (Live Demo)](https://leja-e0j6.onrender.com/swagger-ui/index.html)

### Endpoint Summary

All trade routes live under `/api/v1/trades`.

| Method | Endpoint | Live Server Access | Description |
| --- | --- | --- | --- |
| `GET` | `/api/v1/trades` | **Available** | Retrieves all trades. |
| `GET` | `/api/v1/trades/{id}` | **Available** | Retrieves a specific trade by ID (returns `404` if not found). |
| `GET` | `/actuator/health` | **Available** | Returns application health status. |
| `POST` | `/api/v1/trades` | **Protected** (401 on Live) | Creates a new trade (returns `201`). |
| `PATCH` | `/api/v1/trades/{id}` | **Protected** (401 on Live) | Updates specified trade fields (returns `200`). |
| `DELETE` | `/api/v1/trades/{id}` | **Protected** (401 on Live) | Deletes a trade (returns `204`, or `404` if not found). |

> **Note:** `PUT /api/v1/trades/{id}` is not supported (`405 Method Not Allowed`). Use `PATCH` for updates.

---

## Live Demo Access Restrictions

On the **Live Demo**, public access is strictly **read-only**.

### What you can use on the Live Server:

- `GET /api/v1/trades` (List trades)
- `GET /api/v1/trades/{id}` (Fetch individual trade)
- `GET /actuator/health` (Health check)
- **Swagger UI** (Browsing documentation)

### Why write actions (`POST`, `PATCH`, `DELETE`) are disabled on the Live Server:

Write operations are secured with **HTTP Basic Authentication**. The required password is stored exclusively in the host environment variables (Render) to keep the live demonstration database clean, consistent, and protected from unauthorized modifications or spam.

Executing a `POST`, `PATCH`, or `DELETE` request against the live server (via Swagger UI, cURL, or Postman) will intentionally return a `401 Unauthorized` status. To create, update, or delete trades, you can run your own local instance.

---

## Testing & Interacting with Postman

You can easily interact with the API using tools like **Postman** or **cURL**.

### Testing Local Write Operations (`POST`)

1. Set the request method to **`POST`**.
2. URL: `http://localhost:8080/api/v1/trades`
3. Under the **Authorization** tab:
   - **Type:** Basic Auth
   - **Username:** `leja`
   - **Password:** *(The value configured in your local `LEJA_PASSWORD` variable)*
4. Under the **Headers** tab:
   - **Key:** `Content-Type`, **Value:** `application/json`
5. Under the **Body** tab, select **raw** and choose **JSON**.

### Example Request Payload (`POST /api/v1/trades`)

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

### Example Server Response (`201 Created`)

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

---

## Data Validation Rules

- **Trade Level Logic:**
  - **`BUY` trades:** Must have `stopLoss < entryPrice` and `takeProfit > entryPrice`.
  - **`SELL` trades:** Must have `stopLoss > entryPrice` and `takeProfit < entryPrice`.
  - Invalid configurations return a `400 Bad Request` explicitly identifying the misconfigured price levels.
- **Field Constraints:**
  - `direction`: Must be `BUY` or `SELL`.
  - `session`: Must be `NY`, `ASIA`, or `LONDON`.
  - `symbol`: Saved in uppercase (max 32 characters).
  - `reason`: Optional trade notes (max 255 characters). Send `"reason": ""` in a `PATCH` request to clear existing notes.
  - `executedAt`: Optional ISO timestamp. Defaults to the current server time if omitted.

---

## Quick Start: Live Demo (cURL)

Query trade data directly from your terminal. On Linux or macOS, use `curl` instead of `curl.exe`.

```powershell
# Health check
curl.exe https://leja-e0j6.onrender.com/actuator/health

# List all trades
curl.exe https://leja-e0j6.onrender.com/api/v1/trades

# Fetch a single trade by ID
curl.exe https://leja-e0j6.onrender.com/api/v1/trades/1
```

---

## Running Locally

To enable full write features (`POST`, `PATCH`, `DELETE`), clone and launch the project locally against a PostgreSQL instance. Create a database named `leja` on that machine before you start the app. The app creates the tables. It does not create this database.

```sql
CREATE DATABASE leja;
```

### Prerequisites

- **JDK 21**
- **Maven 3.9+** (or use the included `./mvnw` wrapper)
- **PostgreSQL** (running locally)
- **Docker** *(Optional)*

### 1. Clone the Repository

```bash
git clone https://github.com/tomi-alo/leja.git
cd leja
```

### 2. Set Environment Variables & Start

Leja reads database credentials and the API security password from environment variables at startup.

**Linux / macOS (Bash):**

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/leja"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="your-db-password"
export LEJA_PASSWORD="your-api-password"

./mvnw spring-boot:run
```

**Windows (PowerShell):**

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/leja"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "your-db-password"
$env:LEJA_PASSWORD = "your-api-password"

.\mvnw.cmd spring-boot:run
```

Local access endpoints:

- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **List Trades:** [http://localhost:8080/api/v1/trades](http://localhost:8080/api/v1/trades)
- **Health Check:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### 3. Create a Trade via cURL

On Linux or macOS, use `curl` instead of `curl.exe`.

```powershell
curl.exe -u leja:your-api-password -X POST http://localhost:8080/api/v1/trades -H "Content-Type: application/json" -d '{"symbol":"eurusd","direction":"BUY","session":"NY","positionSize":"1","contractSize":"1","entryPrice":"100","exitPrice":"110","stopLoss":"90","takeProfit":"120","reason":"liq sweep"}'
```

---

## Docker Setup

Docker is optional. If you decide to run a container instead of `./mvnw spring-boot:run`, build the image and start it against PostgreSQL on your machine:

```bash
# Build the Docker image
docker build -t leja .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/leja \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=your-db-password \
  -e LEJA_PASSWORD=your-api-password \
  leja
```

---

## Testing

Unit and controller test suites are executed without needing a live database connection:

```bash
# Linux / macOS
./mvnw test

# Windows PowerShell
.\mvnw.cmd test
```

---

## CI/CD & Deployment Setup

- **Continuous Integration (`.github/workflows/ci.yml`):** Runs automated test builds on push or pull requests targeting `main` using JDK 21 (Temurin).
- **Continuous Deployment:** Successful CI runs trigger Render to build and deploy directly using the repository's `Dockerfile`.
- **Cloud Database:** The live production service connects to a managed **Neon PostgreSQL** database.
