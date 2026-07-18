package test;

import graph.Dijkstra;
import graph.Graph;

import model.*;

import service.*;

import java.util.List;



public class EmergencySystemTest {


    public static void main(String[] args) {



        System.out.println(
                "===== EMERGENCY BLOOD NETWORK TEST ====="
        );



        /*
         * 1. Create Locations
         */

        Location hospitalLocation =
                new Location(
                        "L001",
                        "Colombo General Hospital",
                        6.9271,
                        79.8612
                );


        Location bloodBankLocation =
                new Location(
                        "L002",
                        "Colombo Blood Bank",
                        6.9147,
                        79.8737
                );


        Location donorLocation1 =
                new Location(
                        "L003",
                        "Donor Area 1",
                        7.0000,
                        79.9000
                );


        Location donorLocation2 =
                new Location(
                        "L004",
                        "Donor Area 2",
                        7.0500,
                        79.9500
                );





        /*
         * 2. Create Graph
         */

        Graph graph =
                new Graph();



        graph.addLocation(
                hospitalLocation
        );


        graph.addLocation(
                bloodBankLocation
        );


        graph.addLocation(
                donorLocation1
        );


        graph.addLocation(
                donorLocation2
        );




        graph.addRoad(
                hospitalLocation,
                bloodBankLocation,
                5,
                10
        );


        graph.addRoad(
                bloodBankLocation,
                donorLocation1,
                15,
                25
        );


        graph.addRoad(
                donorLocation1,
                donorLocation2,
                10,
                15
        );





        /*
         * 3. Create Blood Bank
         */

        BloodBank bloodBank =
                new BloodBank(
                        "B001",
                        "Colombo Blood Bank",
                        bloodBankLocation
                );



        BloodBankService bloodBankService =
                new BloodBankService();



        bloodBankService.addBloodBank(
                bloodBank
        );



        /*
         * Add only A+ blood
         */

        bloodBankService.addStock(
                "Colombo Blood Bank",
                "A+",
                5
        );






        /*
         * 4. Hospital creates request
         */

        Hospital hospital =
                new Hospital(
                        "H001",
                        "Colombo General Hospital",
                        hospitalLocation
                );



        EmergencyRequest request =
                new EmergencyRequest(
                        "REQ001",
                        hospital,
                        "O+",
                        2
                );



        System.out.println(
                "\nBlood Request Created"
        );


        System.out.println(request);







        /*
         * 5. Check Blood Bank
         */

        List<BloodBank> banks =
                bloodBankService.findAvailableBlood(
                        "O+",
                        2
                );



        if(banks.isEmpty()){


            System.out.println(
                    "\nNo blood available in blood banks"
            );


            System.out.println(
                    "Searching donors..."
            );


        }







        /*
         * 6. Create Donors
         */

        Donor donor1 =
                new Donor(
                        "D001",
                        "John",
                        "O+",
                        25,
                        "0771111111",
                        donorLocation1
                );



        Donor donor2 =
                new Donor(
                        "D002",
                        "Anne",
                        "O+",
                        28,
                        "0772222222",
                        donorLocation2
                );





        DonorService donorService =
                new DonorService();



        donorService.addDonor(
                donor1
        );


        donorService.addDonor(
                donor2
        );








        /*
         * 7. Match donors using Graph
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




        System.out.println(
                "\nRecommended Donors"
        );



        for(DonorMatch match :
                matches){


            System.out.println(
                    match
            );

        }






        /*
         * 8. Send requests
         */

        RequestQueueService queueService =
                new RequestQueueService();



        queueService.createRequests(
                matches
        );





        /*
         * 9. Donor response
         */

        DonorResponseService responseService =
                new DonorResponseService(
                        queueService
                );



        System.out.println(
                "\nDonor 1 rejecting request..."
        );


        responseService.rejectDonorRequest(
                "D001",
                request
        );





        System.out.println(
                "\nDonor 2 accepting request..."
        );


        responseService.acceptDonorRequest(
                "D002",
                request
        );





        System.out.println(
                "\nFinal Request Status:"
        );


        System.out.println(
                request.getStatus()
        );


    }
}

