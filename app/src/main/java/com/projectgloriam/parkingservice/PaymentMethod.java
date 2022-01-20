package com.projectgloriam.parkingservice;

public class PaymentMethod {
    Integer id;
    String name;

    public PaymentMethod(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
