package test;


import model.Donor;
import model.Location;
import service.DonorService;


public class DonorServiceTest {


    public static void main(String[] args){



        Location colombo =
                new Location(
                        "L001",
                        "Colombo",
                        6.9271,
                        79.8612
                );



        Donor donor1 =
                new Donor(
                        "D001",
                        "John",
                        "O+",
                        25,
                        "0771234567",
                        colombo
                );



        Donor donor2 =
                new Donor(
                        "D002",
                        "Anne",
                        "A+",
                        30,
                        "0777654321",
                        colombo
                );



        DonorService service =
                new DonorService();



        service.addDonor(donor1);

        service.addDonor(donor2);




        System.out.println(
                "O+ Available Donors"
        );


        for(Donor donor :
                service.findEligibleDonors("O+")){


            System.out.println(donor);

        }



        service.updateAvailability(
                "D001",
                false
        );



        System.out.println(
                "\nAfter availability update"
        );



        for(Donor donor :
                service.findEligibleDonors("O+")){


            System.out.println(donor);

        }


    }

}