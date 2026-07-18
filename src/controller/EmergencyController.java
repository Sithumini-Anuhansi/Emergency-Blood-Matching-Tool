package controller;


import model.*;
import service.*;

import java.util.List;



public class EmergencyController {


    private HospitalService hospitalService;

    private BloodBankService bloodBankService;

    private DonorService donorService;

    private DonorMatchingService matchingService;

    private RequestQueueService queueService;

    private DonorResponseService responseService;




    public EmergencyController(
            HospitalService hospitalService,
            BloodBankService bloodBankService,
            DonorService donorService,
            DonorMatchingService matchingService,
            RequestQueueService queueService
    ){


        this.hospitalService =
                hospitalService;


        this.bloodBankService =
                bloodBankService;


        this.donorService =
                donorService;


        this.matchingService =
                matchingService;


        this.queueService =
                queueService;


        this.responseService =
                new DonorResponseService(
                        queueService
                );


    }







    /*
     * Create emergency request
     */
    public EmergencyRequest createRequest(
            String requestId,
            String hospitalId,
            String bloodGroup,
            int quantity
    ){


        Hospital hospital =
                hospitalService.findHospitalById(
                        hospitalId
                );



        if(hospital == null){

            System.out.println(
                    "Hospital not found"
            );

            return null;

        }



        EmergencyRequest request =
                new EmergencyRequest(
                        requestId,
                        hospital,
                        bloodGroup,
                        quantity
                );



        return request;

    }







    /*
     * Check blood bank availability
     */
    public void checkBloodBanks(
            EmergencyRequest request
    ){


        List<BloodBank> banks =
                bloodBankService.findAvailableBlood(
                        request.getBloodGroup(),
                        request.getQuantity()
                );



        if(banks.isEmpty()){


            System.out.println(
                    "No blood available in blood banks"
            );


        }
        else{


            System.out.println(
                    "Available Blood Banks:"
            );



            for(BloodBank bank :
                    banks){


                System.out.println(
                        bank
                );

            }


        }


    }








    /*
     * Search donors
     */
    public void findDonors(
            EmergencyRequest request
    ){


        List<DonorMatch> donors =
                matchingService.findBestDonors(
                        request.getBloodGroup(),
                        request.getHospital()
                                .getLocation()
                );



        System.out.println(
                "\nBest Donor Matches:"
        );



        for(DonorMatch donor :
                donors){


            System.out.println(
                    donor
            );

        }



        queueService.createRequests(
                donors
        );


    }








    /*
     * Donor accepts request
     */
    public void acceptDonor(
            String donorId,
            EmergencyRequest request
    ){


        responseService.acceptDonorRequest(
                donorId,
                request
        );


    }







    /*
     * Donor rejects request
     */
    public void rejectDonor(
            String donorId,
            EmergencyRequest request
    ){


        responseService.rejectDonorRequest(
                donorId,
                request
        );


    }



}
