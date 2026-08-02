package com.bloodnetwork.app.graph;

import com.bloodnetwork.app.model.Location;
import com.bloodnetwork.app.model.RouteResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    public static RouteResult shortestRoute(Graph graph, String sourceId, String targetId) {
        if (!graph.hasLocation(sourceId) || !graph.hasLocation(targetId)) {
            return new RouteResult(Collections.emptyList(), 0, 0);
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, Integer> times = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();

        for (String id : graph.getAllLocations().keySet()) {
            distances.put(id, Double.MAX_VALUE);
            times.put(id, Integer.MAX_VALUE);
        }
        distances.put(sourceId, 0.0);
        times.put(sourceId, 0);
        queue.add(new Node(sourceId, 0.0, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (visited.contains(current.id)) continue;
            visited.add(current.id);

            if (current.id.equals(targetId)) break;

            for (Edge edge : graph.getEdges(current.id)) {
                String neighbor = edge.getDestination().getId();
                if (visited.contains(neighbor)) continue;

                double newDist = distances.get(current.id) + edge.getDistance();
                int newTime = times.get(current.id) + edge.getTime();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    times.put(neighbor, newTime);
                    previous.put(neighbor, current.id);
                    queue.add(new Node(neighbor, newDist, newTime));
                }
            }
        }

        if (distances.get(targetId) == Double.MAX_VALUE) {
            return new RouteResult(Collections.emptyList(), 0, 0);
        }

        List<String> path = new ArrayList<>();
        String step = targetId;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        List<String> namedPath = new ArrayList<>();
        for (String id : path) {
            Location loc = graph.getLocation(id);
            namedPath.add(loc.getName());
        }

        return new RouteResult(namedPath, distances.get(targetId), times.get(targetId));
    }

    private static class Node implements Comparable<Node> {
        String id;
        double distance;
        int time;

        Node(String id, double distance, int time) {
            this.id = id;
            this.distance = distance;
            this.time = time;
        }

        @Override
        public int compareTo(Node other) {
            int cmp = Double.compare(this.distance, other.distance);
            return cmp != 0 ? cmp : Integer.compare(this.time, other.time);
        }
    }
}
