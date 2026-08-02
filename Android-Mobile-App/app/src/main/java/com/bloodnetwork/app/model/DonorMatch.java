package com.bloodnetwork.app.model;

public class DonorMatch {
    private Donor donor;
    private double distance;
    private int travelTime;

    public DonorMatch(Donor donor, double distance, int travelTime) {
        this.donor = donor;
        this.distance = distance;
        this.travelTime = travelTime;
    }

    public Donor getDonor() { return donor; }
    public double getDistance() { return distance; }
    public int getTravelTime() { return travelTime; }

    @Override
    public String toString() {
        return String.format("%s | %.1f km | %d min", donor.toString(), distance, travelTime);
    }
}
