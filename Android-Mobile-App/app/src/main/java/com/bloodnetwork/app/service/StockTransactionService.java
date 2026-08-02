package com.bloodnetwork.app.service;

import android.content.Context;
import com.bloodnetwork.app.model.StockTransaction;
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

public class StockTransactionService {
    private static final String FILE_NAME = "transactions.json";
    private Map<String, StockTransaction> transactions = new LinkedHashMap<>();
    private int counter = 0;
    private Context context;

    public StockTransactionService(Context context) {
        this.context = context;
        loadFromStorage();
    }

    public StockTransaction record(String bloodBankId, String bloodGroup, int quantity, String type) {
        String id = "TXN" + (++counter);
        StockTransaction t = new StockTransaction(id, bloodBankId, bloodGroup, quantity, type);
        transactions.put(id, t);
        saveToStorage();
        return t;
    }

    public List<StockTransaction> getForBloodBank(String bloodBankId) {
        List<StockTransaction> list = new ArrayList<>();
        for (StockTransaction t : transactions.values()) {
            if (t.getBloodBankId().equals(bloodBankId)) list.add(t);
        }
        return list;
    }

    public List<StockTransaction> getAll() {
        return new ArrayList<>(transactions.values());
    }

    private void saveToStorage() {
        try {
            JSONArray arr = new JSONArray();
            for (StockTransaction t : transactions.values()) {
                JSONObject o = new JSONObject();
                o.put("id", t.getId());
                o.put("bloodBankId", t.getBloodBankId());
                o.put("bloodGroup", t.getBloodGroup());
                o.put("quantity", t.getQuantity());
                o.put("type", t.getType());
                o.put("timestamp", t.getTimestamp());
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
                StockTransaction t = new StockTransaction(
                        o.getString("id"),
                        o.getString("bloodBankId"),
                        o.getString("bloodGroup"),
                        o.getInt("quantity"),
                        o.getString("type")
                );
                // Note: assuming StockTransaction model has setTimestamp or handles it
                transactions.put(t.getId(), t);
                try {
                    int num = Integer.parseInt(t.getId().replace("TXN", ""));
                    if (num > counter) counter = num;
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
