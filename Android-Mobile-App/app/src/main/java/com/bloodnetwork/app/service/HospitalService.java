package com.bloodnetwork.app.service;

import com.bloodnetwork.app.model.Hospital;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HospitalService {
    private Map<String, Hospital> hospitals = new LinkedHashMap<>();

    public void addHospital(Hospital hospital) {
        hospitals.put(hospital.getId(), hospital);
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
