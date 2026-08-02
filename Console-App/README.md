# Emergency Blood Matching Tool (Console Application)

## Overview

The Emergency Blood Matching Tool is a Java console-based application developed as the first phase of the Emergency Blood Network project. It demonstrates the implementation of the **Graph** data structure and **Dijkstra's Shortest Path Algorithm** to solve a real-world emergency blood donation problem.

This application was developed as part of the Data Structures module to demonstrate how graph algorithms can be applied to locate the nearest compatible blood donors and blood banks during emergencies.

---

## Objectives

- Demonstrate the implementation of the Graph data structure.
- Apply Dijkstra's Algorithm for shortest path calculation.
- Perform blood compatibility matching.
- Simulate emergency blood request processing.
- Provide a foundation for the Android application developed in Phase 2.

---

## Features

- Register hospitals
- Register blood donors
- Register blood banks
- Emergency blood request creation
- Blood compatibility checking
- Shortest path calculation using Dijkstra's Algorithm
- Donor ranking based on distance
- Request queue management
- Console-based user interaction

---

## Data Structure Used

### Graph

The Graph data structure represents:

- Hospitals
- Blood Donors
- Blood Banks

Edges represent roads between locations.

Each edge contains a weight representing the travel distance.

The application uses **Dijkstra's Algorithm** to determine the nearest compatible donor.

---

## Technologies Used

- Java
- Object-Oriented Programming
- Graph Data Structure
- Dijkstra's Algorithm
- CSV File Handling

---

## Project Structure

```
Console-App/
│
├── src/
│   ├── graph/
│   ├── model/
│   ├── service/
│   ├── util/
│   └── main/
│
├── data/
│
└── README.md
```

---

## How to Run

1. Clone the repository.
2. Open the Console-App folder in your preferred Java IDE.
3. Compile the project.
4. Run the Main class.

---

## Sample Workflow

Hospital
↓

Create Emergency Request
↓

Check Blood Compatibility
↓

Build Graph
↓

Run Dijkstra Algorithm
↓

Find Nearest Donor
↓

Display Results

---

## Future Improvements

- Database integration
- User authentication
- Android application
- GPS integration
- Push notifications
- AI-based donor recommendation

---

## Authors

Developed as the Phase 1 prototype of the Emergency Blood Network project for the Data Structures module.