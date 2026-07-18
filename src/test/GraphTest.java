package test;

import graph.Dijkstra;
import graph.Graph;
import model.Location;
import model.RouteResult;

import java.util.List;
import java.util.Map;


public class GraphTest {


    public static void main(String[] args) {



        Graph graph = new Graph();



        /*
         * Create Locations
         */

        Location hospital =
                new Location(
                        "L001",
                        "Colombo General Hospital",
                        6.9271,
                        79.8612
                );


        Location bloodBank =
                new Location(
                        "L002",
                        "Colombo Blood Bank",
                        6.9147,
                        79.8737
                );


        Location gampaha =
                new Location(
                        "L003",
                        "Gampaha Junction",
                        7.0917,
                        80.0000
                );


        Location donor1 =
                new Location(
                        "L004",
                        "Donor Location A",
                        7.2000,
                        80.0500
                );


        Location donor2 =
                new Location(
                        "L005",
                        "Donor Location B",
                        6.8500,
                        79.9000
                );




        /*
         * Add locations to graph
         */

        graph.addLocation(hospital);

        graph.addLocation(bloodBank);

        graph.addLocation(gampaha);

        graph.addLocation(donor1);

        graph.addLocation(donor2);




        /*
         * Create road network
         */

        graph.addRoad(
                hospital,
                bloodBank,
                3,
                10
        );


        graph.addRoad(
                bloodBank,
                gampaha,
                25,
                35
        );


        graph.addRoad(
                gampaha,
                donor1,
                15,
                20
        );


        graph.addRoad(
                gampaha,
                donor2,
                30,
                40
        );


        graph.addRoad(
                hospital,
                donor2,
                18,
                25
        );




        /*
         * Display Graph
         */

        System.out.println(
                "\n===== BLOOD NETWORK GRAPH ====="
        );


        graph.displayGraph();





        /*
         * Dijkstra Testing
         */

        Dijkstra dijkstra =
                new Dijkstra(graph);




        System.out.println(
                "\n===== DISTANCES FROM HOSPITAL ====="
        );


        Map<Location,Double> distances =
                dijkstra.shortestDistances(
                        hospital
                );



        for(Location location :
                distances.keySet()) {


            System.out.println(
                    location.getLocationName()
                    +
                    " : "
                    +
                    distances.get(location)
                    +
                    " km"
            );

        }





        /*
         * Test shortest route
         */

        System.out.println(
                "\n===== SHORTEST ROUTE TO DONOR ====="
        );



        List<Location> route =
                dijkstra.getShortestPath(
                        hospital,
                        donor1
                );



        for(Location location :
                route) {


            System.out.println(
                    "↓ "
                    +
                    location.getLocationName()
            );

        }




        /*
         * Test Route Result
         */

        System.out.println(
                "\n===== ROUTE RESULT ====="
        );



        RouteResult result =
                dijkstra.getRouteResult(
                        hospital,
                        donor1
                );


        result.displayRoute();

    }

}