package service;

import model.Donor;

import java.util.ArrayList;
import java.util.List;

public class DonorService 
{
    private List<Donor> donors;

    public DonorService()
    {
        donors = new ArrayList<>();
    }

    // Add new donor
    public void addDonor(Donor donor)
    {
        donors.add(donor);
    }

    // Return all donors
    public List<Donor> getAllDonors()
    {
        return donors;
    }

    // Find donors by blood group
    public List<Donor> findByBloodGroup( String bloodGroup )
    {
        List<Donor> result = new ArrayList<>();

        for(Donor donor : donors)
        {
            if(donor.getBloodGroup().equalsIgnoreCase( bloodGroup ))
            {
                result.add(donor);
            }
        }
        return result;
    }

    // Find available and eligible donors
    public List<Donor> findEligibleDonors( String bloodGroup )
    {
        List<Donor> result = new ArrayList<>();

        for(Donor donor : donors)
        {
            if(donor.getBloodGroup().equalsIgnoreCase(bloodGroup ) && donor.isAvailable() && donor.isEligible() )
            {
                result.add(donor);
            }
        }
        return result;
    }

    // Update donor availability
    public boolean updateAvailability( String donorId, boolean status )
    {
        for(Donor donor : donors)
        {
            if(donor.getDonorId().equals(donorId))
            {
                donor.setAvailable(status);
                return true;
            }
        }
        return false;
    }

    // Update donor eligibility
    public boolean updateEligibility( String donorId, boolean status )
    {
        for(Donor donor : donors)
        {
            if(donor.getDonorId().equals(donorId))
            {
                donor.setEligible(status);
                return true;
            }
        }
        return false;
    }
}