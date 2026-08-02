package model;

public class DonorRequest 
{
    private Donor donor;
    private String status;

    public DonorRequest( Donor donor )
    {
        this.donor = donor;
        this.status = "PENDING";
    }

    public Donor getDonor()
    { return donor; }

    public String getStatus()
    { return status; }

    public void accept()
    { status = "ACCEPTED"; }

    public void reject()
    { status = "REJECTED"; }

    @Override
    public String toString()
    {
        return donor.getName() + " - " + status;
    }
}