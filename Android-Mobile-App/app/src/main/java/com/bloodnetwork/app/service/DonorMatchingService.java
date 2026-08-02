package com.bloodnetwork.app.service;

import com.bloodnetwork.app.graph.Dijkstra;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.DonorMatch;
import com.bloodnetwork.app.model.RouteResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DonorMatchingService {
    private DonorService donorService;
    private Graph graph;

    public DonorMatchingService(DonorService donorService, Graph graph) {
        this.donorService = donorService;
        this.graph = graph;
    }

    public List<DonorMatch> findTopDonors(String bloodGroup, String hospitalLocationId, int limit) {
        List<Donor> eligible = donorService.getEligibleDonors(bloodGroup);
        List<DonorMatch> matches = new ArrayList<>();

        for (Donor donor : eligible) {
            RouteResult route = Dijkstra.shortestRoute(graph, hospitalLocationId, donor.getLocationId());
            if (route.isValid()) {
                matches.add(new DonorMatch(donor, route.getTotalDistance(), route.getTotalTime()));
            }
        }

        matches.sort(Comparator.comparingDouble(DonorMatch::getDistance));
        if (matches.size() > limit) {
            return new ArrayList<>(matches.subList(0, limit));
        }
        return matches;
    }
}
