package com.projectgloriam.parkingservice;

import java.text.ParseException;
import java.util.Date;

public class ParkingGauge {
    Integer id;
    Integer spot_id;
    Integer ticket_id;
    Date start;
    Date end;

    public ParkingGauge(Integer id, Integer spot_id, Integer ticket_id, Date start, Date end) {
        this.id = id;
        this.spot_id = spot_id;
        this.ticket_id = ticket_id;
        this.start = start;
        this.end = end;

    }

    public Integer getId() {
        return id;
    }

    public Integer getSpot_id() {
        return spot_id;
    }

    public Integer getTicket_id() {
        return ticket_id;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getDuration() throws ParseException {

        // Calculate time difference
        // in milliseconds
        long difference_In_Time
                = end.getTime() - start.getTime();

        // Calculate time difference in
        // seconds, minutes, hours
        long difference_In_Seconds
                = (difference_In_Time
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        // Return the date difference in
        // hours, in
        // minutes, and in seconds

        return difference_In_Hours
                + ":"
                + difference_In_Minutes
                + ":"
                + difference_In_Seconds;
    }

    Boolean isPaidFor(){

        return false;
    }
}
