# Emergency Blood Network (Android Application)

## Overview

The Emergency Blood Network is an Android application developed as the second phase of the Emergency Blood Matching Tool project.

It extends the original Java console application by providing a modern mobile interface with role-based access for hospitals, blood donors, blood banks, and administrators while continuing to utilize the Graph data structure for emergency donor matching.

---

## Project Evolution

Phase 1

✔ Java Console Application

↓

Phase 2

✔ Android Mobile Application

The Android version reuses the same graph-based logic while introducing a graphical user interface and additional real-world features.

---

## Features

### Authentication

- User Registration
- User Login
- Role-based Access
- Session Management

### Hospital Module

- Create Emergency Blood Requests
- View Request History
- Track Request Status

### Donor Module

- Donor Registration
- Update Availability
- Blood Group Management
- View Notifications

### Blood Bank Module

- Register Blood Banks
- Manage Blood Stock
- Update Inventory

### Matching Module

- Blood Compatibility Checking
- Graph Construction
- Dijkstra's Shortest Path Algorithm
- Intelligent Donor Ranking

### Notifications

- Emergency Alerts
- Request Updates
- Blood Availability Notifications

---

## Data Structure Used

### Graph

Vertices

- Hospitals
- Blood Donors
- Blood Banks

Edges

- Road connections
- Weighted by travel distance

Algorithm

- Dijkstra's Shortest Path Algorithm

Purpose

- Locate the nearest compatible donor
- Reduce emergency response time

---

## Technologies Used

- Java
- Android Studio
- XML
- Graph Data Structure
- Dijkstra's Algorithm
- CSV Data Storage

---

## Project Structure

```
Android-App/
│
├── app/
│   ├── src/
│   │   ├── java/
│   │   ├── res/
│   │   └── assets/
│
├── gradle/
│
├── build.gradle
│
└── README.md
```

---

## Application Workflow

User Login
↓

Select Role

↓

Hospital Creates Emergency Request

↓

Blood Compatibility Check

↓

Graph Construction

↓

Run Dijkstra Algorithm

↓

Find Nearest Donor

↓

Notify Donor

↓

Request Completed

---

## Future Enhancements

- Firebase Authentication
- Cloud Database
- GPS Tracking
- Real-time Notifications
- AI Donor Prediction
- Google Maps Integration
- QR Code Support
- Blood Bank Live Inventory

---

## Screenshots

Screenshots can be found in the `Documents/Screenshots/` directory.

---

## Authors

Developed as the Phase 2 mobile implementation of the Emergency Blood Matching Tool project for the Data Structures module.