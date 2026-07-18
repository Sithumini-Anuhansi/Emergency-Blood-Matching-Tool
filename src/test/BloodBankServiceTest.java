package test;


import model.BloodBank;
import model.Location;
import service.BloodBankService;


public class BloodBankServiceTest {


    public static void main(String[] args) {



        Location colombo =
                new Location(
                        "L001",
                        "Colombo",
                        6.9271,
                        79.8612
                );



        BloodBank bank =
                new BloodBank(
                        "B001",
                        "Colombo Blood Bank",
                        colombo
                );



        BloodBankService service =
                new BloodBankService();



        service.addBloodBank(bank);



        service.addStock(
                "Colombo Blood Bank",
                "O+",
                10
        );



        System.out.println(
                "Searching O+ blood"
        );



        for(BloodBank result :
                service.findAvailableBlood(
                        "O+",
                        2
                )){


            System.out.println(result);

        }



    }

}