package util;

import java.io.*;
import java.util.*;

public class DataGenerator 
{
    private static final String INPUT_FILE = "data/donor_dataset.csv";
    private static final String OUTPUT_FOLDER = "data/";

    public static void main(String[] args) 
    {
        try 
        {
            generateDatasets();
            System.out.println( "Dataset generation completed successfully!" );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void generateDatasets()
        throws Exception 
    {
        BufferedReader reader = new BufferedReader( new FileReader(INPUT_FILE) );

        // Skip header
        String header = reader.readLine();

        Map<String,String> districtLocationMap = new LinkedHashMap<>();
        List<String[]> donors = new ArrayList<>();
        String line;
        int locationCounter = 1;

        while((line = reader.readLine()) != null)
        {
            if(line.trim().isEmpty())
                continue;

            String[] data = line.split(",");

            for(int i=0;i<data.length;i++)
            {
                data[i]=data[i].trim();
            }

            String district = data[6];

            if(!districtLocationMap.containsKey(district))
            {
                String locationId = "L" + String.format( "%03d", locationCounter++ );

                districtLocationMap.put( district, locationId );
            }

            donors.add(data);
        }

        reader.close();

        generateLocations( districtLocationMap );
        generateDonors( donors, districtLocationMap );
        generateHospitals( districtLocationMap );
        generateBloodBanks( districtLocationMap );
        generateRoads( districtLocationMap );
    }

    private static void generateLocations( Map<String,String> locations )
        throws Exception
    {
        PrintWriter writer = new PrintWriter( OUTPUT_FOLDER +"locations.csv" );

        writer.println( "LocationID,LocationName,Latitude,Longitude" );

        Random random = new Random();

        for(String district : locations.keySet())
        {
            String id = locations.get(district);
            double latitude = 5.9 + random.nextDouble();
            double longitude = 79.8 + random.nextDouble();

            writer.println( id + "," + district + "," + latitude + "," + longitude );
        }
        writer.close();
    }

    private static void generateDonors( List<String[]> donors, Map<String,String> locations )
        throws Exception
    {
        PrintWriter writer = new PrintWriter( OUTPUT_FOLDER +"donors.csv" );

        writer.println( "DonorID,Name,BloodGroup,Age,Phone,Available,Eligible,LocationID" );

        int id=1;

        for(String[] donor : donors)
        {
            String donorId = "D" + String.format( "%03d", id++ );

            String name = donor[1] +" " +donor[2];

            String bloodGroup = donor[5];

            String age = donor[4];

            String phone = donor[7];

            String available = donor[12];

            String eligible = donor[11];

            String locationId = locations.get( donor[6] );

            writer.println( donorId + "," + name + "," + bloodGroup + "," + age + "," + phone + "," + available + "," + eligible + "," + locationId );
        }
        writer.close();
    }

    private static void generateHospitals( Map<String,String> locations )
        throws Exception 
    {
        PrintWriter writer = new PrintWriter( OUTPUT_FOLDER +"hospitals.csv" );

        writer.println( "HospitalID,HospitalName,LocationID" );

        int id=1;

        for(String district : locations.keySet())
        {
            writer.println( "H" + String.format( "%03d", id++ )
                    + ",District General Hospital " + district + "," + locations.get(district) );
        }
        writer.close();
    }

    private static void generateBloodBanks( Map<String,String> locations )
        throws Exception 
    {
        PrintWriter writer = new PrintWriter( OUTPUT_FOLDER +"bloodbanks.csv" );

        writer.println( "BloodBankID,Name,LocationID,O+,A+,B+,AB+,O-,A-,B-,AB-" );

        Random random = new Random();

        int id=1;

        for(String district : locations.keySet())
        {
            writer.println( "B" + String.format( "%03d", id++ ) + "," + district 
                + " Blood Bank," + locations.get(district) + "," + random.nextInt(20) + "," + random.nextInt(20) 
                + "," + random.nextInt(20) + "," + random.nextInt(10) + "," + random.nextInt(10)
                + "," + random.nextInt(10) + "," + random.nextInt(10) + "," + random.nextInt(10) );
        }
        writer.close();
    }

    private static void generateRoads( Map<String,String> locations )
        throws Exception 
    {
        PrintWriter writer = new PrintWriter( OUTPUT_FOLDER +"roads.csv" );

        writer.println( "Source,Destination,Distance,TravelTime" );

        List<String> ids = new ArrayList<>( locations.values() );

        Random random = new Random();

        // Create connected graph.  Every location connects to the next location
        for(int i=0;i<ids.size()-1;i++)
        {
            int distance = 20 + random.nextInt(100);
            int time = distance * 2;

            writer.println( ids.get(i) + "," + ids.get(i+1) + "," + distance + "," + time );
        }

        // Add extra roads
        for(int i=0;i<ids.size();i++)
        {
            int destination = random.nextInt( ids.size() );

            if(destination!=i)
            {
                writer.println( ids.get(i) + "," + ids.get(destination) + "," + (20+random.nextInt(100)) + "," + (40+random.nextInt(150)) );
            }
        }
        writer.close();
    }
}