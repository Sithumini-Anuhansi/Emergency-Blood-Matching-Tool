package com.bloodnetwork.app.model;

import java.util.HashMap;
import java.util.Map;

public class BloodBank {
    private String id;
    private String name;
    private String locationId;
    private String phone;
    private Map<String, Integer> stock;

    public BloodBank(String id, String name, String locationId, String phone) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
        this.phone = phone;
        this.stock = new HashMap<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocationId() { return locationId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getStock(String bloodGroup) {
        return stock.getOrDefault(bloodGroup, 0);
    }

    public void setStock(String bloodGroup, int units) {
        stock.put(bloodGroup, units);
    }

    public boolean hasBlood(String bloodGroup, int quantity) {
        return getStock(bloodGroup) >= quantity;
    }

    public void reduceStock(String bloodGroup, int quantity) {
        int current = getStock(bloodGroup);
        stock.put(bloodGroup, Math.max(0, current - quantity));
    }

    public Map<String, Integer> getAllStock() {
        return new HashMap<>(stock);
    }

    @Override
    public String toString() {
        return name + " [" + id + "] @ " + locationId;
    }
}
