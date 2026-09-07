package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.Location;
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

public class LocationService {
    private static final String FILE_NAME = "locations_extra.json";
    private Map<String, Location> locations = new LinkedHashMap<>();
    private int counter = 0;
    private Context context;

    public LocationService(Context context) {
        this.context = context;
    }

    public void addLocation(Location loc) {
        locations.put(loc.getId(), loc);
        updateCounter(loc.getId());
    }

    private void updateCounter(String id) {
        try {
            if (id.startsWith("L")) {
                int num = Integer.parseInt(id.substring(1));
                if (num > counter) counter = num;
            }
        } catch (Exception ignored) {}
    }

    public Location addNewLocation(String name) {
        // Check if exists
        for (Location loc : locations.values()) {
            if (loc.getName().equalsIgnoreCase(name)) return loc;
        }
        
        String id = "L" + String.format("%03d", ++counter);
        // Default coordinates if not provided (near center or random)
        Location loc = new Location(id, name, 7.0, 80.0); 
        locations.put(id, loc);
        saveToStorage();
        return loc;
    }

    public Location findByName(String name) {
        for (Location loc : locations.values()) {
            if (loc.getName().equalsIgnoreCase(name)) return loc;
        }
        return null;
    }

    public Location getLocation(String id) {
        return locations.get(id);
    }

    public Map<String, Location> getAllLocations() {
        return locations;
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
                Location loc = new Location(
                        o.getString("id"),
                        o.getString("name"),
                        o.getDouble("latitude"),
                        o.getDouble("longitude")
                );
                addLocation(loc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToStorage() {
        try {
            JSONArray arr = new JSONArray();
            for (Location loc : locations.values()) {
                // Only save non-seeded locations (ID > L010 assuming 10 are seeded)
                try {
                    int num = Integer.parseInt(loc.getId().substring(1));
                    if (num <= 10) continue;
                } catch (Exception ignored) {}
                
                JSONObject o = new JSONObject();
                o.put("id", loc.getId());
                o.put("name", loc.getName());
                o.put("latitude", loc.getLatitude());
                o.put("longitude", loc.getLongitude());
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
}
