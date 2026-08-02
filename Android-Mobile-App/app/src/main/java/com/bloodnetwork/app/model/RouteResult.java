package com.bloodnetwork.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteResult {
    private List<String> path = new ArrayList<>();
    private double totalDistance;
    private int totalTime;

    public RouteResult(List<String> path, double totalDistance, int totalTime) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
    }

    public List<String> getPath() { return Collections.unmodifiableList(path); }
    public double getTotalDistance() { return totalDistance; }
    public int getTotalTime() { return totalTime; }

    public boolean isValid() {
        return !path.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(path.get(i));
        }
        sb.append(String.format(" | %.1f km | %d min", totalDistance, totalTime));
        return sb.toString();
    }
}
