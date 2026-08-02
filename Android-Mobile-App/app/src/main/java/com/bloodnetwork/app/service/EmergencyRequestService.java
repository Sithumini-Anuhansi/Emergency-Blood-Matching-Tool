package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.RequestPriority;
import com.bloodnetwork.app.model.RequestStatus;
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

public class EmergencyRequestService {
    private static final String FILE_NAME = "emergency_requests.json";
    private Map<String, EmergencyRequest> requests = new LinkedHashMap<>();
    private int counter = 0;
    private Context context;

    public EmergencyRequestService(Context context) {
        this.context = context;
        loadFromStorage();
    }

    public EmergencyRequest createRequest(String hospitalId, String bloodGroup, int quantity, RequestPriority priority) {
        String id = "ER" + (++counter);
        EmergencyRequest er = new EmergencyRequest(id, hospitalId, bloodGroup, quantity, priority);
        requests.put(id, er);
        saveToStorage();
        return er;
    }

    public void updateStatus(String requestId, RequestStatus status) {
        EmergencyRequest er = requests.get(requestId);
        if (er != null) {
            er.setStatus(status);
            saveToStorage();
        }
    }

    public void setFulfiller(String requestId, String fulfilledById, String fulfilledByType) {
        EmergencyRequest er = requests.get(requestId);
        if (er != null) {
            er.setFulfiller(fulfilledById, fulfilledByType);
            saveToStorage();
        }
    }

    public EmergencyRequest getRequest(String id) {
        return requests.get(id);
    }

    public Map<String, EmergencyRequest> getAllRequests() {
        return requests;
    }

    private void saveToStorage() {
        try {
            JSONArray arr = new JSONArray();
            for (EmergencyRequest er : requests.values()) {
                JSONObject o = new JSONObject();
                o.put("id", er.getId());
                o.put("hospitalId", er.getHospitalId());
                o.put("bloodGroup", er.getBloodGroup());
                o.put("quantity", er.getQuantity());
                o.put("status", er.getStatus().name());
                o.put("priority", er.getPriority().name());
                o.put("timestamp", er.getTimestamp());
                if (er.getFulfilledById() != null) {
                    o.put("fulfilledById", er.getFulfilledById());
                    o.put("fulfilledByType", er.getFulfilledByType());
                }
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

    private void loadFromStorage() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                EmergencyRequest er = new EmergencyRequest(
                        o.getString("id"),
                        o.getString("hospitalId"),
                        o.getString("bloodGroup"),
                        o.getInt("quantity"),
                        RequestPriority.valueOf(o.getString("priority"))
                );
                er.setStatus(RequestStatus.valueOf(o.getString("status")));
                if (o.has("timestamp")) {
                    er.setTimestamp(o.getLong("timestamp"));
                }
                if (o.has("fulfilledById")) {
                    er.setFulfiller(o.getString("fulfilledById"), o.optString("fulfilledByType", null));
                }
                requests.put(er.getId(), er);
                try {
                    int num = Integer.parseInt(er.getId().replace("ER", ""));
                    if (num > counter) counter = num;
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
