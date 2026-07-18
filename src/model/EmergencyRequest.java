package model;


import java.time.LocalDateTime;


public class EmergencyRequest {


    private String requestId;


    private Hospital hospital;


    private String bloodGroup;


    private int quantity;



    private RequestStatus status;


    private LocalDateTime createdTime;




    public EmergencyRequest(
            String requestId,
            Hospital hospital,
            String bloodGroup,
            int quantity
    ){

        this.requestId = requestId;

        this.hospital = hospital;

        this.bloodGroup = bloodGroup;

        this.quantity = quantity;


        this.status =
                RequestStatus.SEARCHING;


        this.createdTime =
                LocalDateTime.now();

    }





    public String getRequestId(){

        return requestId;

    }



    public Hospital getHospital(){

        return hospital;

    }




    public String getBloodGroup(){

        return bloodGroup;

    }




    public int getQuantity(){

        return quantity;

    }




    public RequestStatus getStatus(){

        return status;

    }





    public void setStatus(
            RequestStatus status
    ){

        this.status = status;

    }




    public LocalDateTime getCreatedTime(){

        return createdTime;

    }




    @Override
    public String toString(){

        return
        "Request ID: "
        + requestId
        +
        "\nHospital: "
        + hospital.getName()
        +
        "\nBlood Group: "
        + bloodGroup
        +
        "\nQuantity: "
        + quantity
        +
        "\nStatus: "
        + status;

    }

}
