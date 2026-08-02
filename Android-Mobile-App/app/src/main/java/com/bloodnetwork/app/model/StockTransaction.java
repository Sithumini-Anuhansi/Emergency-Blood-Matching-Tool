package com.bloodnetwork.app.model;

public class StockTransaction {
    private String id;
    private String bloodBankId;
    private String bloodGroup;
    private int quantity;
    private String type; // "ADD" or "ISSUE"
    private long timestamp;

    public StockTransaction(String id, String bloodBankId, String bloodGroup, int quantity, String type) {
        this.id = id;
        this.bloodBankId = bloodBankId;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getBloodBankId() { return bloodBankId; }
    public String getBloodGroup() { return bloodGroup; }
    public int getQuantity() { return quantity; }
    public String getType() { return type; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return type + " " + quantity + " units of " + bloodGroup + " @ " + bloodBankId;
    }
}
