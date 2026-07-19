package service;

import model.Donor;
import model.DonorMatch;
import model.DonorRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RequestQueueService 
{
    private Queue<Donor> waitingQueue;
    private List<DonorRequest> activeRequests;
    private final int MAX_ACTIVE_REQUESTS = 10;

    public RequestQueueService()
    {
        waitingQueue = new LinkedList<>();
        activeRequests = new ArrayList<>();
    }

    // Load matched donors
    public void createRequests( List<DonorMatch> matches )
    {
        for(DonorMatch match : matches)
        {
            waitingQueue.add( match.getDonor() );
        }
        fillActiveRequests();
    }

    // Keep 10 active requests
    private void fillActiveRequests()
    {
        while( activeRequests.size() < MAX_ACTIVE_REQUESTS && !waitingQueue.isEmpty() )
        {
            Donor donor = waitingQueue.poll();

            activeRequests.add( new DonorRequest( donor ) );

            System.out.println( "Request sent to " + donor.getName() );
        }
    }

    // Donor accepts request
    public boolean acceptRequest( String donorId )
    {
        for(DonorRequest request : activeRequests)
        {
            if(request.getDonor() .getDonorId() .equals(donorId))
            {
                request.accept();
                return true;
            }
        }
        return false;
    }

    // Donor rejects request
    public boolean rejectRequest( String donorId )
    {
        for(int i=0; i<activeRequests.size(); i++)
        {
            DonorRequest request = activeRequests.get(i);

            if(request.getDonor() .getDonorId() .equals(donorId))
            {
                request.reject();
                activeRequests.remove(i);
                fillActiveRequests();
                return true;
            }
        }
        return false;
    }

    public List<DonorRequest> getActiveRequests()
    { return activeRequests; }
}