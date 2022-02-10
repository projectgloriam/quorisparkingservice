package com.projectgloriam.parkingservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("firstname")
    @Expose
    public String first_name;

    @SerializedName("lastname")
    @Expose
    public String last_name;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("phone")
    @Expose
    public String phone;

    public Integer getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
