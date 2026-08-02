package controller;

import model.*;
import service.*;

import java.util.List;

public class EmergencyController
{
    private HospitalService hospitalService;
    private BloodBankService bloodBankService;
    private DonorService donorService;
    private DonorMatchingService matchingService;
    private RequestQueueService queueService;
    private DonorResponseService responseService;

    private int requestCounter = 1;

    public EmergencyController
        ( HospitalService hospitalService, BloodBankService bloodBankService, DonorService donorService,
            DonorMatchingService matchingService, RequestQueueService queueService)
    {
        this.hospitalService = hospitalService;
        this.bloodBankService = bloodBankService;
        this.donorService = donorService;
        this.matchingService = matchingService;
        this.queueService = queueService;
        this.responseService = new DonorResponseService( queueService );
    }

    // Create emergency request with an auto-generated request ID
    public EmergencyRequest createRequest( String hospitalId, String bloodGroup, int quantity )
    {
        Hospital hospital = hospitalService.findHospitalById( hospitalId );

        if(hospital == null)
        {
            System.out.println( "Hospital not found" );
            return null;
        }

        String requestId = "REQ" + String.format( "%04d", requestCounter++ );

        return new EmergencyRequest( requestId, hospital, bloodGroup, quantity );
    }

    // Full emergency workflow (Phase 9): check blood banks first; if stock is available,
    // issue it immediately and complete the request. Otherwise fall back to donor matching.
    public void processRequest( EmergencyRequest request )
    {
        List<BloodBank> banks = bloodBankService.findAvailableBlood
                ( request.getBloodGroup(), request.getQuantity() );

        if(!banks.isEmpty())
        {
            BloodBank bank = banks.get(0);

            bloodBankService.removeStock( bank.getName(), request.getBloodGroup(), request.getQuantity() );

            request.setStatus( RequestStatus.COMPLETED );

            System.out.println( "\nBlood issued from " + bank.getName() + ". Request completed." );
            return;
        }

        System.out.println( "\nNo blood available in blood banks. Searching for donors..." );

        request.setStatus( RequestStatus.WAITING_FOR_DONOR );

        dispatchDonors( request );
    }

    // Read-only view of current blood bank stock levels
    public void viewBloodBankStock()
    {
        List<BloodBank> banks = bloodBankService.getAllBloodBanks();

        if(banks.isEmpty())
        {
            System.out.println( "No blood banks loaded." );
            return;
        }

        for(BloodBank bank : banks)
        {
            System.out.println( bank.getName() + " -> " + bank.getBloodStock() );
        }
    }

    // Read-only preview of matching donors for the current request (does not send requests)
    public void previewDonors( EmergencyRequest request )
    {
        List<DonorMatch> donors = matchingService.findBestDonors
                ( request.getBloodGroup(), request.getHospital() .getLocation() );

        if(donors.isEmpty())
        {
            System.out.println( "No eligible donors found." );
            return;
        }

        System.out.println( "\nMatching Donors:" );

        for(DonorMatch donor : donors)
        {
            System.out.println( donor );
        }
    }

    // Find, rank and queue up to 10 eligible donors, then show each one's route (Dijkstra)
    private void dispatchDonors( EmergencyRequest request )
    {
        List<DonorMatch> donors = matchingService.findBestDonors
                ( request.getBloodGroup(), request.getHospital() .getLocation() );

        if(donors.isEmpty())
        {
            System.out.println( "No eligible donors found." );
            request.setStatus( RequestStatus.CANCELLED );
            return;
        }

        System.out.println( "\nBest Donor Matches:" );

        for(DonorMatch donor : donors)
        {
            System.out.println( donor );
            donor.getRoute().displayRoute();
        }

        queueService.createRequests( donors );
    }

    public List<Hospital> getAllHospitals()
    { return hospitalService.getAllHospitals(); }

    // Donor accepts request
    public void acceptDonor( String donorId, EmergencyRequest request )
    {
        boolean ok = responseService.acceptDonorRequest( donorId, request );

        if(!ok)
        {
            System.out.println( "Donor ID not found among the active requests." );
        }
    }

    // Donor rejects request
    public void rejectDonor( String donorId, EmergencyRequest request )
    {
        boolean ok = responseService.rejectDonorRequest( donorId, request);

        if(!ok)
        {
            System.out.println( "Donor ID not found among the active requests." );
        }
    }
}
