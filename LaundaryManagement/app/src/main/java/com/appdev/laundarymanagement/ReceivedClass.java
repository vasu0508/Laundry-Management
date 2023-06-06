package com.appdev.laundarymanagement;

public class ReceivedClass {
    private String roomno;
    private String name;
    private String amount;
    private String quantity;
    private String date;
    private String time;
    private String cardNo;

    public ReceivedClass(String name, String roomno,String amount,String quantity,String date,String time,String cardNo){
        this.roomno = roomno;
        this.name = name;
        this.quantity=quantity;
        this.amount=amount;
        this.date=date;
        this.time=time;
        this.cardNo=cardNo;

    }

    public String getQuantity() {
        return quantity;
    }

    public String getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
