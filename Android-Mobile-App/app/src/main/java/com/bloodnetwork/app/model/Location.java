package com.bloodnetwork.app.model;

public class Location {
    private String id;
    private String name;
    private double latitude;
    private double longitude;

    public Location(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        return id.equals(((Location) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
