package system;

import controller.EmergencyController;
import model.Location;
import service.*;
import graph.Dijkstra;
import graph.Graph;
import util.DataLoader;

import java.util.Map;

// Wires up the whole system: loads CSV data, builds the graph, creates services,
// and exposes the controller that the UI drives.
public class EmergencyBloodSystem
{
    private final Graph graph;
    private final HospitalService hospitalService;
    private final BloodBankService bloodBankService;
    private final DonorService donorService;
    private final EmergencyController controller;

    public EmergencyBloodSystem()
    {
        System.out.println( "======================================" );
        System.out.println( "   Emergency Blood Network System" );
        System.out.println( "======================================\n" );

        // Create services
        hospitalService = new HospitalService();
        bloodBankService = new BloodBankService();
        donorService = new DonorService();

        // Create Graph
        graph = new Graph();

        // Load locations
        Map<String, Location> locations = DataLoader.loadLocations( "data/locations.csv", graph );

        System.out.println( locations.size() + " locations loaded" );

        // Load roads
        DataLoader.loadRoads( "data/roads.csv", graph, locations );

        System.out.println( graph.getNumberOfLocations() + " graph vertices created" );

        // Load hospitals
        DataLoader.loadHospitals( "data/hospitals.csv", hospitalService, locations );

        System.out.println( hospitalService.getAllHospitals().size() + " hospitals loaded" );

        // Load blood banks
        DataLoader.loadBloodBanks( "data/bloodbanks.csv", bloodBankService, locations );

        System.out.println( bloodBankService.getAllBloodBanks().size() + " blood banks loaded" );

        // Load donors
        DataLoader.loadDonors( "data/donors.csv", donorService, locations );

        System.out.println( donorService.getAllDonors().size() + " donors loaded" );

        // Create algorithm classes
        Dijkstra dijkstra = new Dijkstra(graph);

        DonorMatchingService matchingService = new DonorMatchingService( donorService, dijkstra );

        // Create request services
        RequestQueueService queueService = new RequestQueueService();

        // Create main controller
        controller = new EmergencyController
                ( hospitalService, bloodBankService, donorService, matchingService, queueService );

        // System ready
        System.out.println( "\n======================================" );
        System.out.println( "Emergency Blood Network Ready" );
        System.out.println( "======================================" );
    }

    public EmergencyController getController()
    { return controller; }

    public Graph getGraph()
    { return graph; }
}
