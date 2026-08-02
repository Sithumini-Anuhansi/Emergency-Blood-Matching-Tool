package com.bloodnetwork.app.graph;

import com.bloodnetwork.app.model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private Map<String, Location> locations = new LinkedHashMap<>();
    private Map<String, List<Edge>> adjacency = new LinkedHashMap<>();

    public void addLocation(Location location) {
        locations.put(location.getId(), location);
        adjacency.putIfAbsent(location.getId(), new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        adjacency.get(edge.getSource().getId()).add(edge);
        adjacency.get(edge.getDestination().getId()).add(
                new Edge(edge.getDestination(), edge.getSource(), edge.getDistance(), edge.getTime()));
    }

    public Location getLocation(String id) {
        return locations.get(id);
    }

    public List<Edge> getEdges(String locationId) {
        return adjacency.getOrDefault(locationId, Collections.emptyList());
    }

    public Map<String, Location> getAllLocations() {
        return Collections.unmodifiableMap(locations);
    }

    public boolean hasLocation(String id) {
        return locations.containsKey(id);
    }
}
