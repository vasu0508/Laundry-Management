package com.appdev.laundarymanagement;

public class UserHistoryClass {
    private String status;
    private String amount;
    private String quantity;
    private String date;
    private String time;

    public UserHistoryClass(String status, String amount,String quantity,String date,String time) {
        this.status = status;
        this.quantity=quantity;
        this.amount=amount;
        this.date=date;
        this.time=time;

    }

    public String getQuantity() {
        return quantity;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {this.status = status;}

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
