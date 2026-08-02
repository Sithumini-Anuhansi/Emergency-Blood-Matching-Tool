package com.bloodnetwork.app;

import com.bloodnetwork.app.graph.Dijkstra;
import com.bloodnetwork.app.graph.Edge;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.Location;
import com.bloodnetwork.app.model.RouteResult;

public class GraphTest {
    public static void main(String[] args) {
        System.out.println("===== GraphTest =====");

        Location hospital = new Location("L001", "Colombo Hospital", 6.9271, 79.8612);
        Location bank = new Location("L002", "Colombo Blood Bank", 6.9147, 79.8737);
        Location mid = new Location("L003", "Gampaha Junction", 7.0917, 80.0000);
        Location donorNear = new Location("L004", "Donor Area A", 7.2000, 80.0500);
        Location donorFar = new Location("L005", "Donor Area B", 6.8500, 79.9000);

        Graph graph = new Graph();
        graph.addLocation(hospital);
        graph.addLocation(bank);
        graph.addLocation(mid);
        graph.addLocation(donorNear);
        graph.addLocation(donorFar);

        graph.addEdge(new Edge(hospital, bank, 3, 10));
        graph.addEdge(new Edge(bank, mid, 25, 35));
        graph.addEdge(new Edge(mid, donorNear, 15, 20));
        graph.addEdge(new Edge(hospital, donorFar, 18, 25));

        Assert.assertTrue("graph has 5 locations", graph.getAllLocations().size() == 5);

        RouteResult toBank = Dijkstra.shortestRoute(graph, "L001", "L002");
        Assert.assertEquals("distance hospital -> bank is the direct 3km road", 3.0, toBank.getTotalDistance());

        RouteResult toDonorFar = Dijkstra.shortestRoute(graph, "L001", "L005");
        Assert.assertEquals("distance hospital -> far donor uses the direct 18km road", 18.0, toDonorFar.getTotalDistance());

        RouteResult toDonorNear = Dijkstra.shortestRoute(graph, "L001", "L004");
        Assert.assertEquals("distance hospital -> near donor sums the only path (3+25+15)", 43.0, toDonorNear.getTotalDistance());
        Assert.assertEquals("route time sums each hop (10+35+20)", 65, toDonorNear.getTotalTime());
        Assert.assertEquals("route path has 4 stops", 4, toDonorNear.getPath().size());

        RouteResult unreachable = Dijkstra.shortestRoute(graph, "L001", "L999");
        Assert.assertTrue("route to an unknown location is reported invalid, not a crash", !unreachable.isValid());

        Assert.printSummary();
    }
}
