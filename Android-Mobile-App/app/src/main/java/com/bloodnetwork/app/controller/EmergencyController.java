package com.bloodnetwork.app.controller;

import com.bloodnetwork.app.graph.Dijkstra;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.AppNotification;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.DonorMatch;
import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.MatchResult;
import com.bloodnetwork.app.model.RequestPriority;
import com.bloodnetwork.app.model.RequestStatus;
import com.bloodnetwork.app.model.RouteResult;
import com.bloodnetwork.app.model.StockTransaction;
import com.bloodnetwork.app.service.BloodBankService;
import com.bloodnetwork.app.service.DonorMatchingService;
import com.bloodnetwork.app.service.DonorResponseService;
import com.bloodnetwork.app.service.DonorService;
import com.bloodnetwork.app.service.EmergencyRequestService;
import com.bloodnetwork.app.service.HospitalService;
import com.bloodnetwork.app.service.NotificationService;
import com.bloodnetwork.app.service.RequestQueueService;
import com.bloodnetwork.app.service.StockTransactionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmergencyController {

    private HospitalService hospitalService;
    private BloodBankService bloodBankService;
    private DonorService donorService;
    private DonorMatchingService matchingService;
    private RequestQueueService queueService;
    private DonorResponseService responseService;
    private NotificationService notificationService;
    private StockTransactionService transactionService;
    private EmergencyRequestService emergencyRequestService;
    private Graph graph;

    private Map<String, List<MatchResult>> requestMatches = new HashMap<>();
    private Map<String, Integer> nextMatchIndex = new HashMap<>();

    public EmergencyController(HospitalService hospitalService,
                               BloodBankService bloodBankService,
                               DonorService donorService,
                               DonorMatchingService matchingService,
                               RequestQueueService queueService,
                               DonorResponseService responseService,
                               Graph graph,
                               NotificationService notificationService,
                               StockTransactionService transactionService,
                               EmergencyRequestService emergencyRequestService) {
        this.hospitalService = hospitalService;
        this.bloodBankService = bloodBankService;
        this.donorService = donorService;
        this.matchingService = matchingService;
        this.queueService = queueService;
        this.responseService = responseService;
        this.graph = graph;
        this.notificationService = notificationService;
        this.transactionService = transactionService;
        this.emergencyRequestService = emergencyRequestService;
    }

    // ── Emergency request lifecycle ─────────────────────────────────────────

    public EmergencyRequest createRequest(String hospitalId, String bloodGroup, int quantity, RequestPriority priority) {
        Hospital hospital = hospitalService.findHospital(hospitalId);
        if (hospital == null) return null;
        return emergencyRequestService.createRequest(hospitalId, bloodGroup, quantity, priority);
    }

    public List<MatchResult> findMatches(String bloodGroup, int quantity, String hospitalLocationId) {
        List<MatchResult> allMatches = new ArrayList<>();

        // 1. Blood Bank matches
        for (BloodBank bb : bloodBankService.getAllBloodBanks()) {
            if (bb.hasBlood(bloodGroup, quantity)) {
                RouteResult route = Dijkstra.shortestRoute(graph, hospitalLocationId, bb.getLocationId());
                if (route.isValid()) {
                    allMatches.add(new MatchResult(bb.getId(), bb.getName(), MatchResult.Type.BLOOD_BANK,
                            route.getTotalDistance(), route.getTotalTime(), bb));
                }
            }
        }

        // 2. Donor matches
        List<DonorMatch> donors = matchingService.findTopDonors(bloodGroup, hospitalLocationId, 100);
        for (DonorMatch dm : donors) {
            allMatches.add(new MatchResult(dm.getDonor().getId(), dm.getDonor().getName(), MatchResult.Type.DONOR,
                    dm.getDistance(), dm.getTravelTime(), dm.getDonor()));
        }

        // 3. Sort by distance, then type (Blood Bank first)
        Collections.sort(allMatches, new Comparator<MatchResult>() {
            @Override
            public int compare(MatchResult m1, MatchResult m2) {
                int dist = Double.compare(m1.getDistance(), m2.getDistance());
                if (dist != 0) return dist;
                return m1.getType().compareTo(m2.getType());
            }
        });

        return allMatches;
    }

    public void processRequest(EmergencyRequest er, List<MatchResult> allMatches) {
        Hospital hospital = hospitalService.findHospital(er.getHospitalId());
        String hospitalName = hospital != null ? hospital.getName() : "Hospital";
        String priorityPrefix = er.getPriority() == RequestPriority.EMERGENCY ? "🚨 EMERGENCY: " : "🩸 Request: ";

        for (MatchResult match : allMatches) {
            notifyMatch(er, match, hospitalName, priorityPrefix);
        }
    }

    private void notifyMatch(EmergencyRequest er, MatchResult match, String hospitalName, String prefix) {
        String title = prefix + er.getBloodGroup();
        String body = hospitalName + " needs " + er.getBloodGroup() + " x" + er.getQuantity()
                + ". Distance: " + String.format("%.1f km", match.getDistance());

        notificationService.send(match.getId(), title, body);

        if (match.getType() == MatchResult.Type.DONOR) {
            queueService.createRequest(er.getId(), match.getId());
        }
        // In a real app, blood banks might also have a formal queue entry
    }

    public boolean donorReject(String donorRequestId) {
        DonorRequest dr = queueService.getRequest(donorRequestId);
        if (dr == null) return false;
        String donorId = dr.getDonorId();

        boolean ok = responseService.rejectRequest(donorRequestId);
        if (ok) {
            notificationService.send(donorId, "Request Rejected", "Thank you for the response.");
        }
        return ok;
    }

    public boolean donorAccept(String requestId, EmergencyRequest er) {
        boolean ok = responseService.acceptRequest(requestId, er);
        if (ok) {
            emergencyRequestService.updateStatus(er.getId(), RequestStatus.COMPLETED);
            queueService.closeAllPendingForEmergency(er.getId());

            DonorRequest dr = queueService.getRequest(requestId);
            if (dr != null) {
                emergencyRequestService.setFulfiller(er.getId(), dr.getDonorId(), "DONOR");
                com.bloodnetwork.app.model.Donor d = donorService.findDonor(dr.getDonorId());
                String contact = (d != null) ? d.getPhone() : "N/A";
                String donorName = (d != null) ? d.getName() : "A donor";
                
                notificationService.send(er.getHospitalId(), "✅ Request Accepted",
                        donorName + " has accepted your request " + er.getId() + 
                        ". Contact: " + contact);
                
                notificationService.send(dr.getDonorId(), "Success", 
                        "Hospital has been notified. They may contact you at " + contact);
            }
        }
        return ok;
    }

    public boolean bankAccept(String erId, String bankId) {
        EmergencyRequest er = emergencyRequestService.getRequest(erId);
        BloodBank bb = bloodBankService.getBloodBankById(bankId);
        
        if (er != null && bb != null && bb.hasBlood(er.getBloodGroup(), er.getQuantity())) {
            reduceStock(bankId, er.getBloodGroup(), er.getQuantity());
            emergencyRequestService.updateStatus(erId, RequestStatus.COMPLETED);
            emergencyRequestService.setFulfiller(erId, bankId, "BLOOD_BANK");
            queueService.closeAllPendingForEmergency(erId);
            
            notificationService.send(er.getHospitalId(), "✅ Request Accepted by Bank",
                    bb.getName() + " has fulfilled your request " + erId + 
                    ". Contact: " + bb.getPhone());
            
            notificationService.send(bankId, "Request Fulfilled", 
                    "You have issued blood for " + erId + ". Hospital notified.");
            return true;
        }
        return false;
    }

    public RouteResult getRoute(String sourceId, String targetId) {
        return Dijkstra.shortestRoute(graph, sourceId, targetId);
    }

    public String getLocationName(String locationId) {
        if (locationId == null) return "Unknown";
        com.bloodnetwork.app.model.Location loc = graph.getLocation(locationId);
        return loc != null ? loc.getName() : locationId;
    }

    public String getRequestingHospitalName(EmergencyRequest er) {
        Hospital h = hospitalService.findHospital(er.getHospitalId());
        return h != null ? h.getName() : er.getHospitalId();
    }

    public String getFulfillerDisplay(EmergencyRequest er) {
        if (er.getStatus() != RequestStatus.COMPLETED || er.getFulfilledById() == null) return null;
        if ("DONOR".equals(er.getFulfilledByType())) {
            com.bloodnetwork.app.model.Donor d = donorService.findDonor(er.getFulfilledById());
            if (d == null) return null;
            return d.getName() + " (Donor) - " + getLocationName(d.getLocationId());
        } else if ("BLOOD_BANK".equals(er.getFulfilledByType())) {
            BloodBank bb = bloodBankService.getBloodBankById(er.getFulfilledById());
            if (bb == null) return null;
            return bb.getName() + " (Blood Bank) - " + getLocationName(bb.getLocationId());
        }
        return null;
    }

    // ── Stock management ───────────────────────────────────────────────────

    public void addStock(String bankId, String bloodGroup, int quantity) {
        bloodBankService.updateStock(bankId, bloodGroup,
                bloodBankService.getStock(bankId, bloodGroup) + quantity);
        transactionService.record(bankId, bloodGroup, quantity, "ADD");
        notificationService.send("ADMIN", "Stock Updated",
                bankId + " added " + quantity + " units of " + bloodGroup);
        notificationService.send(bankId, "Stock Added",
                "+" + quantity + " units of " + bloodGroup + " recorded.");
    }

    public void reduceStock(String bankId, String bloodGroup, int quantity) {
        bloodBankService.reduceStock(bankId, bloodGroup, quantity);
        transactionService.record(bankId, bloodGroup, quantity, "ISSUE");
        // Low stock warning
        int remaining = bloodBankService.getStock(bankId, bloodGroup);
        if (remaining <= 3) {
            notificationService.send(bankId, "⚠️ Low Stock",
                    bloodGroup + " stock is low: only " + remaining + " units remaining.");
            notificationService.send("ADMIN", "⚠️ Low Stock Alert",
                    bankId + " has only " + remaining + " units of " + bloodGroup + " left.");
        }
    }

    public List<StockTransaction> getTransactionsForBank(String bankId) {
        return transactionService.getForBloodBank(bankId);
    }

    // ── Notifications ──────────────────────────────────────────────────────

    public void notifyRegistrationApproved(String userId, String username) {
        notificationService.send(userId,
                "✅ Account Approved",
                "Your account '" + username + "' has been approved. You can now log in.");
        notificationService.send("ADMIN", "User Approved", "Account approved: " + username);
    }

    public void notifyRegistrationRejected(String username) {
        notificationService.send("ADMIN", "User Rejected", "Account rejected: " + username);
    }

    public void notifyAdminNewRegistration(String username, String role) {
        notificationService.send("ADMIN", "📋 New Registration",
                "New " + role + " account pending approval: " + username);
    }

    public List<AppNotification> getNotifications(String recipientId) {
        return notificationService.getForRecipient(recipientId);
    }

    public int getUnreadNotificationCount(String recipientId) {
        return notificationService.getUnreadCount(recipientId);
    }

    public void markNotificationsRead(String recipientId) {
        notificationService.markAllRead(recipientId);
    }

    // ── Lookup helpers ─────────────────────────────────────────────────────

    public EmergencyRequest getEmergencyRequest(String id) {
        return emergencyRequestService.getRequest(id);
    }

    public Map<String, EmergencyRequest> getAllEmergencyRequests() {
        return emergencyRequestService.getAllRequests();
    }

    public Hospital getHospital(String id) {
        return hospitalService.findHospital(id);
    }

    public List<Hospital> getAllHospitals() {
        return hospitalService.getAllHospitals();
    }

    public List<BloodBank> getAllBloodBanks() {
        return bloodBankService.getAllBloodBanks();
    }

    public BloodBank getBloodBank(String id) {
        return bloodBankService.getBloodBankById(id);
    }

    public Graph getGraph() {
        return graph;
    }

    public DonorService getDonorService() {
        return donorService;
    }

    public BloodBankService getBloodBankService() {
        return bloodBankService;
    }

    public RequestQueueService getQueueService() {
        return queueService;
    }

    public HospitalService getHospitalService() {
        return hospitalService;
    }

    public List<DonorRequest> getPendingRequestsForDonor(String donorId) {
        return queueService.getPendingRequestsForDonor(donorId);
    }
}
