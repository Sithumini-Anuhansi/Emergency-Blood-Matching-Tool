package com.bloodnetwork.app;

import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.DonorMatch;
import com.bloodnetwork.app.model.DonorRequest;
import com.bloodnetwork.app.model.RequestStatus;
import com.bloodnetwork.app.service.RequestQueueService;

import java.util.ArrayList;
import java.util.List;

public class RequestQueueTest {
    public static void main(String[] args) {
        System.out.println("===== RequestQueueTest =====");

        List<DonorMatch> matches = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            Donor donor = new Donor(
                    "D" + String.format("%02d", i), "Donor " + i, "O+", "L001", "0770000000", 25, true);
            matches.add(new DonorMatch(donor, i, i * 2));
        }

        RequestQueueService queue = new RequestQueueService();
        List<DonorRequest> created = queue.createRequests("ER1", matches);

        Assert.assertEquals("only the first 10 (active slots) get DonorRequest objects up front", 10, created.size());
        Assert.assertEquals("exactly 10 stay active, not all 13", 10, queue.getActiveRequestsForEmergency("ER1").size());

        String firstDonorRequestId = created.get(0).getId();

        boolean rejected = queue.rejectRequest(firstDonorRequestId);
        Assert.assertTrue("rejecting an active request succeeds", rejected);
        Assert.assertEquals("queue is topped back up to exactly 10 after a rejection",
                10, queue.getActiveRequestsForEmergency("ER1").size());

        boolean stillActive = queue.getActiveRequestsForEmergency("ER1").stream()
                .anyMatch(dr -> dr.getId().equals(firstDonorRequestId));
        Assert.assertTrue("the rejected request is no longer active", !stillActive);

        boolean donor11Pulled = queue.getActiveRequestsForEmergency("ER1").stream()
                .anyMatch(dr -> dr.getDonorId().equals("D11"));
        Assert.assertTrue("the next waiting donor (D11, the 11th nearest) was pulled in automatically", donor11Pulled);

        // Reject two more times -- should still stay at 10 active, pulling in D12 then D13.
        String secondId = queue.getActiveRequestsForEmergency("ER1").get(0).getId();
        queue.rejectRequest(secondId);
        Assert.assertEquals("still exactly 10 active after a second rejection",
                10, queue.getActiveRequestsForEmergency("ER1").size());

        String thirdId = queue.getActiveRequestsForEmergency("ER1").get(0).getId();
        boolean acceptedOk = queue.acceptRequest(thirdId);

        Assert.assertTrue("accepting an active request succeeds", acceptedOk);
        Assert.assertEquals("once one donor accepts, every other active request is closed",
                1, queue.getActiveRequestsForEmergency("ER1").size());
        Assert.assertEquals("the one remaining active entry is the accepted donor, marked ACCEPTED",
                RequestStatus.ACCEPTED, queue.getRequest(thirdId).getStatus());

        long closedCount = created.stream()
                .filter(dr -> dr.getStatus() == RequestStatus.CLOSED)
                .count();
        Assert.assertTrue("at least one other donor request was marked CLOSED once one accepted", closedCount > 0);

        boolean rejectUnknown = queue.rejectRequest("DR999");
        Assert.assertTrue("rejecting an unknown request ID fails cleanly instead of crashing", !rejectUnknown);

        Assert.printSummary();
    }
}
