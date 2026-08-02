package com.bloodnetwork.app.graph;

import com.bloodnetwork.app.model.Location;

public class Edge {
    private Location source;
    private Location destination;
    private double distance;
    private int time;

    public Edge(Location source, Location destination, double distance, int time) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.time = time;
    }

    public Location getSource() { return source; }
    public Location getDestination() { return destination; }
    public double getDistance() { return distance; }
    public int getTime() { return time; }

    @Override
    public String toString() {
        return source.getName() + " -- " + destination.getName()
                + " | " + distance + " km | " + time + " min";
    }
}
