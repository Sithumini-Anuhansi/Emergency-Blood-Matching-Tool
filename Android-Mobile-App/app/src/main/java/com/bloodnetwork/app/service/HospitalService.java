package com.bloodnetwork.app.service;

import com.bloodnetwork.app.model.Hospital;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HospitalService {
    private static final String FILE_NAME = "hospital_updates.json";
    private Map<String, Hospital> hospitals = new LinkedHashMap<>();
    private android.content.Context context;

    public HospitalService(android.content.Context context) {
        this.context = context;
        loadPersistedUpdates();
    }

    public void addHospital(Hospital hospital) {
        hospitals.put(hospital.getId(), hospital);
        saveUpdates();
    }

    private void saveUpdates() {
        if (context == null) return;
        try {
            org.json.JSONArray arr = new org.json.JSONArray();
            for (Hospital h : hospitals.values()) {
                // Skip seeded hospitals (H101-H110 approx)
                try {
                    int num = Integer.parseInt(h.getId().substring(1));
                    if (num <= 110) continue;
                } catch (Exception ignored) {}

                org.json.JSONObject o = new org.json.JSONObject();
                o.put("id", h.getId());
                o.put("name", h.getName());
                o.put("locationId", h.getLocationId());
                arr.put(o);
            }
            java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME);
            try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
                fw.write(arr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPersistedUpdates() {
        if (context == null) return;
        java.io.File file = new java.io.File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            org.json.JSONArray arr = new org.json.JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject o = arr.getJSONObject(i);
                Hospital h = new Hospital(
                        o.getString("id"),
                        o.getString("name"),
                        o.getString("locationId")
                );
                hospitals.put(h.getId(), h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Hospital findHospital(String id) {
        return hospitals.get(id);
    }

    public List<Hospital> searchHospital(String keyword) {
        List<Hospital> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Hospital h : hospitals.values()) {
            if (h.getName().toLowerCase().contains(lower) || h.getId().toLowerCase().contains(lower)) {
                results.add(h);
            }
        }
        return results;
    }

    public List<Hospital> getAllHospitals() {
        return new ArrayList<>(hospitals.values());
    }
}
