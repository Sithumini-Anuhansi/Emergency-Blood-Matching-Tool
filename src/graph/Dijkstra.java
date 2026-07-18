package graph;

import model.Location;

import java.util.*;

public class Dijkstra {

    private Graph graph;

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }


    /*
     * Finds shortest distance from source
     * to all locations
     */
    public Map<Location, Double> shortestDistances(Location source) {

        Map<Location, Double> distances = new HashMap<>();

        PriorityQueue<LocationDistance> queue =
                new PriorityQueue<>(
                        Comparator.comparingDouble(
                                LocationDistance::getDistance
                        )
                );


        // Initialize all distances as infinity
        for (Location location : graph.getLocations()) {

            distances.put(
                    location,
                    Double.MAX_VALUE
            );

        }


        // Distance to starting point = 0
        distances.put(source, 0.0);


        queue.add(
                new LocationDistance(
                        source,
                        0.0
                )
        );


        while (!queue.isEmpty()) {


            LocationDistance current =
                    queue.poll();


            Location currentLocation =
                    current.getLocation();


            double currentDistance =
                    current.getDistance();



            // Check all connected locations
            for (Edge edge :
                    graph.getNeighbors(currentLocation)) {


                Location neighbour =
                        edge.getDestination();



                double newDistance =
                        currentDistance
                        + edge.getDistance();



                // Found shorter path
                if(newDistance <
                        distances.get(neighbour)) {


                    distances.put(
                            neighbour,
                            newDistance
                    );


                    queue.add(
                            new LocationDistance(
                                    neighbour,
                                    newDistance
                            )
                    );

                }

            }

        }


        return distances;

    }



    /*
     * Finds shortest route between
     * two locations
     */
    public List<Location> getShortestPath(
            Location source,
            Location destination
    ) {


        Map<Location, Double> distances =
                new HashMap<>();

        Map<Location, Location> previous =
                new HashMap<>();


        PriorityQueue<LocationDistance> queue =
                new PriorityQueue<>(
                        Comparator.comparingDouble(
                                LocationDistance::getDistance
                        )
                );



        // Initialize
        for(Location location :
                graph.getLocations()) {


            distances.put(
                    location,
                    Double.MAX_VALUE
            );

            previous.put(
                    location,
                    null
            );

        }



        distances.put(source,0.0);


        queue.add(
                new LocationDistance(
                        source,
                        0.0
                )
        );



        while(!queue.isEmpty()) {


            LocationDistance current =
                    queue.poll();


            Location currentLocation =
                    current.getLocation();



            if(currentLocation.equals(destination)) {
                break;
            }



            for(Edge edge :
                    graph.getNeighbors(currentLocation)) {


                Location neighbour =
                        edge.getDestination();



                double newDistance =
                        distances.get(currentLocation)
                        + edge.getDistance();



                if(newDistance <
                        distances.get(neighbour)) {



                    distances.put(
                            neighbour,
                            newDistance
                    );


                    previous.put(
                            neighbour,
                            currentLocation
                    );


                    queue.add(
                            new LocationDistance(
                                    neighbour,
                                    newDistance
                            )
                    );

                }

            }

        }



        return buildPath(
                previous,
                destination
        );

    }



    /*
     * Reconstruct route
     */
    private List<Location> buildPath(
            Map<Location, Location> previous,
            Location destination
    ) {


        List<Location> path =
                new ArrayList<>();


        Location current =
                destination;



        while(current != null) {

            path.add(current);

            current =
                    previous.get(current);

        }



        Collections.reverse(path);



        return path;

    }




    /*
     * Get shortest distance between
     * two locations
     */
    public double getShortestDistance(
            Location source,
            Location destination
    ) {


        Map<Location, Double> distances =
                shortestDistances(source);


        return distances.get(destination);

    }




    /*
     * Helper class for Priority Queue
     */
    private static class LocationDistance {


        private Location location;

        private double distance;



        public LocationDistance(
                Location location,
                double distance
        ) {

            this.location = location;

            this.distance = distance;

        }



        public Location getLocation() {

            return location;

        }



        public double getDistance() {

            return distance;

        }

    }

}