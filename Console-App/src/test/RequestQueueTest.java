package test;

import model.*;
import service.RequestQueueService;

import java.util.ArrayList;
import java.util.List;

public class RequestQueueTest 
{
    public static void main(String[] args)
    {
        Location location = new Location( "L001", "Colombo", 6.9, 79.8 );

        List<DonorMatch> matches = new ArrayList<>();

        for(int i=1;i<=12;i++)
        {
            Donor donor = new Donor( "D00"+i, "Donor "+i, "O+", 25, "077000000", location );
            matches.add( new DonorMatch( donor, i ) );
        }

        RequestQueueService service = new RequestQueueService();

        service.createRequests( matches );

        System.out.println( "\nActive Requests" );

        for(DonorRequest request : service.getActiveRequests())
        {
            System.out.println(request);
        }

        System.out.println( "\nDonor 3 rejected" );

        service.rejectRequest( "D003" );

        System.out.println( "\nAfter Replacement" );

        for(DonorRequest request : service.getActiveRequests())
        {
            System.out.println(request);
        }
    }
}