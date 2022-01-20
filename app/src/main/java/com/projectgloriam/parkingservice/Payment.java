package com.projectgloriam.parkingservice;

public class Payment {
    public Integer id;
    public Integer ticket_id;
    public Double amount;
    public String description;
    public Integer payment_method_id;

    public Payment(Integer id, Integer ticket_id, Double amount, String description, Integer payment_method_id) {
        this.id = id;
        this.ticket_id = ticket_id;
        this.amount = amount;
        this.description = description;
        this.payment_method_id = payment_method_id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTicket_id() {
        return ticket_id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPayment_method_id() {
        return payment_method_id;
    }

    public Boolean completePayment(){
       //    If the payment description is overtime, check if the money amount of the payments belonging to this ticket id that are overtime match ticket’s overtime amount by ticket id. If so, set ticket status to true
       return false;
    }

}
