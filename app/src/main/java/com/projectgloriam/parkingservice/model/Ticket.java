package com.projectgloriam.parkingservice.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ticket implements Parcelable {
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

    protected Ticket(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        code = in.readString();
        if (in.readByte() == 0) {
            rate_unit = null;
        } else {
            rate_unit = in.readDouble();
        }
        if (in.readByte() == 0) {
            user_id = null;
        } else {
            user_id = in.readInt();
        }
        if (in.readByte() == 0) {
            park_id = null;
        } else {
            park_id = in.readInt();
        }
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(code);
        if (rate_unit == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rate_unit);
        }
        if (user_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(user_id);
        }
        if (park_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(park_id);
        }
        dest.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
    }
}
