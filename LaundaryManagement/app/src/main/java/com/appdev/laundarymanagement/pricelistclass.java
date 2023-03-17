package com.appdev.laundarymanagement;

public class pricelistclass {
        private String particular;
        private String unit;
        private String rate;
        private String quantity="0";
        private String totalprice="0";

        public pricelistclass(String particular, String unit,String rate) {
            this.particular = particular;
            this.unit = unit;
            this.rate = rate;
        }
    public pricelistclass(String particular, String unit,String rate,String totalprice) {
        this.particular = particular;
        this.unit = unit;
        this.rate = rate;
        this.totalprice=totalprice;
    }
        public String getParticular() {
            return particular;
        }

        public void setParticular(String particular) {
            this.particular = particular;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getUnit() {
        return unit;
    }

        public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getTotalprice() {
        return totalprice;
    }
}
