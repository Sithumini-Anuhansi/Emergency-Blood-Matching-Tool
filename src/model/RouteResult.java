package model;

import java.util.List;

public class RouteResult 
{
    private List<Location> path;
    private double totalDistance;
    private int estimatedTime;

    public RouteResult( List<Location> path, double totalDistance, int estimatedTime ) 
    {
        this.path = path;
        this.totalDistance = totalDistance;
        this.estimatedTime = estimatedTime;
    }

    public List<Location> getPath() 
    { return path; }

    public double getTotalDistance() 
    { return totalDistance; }

    public int getEstimatedTime()
    { return estimatedTime; }

    public void displayRoute() 
    {
        System.out.println("Route:");

        for(Location location : path) 
        {
            System.out.println( " ↓ " + location.getLocationName() );
        }

        System.out.println( "Distance: " + totalDistance + " km" );

        System.out.println( "Estimated Time: " + estimatedTime + " minutes" );
    }
}