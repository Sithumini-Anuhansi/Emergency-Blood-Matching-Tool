package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.RequestStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequestQueueService {
    private static final String FILE_NAME = "donor_requests.json";
    private static final int MAX_PENDING = 1000;

    private Map<String, DonorRequest> allRequests = new LinkedHashMap<>();
    private Deque<String> pendingQueue = new ArrayDeque<>();
    private int counter = 0;
    private Context context;

    public RequestQueueService(Context context) {
        this.context = context;
        loadFromStorage();
    }

    public DonorRequest createRequest(String emergencyRequestId, String donorId) {
        String id = "DR" + (++counter);
        DonorRequest dr = new DonorRequest(id, emergencyRequestId, donorId);
        allRequests.put(id, dr);
        if (pendingQueue.size() < MAX_PENDING) {
            pendingQueue.add(id);
        }
        saveToStorage();
        return dr;
    }

    public DonorRequest getRequest(String id) {
        return allRequests.get(id);
    }

    public List<DonorRequest> getPendingRequests() {
        List<DonorRequest> list = new ArrayList<>();
        for (String id : pendingQueue) {
            list.add(allRequests.get(id));
        }
        return list;
    }

    public List<DonorRequest> getPendingRequestsForDonor(String donorId) {
        List<DonorRequest> list = new ArrayList<>();
        for (String id : pendingQueue) {
            DonorRequest dr = allRequests.get(id);
            if (dr != null && dr.getDonorId().equals(donorId)) list.add(dr);
        }
        return list;
    }

    public boolean hasPendingSlots() {
        return pendingQueue.size() < MAX_PENDING;
    }

    public int pendingCount() {
        return pendingQueue.size();
    }

    public void removeRequest(String requestId) {
        pendingQueue.remove(requestId);
        DonorRequest dr = allRequests.get(requestId);
        if (dr != null) {
            dr.setStatus(RequestStatus.CLOSED);
            saveToStorage();
        }
    }

    public void closeAllPendingForEmergency(String emergencyRequestId) {
        List<String> toRemove = new ArrayList<>();
        for (String id : pendingQueue) {
            DonorRequest dr = allRequests.get(id);
            if (dr.getEmergencyRequestId().equals(emergencyRequestId)) {
                dr.setStatus(RequestStatus.CLOSED);
                toRemove.add(id);
            }
        }
        if (!toRemove.isEmpty()) {
            pendingQueue.removeAll(toRemove);
            saveToStorage();
        }
    }

    public void rejectRequest(String requestId) {
        DonorRequest dr = allRequests.get(requestId);
        if (dr != null) {
            dr.setStatus(RequestStatus.REJECTED);
            pendingQueue.remove(requestId);
            saveToStorage();
        }
    }

    public void acceptRequest(String requestId) {
        DonorRequest dr = allRequests.get(requestId);
        if (dr != null) {
            dr.setStatus(RequestStatus.ACCEPTED);
            pendingQueue.remove(requestId);
            saveToStorage();
        }
    }

    public List<DonorRequest> getAllRequests() {
        return new ArrayList<>(allRequests.values());
    }

    private void saveToStorage() {
        try {
            JSONArray arr = new JSONArray();
            for (DonorRequest dr : allRequests.values()) {
                JSONObject o = new JSONObject();
                o.put("id", dr.getId());
                o.put("emergencyRequestId", dr.getEmergencyRequestId());
                o.put("donorId", dr.getDonorId());
                o.put("status", dr.getStatus().name());
                o.put("inPendingQueue", pendingQueue.contains(dr.getId()));
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
                DonorRequest dr = new DonorRequest(
                        o.getString("id"),
                        o.getString("emergencyRequestId"),
                        o.getString("donorId")
                );
                dr.setStatus(RequestStatus.valueOf(o.getString("status")));
                allRequests.put(dr.getId(), dr);
                if (o.getBoolean("inPendingQueue")) {
                    pendingQueue.add(dr.getId());
                }
                try {
                    int num = Integer.parseInt(dr.getId().replace("DR", ""));
                    if (num > counter) counter = num;
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
