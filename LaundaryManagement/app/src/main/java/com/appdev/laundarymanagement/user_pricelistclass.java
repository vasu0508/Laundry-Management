package com.appdev.laundarymanagement;

public class user_pricelistclass {
        private String particular;
        private String unit;
        private String rate;

        public user_pricelistclass(String particular, String unit,String rate) {
            this.particular = particular;
            this.unit = unit;
            this.rate = rate;
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
}
