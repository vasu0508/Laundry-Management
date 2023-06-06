package com.appdev.laundarymanagement;

public class newClass {
    private String title;
    private String Value;
    private String CardNo;
    private String InstituteCode;

    public newClass(String name, String roomno,String CardNo,String InstituteCode){
        this.Value = roomno;
        this.title = name;
        this.CardNo=CardNo;
        this.InstituteCode=InstituteCode;
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

    public String getInstituteCode() {
        return InstituteCode;
    }

    public void setInstituteCode(String instituteCode) {
        InstituteCode = instituteCode;
    }
}
