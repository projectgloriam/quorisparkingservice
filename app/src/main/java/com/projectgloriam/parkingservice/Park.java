package com.projectgloriam.parkingservice;

import android.os.Parcel;
import android.os.Parcelable;

public class Park implements Parcelable {
    Integer id;
    String name;
    Double latitude;
    Double longitude;
    Double rate;
    Integer total_spots;
    Integer available_spots;

    public Park(Integer id, String name, Double latitude, Double longitude, Double rate, Integer total_spots, Integer available_spots) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rate = rate;
        this.total_spots = total_spots;
        this.available_spots = available_spots;
    }

    protected Park(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            rate = null;
        } else {
            rate = in.readDouble();
        }
        if (in.readByte() == 0) {
            total_spots = null;
        } else {
            total_spots = in.readInt();
        }
        if (in.readByte() == 0) {
            available_spots = null;
        } else {
            available_spots = in.readInt();
        }
    }

    public static final Creator<Park> CREATOR = new Creator<Park>() {
        @Override
        public Park createFromParcel(Parcel in) {
            return new Park(in);
        }

        @Override
        public Park[] newArray(int size) {
            return new Park[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getRate() {
        return rate;
    }

    public Integer getTotalSpots() {
        return total_spots;
    }

    public Integer getAvailableSpots() {
        return available_spots;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeDouble(rate);
        parcel.writeInt(total_spots);
        parcel.writeInt(available_spots);
    }
}
