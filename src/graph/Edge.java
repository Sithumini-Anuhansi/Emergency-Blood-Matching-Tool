package graph;

import model.Location;

public class Edge {

    private Location destination;
    private double distance;
    private int travelTime;

    public Edge(Location destination,
                double distance,
                int travelTime) {

        this.destination = destination;
        this.distance = distance;
        this.travelTime = travelTime;
    }

    public Location getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public int getTravelTime() {
        return travelTime;
    }

    @Override
    public String toString() {

        return destination.getLocationName()
                + " (" + distance + " km)";
    }

}