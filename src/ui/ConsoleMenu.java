package ui;

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

        System.out.println( "1. Create Blood Request" );

        System.out.println( "2. View Blood Banks" );

        System.out.println( "3. View Available Donors" );

        System.out.println( "4. Donor Response" );

        System.out.println( "5. Exit" );

        System.out.print( "Select option: " );

        return scanner.nextInt();
    }

    public String inputString( String message )
    {
        System.out.print( message );
        return scanner.next();
    }

    public int inputInt( String message )
    {
        System.out.print( message );
        return scanner.nextInt();
    }
}