# Task Tracker Backend (Spring Boot + MongoDB)

A robust backend for a habit & task tracking application built with **Spring Boot** and **MongoDB**, featuring **daily task logging**, **progress tracking**, **heatmaps**, and a **failure-aware streak system with forgiveness**.

---

## Features

### Task Management
- Create, update, delete tasks
- Supports **BOOLEAN** and **QUANTITATIVE** tasks
- Pause / resume tasks
- Edit task details (title, description, target, unit)

### 📊 Daily Progress Tracking
- Log task completion per day
- Undo task logs
- Prevent duplicate logs for the same task/day
- Backend-validated task completion (no frontend trust)

### Heatmap Activity
- Monthly heatmap of user activity
- Each day reflects whether at least one task was completed

### Daily Summary
- Tasks completed today
- Tasks in progress
- Average progress percentage

---

## Advanced Streak System (Key Highlight)

This project implements a **Failure-Aware Streak System**, which is **rare** in habit trackers.

### How streaks work
- A streak increases **only if the user completes at least one task in a day**
- Opening the app or viewing the dashboard does **NOT** count as activity
- Streaks are **not auto-forgiven**

### Forgiveness Logic
- Users are allowed a configurable number of **forgiveness days**
- If a day is missed:
  - The streak enters a **FORGIVENESS PENDING** state
  - The user must explicitly choose:
    - **Accept forgiveness** → streak continues
    - **Decline forgiveness** → streak resets
- Forgiveness usage is tracked per streak

This design ensures:
- No fake streaks
- User accountability
- Transparent recovery from failure

---

## Tech Stack

- **Java 21**
- **Spring Boot**
- **Spring Security + JWT**
- **MongoDB (Atlas)**
- **Lombok**
- **REST APIs**

---

## 📁 Project Structure
src/main/java/com/task/tracker
├── authentication # JWT auth & security
├── controller # REST controllers
├── service # Business logic
├── repository # MongoDB repositories
├── model # Domain models
├── dto # Request/response DTOs
├── process # Config & properties (streak rules)
