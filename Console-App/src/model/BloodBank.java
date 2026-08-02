package model;

import java.util.HashMap;
import java.util.Map;

public class BloodBank 
{
    private String bankId;
    private String name;
    private Location location;
    private Map<String,Integer> bloodStock;

    public BloodBank( String bankId, String name, Location location ) 
    {
        this.bankId = bankId;
        this.name = name;
        this.location = location;
        bloodStock = new HashMap<>();
        initializeStock();
    }

    private void initializeStock()
    {
        bloodStock.put("A+",0);
        bloodStock.put("A-",0);
        bloodStock.put("B+",0);
        bloodStock.put("B-",0);
        bloodStock.put("AB+",0);
        bloodStock.put("AB-",0);
        bloodStock.put("O+",0);
        bloodStock.put("O-",0);
    }

    public void addBlood( String bloodGroup, int quantity )
    {
        bloodStock.put( bloodGroup, bloodStock.getOrDefault( bloodGroup, 0 ) + quantity );
    }

    public boolean hasBlood( String bloodGroup, int quantity )
    {
        return bloodStock.getOrDefault( bloodGroup, 0 ) >= quantity;
    }

    public void removeBlood( String bloodGroup, int quantity )
    {
        if(hasBlood( bloodGroup, quantity ))
        {
            bloodStock.put( bloodGroup, bloodStock.get(bloodGroup) - quantity );
        }
    }

    public String getName()
    {
        return name;
    }

    public Location getLocation()
    {
        return location;
    }

    public Map<String,Integer> getBloodStock()
    {
        return bloodStock;
    }

    @Override
    public String toString()
    {
        return name;
    }
}