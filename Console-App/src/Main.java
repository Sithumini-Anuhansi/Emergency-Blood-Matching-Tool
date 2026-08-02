import controller.EmergencyController;
import model.EmergencyRequest;
import model.Hospital;
import system.EmergencyBloodSystem;
import ui.ConsoleMenu;

import java.util.List;
import java.util.stream.Collectors;

public class Main
{
    public static void main(String[] args)
    {
        // Start System
        EmergencyBloodSystem system = new EmergencyBloodSystem();
        EmergencyController controller = system.getController();
        ConsoleMenu menu = new ConsoleMenu();

        Hospital selectedHospital = null;
        EmergencyRequest currentRequest = null;
        boolean running = true;

        while(running)
        {
            int option = menu.showMenu();

            switch(option)
            {
                case 1:
                {
                    List<Hospital> hospitals = controller.getAllHospitals();

                    if(hospitals.isEmpty())
                    {
                        System.out.println( "No hospitals loaded." );
                        break;
                    }

                    List<String> names = hospitals.stream()
                            .map( h -> h.getHospitalId() + " - " + h.getName() )
                            .collect( Collectors.toList() );

                    int index = menu.selectFromList( "Select a hospital:", names );

                    selectedHospital = hospitals.get(index);

                    System.out.println( "Selected: " + selectedHospital.getName() );
                    break;
                }

                case 2:
                    if(selectedHospital == null)
                    {
                        System.out.println( "Select a hospital first (option 1)." );
                        break;
                    }

                    System.out.println( "\n--- Create Blood Request ---" );

                    String bloodGroup = menu.inputString( "Blood Group (e.g. O+): " );
                    int quantity = menu.inputInt( "Quantity (units): " );

                    currentRequest = controller.createRequest
                            ( selectedHospital.getHospitalId(), bloodGroup, quantity );

                    if(currentRequest != null)
                    {
                        System.out.println( "\nRequest Created" );
                        System.out.println( currentRequest );

                        // Check blood banks first; if none available, this automatically
                        // finds, ranks and queues the top 10 eligible donors.
                        controller.processRequest( currentRequest );
                    }
                    break;

                case 3:
                    controller.viewBloodBankStock();
                    break;

                case 4:
                    if(currentRequest != null)
                    {
                        controller.previewDonors( currentRequest );
                    }
                    else
                    {
                        System.out.println( "Create a request first (option 2)." );
                    }
                    break;

                case 5:
                    if(currentRequest != null)
                    {
                        System.out.println( "\n" + currentRequest );
                    }
                    else
                    {
                        System.out.println( "No active request." );
                    }
                    break;

                case 6:
                    if(currentRequest == null)
                    {
                        System.out.println( "No active request." );
                        break;
                    }

                    String donorId = menu.inputString( "Donor ID: " );
                    String response = menu.inputString( "Accept or Reject (A/R): " );

                    if(response.equalsIgnoreCase("A"))
                    {
                        controller.acceptDonor( donorId, currentRequest );
                    }
                    else
                    {
                        controller.rejectDonor( donorId, currentRequest );
                    }

                    System.out.println( "Status: " + currentRequest.getStatus() );
                    break;

                case 7:
                    running = false;
                    System.out.println( "System Closed" );
                    break;

                default:
                    System.out.println( "Invalid Option" );
            }
        }
    }
}
