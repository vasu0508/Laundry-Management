package com.appdev.laundarymanagement;

public class newClass {
    private String title;
    private String Value;
    private String CardNo;

    public newClass(String name, String roomno,String CardNo) {
        this.Value = roomno;
        this.title = name;
        this.CardNo=CardNo;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getCardNo() {
        return CardNo;
    }
}
