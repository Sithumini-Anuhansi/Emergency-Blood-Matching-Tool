package test;


import graph.Dijkstra;
import graph.Graph;
import model.*;
import service.*;

import java.util.List;



public class DonorMatchingTest {


    public static void main(String[] args){



        Graph graph =
                new Graph();



        Location hospitalLocation =
                new Location(
                        "L001",
                        "Colombo Hospital",
                        6.9271,
                        79.8612
                );



        Location donorLocation =
                new Location(
                        "L002",
                        "Colombo Donor",
                        6.9500,
                        79.9000
                );



        graph.addLocation(
                hospitalLocation
        );


        graph.addLocation(
                donorLocation
        );



        graph.addRoad(
                hospitalLocation,
                donorLocation,
                5,
                10
        );



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




        Dijkstra dijkstra =
                new Dijkstra(graph);



        DonorMatchingService service =
                new DonorMatchingService(
                        donorService,
                        dijkstra
                );




        List<DonorMatch> result =
                service.findBestDonors(
                        "O+",
                        hospitalLocation
                );




        System.out.println(
                "Recommended Donors"
        );



        for(DonorMatch match :
                result){


            System.out.println(match);

        }


    }

}
