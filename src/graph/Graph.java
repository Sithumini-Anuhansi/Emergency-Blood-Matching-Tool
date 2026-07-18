package graph;

import model.Location;

import java.util.*;

/**
 * Graph implementation using an Adjacency List.
 *
 * Vertices  : Location
 * Edges     : Road connections between locations
 * Weight    : Distance (km) and Travel Time (minutes)
 */
public class Graph {

    // Adjacency List
    private final Map<Location, List<Edge>> adjacencyList;

    /**
     * Constructor
     */
    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /**
     * Add a location (vertex) to the graph.
     *
     * @param location Location object
     */
    public void addLocation(Location location) {

        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }

        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    /**
     * Remove a location and all connected roads.
     *
     * @param location Location to remove
     */
    public void removeLocation(Location location) {

        if (!adjacencyList.containsKey(location)) {
            return;
        }

        adjacencyList.remove(location);

        for (List<Edge> edges : adjacencyList.values()) {
            edges.removeIf(edge ->
                    edge.getDestination().equals(location));
        }
    }

    /**
     * Add a two-way road between two locations.
     *
     * @param source Source location
     * @param destination Destination location
     * @param distance Distance in KM
     * @param travelTime Estimated travel time in minutes
     */
    public void addRoad(Location source,
                        Location destination,
                        double distance,
                        int travelTime) {

        if (source == null || destination == null) {
            throw new IllegalArgumentException("Locations cannot be null.");
        }

        if (!adjacencyList.containsKey(source)
                || !adjacencyList.containsKey(destination)) {

            throw new IllegalArgumentException(
                    "Both locations must be added before creating a road."
            );
        }

        adjacencyList.get(source)
                .add(new Edge(destination, distance, travelTime));

        adjacencyList.get(destination)
                .add(new Edge(source, distance, travelTime));
    }

    /**
     * Remove a road between two locations.
     *
     * @param source Source location
     * @param destination Destination location
     */
    public void removeRoad(Location source,
                           Location destination) {

        if (!adjacencyList.containsKey(source)
                || !adjacencyList.containsKey(destination)) {

            return;
        }

        adjacencyList.get(source)
                .removeIf(edge ->
                        edge.getDestination().equals(destination));

        adjacencyList.get(destination)
                .removeIf(edge ->
                        edge.getDestination().equals(source));
    }

    /**
     * Get neighbouring locations.
     *
     * @param location Location
     * @return List of connected edges
     */
    public List<Edge> getNeighbors(Location location) {

        return adjacencyList.getOrDefault(location,
                Collections.emptyList());
    }

    /**
     * Get all graph locations.
     *
     * @return Set of locations
     */
    public Set<Location> getLocations() {
        return adjacencyList.keySet();
    }

    /**
     * Check whether a location exists.
     *
     * @param location Location
     * @return true if exists
     */
    public boolean containsLocation(Location location) {
        return adjacencyList.containsKey(location);
    }

    /**
     * Number of locations.
     *
     * @return vertex count
     */
    public int getNumberOfLocations() {
        return adjacencyList.size();
    }

    /**
     * Print graph.
     */
    public void displayGraph() {

        System.out.println("\n========== ROAD NETWORK ==========\n");

        for (Location location : adjacencyList.keySet()) {

            System.out.println(location.getLocationName());

            List<Edge> edges = adjacencyList.get(location);

            if (edges.isEmpty()) {
                System.out.println("   No connected roads.");
            }

            for (Edge edge : edges) {

                System.out.printf(
                        "   -> %-15s Distance: %.1f km | Time: %d mins%n",
                        edge.getDestination().getLocationName(),
                        edge.getDistance(),
                        edge.getTravelTime()
                );
            }

            System.out.println();
        }
    }

    /**
     * Display one location's neighbours.
     *
     * @param location Location
     */
    public void displayNeighbors(Location location) {

        if (!adjacencyList.containsKey(location)) {

            System.out.println("Location not found.");

            return;
        }

        System.out.println("\nNeighbours of "
                + location.getLocationName());

        for (Edge edge : adjacencyList.get(location)) {

            System.out.printf(
                    "-> %s (%.1f km)%n",
                    edge.getDestination().getLocationName(),
                    edge.getDistance()
            );
        }
    }

    /**
     * Return adjacency list.
     * Needed later for Dijkstra.
     *
     * @return adjacency list
     */
    public Map<Location, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

}