package model;

import java.util.Objects;

public class Location 
{
    private String locationId;
    private String locationName;
    private double latitude;
    private double longitude;

    public Location(String locationId, String locationName, double latitude, double longitude) 
    {
        this.locationId = locationId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationId() 
    { return locationId; }

    public String getLocationName() 
    { return locationName; }    

    public double getLatitude() 
    { return latitude; }

    public double getLongitude() 
    { return longitude; }

    @Override
    public String toString() 
    { return locationName; }

    @Override
    public boolean equals(Object o) 
    {

        if (this == o)
            return true;

        if (!(o instanceof Location))
            return false;

        Location location = (Location) o;

        return Objects.equals(locationId, location.locationId);
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(locationId);
    }
}