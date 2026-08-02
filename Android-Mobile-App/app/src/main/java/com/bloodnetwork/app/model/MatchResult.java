package com.bloodnetwork.app.model;

public class MatchResult {
    public enum Type { BLOOD_BANK, DONOR }

    private String id;
    private String name;
    private Type type;
    private double distance;
    private int travelTime;
    private Object originalObject; // To keep reference to BloodBank or Donor

    public MatchResult(String id, String name, Type type, double distance, int travelTime, Object originalObject) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.distance = distance;
        this.travelTime = travelTime;
        this.originalObject = originalObject;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public double getDistance() { return distance; }
    public int getTravelTime() { return travelTime; }
    public Object getOriginalObject() { return originalObject; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %.1f km | %d min", type, name, distance, travelTime);
    }
}
