package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.BloodBank;
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

public class BloodBankService {
    private static final String FILE_NAME = "bloodbank_updates.json";
    private Map<String, BloodBank> bloodBanks = new LinkedHashMap<>();
    private Context context;

    public BloodBankService(Context context) {
        this.context = context;
    }

    public void addBloodBank(BloodBank bb) {
        bloodBanks.put(bb.getId(), bb);
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
                BloodBank bb = bloodBanks.get(id);
                if (bb != null) {
                    JSONObject stock = o.getJSONObject("stock");
                    JSONArray groups = stock.names();
                    if (groups != null) {
                        for (int j = 0; j < groups.length(); j++) {
                            String bg = groups.getString(j);
                            bb.setStock(bg, stock.getInt(bg));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUpdates() {
        try {
            JSONArray arr = new JSONArray();
            for (BloodBank bb : bloodBanks.values()) {
                JSONObject o = new JSONObject();
                o.put("id", bb.getId());
                JSONObject stock = new JSONObject();
                for (Map.Entry<String, Integer> entry : bb.getAllStock().entrySet()) {
                    stock.put(entry.getKey(), entry.getValue());
                }
                o.put("stock", stock);
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

    public boolean checkBlood(String bloodGroup, int quantity) {
        for (BloodBank bb : bloodBanks.values()) {
            if (bb.hasBlood(bloodGroup, quantity)) return true;
        }
        return false;
    }

    public BloodBank findAvailableBank(String bloodGroup, int quantity) {
        for (BloodBank bb : bloodBanks.values()) {
            if (bb.hasBlood(bloodGroup, quantity)) return bb;
        }
        return null;
    }

    public void updateStock(String bankId, String bloodGroup, int units) {
        BloodBank bb = bloodBanks.get(bankId);
        if (bb != null) {
            bb.setStock(bloodGroup, units);
            saveUpdates();
        }
    }

    public void reduceStock(String bankId, String bloodGroup, int quantity) {
        BloodBank bb = bloodBanks.get(bankId);
        if (bb != null) {
            bb.reduceStock(bloodGroup, quantity);
            saveUpdates();
        }
    }

    public int getStock(String bankId, String bloodGroup) {
        BloodBank bb = bloodBanks.get(bankId);
        if (bb == null) return 0;
        return bb.getStock(bloodGroup);
    }

    public BloodBank getBloodBankById(String bankId) {
        return bloodBanks.get(bankId);
    }

    public List<BloodBank> getAllBloodBanks() {
        return new ArrayList<>(bloodBanks.values());
    }
}
