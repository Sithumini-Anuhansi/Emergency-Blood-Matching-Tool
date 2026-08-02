package model;

import java.util.Collections;

public class DonorMatch
{
    private Donor donor;
    private RouteResult route;

    public DonorMatch( Donor donor, RouteResult route )
    {
        this.donor = donor;
        this.route = route;
    }

    // Convenience constructor for callers that only have a distance (e.g. simple tests).
    // Builds a minimal single-hop route so distance-based sorting still works.
    public DonorMatch( Donor donor, double distance )
    {
        this( donor, new RouteResult( Collections.singletonList( donor.getLocation() ), distance, 0 ) );
    }

    public Donor getDonor()
    { return donor; }

    public RouteResult getRoute()
    { return route; }

    public double getDistance()
    { return route.getTotalDistance(); }

    public int getEstimatedTime()
    { return route.getEstimatedTime(); }

    @Override
    public String toString()
    {
        return String.format( "%-20s | %-4s | %6.1f km | %3d mins",
                donor.getName(), donor.getBloodGroup(), getDistance(), getEstimatedTime() );
    }
}
