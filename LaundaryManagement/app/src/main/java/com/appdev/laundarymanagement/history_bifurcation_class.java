package com.appdev.laundarymanagement;

public class history_bifurcation_class {
    private String particular;
    private String quantity;

    public history_bifurcation_class(String particular, String quantity) {
        this.particular = particular;
        this.quantity=quantity;

    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }
}
