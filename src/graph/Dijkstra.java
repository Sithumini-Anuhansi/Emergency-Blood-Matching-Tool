package graph;

import model.Location;

import java.util.*;

public class Dijkstra {

    private Graph graph;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    /**
     * Returns shortest distance from source
     * to every location
     */
    public Map<Location, Double> shortestDistances(Location source) {

        Map<Location, Double> distances = new HashMap<>();

        PriorityQueue<LocationDistance> pq =
                new PriorityQueue<>(Comparator.comparingDouble(ld -> ld.distance));

        // Initialize distances
        for (Location location : graph.getLocations()) {
            distances.put(location, Double.MAX_VALUE);
        }

        distances.put(source, 0.0);

        pq.add(new LocationDistance(source, 0));

        while (!pq.isEmpty()) {

            LocationDistance current = pq.poll();

            Location currentLocation = current.location;

            for (Edge edge : graph.getNeighbors(currentLocation)) {

                double newDistance =
                        distances.get(currentLocation)
                                + edge.getDistance();

                if (newDistance < distances.get(edge.getDestination())) {

                    distances.put(edge.getDestination(), newDistance);

                    pq.add(
                            new LocationDistance(
                                    edge.getDestination(),
                                    newDistance
                            )
                    );

                }

            }

        }

        return distances;

    }

    /**
     * Helper class
     */
    private static class LocationDistance {

        Location location;
        double distance;

        LocationDistance(Location location,
                         double distance) {

            this.location = location;
            this.distance = distance;

        }

    }

}