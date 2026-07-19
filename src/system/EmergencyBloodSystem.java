package system;

import controller.EmergencyController;
import model.Location;
import service.*;
import graph.Dijkstra;
import graph.Graph;
import util.DataLoader;

import java.util.Map;

public class EmergencyBloodSystem 
{
    public static void main(String[] args) 
    {
        System.out.println( "======================================" );

        System.out.println( "   Emergency Blood Network System" );

        System.out.println( "======================================\n" );

        // Create services
        HospitalService hospitalService = new HospitalService();
        BloodBankService bloodBankService = new BloodBankService();
        DonorService donorService = new DonorService();

        // Create Graph
        Graph graph = new Graph();

        // Load locations
        Map<String, Location> locations = DataLoader.loadLocations( "data/locations.csv", graph );

        System.out.println( locations.size() + " locations loaded" );

        // Load roads
        DataLoader.loadRoads( "data/roads.csv", graph, locations );

        System.out.println( graph.getNumberOfLocations() + " graph vertices created" );

        // Load hospitals
        DataLoader.loadHospitals( "data/hospitals.csv", hospitalService, locations );

        System.out.println( hospitalService .getAllHospitals() .size() + " hospitals loaded" );

        // Load blood banks
        DataLoader.loadBloodBanks( "data/bloodbanks.csv", bloodBankService, locations );

        System.out.println( bloodBankService .getAllBloodBanks() .size() + " blood banks loaded" );

        // Load donors
        DataLoader.loadDonors( "data/donors.csv", donorService, locations );

        System.out.println( donorService .getAllDonors() .size() + " donors loaded" );

        // Create algorithm classes
        Dijkstra dijkstra = new Dijkstra(graph);

        DonorMatchingService matchingService = new DonorMatchingService( donorService, dijkstra );

        // Create request services
        RequestQueueService queueService = new RequestQueueService();

        // Create main controller
        EmergencyController controller = new EmergencyController
                ( hospitalService, bloodBankService, donorService, matchingService, queueService );

        // System ready
        System.out.println( "\n======================================" );

        System.out.println( "Emergency Blood Network Ready" );

        System.out.println( "======================================" );

        // Temporary testing (Later this will be replaced by UI/menu)
        System.out.println( "\nTesting Graph:" );

        graph.displayGraph();
    }
}