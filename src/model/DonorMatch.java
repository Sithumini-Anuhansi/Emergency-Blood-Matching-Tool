package model;

public class DonorMatch 
{
    private Donor donor;
    private double distance;

    public DonorMatch( Donor donor, double distance )
    {
        this.donor = donor;
        this.distance = distance;
    }

    public Donor getDonor()
    { return donor; }

    public double getDistance()
    { return distance; }

    @Override
    public String toString()
    {
        return donor.getName() + " | Distance: " + distance + " km";
    }
}