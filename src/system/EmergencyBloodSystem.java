package system;

import graph.Graph;
import graph.Dijkstra;

import model.*;

import service.*;

import controller.EmergencyController;



public class EmergencyBloodSystem {



    private Graph graph;


    private Dijkstra dijkstra;



    private HospitalService hospitalService;


    private BloodBankService bloodBankService;


    private DonorService donorService;


    private DonorMatchingService matchingService;


    private RequestQueueService queueService;


    private EmergencyController controller;







    public EmergencyBloodSystem(){


        initializeSystem();


    }








    private void initializeSystem(){



        /*
         * Create Graph
         */

        graph =
                new Graph();




        /*
         * Create Locations
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
         * Add locations to graph
         */

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






        /*
         * Create Roads
         */

        graph.addRoad(
                hospitalLocation,
                bloodBankLocation,
                5,
                10
        );



        graph.addRoad(
                hospitalLocation,
                donorLocation1,
                20,
                25
        );



        graph.addRoad(
                hospitalLocation,
                donorLocation2,
                30,
                40
        );





        /*
         * Initialize Dijkstra
         */

        dijkstra =
                new Dijkstra(
                        graph
                );







        /*
         * Create Services
         */

        hospitalService =
                new HospitalService();



        bloodBankService =
                new BloodBankService();



        donorService =
                new DonorService();








        /*
         * Add Hospital
         */

        Hospital hospital =
                new Hospital(
                        "H001",
                        "Colombo General Hospital",
                        hospitalLocation
                );



        hospitalService.addHospital(
                hospital
        );








        /*
         * Add Blood Bank
         */

        BloodBank bloodBank =
                new BloodBank(
                        "B001",
                        "Colombo Blood Bank",
                        bloodBankLocation
                );



        bloodBankService.addBloodBank(
                bloodBank
        );



        /*
         * Add sample blood
         */

        bloodBankService.addStock(
                "Colombo Blood Bank",
                "A+",
                10
        );







        /*
         * Add Donors
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



        donorService.addDonor(
                donor1
        );


        donorService.addDonor(
                donor2
        );








        /*
         * Create Matching System
         */

        matchingService =
                new DonorMatchingService(
                        donorService,
                        dijkstra
                );





        queueService =
                new RequestQueueService();






        /*
         * Create Controller
         */

        controller =
                new EmergencyController(
                        hospitalService,
                        bloodBankService,
                        donorService,
                        matchingService,
                        queueService
                );


    }








    public EmergencyController getController(){

        return controller;

    }




    public HospitalService getHospitalService(){

        return hospitalService;

    }



    public DonorService getDonorService(){

        return donorService;

    }



    public BloodBankService getBloodBankService(){

        return bloodBankService;

    }

}
