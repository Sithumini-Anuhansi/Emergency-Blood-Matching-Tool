package com.bloodnetwork.app.model;

public class Hospital {
    private String id;
    private String name;
    private String locationId;

    public Hospital(String id, String name, String locationId) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocationId() { return locationId; }

    @Override
    public String toString() {
        return name + " [" + id + "] @ " + locationId;
    }
}
