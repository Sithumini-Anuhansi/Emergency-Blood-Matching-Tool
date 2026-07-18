import graph.Graph;
import model.Location;

public class Main {

    public static void main(String[] args) {

        Graph graph = new Graph();

        Location colombo =
                new Location("L001", "Colombo", 0, 0);

        Location negombo =
                new Location("L002", "Negombo", 0, 0);

        Location gampaha =
                new Location("L003", "Gampaha", 0, 0);

        graph.addLocation(colombo);
        graph.addLocation(negombo);
        graph.addLocation(gampaha);

        graph.addRoad(colombo, negombo, 35, 45);
        graph.addRoad(colombo, gampaha, 28, 35);
        graph.addRoad(negombo, gampaha, 22, 30);

        graph.displayGraph();

    }

}