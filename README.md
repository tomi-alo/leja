# leja

## Project Overview

leja is a RESTful trading journal API built with Spring Boot, PostgreSQL, and Docker. A trade is a closed position. The server calculates PnL, risk-to-reward, and the outcome. The API is deployed on Render, with PostgreSQL hosted on Neon.

## Key Features

- CRUD operations for trades
- Public read access for trades, with HTTP Basic authentication on create, update, and delete
- Layered architecture: controller, service, mapper, and repository
- Price checks for stop loss and take profit
- Dockerized application
- Tests for the controller, service, mapper, and trade model
- PostgreSQL on Neon for the deployed database
- CI workflow for automated testing and Docker image builds with GitHub Actions

## Accessing the Deployed API

The deployed API is available at [https://leja-e0j6.onrender.com](https://leja-e0j6.onrender.com). Trade routes live under `/api/v1/trades`.

Anyone can list and read trades. Creating, updating, and deleting a trade requires Basic authentication.

`GET /actuator/health` is public and does not require a password.

Swagger UI is public at [https://leja-e0j6.onrender.com/swagger-ui/index.html](https://leja-e0j6.onrender.com/swagger-ui/index.html).

## Authentication Details

Create, update, and delete use Basic authentication.

- Username: `leja`
- Password: the value of `LEJA_PASSWORD` set on the server

Do not commit that password. Set it in the environment where the app runs.

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/trades` | Creates a trade. Returns `201`. |
| `GET` | `/api/v1/trades` | Returns every trade. |
| `GET` | `/api/v1/trades/{id}` | Returns one trade, or `404`. |
| `PATCH` | `/api/v1/trades/{id}` | Updates the fields you send. Returns `200`. |
| `DELETE` | `/api/v1/trades/{id}` | Deletes a trade. Returns `204`, or `404`. |

`PUT /api/v1/trades/{id}` returns `405`. Updates use `PATCH`. A create, update, or delete with a missing or wrong password returns `401`. Listing and reading trades does not require a password.

`direction` is `BUY` or `SELL`. `session` is `NY`, `ASIA`, or `LONDON`. `symbol` is stored in uppercase and is at most 32 characters. `reason` is optional and at most 255 characters. Sending `"reason": ""` on a `PATCH` clears the note. `executedAt` is optional. If it is omitted, the server uses the current time.

A buy is saved only when the stop is below the entry and the target is above it. A sell is the opposite. A wrong level returns `400`, and the message names the stop, the target, or both. The response includes `pnl`, `riskToRewardRatio`, and `outcome` (`WIN`, `LOSS`, or `BREAK EVEN`).

Create a trade:

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

Use Postman or curl. This lists every trade:

```bash
curl https://leja-e0j6.onrender.com/api/v1/trades
```

This creates a trade:

```bash
curl -u leja:YOUR_PASSWORD -X POST https://leja-e0j6.onrender.com/api/v1/trades \
  -H "Content-Type: application/json" \
  -d "{\"symbol\":\"eurusd\",\"direction\":\"BUY\",\"session\":\"NY\",\"positionSize\":\"1\",\"contractSize\":\"1\",\"entryPrice\":\"100\",\"exitPrice\":\"110\",\"stopLoss\":\"90\",\"takeProfit\":\"120\",\"reason\":\"liq sweep\"}"
```

## Getting Started

### Prerequisites

- JDK 21
- Maven 3.9+ (or the included Maven wrapper)
- Docker, if you want to run the image
- PostgreSQL locally, or the Neon database

### Local Setup

Clone the repository:

```bash
git clone https://github.com/tomi-alo/leja.git
cd leja
```

The database URL, database user, database password, and API password come from environment variables. They are not stored in `application.properties`.

### Running the Application

PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/leja"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "your-db-password"
$env:LEJA_PASSWORD = "your-api-password"
.\mvnw.cmd spring-boot:run
```

The API is at `http://localhost:8080`. The app does not start when `LEJA_PASSWORD` is missing. The password is read once at startup.

## Docker

Build the image:

```bash
docker build -t leja .
```

Run it with the same variables the host would use:

```bash
docker run -p 8080:8080 ^
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/leja ^
  -e SPRING_DATASOURCE_USERNAME=postgres ^
  -e SPRING_DATASOURCE_PASSWORD=your-db-password ^
  -e LEJA_PASSWORD=your-api-password ^
  leja
```

On macOS or Linux, replace `^` with `\`. From inside the container, `host.docker.internal` is the machine running Docker.

## Testing

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

The tests do not need a running database. The controller tests sign in with their own password.

## Workflow

GitHub Actions is defined in `.github/workflows/ci.yml`. A push or pull request to `main` runs two jobs.

**Test**

- Checks out the repository.
- Installs JDK 21 (Temurin).
- Runs `./mvnw test`. A failing test stops the workflow.

**Image**

- Runs only after the tests pass.
- Builds the Docker image with `docker build -t leja:ci .`.
- Does not push the image to a registry. Render builds and deploys from the GitHub repository using the `Dockerfile`.

On Render, the service environment is:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST/DATABASE?sslmode=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=the Neon password
LEJA_PASSWORD=the API password
```

The URL contains the host and database name only. The Neon username and password stay in the other two variables. The health check path is `/actuator/health`.
