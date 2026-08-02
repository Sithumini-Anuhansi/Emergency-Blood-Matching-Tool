package com.bloodnetwork.app;

import com.bloodnetwork.app.graph.Edge;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.DonorMatch;
import com.bloodnetwork.app.model.Location;
import com.bloodnetwork.app.service.DonorMatchingService;
import com.bloodnetwork.app.service.DonorService;

import java.util.List;

public class DonorMatchingTest {
    public static void main(String[] args) {
        System.out.println("===== DonorMatchingTest =====");

        Location hospitalLoc = new Location("L001", "Colombo Hospital", 6.9271, 79.8612);
        Location nearLoc = new Location("L002", "Nearby Donor Area", 6.9500, 79.9000);
        Location farLoc = new Location("L003", "Far Donor Area", 7.5000, 80.5000);

        Graph graph = new Graph();
        graph.addLocation(hospitalLoc);
        graph.addLocation(nearLoc);
        graph.addLocation(farLoc);
        graph.addEdge(new Edge(hospitalLoc, nearLoc, 5, 10));
        graph.addEdge(new Edge(hospitalLoc, farLoc, 50, 90));

        DonorService donorService = new DonorService();
        // exact O+ match, nearby
        donorService.addDonor(new Donor("D001", "John (exact match)", "O+", "L002", "0771234567", 25, true));
        // O- universal donor, far away
        donorService.addDonor(new Donor("D002", "Priya (universal donor)", "O-", "L003", "0777777777", 40, true));
        // incompatible A+ donor, nearby but should never appear for an O+ recipient
        donorService.addDonor(new Donor("D003", "Kasun (incompatible)", "A+", "L002", "0778888888", 33, true));
        // eligible in blood type but currently unavailable -- must be excluded
        donorService.addDonor(new Donor("D004", "Nadeesha (unavailable)", "O+", "L002", "0779999999", 29, false));
        // available but not medically eligible -- must be excluded
        donorService.addDonor(new Donor("D005", "Ruwan (not eligible)", "O-", "L003", "0771112222", 50, false));

        DonorMatchingService service = new DonorMatchingService(donorService, graph);

        List<DonorMatch> result = service.findAllMatches("O+", "L001");

        Assert.assertEquals("only the 2 truly available+eligible+compatible donors match", 2, result.size());
        Assert.assertEquals("nearer donor (John) ranked first", "D001", result.get(0).getDonor().getId());
        Assert.assertEquals("farther but compatible donor (Priya, O-) ranked second", "D002", result.get(1).getDonor().getId());

        boolean unavailableIncluded = result.stream().anyMatch(m -> m.getDonor().getId().equals("D004"));
        Assert.assertTrue("unavailable donor correctly excluded", !unavailableIncluded);

        boolean ineligibleIncluded = result.stream().anyMatch(m -> m.getDonor().getId().equals("D005"));
        Assert.assertTrue("medically-ineligible donor correctly excluded", !ineligibleIncluded);

        boolean incompatibleIncluded = result.stream().anyMatch(m -> m.getDonor().getId().equals("D003"));
        Assert.assertTrue("incompatible blood type (A+) correctly excluded from an O+ recipient's matches", !incompatibleIncluded);

        List<DonorMatch> top1 = service.findTopDonors("O+", "L001", 1);
        Assert.assertEquals("findTopDonors correctly caps the result size", 1, top1.size());

        Assert.printSummary();
    }
}
