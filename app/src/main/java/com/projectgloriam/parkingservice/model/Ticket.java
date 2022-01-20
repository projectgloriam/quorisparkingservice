package com.projectgloriam.parkingservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ticket {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("code")
    @Expose
    public String code;

    @SerializedName("rate_units")
    @Expose
    public Double rate_unit;

    @SerializedName("user_id")
    @Expose
    public Integer user_id;

    @SerializedName("park_id")
    @Expose
    public Integer park_id;

    @SerializedName("clock_in")
    @Expose
    public Date clock_in;

    @SerializedName("clock_out")
    @Expose
    public Date clock_out;

    @SerializedName("status")
    @Expose
    public Boolean status;

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Double getRate_unit() {
        return rate_unit;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Date getClock_in() {
        return clock_in;
    }

    public Date getClock_out() {
        return clock_out;
    }

    public Boolean getStatus() {
        return status;
    }

    public Boolean paymentComplete(){
        //Checks if amount paid is equal to sum of overtime amount plus rate amount
        return false;
    }

    public void setClock_out(Date clock_out) {
        if (paymentComplete()){
            this.clock_out = clock_out;
        }
    }

    public Boolean overtimePaid() {
        //Checks if amount paid method value is greater than rate amount
        return false;
    }

    public Double amountPaid(){
        //Check amount paid by total of payment relating to the ticket
        return 0.0;
    }

    public Double getOvertimeAmount() {
        //Calculates the overtime amount by dividing total duration method value by base minutes, flooring it and multiplying it by the rate amount
        return 0.0;
    }

    public String exceeded(){
        //Check if user exceeded booked minutes by comparing get total duration to booked minutes and returning it
        return "";
    }

    public String getTotalDuration(){
        //Sums total of all parking gauges
        return "";
    }

    public Integer getTotalParkingGauges(){
        //get total of parking spots used
        return 0;
    }

    public List<Object> getParkingGauges(){
        //Get list of names of parking spots, duration, their parking lot
        ///ticket/gauges ? id={ticket_id}
        List<Object> parkingGauges = new ArrayList();

        return parkingGauges;
    }

    public Boolean pay(){
        // /payment/new ? ticket_id={ticket_id} [POST]
        // run Payment complete
        if(paymentComplete()){
            return true;
        }
        return false;
    }




}
