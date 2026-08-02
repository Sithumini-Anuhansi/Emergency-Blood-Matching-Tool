package test;

import model.Hospital;
import model.Location;
import service.HospitalService;

public class HospitalServiceTest 
{
    public static void main(String[] args) 
    {
        Location location = new Location( "L001", "Colombo", 6.9271, 79.8612 );

        Hospital hospital = new Hospital( "H001", "Colombo General Hospital", location );

        HospitalService service = new HospitalService();

        service.addHospital( hospital );

        System.out.println( "Registered Hospitals:" );

        for(Hospital h : service.getAllHospitals())
        {
            System.out.println(h);
        }

        System.out.println( "\nSearch Result:" );

        System.out.println( service.searchHospital( "Colombo" ) );
    }
}