package system;

import controller.EmergencyController;
import graph.Dijkstra;
import graph.Graph;
import model.Location;
import service.BloodBankService;
import service.DonorMatchingService;
import service.DonorService;
import service.HospitalService;
import service.RequestQueueService;
import util.DataLoader;

import java.util.Map;

public class EmergencyBloodSystem {

    private HospitalService hospitalService;

    private BloodBankService bloodBankService;

    private DonorService donorService;

    private Graph graph;

    private Dijkstra dijkstra;

    private EmergencyController controller;

    private Map<String, Location> locations;

    public EmergencyBloodSystem() {

        initializeSystem();

    }

    /*
     * Initialize entire system
     */
    private void initializeSystem() {

        System.out.println("======================================");
        System.out.println(" Emergency Blood Route Finder Tool");
        System.out.println(" Initializing System...");
        System.out.println("======================================");

        /*
         * Create Services
         */
        hospitalService = new HospitalService();

        bloodBankService = new BloodBankService();

        donorService = new DonorService();

        /*
         * Create Graph
         */
        graph = new Graph();

        /*
         * Load Locations
         */
        locations =
                DataLoader.loadLocations(
                        "data/locations.csv",
                        graph
                );

        /*
         * Load Roads
         */
        DataLoader.loadRoads(
                "data/roads.csv",
                graph,
                locations
        );

        /*
         * Load Hospitals
         */
        DataLoader.loadHospitals(
                "data/hospitals.csv",
                hospitalService,
                locations
        );

        /*
         * Load Blood Banks
         */
        DataLoader.loadBloodBanks(
                "data/bloodbanks.csv",
                bloodBankService,
                locations
        );

        /*
         * Load Donors
         */
        DataLoader.loadDonors(
                "data/donors.csv",
                donorService,
                locations
        );

        /*
         * Create Dijkstra
         */
        dijkstra =
                new Dijkstra(graph);

        /*
         * Create Donor Matching Service
         */
        DonorMatchingService matchingService =
                new DonorMatchingService(
                        donorService,
                        dijkstra
                );

        /*
         * Create Request Queue
         */
        RequestQueueService queueService =
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

        /*
         * Display Summary
         */
        System.out.println();
        System.out.println("System Loaded Successfully!");
        System.out.println("--------------------------------------");
        System.out.println(
                "Locations    : "
                        + graph.getNumberOfLocations()
        );

        System.out.println(
                "Hospitals    : "
                        + hospitalService
                        .getAllHospitals()
                        .size()
        );

        System.out.println(
                "Blood Banks  : "
                        + bloodBankService
                        .getAllBloodBanks()
                        .size()
        );

        System.out.println(
                "Donors       : "
                        + donorService
                        .getAllDonors()
                        .size()
        );

        System.out.println("--------------------------------------");

    }

    /*
     * Get Controller
     */
    public EmergencyController getController() {

        return controller;

    }

    /*
     * Get Graph
     */
    public Graph getGraph() {

        return graph;

    }

    /*
     * Get Dijkstra
     */
    public Dijkstra getDijkstra() {

        return dijkstra;

    }

    /*
     * Get Hospital Service
     */
    public HospitalService getHospitalService() {

        return hospitalService;

    }

    /*
     * Get Blood Bank Service
     */
    public BloodBankService getBloodBankService() {

        return bloodBankService;

    }

    /*
     * Get Donor Service
     */
    public DonorService getDonorService() {

        return donorService;

    }

    /*
     * Get Locations
     */
    public Map<String, Location> getLocations() {

        return locations;

    }

}