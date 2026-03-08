# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Context

This is the **Spring Boot backend** for the habit & task tracking application. The companion frontend lives in `../task-tracker-ui/` (React, Vite, Tailwind CSS, React Router v7) and communicates with this service at `http://localhost:8080`.

## Commands

```bash
# Run the application (requires MONGO_URI env var)
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

Set `MONGO_URI` to a MongoDB Atlas connection string before running. Configured in `application.properties` as `${MONGO_URI}`.

## Architecture

### Package Structure

```
com.task.tracker
├── authentication/         # JWT security layer
│   ├── config/             # SecurityFilterChain, JwtAuthFilter, CORS (port 5173)
│   ├── controller/         # Public /auth/* endpoints
│   ├── dto/                # AuthRequest
│   └── service/            # AuthService, JwtService
├── controller/             # Protected REST controllers
├── service/                # Business logic
├── repository/             # Spring Data MongoDB repositories
├── model/                  # MongoDB @Document classes
├── dto/                    # Request/Response DTOs
└── process/                # Scheduled cron jobs + StreakProperties config binding
```

### Security

Public endpoints: `/auth/register`, `/auth/token`, `/auth/validate`, `/auth/role`. Everything else requires a JWT Bearer token. `JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter`. CORS is configured for `http://localhost:5173` only.

Controllers extract `userId` from the JWT via `AuthService.getUserFromToken(authHeader)` — a common pattern repeated across controllers.

### REST API Endpoints

| Controller | Base Path | Key Operations |
|-----------|-----------|---------------|
| `TaskController` | `/api/tasks` | CRUD; user scoped via JWT |
| `TaskStateController` | `/api/tasks/state` | GET today/day state, PUT `/{taskId}/status` |
| `TaskProgressController` | `/api/progress` | POST `/boolean/mark`, POST `/log`, POST `/toggle-today`, POST `/complete-all-today`, GET `/day`, GET `/task/{taskId}/history` |
| `StreakController` | `/api/streak` | GET streak, POST `/forgiveness/accept`, POST `/forgiveness/decline` |
| `FatigueController` | `/api/fatigue` | Fatigue state for the logged-in user |
| `HeatMapController` | `/api/heatmap` | Heatmap data |
| `DailySummaryController` | `/api/summary` | Daily summaries |
| `UserController` | `/api/users` | User profile |

### MongoDB Collections

| Model | Collection |
|-------|-----------|
| `User` | `users` |
| `Task` | `tasks` |
| `UserStreak` | `user_streaks` |
| `UserFatigue` | `user_fatigue` |
| `TaskProgress` | (convention-named) |
| `DailySummary` | (convention-named) |
| `Heatmap` | (convention-named) |

### Task Types & Status

- **Types**: `BOOLEAN` (done/not done) or `QUANTITATIVE` (numeric progress toward `targetValue` with a `unit`)
- **Status**: `active`, `paused`, `completed` — transitions handled by `TaskStateService`

### Streak System

Business logic lives in `UserStreak` (domain methods) and `StreakService`. The `UserStreak` document is keyed by `userId` (not a separate ID).

State transitions:
- **start()** — first ever activity
- **increment()** — consecutive day
- **markForgivenessPending(missedDays)** — within gap limit; waits for user decision
- **consumeForgiveness(today)** — user accepts; adds missed days to `forgivenessUsed`
- **reset(today, forgivenessAllowed)** — user declines or gap exceeded; preserves `longestStreak`, resets `currentStreak` to 1

Rules bound from `application.properties` via `StreakProperties`:
- `streak.max-gap-days=2` — max consecutive missed days before gap is unforgivable
- `streak.forgiveness-allowed=1` — total forgiveness days per streak lifetime
- `streak.allow-forgiveness=true` — global toggle

### Fatigue System

`FatigueService.evaluateFatigue()` scores user fatigue (0–100) over a 7-day rolling window using three signals:

| Signal | Max Score | Logic |
|--------|-----------|-------|
| Completion trend | 40 | Negative slope (last day − first day completions) × 10 |
| Low effort days | 20 | Days with only 1 task logged, or any quantitative task < 25% progress |
| Task avoidance | 15 | Active tasks with zero progress logs in the window (capped at 3) |

Level thresholds: `NONE` (<20), `LOW` (<40), `MEDIUM` (<70), `HIGH` (≥70).

`FatigueService.getOrEvaluate()` returns cached result if `evaluatedOn == today`, otherwise re-evaluates — safe to call from the dashboard.

### Scheduled Jobs

| Job | Schedule | Purpose |
|-----|----------|---------|
| `DailySummaryCronJob` | `0 59 23 * * *` (23:59 daily) | Snapshots daily summary for all users |
| `FatigueCronJob` | `0 0 2 * * *` (2 AM daily) | Evaluates and persists `UserFatigue` for all users |

`SchedulerConfig` enables Spring's `@Scheduled` task executor.

---

## Frontend (`../task-tracker-ui/`)

The React frontend consumes all endpoints above. Key details relevant when making backend changes:

### Frontend Key Files

- **`src/api/axios.js`** — Attaches `Authorization: Bearer <token>` from `localStorage` to all non-`/auth` requests. On 401/403, clears the token and redirects to `/login`.
- **`src/context/AuthContext.jsx`** — Exposes `{ token, login, logout }`. Token persisted in `localStorage`.
- **`src/components/ProtectedRoute.jsx`** — Guards routes; redirects to `/login` if unauthenticated.

### Frontend Routes

| Path | Component | Access |
|------|-----------|--------|
| `/login` | `Login` | Public |
| `/dashboard` | `Dashboard` | Protected |
| `/tasks` | `Tasks` | Protected |
| `/tasks/new` | `CreateTaskPage` | Protected |
| `/heatmap` | `HeatmapPage` | Protected |

`/` redirects to `/dashboard`. `*` renders `ErrorPage`.

### Dashboard Data Flow

`Dashboard.jsx` fetches all data in parallel via `Promise.all` on mount:

| Endpoint | Purpose |
|----------|---------|
| `GET /api/summary/today` | Completed/in-progress counts, avg progress % |
| `GET /api/tasks/state/today` | `inProgressToday[]` + `completedToday[]` task lists |
| `GET /api/heatmap/month?year=&month=` | Monthly activity array; sliced to last 7 days for `MiniHeatmap` |
| `GET /api/streak` | Streak state |
| `GET /api/users/me` | `{ name, ... }` |
| `GET /api/fatigue` | Fatigue state |
| `POST /api/fatigue/recompute` | Triggers manual fatigue recomputation |
| `POST /api/progress/log` | Logs task progress `{ taskId, completed, valueCompleted }` |

### Domain Shapes Expected by Frontend

**Streak** (`GET /api/streak`):
```json
{
  "currentStreak": 5,
  "longestStreak": 12,
  "lastActiveDate": "2026-03-07",
  "forgivenessAllowed": 1,
  "forgivenessUsed": 0,
  "forgivenessPending": false
}
```

**Fatigue** (`GET /api/fatigue`):
```json
{
  "level": "NONE | LOW | MODERATE | HIGH",
  "fatigueScore": 3,
  "lowEffortDays": 2,
  "avoidedTasks": ["Task A"]
}
```

### Frontend Component Groups

- **`components/heatmap/`** — `MonthlyHeatMap`, `WeeklyHeatmap`, `YearlyHeatmap`, `HeatmapChart`
- **Streak** — `StreakCard`, `StreakDetailsModal`, `ForgivenessBanner`
- **Fatigue** — `FatigueCard`, `FatigueWarning`, `FatigueWhyModal`
- **Tasks** — `TaskCard`, `TodayTaskList`, `QuickLogModal`, `EditTaskModal`

### Frontend Utilities

- **`src/utils/taskUtils.js`** — `canLog(task)`: BOOLEAN tasks block re-log once `completedToday === true`; QUANTITATIVE tasks block at `progressPercent === 100`
- **`src/utils/heatmapUtils.js`** — Heatmap data transformation helpers
