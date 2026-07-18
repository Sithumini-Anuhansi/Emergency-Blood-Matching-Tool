import graph.Dijkstra;
import graph.Graph;
import model.Location;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Graph graph = new Graph();

        Location colombo =
                new Location("L001","Colombo",0,0);

        Location negombo =
                new Location("L002","Negombo",0,0);

        Location gampaha =
                new Location("L003","Gampaha",0,0);

        Location chilaw =
                new Location("L004","Chilaw",0,0);

        graph.addLocation(colombo);
        graph.addLocation(negombo);
        graph.addLocation(gampaha);
        graph.addLocation(chilaw);

        graph.addRoad(colombo,negombo,35,45);
        graph.addRoad(colombo,gampaha,28,35);
        graph.addRoad(negombo,gampaha,22,30);
        graph.addRoad(negombo,chilaw,40,55);

        Dijkstra dijkstra = new Dijkstra(graph);

        Map<Location,Double> distances =
                dijkstra.shortestDistances(colombo);

        System.out.println("Shortest distances from Colombo\n");

        for(Location location : distances.keySet()){

            System.out.println(
                    location.getLocationName()
                    + " : "
                    + distances.get(location)
                    + " km"
            );

        }

    }

}