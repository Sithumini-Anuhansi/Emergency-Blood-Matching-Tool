package graph;

import model.Location;

import java.util.*;

public class Graph {

    // Adjacency List
    private Map<Location, List<Edge>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /**
     * Add a new location (vertex)
     */
    public void addLocation(Location location) {

        adjacencyList.putIfAbsent(location, new ArrayList<>());

    }

    /**
     * Add a road between two locations
     * Since roads are two-way, add both directions
     */
    public void addRoad(Location source,
                        Location destination,
                        double distance,
                        int travelTime) {

        adjacencyList.get(source)
                .add(new Edge(destination, distance, travelTime));

        adjacencyList.get(destination)
                .add(new Edge(source, distance, travelTime));

    }

    /**
     * Return neighbours
     */
    public List<Edge> getNeighbors(Location location) {

        return adjacencyList.getOrDefault(location, new ArrayList<>());

    }

    /**
     * Return all locations
     */
    public Set<Location> getLocations() {

        return adjacencyList.keySet();

    }

    /**
     * Print graph
     */
    public void displayGraph() {

        for (Location location : adjacencyList.keySet()) {

            System.out.println(location.getLocationName());

            for (Edge edge : adjacencyList.get(location)) {

                System.out.println(
                        "   -> "
                        + edge.getDestination().getLocationName()
                        + " | "
                        + edge.getDistance()
                        + " km | "
                        + edge.getTravelTime()
                        + " mins"
                );

            }

            System.out.println();

        }

    }

}