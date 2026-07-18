package test;


import graph.Dijkstra;
import graph.Graph;
import model.*;
import service.*;

import java.util.List;



public class EmergencyWorkflowTest {


    public static void main(String[] args){



        /*
         * Create locations
         */

        Location hospitalLocation =
                new Location(
                        "L001",
                        "Hospital",
                        6.9,
                        79.8
                );



        Location donorLocation =
                new Location(
                        "L002",
                        "Donor Area",
                        7.0,
                        79.9
                );



        /*
         * Create Graph
         */

        Graph graph =
                new Graph();


        graph.addLocation(
                hospitalLocation
        );


        graph.addLocation(
                donorLocation
        );


        graph.addRoad(
                hospitalLocation,
                donorLocation,
                10,
                20
        );



        /*
         * Create donor
         */

        Donor donor =
                new Donor(
                        "D001",
                        "John",
                        "O+",
                        25,
                        "0771234567",
                        donorLocation
                );



        DonorService donorService =
                new DonorService();



        donorService.addDonor(
                donor
        );



        /*
         * Matching
         */

        Dijkstra dijkstra =
                new Dijkstra(graph);



        DonorMatchingService matchingService =
                new DonorMatchingService(
                        donorService,
                        dijkstra
                );



        List<DonorMatch> matches =
                matchingService.findBestDonors(
                        "O+",
                        hospitalLocation
                );



        /*
         * Create queue
         */

        RequestQueueService queue =
                new RequestQueueService();



        queue.createRequests(
                matches
        );




        /*
         * Create emergency request
         */

        Hospital hospital =
                new Hospital(
                        "H001",
                        "City Hospital",
                        hospitalLocation
                );



        EmergencyRequest request =
                new EmergencyRequest(
                        "REQ001",
                        hospital,
                        "O+",
                        1
                );




        /*
         * Donor response
         */

        DonorResponseService response =
                new DonorResponseService(
                        queue
                );



        response.acceptDonorRequest(
                "D001",
                request
        );



        System.out.println(
                "\nFinal Status:"
        );


        System.out.println(
                request.getStatus()
        );



    }

}
