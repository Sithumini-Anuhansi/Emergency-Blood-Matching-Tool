package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.Donor;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DonorService {
    private static final String FILE_NAME = "donor_updates.json";
    private Map<String, Donor> donors = new LinkedHashMap<>();
    private Context context;

    public DonorService(Context context) {
        this.context = context;
    }

    public void addDonor(Donor donor) {
        donors.put(donor.getId(), donor);
    }

    public void loadPersistedUpdates() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String id = o.getString("id");
                Donor d = donors.get(id);
                if (d != null) {
                    d.setAvailable(o.getBoolean("available"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUpdates() {
        try {
            JSONArray arr = new JSONArray();
            for (Donor d : donors.values()) {
                JSONObject o = new JSONObject();
                o.put("id", d.getId());
                o.put("available", d.isAvailable());
                arr.put(o);
            }
            File file = new File(context.getFilesDir(), FILE_NAME);
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(arr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Donor findDonor(String id) {
        return donors.get(id);
    }

    public List<Donor> getAvailableDonors() {
        List<Donor> list = new ArrayList<>();
        for (Donor d : donors.values()) {
            if (d.isAvailable()) list.add(d);
        }
        return list;
    }

    public List<Donor> getEligibleDonors(String bloodGroup) {
        List<Donor> list = new ArrayList<>();
        for (Donor d : donors.values()) {
            if (d.isEligible() && d.getBloodGroup().equalsIgnoreCase(bloodGroup)) {
                list.add(d);
            }
        }
        return list;
    }

    public void updateAvailability(String donorId, boolean available) {
        Donor d = donors.get(donorId);
        if (d != null) {
            d.setAvailable(available);
            saveUpdates();
        }
    }

    public List<Donor> getAllDonors() {
        return new ArrayList<>(donors.values());
    }
}
