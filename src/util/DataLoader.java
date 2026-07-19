package util;

import graph.Graph;
import model.*;
import service.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataLoader 
{
    // Load locations and add them to Graph --> locations.csv format: LocationID,LocationName,Latitude,Longitude
    public static Map<String, Location> loadLocations( String filePath, Graph graph )
    {
        Map<String, Location> locations = new LinkedHashMap<>();
        List<String[]> rows = CSVReader.readCSV(filePath);

        for(String[] row : rows)
        {
            String id = row[0];
            String name = row[1];
            double latitude = Double.parseDouble(row[2]);
            double longitude = Double.parseDouble(row[3]);
            Location location = new Location( id, name, latitude, longitude );

            locations.put( id, location );

            graph.addLocation( location );
        }
        return locations;
    }

    // Load roads into Graph --> roads.csv format: Source,Destination,Distance,TravelTime
    public static void loadRoads( String filePath, Graph graph, Map<String, Location> locations )
    {
        List<String[]> rows = CSVReader.readCSV(filePath);

        for(String[] row : rows)
        {
            if(row.length < 4)
                continue;    

            Location source = locations.get(row[0]);
            Location destination = locations.get(row[1]);
            double distance = Double.parseDouble(row[2]);
            int travelTime = Integer.parseInt(row[3]);

            if(source != null && destination != null)
            {
                graph.addRoad( source, destination, distance, travelTime );
            }
        }
    }

    // Load hospitals --> hospitals.csv format: HospitalID,HospitalName,LocationID
    public static void loadHospitals( String filePath, HospitalService hospitalService, Map<String, Location> locations )
    {
        List<String[]> rows = CSVReader.readCSV(filePath);

        for(String[] row : rows)
        {
            if(row.length < 4)
                continue;    

            Location location = locations.get(row[2]);

            if(location != null)
            {
                Hospital hospital = new Hospital( row[0], row[1], location );

                hospitalService.addHospital( hospital );
            }
        }
    }

    // Load blood banks --> bloodbanks.csv format: BankID,Name,LocationID, O+,A+,B+,AB+,O-,A-,B-,AB-
    public static void loadBloodBanks( String filePath, BloodBankService bloodBankService, Map<String, Location> locations )
    {
        List<String[]> rows = CSVReader.readCSV(filePath);

        for(String[] row : rows)
        {
            if(row.length < 4)
                continue;    

            Location location = locations.get(row[2]);

            if(location != null)
            {
                BloodBank bank = new BloodBank( row[0], row[1], location );

                // Add blood stock --> Index: 3 = O+, 4 = A+, 5 = B+, 6 = AB+, 7 = O-, 8 = A-, 9 = B-, 10 = AB-
                bank.addBlood( "O+", Integer.parseInt(row[3]) );
                bank.addBlood( "A+", Integer.parseInt(row[4]) );
                bank.addBlood( "B+", Integer.parseInt(row[5]) );
                bank.addBlood( "AB+", Integer.parseInt(row[6]) );
                bank.addBlood( "O-", Integer.parseInt(row[7]) );
                bank.addBlood( "A-", Integer.parseInt(row[8]) );
                bank.addBlood( "B-", Integer.parseInt(row[9]) );
                bank.addBlood( "AB-", Integer.parseInt(row[10]) );

                bloodBankService.addBloodBank( bank );
            }
        }
    }

    // Load donors -->  donors.csv format: DonorID,Name,BloodGroup,Age,Phone,Available,Eligible,LocationID
    public static void loadDonors( String filePath, DonorService donorService, Map<String, Location> locations )
    {
        List<String[]> rows = CSVReader.readCSV(filePath);

        for(String[] row : rows)
        {
            if(row.length < 4)
                continue;    

            Location location = locations.get(row[7]);

            if(location != null)
            {
                Donor donor = new Donor( row[0], row[1], row[2], Integer.parseInt(row[3]), row[4], location );

                donor.setAvailable( Boolean.parseBoolean(row[5]) );
                donor.setEligible( Boolean.parseBoolean(row[6]) );

                donorService.addDonor( donor );
            }
        }
    }
}