package com.bloodnetwork.app.model;

public class EmergencyRequest {
    private String id;
    private String hospitalId;
    private String bloodGroup;
    private int quantity;
    private RequestStatus status;
    private RequestPriority priority;
    private long timestamp;
    private String fulfilledById;
    private String fulfilledByType; // "DONOR" or "BLOOD_BANK"

    public EmergencyRequest(String id, String hospitalId, String bloodGroup, int quantity, RequestPriority priority) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.status = RequestStatus.PENDING;
        this.priority = priority;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getHospitalId() { return hospitalId; }
    public String getBloodGroup() { return bloodGroup; }
    public int getQuantity() { return quantity; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public RequestPriority getPriority() { return priority; }
    public void setPriority(RequestPriority priority) { this.priority = priority; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getFulfilledById() { return fulfilledById; }
    public String getFulfilledByType() { return fulfilledByType; }
    public void setFulfiller(String fulfilledById, String fulfilledByType) {
        this.fulfilledById = fulfilledById;
        this.fulfilledByType = fulfilledByType;
    }

    @Override
    public String toString() {
        return "ER[" + id + "] " + bloodGroup + " x" + quantity + " for " + hospitalId + " - " + status;
    }
}
