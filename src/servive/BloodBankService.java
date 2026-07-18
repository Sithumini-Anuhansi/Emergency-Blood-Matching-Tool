package service;

import model.BloodBank;

import java.util.ArrayList;
import java.util.List;


public class BloodBankService {


    private List<BloodBank> bloodBanks;



    public BloodBankService(){

        bloodBanks = new ArrayList<>();

    }




    /*
     * Add new blood bank
     */
    public void addBloodBank(BloodBank bloodBank){

        bloodBanks.add(bloodBank);

    }




    /*
     * Get all blood banks
     */
    public List<BloodBank> getAllBloodBanks(){

        return bloodBanks;

    }





    /*
     * Search blood banks
     * with required blood group
     */
    public List<BloodBank> findAvailableBlood(
            String bloodGroup,
            int quantity
    ){


        List<BloodBank> result =
                new ArrayList<>();



        for(BloodBank bank : bloodBanks){


            if(bank.hasBlood(
                    bloodGroup,
                    quantity
            )){


                result.add(bank);

            }

        }


        return result;

    }







    /*
     * Add stock to blood bank
     */
    public boolean addStock(
            String bankName,
            String bloodGroup,
            int quantity
    ){


        for(BloodBank bank : bloodBanks){


            if(bank.getName()
                    .equalsIgnoreCase(
                            bankName
                    )){


                bank.addBlood(
                        bloodGroup,
                        quantity
                );


                return true;

            }

        }


        return false;

    }







    /*
     * Remove stock after successful request
     */
    public boolean removeStock(
            String bankName,
            String bloodGroup,
            int quantity
    ){


        for(BloodBank bank : bloodBanks){


            if(bank.getName()
                    .equalsIgnoreCase(
                            bankName
                    )){


                bank.removeBlood(
                        bloodGroup,
                        quantity
                );


                return true;

            }

        }


        return false;

    }




}