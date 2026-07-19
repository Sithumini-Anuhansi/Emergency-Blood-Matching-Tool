import controller.EmergencyController;

import model.EmergencyRequest;

import system.EmergencyBloodSystem;

import ui.ConsoleMenu;



public class Main {


    public static void main(String[] args) {



        /*
         * Start System
         */

        EmergencyBloodSystem system =
                new EmergencyBloodSystem();



        EmergencyController controller =
                system.getController();



        ConsoleMenu menu =
                new ConsoleMenu();





        EmergencyRequest currentRequest = null;



        boolean running = true;



        while(running){



            int option =
                    menu.showMenu();




            switch(option){



                case 1:



                    System.out.println(
                            "\n--- Create Blood Request ---"
                    );



                    String bloodGroup =
                            menu.inputString(
                                    "Blood Group: "
                            );



                    int quantity =
                            menu.inputInt(
                                    "Quantity: "
                            );



                    currentRequest =
                            controller.createRequest(
                                    "REQ001",
                                    "H001",
                                    bloodGroup,
                                    quantity
                            );



                    if(currentRequest != null){


                        System.out.println(
                                "\nRequest Created"
                        );


                        System.out.println(
                                currentRequest
                        );



                        controller.checkBloodBanks(
                                currentRequest
                        );



                        controller.findDonors(
                                currentRequest
                        );

                    }



                    break;






                case 2:



                    if(currentRequest != null){


                        controller.checkBloodBanks(
                                currentRequest
                        );


                    }
                    else{


                        System.out.println(
                                "Create request first"
                        );


                    }


                    break;

                case 3:

                    if(currentRequest != null){


                        controller.findDonors(
                                currentRequest
                        );


                    }
                    else{


                        System.out.println(
                                "Create request first"
                        );


                    }



                    break;


                case 4:

                    if(currentRequest != null){

                        String donorId =
                                menu.inputString(
                                        "Donor ID: "
                                );



                        controller.acceptDonor(
                                donorId,
                                currentRequest
                        );



                        System.out.println(
                                currentRequest.getStatus()
                        );


                    }

                    break;

                case 5:

                    if(currentRequest != null){

                        String donorId =
                                menu.inputString(
                                        "Donor ID: "
                                );

                        controller.rejectDonor(
                                donorId,
                                currentRequest
                        );
                        System.out.println(
                                currentRequest.getStatus()
                        );
                    }
                    break;

                case 6:

                    running = false;
                    System.out.println(
                            "System Closed"
                    );
                    break;
                default:

                    System.out.println(
                            "Invalid Option"
                    );
            }

        }

    }

}
