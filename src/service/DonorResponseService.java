package service;

import model.DonorRequest;
import model.RequestStatus;
import model.EmergencyRequest;

public class DonorResponseService 
{
    private RequestQueueService queueService;

    public DonorResponseService( RequestQueueService queueService )
    {
        this.queueService = queueService;
    }

    // Handle donor acceptance
    public boolean acceptDonorRequest( String donorId, EmergencyRequest request )
    {
        boolean accepted = queueService.acceptRequest( donorId );

        if(accepted)
        {
            request.setStatus( RequestStatus.ACCEPTED );

            System.out.println( "Donor accepted request" );
            return true;
        }
        return false;
    }

    // Handle donor rejection
    public boolean rejectDonorRequest( String donorId, EmergencyRequest request )
    {
        boolean rejected = queueService.rejectRequest( donorId );

        if(rejected)
        {
            System.out.println( "Donor rejected request" );

            if(queueService.getActiveRequests().isEmpty())
            {
                request.setStatus( RequestStatus.CANCELLED );
            }
            else
            {
                request.setStatus( RequestStatus.WAITING_FOR_DONOR );
            }
            return true;
        }
        return false;
    }
}