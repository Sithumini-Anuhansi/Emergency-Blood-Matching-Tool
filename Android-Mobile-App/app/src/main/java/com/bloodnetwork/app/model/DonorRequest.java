package com.bloodnetwork.app.model;

public class DonorRequest {
    private String id;
    private String emergencyRequestId;
    private String donorId;
    private RequestStatus status;

    public DonorRequest(String id, String emergencyRequestId, String donorId) {
        this.id = id;
        this.emergencyRequestId = emergencyRequestId;
        this.donorId = donorId;
        this.status = RequestStatus.PENDING;
    }

    public String getId() { return id; }
    public String getEmergencyRequestId() { return emergencyRequestId; }
    public String getDonorId() { return donorId; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "DR[" + id + "] donor=" + donorId + " er=" + emergencyRequestId + " - " + status;
    }
}
