package com.bloodnetwork.app.service;

import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.RequestStatus;

public class DonorResponseService {

    private RequestQueueService queueService;

    public DonorResponseService(RequestQueueService queueService) {
        this.queueService = queueService;
    }

    public boolean acceptRequest(String requestId, EmergencyRequest emergencyRequest) {
        DonorRequest dr = queueService.getRequest(requestId);
        if (dr == null || dr.getStatus() != RequestStatus.PENDING) return false;

        queueService.acceptRequest(requestId);
        return true;
    }

    public boolean rejectRequest(String requestId) {
        DonorRequest dr = queueService.getRequest(requestId);
        if (dr == null || dr.getStatus() != RequestStatus.PENDING) return false;
        queueService.rejectRequest(requestId);
        return true;
    }
}
