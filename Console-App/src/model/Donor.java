package model;

public class Donor 
{
    private String donorId;
    private String name;
    private String bloodGroup;
    private int age;
    private boolean available;
    private boolean eligible;
    private String phoneNumber;
    private Location location;

    public Donor( String donorId, String name, String bloodGroup, int age, String phoneNumber, Location location ) 
    {
        this.donorId = donorId;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.available = true;
        this.eligible = true;
    }

    public String getDonorId() 
    { return donorId; }

    public String getName() 
    { return name; }

    public String getBloodGroup() 
    { return bloodGroup; }

    public int getAge() 
    { return age; }

    public boolean isAvailable() 
    { return available; }

    public boolean isEligible() 
    { return eligible; }

    public String getPhoneNumber() 
    { return phoneNumber; }

    public Location getLocation() 
    { return location; }

    public void setAvailable(boolean available) 
    { this.available = available; }

    public void setEligible(boolean eligible) 
    { this.eligible = eligible; }

    @Override
    public String toString() 
    {
        return name + " | Blood Group: " + bloodGroup + " | Available: " + available;
    }
}