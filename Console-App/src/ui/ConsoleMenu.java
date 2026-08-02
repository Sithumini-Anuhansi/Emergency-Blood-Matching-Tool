package ui;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu
{
    private Scanner scanner;

    public ConsoleMenu()
    {
        scanner = new Scanner(System.in);
    }

    public int showMenu()
    {
        System.out.println( "\n===== Emergency Blood Network =====" );
        System.out.println( "1. Select Hospital" );
        System.out.println( "2. Create Blood Request" );
        System.out.println( "3. View Blood Bank Stock" );
        System.out.println( "4. View Matching Donors" );
        System.out.println( "5. View Request Status" );
        System.out.println( "6. Donor Response (Accept/Reject)" );
        System.out.println( "7. Exit" );

        return inputInt( "Select option: " );
    }

    public String inputString( String message )
    {
        System.out.print( message );
        return scanner.next().trim();
    }

    // Robust integer input -- re-prompts on invalid entry instead of crashing
    // the whole program with an InputMismatchException.
    public int inputInt( String message )
    {
        while(true)
        {
            System.out.print( message );

            String line = scanner.next().trim();

            try
            {
                return Integer.parseInt(line);
            }
            catch(NumberFormatException e)
            {
                System.out.println( "Please enter a valid whole number." );
            }
        }
    }

    // Display a numbered list and let the user pick one, returning its index (0-based).
    public int selectFromList( String prompt, List<String> options )
    {
        System.out.println( "\n" + prompt );

        for(int i = 0; i < options.size(); i++)
        {
            System.out.println( (i+1) + ". " + options.get(i) );
        }

        int choice;

        do
        {
            choice = inputInt( "Select option: " );
        }
        while( choice < 1 || choice > options.size() );

        return choice - 1;
    }
}
