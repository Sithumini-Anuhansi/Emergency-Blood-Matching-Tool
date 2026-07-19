package service;

import model.Hospital;

import java.util.ArrayList;
import java.util.List;

public class HospitalService 
{
    private List<Hospital> hospitals;

    public HospitalService()
    {
        hospitals = new ArrayList<>();
    }

    // Register a hospital
    public void addHospital( Hospital hospital )
    { hospitals.add(hospital); }

    // Return all hospitals
    public List<Hospital> getAllHospitals()
    { return hospitals; }

    // Find hospital by ID
    public Hospital findHospitalById( String hospitalId )
    {
        for(Hospital hospital : hospitals)
        {
            if(hospital.getHospitalId().equals(hospitalId))
            { return hospital; }
        }
        return null;
    }

    // Search hospitals by name
    public List<Hospital> searchHospital( String name )
    {
        List<Hospital> result = new ArrayList<>();

        for(Hospital hospital : hospitals)
        {
            if(hospital.getName().toLowerCase().contains( name.toLowerCase() ))
            {
                result.add(hospital);
            }
        }
        return result;
    }
}