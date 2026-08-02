package com.bloodnetwork.app.util;

import android.content.Context;

import com.bloodnetwork.app.graph.Edge;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {

    private Context context;

    public DataLoader(Context context) {
        this.context = context;
    }

    public List<Location> loadLocations() throws IOException {
        List<String[]> rows = CSVReader.readFromAssets(context, "locations.csv");
        List<Location> list = new ArrayList<>();
        for (String[] r : rows) {
            list.add(new Location(r[0], r[1],
                    Double.parseDouble(r[2]), Double.parseDouble(r[3])));
        }
        return list;
    }

    public List<Edge> loadRoads(Graph graph) throws IOException {
        List<String[]> rows = CSVReader.readFromAssets(context, "roads.csv");
        List<Edge> edges = new ArrayList<>();
        for (String[] r : rows) {
            Location src = graph.getLocation(r[0]);
            Location dst = graph.getLocation(r[1]);
            double dist = Double.parseDouble(r[2]);
            int time = Integer.parseInt(r[3]);
            edges.add(new Edge(src, dst, dist, time));
        }
        return edges;
    }

    public List<Hospital> loadHospitals() throws IOException {
        List<String[]> rows = CSVReader.readFromAssets(context, "hospitals.csv");
        List<Hospital> list = new ArrayList<>();
        for (String[] r : rows) {
            list.add(new Hospital(r[0], r[1], r[2]));
        }
        return list;
    }

    public List<BloodBank> loadBloodBanks() throws IOException {
        List<String[]> rows = CSVReader.readFromAssets(context, "bloodbanks.csv");
        String[] groups = {"O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-"};
        List<BloodBank> list = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            // BloodBankID,Name,LocationID,O+,A+,B+,AB+,O-,A-,B-,AB-
            // Assume Phone is not in CSV yet, or add it if you updated CSV. 
            // For now, let's use a placeholder or read from a new column if exists.
            String phone = (r.length > 11) ? r[11] : "011-XXXXXXX"; 
            BloodBank bb = new BloodBank(r[0], r[1], r[2], phone);
            for (int j = 0; j < groups.length; j++) {
                bb.setStock(groups[j], Integer.parseInt(r[j + 3]));
            }
            list.add(bb);
        }
        return list;
    }

    public List<Donor> loadDonors() throws IOException {
        List<String[]> rows = CSVReader.readFromAssets(context, "donors.csv");
        List<Donor> list = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            String[] r = rows.get(i);
            // DonorID,Name,BloodGroup,Age,Phone,Available,Eligible,LocationID
            list.add(new Donor(r[0], r[1], r[2], r[7], r[4],
                    Integer.parseInt(r[3]), r[5].equalsIgnoreCase("Available")));
        }
        return list;
    }

    public Graph buildGraph() throws IOException {
        Graph graph = new Graph();
        for (Location loc : loadLocations()) {
            graph.addLocation(loc);
        }
        for (Edge edge : loadRoads(graph)) {
            graph.addEdge(edge);
        }
        return graph;
    }
}
