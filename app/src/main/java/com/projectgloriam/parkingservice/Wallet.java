package com.projectgloriam.parkingservice;

public class Wallet {
    Integer id;
    Integer user_id;
    Integer payment_method_id;

    public Wallet(Integer id, Integer user_id, Integer payment_method_id) {
        this.id = id;
        this.user_id = user_id;
        this.payment_method_id = payment_method_id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(Integer payment_method_id) {
        this.payment_method_id = payment_method_id;
    }
}
