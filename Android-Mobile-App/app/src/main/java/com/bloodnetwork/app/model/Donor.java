package com.bloodnetwork.app.model;

public class Donor {
    private String id;
    private String name;
    private String bloodGroup;
    private String locationId;
    private String phone;
    private boolean available;
    private int age;

    public Donor(String id, String name, String bloodGroup, String locationId, String phone, int age, boolean available) {
        this.id = id;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.locationId = locationId;
        this.phone = phone;
        this.age = age;
        this.available = available;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBloodGroup() { return bloodGroup; }
    public String getLocationId() { return locationId; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isEligible() {
        return age >= 18 && age <= 65 && available;
    }

    @Override
    public String toString() {
        return name + " [" + id + "] " + bloodGroup + " @ " + locationId
                + (available ? " (Available)" : " (Unavailable)");
    }
}
