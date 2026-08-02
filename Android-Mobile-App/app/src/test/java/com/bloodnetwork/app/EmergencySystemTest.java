package com.bloodnetwork.app;

import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.graph.Edge;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.EmergencyRequest;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.model.Location;
import com.bloodnetwork.app.model.RequestStatus;
import com.bloodnetwork.app.service.BloodBankService;
import com.bloodnetwork.app.service.DonorMatchingService;
import com.bloodnetwork.app.service.DonorResponseService;
import com.bloodnetwork.app.service.DonorService;
import com.bloodnetwork.app.service.HospitalService;
import com.bloodnetwork.app.service.RequestQueueService;

// End-to-end scenario exercising EmergencyController.processRequest() in both branches:
// bank-stock issuance, and falling back to donor search with reject-then-accept.
public class EmergencySystemTest {
    public static void main(String[] args) {
        System.out.println("===== EmergencySystemTest =====");

        Location hospitalLoc = new Location("L001", "Colombo General Hospital", 6.9271, 79.8612);
        Location bankLoc = new Location("L002", "Colombo Blood Bank", 6.9147, 79.8737);
        Location donorLoc1 = new Location("L003", "Donor Area 1", 7.0000, 79.9000);
        Location donorLoc2 = new Location("L004", "Donor Area 2", 7.0500, 79.9500);

        Graph graph = new Graph();
        graph.addLocation(hospitalLoc);
        graph.addLocation(bankLoc);
        graph.addLocation(donorLoc1);
        graph.addLocation(donorLoc2);
        graph.addEdge(new Edge(hospitalLoc, bankLoc, 5, 10));
        graph.addEdge(new Edge(bankLoc, donorLoc1, 15, 25));
        graph.addEdge(new Edge(donorLoc1, donorLoc2, 10, 15));

        HospitalService hospitalService = new HospitalService();
        hospitalService.addHospital(new Hospital("H001", "Colombo General Hospital", "L001"));

        BloodBankService bloodBankService = new BloodBankService();
        BloodBank bank = new BloodBank("B001", "Colombo Blood Bank", "L002");
        bank.setStock("A+", 5);   // wrong type for the first request below
        bloodBankService.addBloodBank(bank);

        DonorService donorService = new DonorService();
        donorService.addDonor(new Donor("D001", "John", "O+", "L003", "0771111111", 25, true));
        donorService.addDonor(new Donor("D002", "Anne", "O+", "L004", "0772222222", 28, true));

        DonorMatchingService matchingService = new DonorMatchingService(donorService, graph);
        RequestQueueService queueService = new RequestQueueService();
        DonorResponseService responseService = new DonorResponseService(queueService);

        EmergencyController controller = new EmergencyController(
                hospitalService, bloodBankService, donorService, matchingService, queueService, responseService, graph);

        // --- Scenario A: bank has the wrong blood type, must fall back to donors ---
        EmergencyRequest reqA = controller.createRequest("H001", "O+", 2);
        Assert.assertTrue("request A created", reqA != null);
        Assert.assertEquals("request A starts PENDING", RequestStatus.PENDING, reqA.getStatus());

        controller.processRequest(reqA);

        Assert.assertEquals("no O+ stock anywhere, so the request moves to SEARCHING",
                RequestStatus.SEARCHING, reqA.getStatus());
        Assert.assertEquals("both donors were found and dispatched", 2, controller.getDispatchedMatches(reqA.getId()).size());

        String johnRequestId = queueService.getPendingRequestsForDonor("D001").get(0).getId();
        boolean johnRejected = controller.donorReject(johnRequestId);
        Assert.assertTrue("John's rejection is processed", johnRejected);
        Assert.assertEquals("request A stays SEARCHING while Anne is still pending",
                RequestStatus.SEARCHING, reqA.getStatus());

        String anneRequestId = queueService.getPendingRequestsForDonor("D002").get(0).getId();
        boolean anneAccepted = controller.donorAccept(anneRequestId, reqA);
        Assert.assertTrue("Anne's acceptance is processed", anneAccepted);
        Assert.assertEquals("request A is COMPLETED once Anne accepts",
                RequestStatus.COMPLETED, reqA.getStatus());

        // --- Scenario B: bank DOES have the right type and enough of it ---
        bloodBankService.updateStock("B001", "AB-", 10);
        EmergencyRequest reqB = controller.createRequest("H001", "AB-", 3);
        controller.processRequest(reqB);

        Assert.assertEquals("bank stock is issued directly, request B completes immediately",
                RequestStatus.COMPLETED, reqB.getStatus());
        Assert.assertEquals("issuing 3 of 10 AB- units leaves 7 in stock", 7, bank.getStock("AB-"));

        // --- Scenario C: no bank stock and no eligible donors at all ---
        EmergencyRequest reqC = controller.createRequest("H001", "B-", 1);
        controller.processRequest(reqC);

        Assert.assertEquals("no bank stock and no eligible donors -> CANCELLED",
                RequestStatus.CANCELLED, reqC.getStatus());

        Assert.printSummary();
    }
}
