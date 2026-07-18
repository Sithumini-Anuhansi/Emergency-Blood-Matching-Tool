package graph;

import model.Location;
import model.RouteResult;

import java.util.*;

public class Dijkstra {

    private Graph graph;


    public Dijkstra(Graph graph) {
        this.graph = graph;
    }


    /**
     * Find shortest distance from source
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


        // Initialize all distances
        for (Location location : graph.getLocations()) {

            distances.put(
                    location,
                    Double.MAX_VALUE
            );

        }


        // Starting location
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



            for (Edge edge :
                    graph.getNeighbors(currentLocation)) {


                Location neighbour =
                        edge.getDestination();


                double newDistance =
                        currentDistance
                        + edge.getDistance();



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




    /**
     * Find shortest path between
     * source and destination
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
                        +
                        edge.getDistance();



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




    /**
     * Build route from previous nodes
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




    /**
     * Return shortest distance
     */
    public double getShortestDistance(
            Location source,
            Location destination
    ) {


        Map<Location, Double> distances =
                shortestDistances(source);


        return distances.get(destination);

    }




    /**
     * Return complete route information
     */
    public RouteResult getRouteResult(
            Location source,
            Location destination
    ) {


        List<Location> path =
                getShortestPath(
                        source,
                        destination
                );


        double distance =
                getShortestDistance(
                        source,
                        destination
                );


        int travelTime = 0;



        for(int i = 0;
            i < path.size()-1;
            i++) {



            Location current =
                    path.get(i);


            Location next =
                    path.get(i+1);



            for(Edge edge :
                    graph.getNeighbors(current)) {



                if(edge.getDestination()
                        .equals(next)) {


                    travelTime +=
                            edge.getTravelTime();

                    break;

                }

            }

        }



        return new RouteResult(
                path,
                distance,
                travelTime
        );

    }




    /**
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