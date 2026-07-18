package model;


public class Hospital {


    private String hospitalId;

    private String name;

    private Location location;



    public Hospital(
            String hospitalId,
            String name,
            Location location
    ) {

        this.hospitalId = hospitalId;

        this.name = name;

        this.location = location;

    }



    public String getHospitalId() {

        return hospitalId;

    }



    public String getName() {

        return name;

    }



    public Location getLocation() {

        return location;

    }



    @Override
    public String toString() {

        return name;

    }

}